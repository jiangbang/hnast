package com.glface.modules.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.glface.base.bean.DynamicBean;
import com.glface.base.bean.R;
import com.glface.base.utils.Encodes;
import com.glface.base.utils.StringUtils;
import com.glface.base.utils.Valid;
import com.glface.common.ImageCode;
import com.glface.common.exeception.Exception401;
import com.glface.common.exeception.ServiceException;
import com.glface.common.exeception.ValidateCodeException;
import com.glface.common.web.ApiCode;
import com.glface.model.*;
import com.glface.modules.service.*;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.glface.common.web.ApiCode.*;

/**
 * 登录Controller
 */
@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/system/account")
public class AccountController {
    @Resource
    private UserService userService;
    @Resource
    private AccountService accountService;
    @Resource
    private MenuService menuService;
    @Resource
    private RoleService roleService;
    @Resource
    private UserRoleService userRoleService;
    /**
     * 登录
     */
    @RequestMapping(value = "/login")
    public R<Object> login(String account,
                           String password, String imageCode, String imageCodeHash, String imageCodeTamp,
                           HttpServletRequest request, HttpServletResponse response) {

        if(StringUtils.isBlank(imageCode)||StringUtils.isBlank(imageCodeHash)||StringUtils.isBlank(imageCodeTamp)){
            return R.fail(ApiCode.IMAGE_CODE_EMPTY.getMsg());
        }
        //校验验证码
        String sHash =  Encodes.md5(imageCode + "@" + imageCodeTamp + "@"  + imageCode.length(),null);//生成MD5值
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar c = Calendar.getInstance();
        String currentTime = sf.format(c.getTime());
        if (imageCodeTamp.compareTo(currentTime) > 0) {
            if (!sHash.equalsIgnoreCase(imageCodeHash)) {
                //验证码不正确，校验失败
                return R.fail(ApiCode.IMAGE_CODE_ERROR.getMsg());
            }
        } else {
            // 超时
            return R.fail(ApiCode.IMAGE_CODE_TIME_OUT.getMsg());
        }
        SysUser user = null;
        if (!Valid.isPasswords(password)){
            return R.fail(ACCOUNT_EDIT_PASSWORD.getMsg());
        }
        if(Valid.isAccount(account)){
            user = accountService.login(account, password, request, response);
        }else if(Valid.isPhone(account)){
            user = accountService.loginByMobile(account, password, request, response);
        }
//        if (user.getCheckAccount() >= 5) {
//            return R.fail(ApiCode.ACCOUNT_LOGIN_CHECK_ACCOUNT.getMsg());
//        }
        if (user == null) {
            return R.fail(ApiCode.ACCOUNT_LOGIN_FAILED.getMsg());
        }
        //菜单权限
        List<SysMenu> menuList;
        if (user.isAdmin()) {
            menuList = menuService.findAll();
        } else {
            menuList = userService.findMenusByUserId(user.getId());
        }

        List<Object> permissions = new DynamicBean.Builder()
                .setPV("permission", null)
                .build()
                .copyList(menuList);
        // 构造响应数据
        Object data = new DynamicBean.Builder()
                .setPV("token", user.getToken())
                .setPV("id", user.getId())
                .setPV("nickname", user.getNickname(), String.class)
                .setPV("access", permissions)
                .setPV("code", user.getOfficeCode())
                .setPV("isAdmin",user.isAdmin())
                .setPV("headimgurl",user.getHeadimgurl())
                .setPV("account", user.getAccount()).build().getObject();
        return R.ok(data);
    }

    @RequestMapping(value = "/reg")
    public R<Object> reg(String account, String password, String mobile,
                         String hash,String tamp,String code,String unionid) {
        if (StringUtils.isBlank(account)) {
            throw new ServiceException(ACCOUNT_LOGIN_ACCOUNT_REQUIRED);
        }
        if (!Valid.isAccount(account)) {
            throw new ServiceException(ACCOUNT_LOGIN_ACCOUNT_FORMAT.getMsg());
        }
        if (userService.findUserByAccount(account) != null) {
            throw new ServiceException(ACCOUNT_USER_ACCOUNT_EXIST);
        }

        if (StringUtils.isBlank(mobile)) {
            throw new ServiceException(ACCOUNT_USER_MOBILE_REQUIRED);
        } else if (!Valid.isPhone(mobile)) {
            throw new ServiceException(ACCOUNT_USER_MOBILE_FORMAT);
        } else {
            if (userService.findUserByMobile(mobile)!=null) {
                throw new ServiceException(ACCOUNT_USER_MOBILE_ONLY);
            }
        }
        if (StringUtils.isBlank(password)) {
            throw new ServiceException(ACCOUNT_USER_PASSWORD_REQUIRED);
        } else if(!Valid.isPassword(password)){
            throw new ServiceException(ACCOUNT_USER_PASSWORD_FORMAT);
        }
        //校验短信验证码
        String sHash =  Encodes.md5(mobile + "@" + tamp + "@" + code,null);//生成MD5值
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar c = Calendar.getInstance();
        String currentTime = sf.format(c.getTime());
        Date currentDate = new Date();
        if (tamp.compareTo(currentTime) > 0) {
            if (sHash.equalsIgnoreCase(hash)) {//验证码正确
                SysUser user = new SysUser();
                user.setLastLoginTime(currentDate);
                user.setLastVisitTime(currentDate);
                user.setAccount(account);
                user.setPassword(Encodes.md5(password, null));
                user.setMobile(mobile);
                user.setUnionid(unionid);
                user.setCheckAccount(0);
                userService.create(user);
                //设置默认角色
                SysRole nRole = roleService.findByName("普通用户");
                if(nRole!=null){
                    SysUserRole userRole = new SysUserRole();
                    userRole.setUserId(user.getId());
                    userRole.setRoleId(nRole.getId());
                    com.glface.modules.sys.utils.UserUtils.preAdd(userRole);
                    userRoleService.insert(userRole);
                }
                return R.ok(ACCOUNT_REG_SUCCESS.getMsg());

            } else {
                //验证码不正确，校验失败
                throw new ServiceException(CODE_ERROR);
            }
        } else {
            // 超时
            throw new ServiceException(CODE_TIME_OUT_ERROR);
        }

    }

    @RequestMapping(value = "/loginByCode")
    public R<Object> loginByCode(String mobile, String hash,String tamp,String code,HttpServletRequest request) {
        if (StringUtils.isBlank(mobile)) {
            throw new ServiceException(ACCOUNT_USER_MOBILE_REQUIRED);
        } else if (!Valid.isPhone(mobile)) {
            throw new ServiceException(ACCOUNT_USER_MOBILE_FORMAT);
        }
        //校验短信验证码
        String sHash =  Encodes.md5(mobile + "@" + tamp + "@" + code,null);//生成MD5值
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar c = Calendar.getInstance();
        String currentTime = sf.format(c.getTime());
        if (tamp.compareTo(currentTime) > 0) {
            if (sHash.equalsIgnoreCase(hash)) {//验证码正确
                SysUser user = accountService.login(mobile, request);
                if (user == null) {
                    return R.fail(ApiCode.ACCOUNT_LOGIN_FAILED.getMsg());
                }
                //菜单权限
                List<SysMenu> menuList;
                if (user.isAdmin()) {
                    menuList = menuService.findAll();
                } else {
                    menuList = userService.findMenusByUserId(user.getId());
                }

                List<Object> permissions = new DynamicBean.Builder()
                        .setPV("permission", null)
                        .build()
                        .copyList(menuList);
                // 构造响应数据
                Object data = new DynamicBean.Builder()
                        .setPV("token", user.getToken())
                        .setPV("id", user.getId())
                        .setPV("nickname", user.getNickname(), String.class)
                        .setPV("access", permissions)
                        .setPV("code", user.getOfficeCode())
                        .setPV("isAdmin",user.isAdmin())
                        .setPV("headimgurl",user.getHeadimgurl())
                        .setPV("account", user.getAccount()).build().getObject();
                return R.ok(data);

            } else {
                //验证码不正确，校验失败
                return R.fail(CODE_ERROR.getMsg());
            }
        }
        return R.fail(CODE_TIME_OUT_ERROR.getMsg());
    }


    @RequestMapping(value = "/resetPassword")
    public R<Object> resetPassword(String mobile,String password,String hash,String tamp,String code,HttpServletRequest request) {
        if (StringUtils.isBlank(mobile)) {
            throw new ServiceException(ACCOUNT_USER_MOBILE_REQUIRED);
        } else if (!Valid.isPhone(mobile)) {
            throw new ServiceException(ACCOUNT_USER_MOBILE_FORMAT);
        }
        if (StringUtils.isBlank(password)) {
            throw new ServiceException(ACCOUNT_USER_PASSWORD_REQUIRED);
        } else if (!Valid.isPasswords(password)) {
            throw new ServiceException(ACCOUNT_EDIT_PASSWORD);
        } else if(!Valid.isPassword(password)){
            throw new ServiceException(ACCOUNT_USER_PASSWORD_FORMAT);
        }
        SysUser user = userService.findUserByMobile(mobile);
        if(user == null){
            return R.fail(ApiCode.ACCOUNT_USER_NOTEXIST.getMsg());
        }
        //校验短信验证码
        String sHash =  Encodes.md5(mobile + "@" + tamp + "@" + code,null);//生成MD5值
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar c = Calendar.getInstance();
        String currentTime = sf.format(c.getTime());
        if (tamp.compareTo(currentTime) > 0) {
            if (sHash.equalsIgnoreCase(hash)) {//验证码正确
                //存储
                user.setPassword(Encodes.md5(password, null));
                userService.update(user);
                return R.ok(ACCOUNT_USER_RESET_PASSWORD_SUCCESS);
            } else {
                //验证码不正确，校验失败
                return R.fail(CODE_ERROR.getMsg());
            }
        }
        return R.fail(ACCOUNT_USER_RESET_PASSWORD_FAIL);
    }


    /**
     * 退出
     */
    @RequestMapping(value = "/logout")
    public R<Object> login(HttpServletRequest request, HttpServletResponse response) {
        accountService.logout(request, response);
        return R.ok();
    }

    /**
     * 编辑当前登录用户信息
     */
    @RequestMapping(value = "/editInfo")
    public R<Object> editInfo(String nickname,String email,String mobile) {
        SysUser user = accountService.editInfo(nickname, email, mobile);
        Object data = new DynamicBean.Builder()
                .setPV("nickname", user.getNickname(), String.class)
                .build().getObject();
        return R.ok(data);
    }

    /**
     * 修改密码
     */
    @RequestMapping(value = "/editPassword")
    public R<Object> editPassword(String password,String newPassword) {
        accountService.editPassword(password,newPassword);
        return R.ok();
    }

}
