package com.glface.modules.controller;

import com.glface.base.bean.DynamicBean;
import com.glface.base.bean.Page;
import com.glface.base.bean.R;
import com.glface.base.utils.AntiSQLInjectionUtil;
import com.glface.base.utils.DateUtils;
import com.glface.base.utils.NamingStrategyUtils;
import com.glface.base.utils.StringUtils;
import com.glface.common.exeception.Exception401;
import com.glface.common.web.ApiCode;
import com.glface.model.*;
import com.glface.modules.service.*;
import com.glface.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/system/user")
public class UserController {
    @Resource
    private UserService userService;
    @Resource
    private OfficeService officeService;
    @Resource
    private UserOfficeService userOfficeService;
    @Resource
    private UserRoleService userRoleService;

    /**
     * 得到当前登录用户信息
     */
    @RequestMapping(value = "/loginUser")
    public R<Object> loginUser() {

        SysUser user = UserUtils.getUser();
        if (user == null) {
            throw new Exception401(ApiCode.ACCOUNT_NOT_LOGIN.getMsg());
        }

        // 获取用户所有角色数据
        List<SysRole> roles = userService.findRolesByUserId(user.getId());
        //获取用户部门
        List<SysOffice> offices = userService.findOfficesByUserId(user.getId());

        //构造返回数据
        List<Object> roleList = Lists.newArrayList();
        for (SysRole role : roles) {
            roleList.add(role.convertToDynamicBean().getObject());
        }
        List<Object> officeList = Lists.newArrayList();
        for (SysOffice office : offices) {
            officeList.add(office.convertToDynamicBean().getObject());
        }

        Object object = new DynamicBean.Builder().setPV("id", user.getId())
                .setPV("account", user.getAccount())
                .setPV("nickname", user.getNickname(), String.class)
                .setPV("postName", user.getPostName())
                .setPV("email", user.getEmail())
                .setPV("mobile", user.getMobile())
                .setPV("unionid", user.getUnionid())
                .setPV("roles", roleList, List.class)
                .setPV("offices", officeList, List.class).build().getObject();
        return R.ok(object);
    }

    @PreAuthorize("hasAuthority('permission:user:view')")
    @RequestMapping(value = "/get")
    public R<Object> get(String id) {
        SysUser user = userService.get(id);
        // 获取用户所有角色数据
        Set<SysUserRole> userRoles = userRoleService.findUserRolesByUserId(id);
        List<String> roleIds = new ArrayList<>(userRoles.size());
        for (SysUserRole userRole : userRoles) {
            roleIds.add(userRole.getRoleId());
        }
        // 获取所属机构
        List<SysUserOffice> userOffices = userOfficeService.findUserOfficesByUserId(id);
        List<List<String>> officeIds = new ArrayList<>(userOffices.size());
        for (SysUserOffice userOffice : userOffices) {
            officeIds.add(officeService.findOneOffice(userOffice.getOfficeId()));
        }

        Object object = new DynamicBean.Builder().setPV("id", user.getId())
                .setPV("account", user.getAccount())
                .setPV("nickname", user.getNickname(), String.class)
                .setPV("postName", user.getPostName())
                .setPV("email", user.getEmail())
                .setPV("mobile", user.getMobile())
                .setPV("roleIds", roleIds, List.class)
                .setPV("officeIds", officeIds, List.class)
                .build().getObject();

        return R.ok(object);
    }

    /**
     * 获取所有部门，并组织成树形结构
     */
    @PreAuthorize("hasAuthority('permission:user:view')")
    @RequestMapping(value = "/officeTree")
    public R<List<SysOffice>> tree() {
        List<SysOffice> officeList = officeService.allListTree();
        return R.ok(officeList);
    }

    @PreAuthorize("hasAuthority('permission:user:view')")
    @RequestMapping(value = "/search")
    public R<Object> search(String account,String mobile, String name, String officeId,
                            @RequestParam(value = "page", defaultValue = "1") int pageNo,
                            @RequestParam(value = "limit", defaultValue = "10") int limit,
                            @RequestParam(value = "order", defaultValue = "createDate asc") String order) {
        order = NamingStrategyUtils.underscoreName(AntiSQLInjectionUtil.filter(order.trim()));
        order = "u." + order;
        // 设置查询条件
        Page<SysUser> page = new Page<>(pageNo,limit);
        page.setOrderBy(order);
        SysUser user = new SysUser();
        user.setNickname(name);
        user.setAccount(account);
        user.setMobile(mobile);
        if (StringUtils.isNotEmpty(officeId)) {
            SysOffice office = officeService.get(officeId);
            if (office != null) {
                user.setOfficeCode(office.getCode());
            } else {
                user.setOfficeCode("nnnnnnnnn");
            }
        }

        // 查询
        page = userService.pageSearch(page,user);

        // 构造返回数据
        List<Object> userList = new ArrayList<>();
        for (SysUser u : page.getList()) {
            //查询用户部门
            Set<SysOffice> offices = userOfficeService.findOfficesByUserId(u.getId());
            Object userBean = new DynamicBean.Builder().setPV("id", u.getId())
                    .setPV("account", u.getAccount())
                    .setPV("nickname", u.getNickname())
                    .setPV("mobile", u.getMobile())
                    .setPV("postName", u.getPostName())
                    .setPV("email", u.getEmail())
                    .setPV("lastLoginIp", u.getLastLoginIp())
                    .setPV("loginCount", u.getLoginCount())
                    .setPV("lastLoginTime", DateUtils.formatDate(u.getLastLoginTime(), "yyyy-MM-dd HH:mm:ss"))
                    .setPV("offices", offices, Set.class)
                    .setPV("createDate", DateUtils.formatDate(u.getCreateDate(), "yyyy-MM-dd HH:mm:ss")).build().getObject();
            userList.add(userBean);
        }

        Object data = new DynamicBean.Builder()
                .setPV("total", page.getCount())
                .setPV("users", userList, List.class)
                .build().getObject();

        return R.ok(data);
    }

    /**
     * 删除用户 同时删除用户角色 用户部门
     */
    @PreAuthorize("hasAuthority('permission:user:del')")
    @RequestMapping(value = "/delete")
    public R<Object> delete(String id) {
        userService.delete(id);
        return R.ok();
    }

    /**
     * 创建用户
     *
     * @param account   账号
     * @param nickname  昵称
     * @param email     邮箱
     * @param officeIds 归属部门
     * @param password  密码
     * @param mobile    手机号
     * @param roleIds   角色
     */
    @PreAuthorize("hasAuthority('permission:user:add')")
    @RequestMapping(value = "/create")
    public R<Object> create(
            String account,
            String nickname,
            String email,
            @RequestParam(value = "officeIds[]",required = false) List<String> officeIds,
            String password,
            String mobile,
            @RequestParam(value = "roleIds[]",required = false) List<String> roleIds) {
        userService.create(account, officeIds, password,
                mobile, roleIds,nickname,email);
        return R.ok();
    }

    /**
     * 编辑用户 同时修改用户角色信息.如果password为空，表示不修改密码
     *
     * @param id        用户id
     * @param nickname  名称
     * @param email     邮箱
     * @param officeIds 归属部门
     * @param password  如果密码为空 则表示不修改
     * @param mobile    联系电话
     * @param roleIds   角色
     */
    @PreAuthorize("hasAuthority('permission:user:edit')")
    @RequestMapping(value = "/update")
    public R<Object> update(
            String id,
            String nickname,
            String email,
            @RequestParam(required = false) String password,
            String mobile,
            @RequestParam(value = "officeIds[]",required = false) List<String> officeIds,
            @RequestParam(value = "roleIds[]",required = false) List<String> roleIds) {
        userService.update(id, officeIds, password, mobile,
                roleIds,nickname,email);
        return R.ok();
    }

}
