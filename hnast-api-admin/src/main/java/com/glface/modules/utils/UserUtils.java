package com.glface.modules.sys.utils;

import com.glface.base.bean.BaseEntity;
import com.glface.model.SysUser;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Date;

/**
 * 用户工具类
 */
public class UserUtils {

	/**
	 * 得到当前登录用户
	 */
	public static SysUser getUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null) {
			if (authentication instanceof AnonymousAuthenticationToken) {
				return null;
			}
			return ((SysUser)authentication.getPrincipal());
		}
		//如果没有登录返回null
		return null;
	}

	/**
	 * 得到当前登录用户id
	 */
	public static String getUserId(){
		String userId = null;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null) {
			if (authentication instanceof AnonymousAuthenticationToken) {
				return null;
			}
			SysUser sysUser=((SysUser)authentication.getPrincipal());
			userId=sysUser.getId();
		}
		//如果没有登录返回null
		return userId;
	}

	public static <T extends BaseEntity> void preAdd(T entity){
		SysUser loginUser = getUser();
		if(loginUser!=null){
			entity.setCreateBy(loginUser.getId());
			entity.setUpdateBy(loginUser.getId());
		}
		Date now = new Date();
		entity.setCreateDate(now);
		entity.setUpdateDate(now);
	}
	public static <T extends BaseEntity> void preUpdate(T entity){
		SysUser loginUser = getUser();
		if(loginUser!=null){
			entity.setUpdateBy(loginUser.getId());
		}
		Date now = new Date();
		entity.setUpdateDate(now);
	}
}
