package com.glface.security;

import com.glface.constant.Common;
import com.glface.model.SysMenu;
import com.glface.model.SysUser;
import com.glface.model.SysUserRole;
import com.glface.modules.service.MenuService;
import com.glface.modules.service.RoleMenuService;
import com.glface.modules.service.UserRoleService;
import com.glface.modules.service.UserService;
import com.glface.modules.sys.utils.UserUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Token过滤器
 * 主要实现了对请求的拦截，当拿到token之后去数据库或者缓存里拿用户信息进行授权即可。
 */
@Component
public class TokenFilter extends OncePerRequestFilter {

	private static final String TOKEN_KEY = Common.Service.IOT_AUTH_TOKEN;

	@Resource
	private UserService userService;
	@Resource
	private UserRoleService userRoleService;
	@Resource
	private RoleMenuService roleMenuService;
	@Resource
	private MenuService menuService;
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
			String token = getToken(request);
			if (StringUtils.isNotBlank(token) && !"undefined".equals(token)) {
				SysUser user = UserUtils.getUser();
				if(user==null){
					user=userService.findUserByToken(token);
					if(user!=null){
						if(user.isAdmin()){//超级管理员
							List<SysMenu> menus = menuService.findAll();
							if(menus!=null)user.setMenus(new HashSet<>(menus));
						}else{
							Set<SysUserRole> userRoles = userRoleService.findUserRolesByUserId(user.getId());
							if(null!=userRoles || userRoles.size()!=0){
								List<String> roleIds=new ArrayList<String>(userRoles.size());
								Iterator<SysUserRole> iterator = userRoles.iterator();
								while (iterator.hasNext()){
									SysUserRole userRole=iterator.next();
									roleIds.add(userRole.getRoleId());
								}
								Set<SysMenu> menus = new HashSet<SysMenu>();
								for (String roleId:roleIds){
									Set<SysMenu> rMenus = roleMenuService.findMenusByRoleId(roleId);
									menus.addAll(rMenus);
								}
								user.setMenus(menus);
							}
						}
						UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user,
								null, getAuthorities(user.getMenus()));
						SecurityContextHolder.getContext().setAuthentication(authentication);
					}
				}
			}
		filterChain.doFilter(request, response);
	}

	 /**
	 * 根据参数或者header获取token
	 */

	private static String getToken(HttpServletRequest request) {
		String token = request.getParameter(TOKEN_KEY);
		if (StringUtils.isBlank(token)) {
			token = request.getHeader(TOKEN_KEY);
		}
		return token;
	}

	private static Collection<? extends GrantedAuthority> getAuthorities(Set<SysMenu> menus) {
		if(menus==null) return null;
		return menus.parallelStream().filter(p -> !org.springframework.util.StringUtils.isEmpty(p.getPermission()))
				.map(p -> new SimpleGrantedAuthority(p.getPermission())).collect(Collectors.toSet());
	}


}
