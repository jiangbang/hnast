package com.glface.modules.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.glface.base.utils.Encodes;
import com.glface.base.utils.IdGen;
import com.glface.base.utils.StringUtils;
import com.glface.base.utils.Valid;
import com.glface.common.exeception.ServiceException;
import com.glface.common.utils.IpAddress;
import com.glface.common.web.ApiCode;
import com.glface.model.SysUser;
import com.glface.modules.mapper.UserMapper;
import com.glface.modules.sys.utils.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

import static com.glface.common.web.ApiCode.PERMISSION_USER_MOBILE_FORMAT;
import static com.glface.common.web.ApiCode.PERMISSION_USER_MOBILE_REQUIRED;

@Slf4j
@Service
@Transactional(readOnly = true)
public class AccountService {
    @Resource
    private UserService userService;

    /**
     * 登录
     * Transactional readOnly默认为false
     *
     * @return 返回登录用户
     */
    @Transactional(noRollbackFor = ServiceException.class)
    public SysUser login(String account, String password, HttpServletRequest request, HttpServletResponse response) {
        //参数验证
        if (StringUtils.isBlank(account)) {
            throw new ServiceException(ApiCode.ACCOUNT_LOGIN_ACCOUNT_REQUIRED.getMsg());
        }
        if (!Valid.isAccount(account)) {
            throw new ServiceException(ApiCode.ACCOUNT_LOGIN_ACCOUNT_FORMAT.getMsg());
        }
        if (StringUtils.isBlank(password)) {
            throw new ServiceException(ApiCode.ACCOUNT_LOGIN_PASSWORD_REQUIRED.getMsg());
        }
        //逻辑处理
        SysUser user = userService.findUserByAccount(account);

        // 判断最后一次登录时间和现在的时间是否相差五分钟
        Date lastLoginTime = user.getLastLoginTime();
        // 获取当前时间的Date对象表示
        Date now = new Date();
        long diffMinutes = (now.getTime() - lastLoginTime.getTime()) / (60 * 1000); // 计算时间差，单位为分钟
        //判断如果在五分钟之内且登录次数大于等于5的
//        if (diffMinutes <= 5 && user.getCheckAccount() >= 5) {
//            throw new ServiceException(ApiCode.ACCOUNT_LOGIN_CHECK_ACCOUNT);
//        } else {
//            // 判断只有当登录次数大于等于5的时候才会重新设置登录次数
//            if (user.getCheckAccount() >= 5) {
//                user.setCheckAccount(0);
//                user.setLastLoginTime(now);
//                user.setLastVisitTime(now);
//                userService.updateCheckAccout(user);
//            }
//        }
//        // 判断登录次数大于等于的时候
//        if (user.getCheckAccount() >= 5) {
//            throw new ServiceException(ApiCode.ACCOUNT_LOGIN_CHECK_ACCOUNT);
//        }
        if (user == null || !Encodes.md5(password, null).equals(user.getPassword())) {
            Integer checkAccount = null;
            if (user.getCheckAccount() == null) {
                checkAccount = 1;
                user.setCheckAccount(checkAccount);
                user.setLastLoginTime(now);
                user.setLastVisitTime(now);
            } else {
                checkAccount = user.getCheckAccount();
                user.setCheckAccount(checkAccount + 1);
                user.setLastLoginTime(now);
                user.setLastVisitTime(now);
            }
            userService.updateCheckAccout(user);
            throw new ServiceException(ApiCode.ACCOUNT_LOGIN_FAILED);
        }
        // 设置登录信息
        String token = IdGen.uuid();
        user.setToken(token);
        user.setLastLoginTime(now);
        user.setLastLoginIp(IpAddress.getIpAddress(request));
        Integer loginCount = user.getLoginCount();
        if (null == loginCount) {
            loginCount = 0;
        }
        user.setLoginCount(loginCount + 1);
        user.setLastVisitTime(now);
        userService.update(user);
        return user;
    }

    /**
     * 登录
     * Transactional readOnly默认为false
     *
     * @return 返回登录用户
     */
    @Transactional
    public SysUser loginByMobile(String mobile, String password, HttpServletRequest request, HttpServletResponse response) {
        //参数验证
        if (StringUtils.isBlank(mobile)) {
            throw new ServiceException(PERMISSION_USER_MOBILE_REQUIRED);
        } else if (!Valid.isPhone(mobile)) {
            throw new ServiceException(PERMISSION_USER_MOBILE_FORMAT);
        }
        if (StringUtils.isBlank(password)) {
            throw new ServiceException(ApiCode.ACCOUNT_LOGIN_PASSWORD_REQUIRED.getMsg());
        }

        //逻辑处理
        SysUser user = userService.findUserByMobile(mobile);

        if (user == null || !Encodes.md5(password, null).equals(user.getPassword())) {
            throw new ServiceException(ApiCode.ACCOUNT_LOGIN_FAILED);
        }

        // 设置登录信息
        Date now = new Date();
        String token = IdGen.uuid();
        user.setToken(token);
        user.setLastLoginTime(now);
        user.setLastLoginIp(IpAddress.getIpAddress(request));
        Integer loginCount = user.getLoginCount();
        if (null == loginCount) {
            loginCount = 0;
        }
        user.setLoginCount(loginCount + 1);
        user.setLastVisitTime(now);
        userService.update(user);
        return user;
    }


    @Transactional
    public SysUser loginByUnionid(String unionid,HttpServletRequest request) {
        //逻辑处理
        SysUser user = userService.findUserByUnionid(unionid);
        if (user == null) {
            return null;
        }
        // 设置登录信息
        Date now = new Date();
        String token = IdGen.uuid();
        user.setToken(token);
        user.setLastLoginTime(now);
        user.setLastLoginIp(IpAddress.getIpAddress(request));
        Integer loginCount = user.getLoginCount();
        if (null == loginCount) {
            loginCount = 0;
        }
        user.setLoginCount(loginCount + 1);
        user.setLastVisitTime(now);
        userService.update(user);
        return user;
    }
    /**
     * 登录 通过手机号
     * Transactional readOnly默认为false
     *
     * @return 返回登录用户
     */
    @Transactional
    public SysUser login(String mobile, HttpServletRequest request) {
        //参数验证
        if (StringUtils.isBlank(mobile)) {
            throw new ServiceException(PERMISSION_USER_MOBILE_REQUIRED);
        } else if (!Valid.isPhone(mobile)) {
            throw new ServiceException(PERMISSION_USER_MOBILE_FORMAT);
        }

        //逻辑处理
        SysUser user = userService.findUserByMobile(mobile);
        if (user == null) {
            throw new ServiceException(ApiCode.ACCOUNT_LOGIN_FAILED);
        }

        // 设置登录信息
        Date now = new Date();
        String token = IdGen.uuid();
        user.setToken(token);
        user.setLastLoginTime(now);
        user.setLastLoginIp(IpAddress.getIpAddress(request));
        Integer loginCount = user.getLoginCount();
        if (null == loginCount) {
            loginCount = 0;
        }
        user.setLoginCount(loginCount + 1);
        user.setLastVisitTime(now);
        userService.update(user);
        return user;
    }

    /**
     * 退出 清空token 清空当前的`SecurityContext`
     */
    @Transactional
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        SysUser user = UserUtils.getUser();
        if (user == null) {
            return;
        }
        user.setToken(null);
        userService.update(user);

        SecurityContext context = SecurityContextHolder.getContext();
        //清空当前的`SecurityContext`
        context.setAuthentication(null);
        SecurityContextHolder.clearContext();
    }

    /**
     * 编辑当前登录用户信息
     */
    @Transactional
    public SysUser editInfo(String nickname, String email, String mobile) {
        SysUser user = UserUtils.getUser();
        if (user == null) {
            throw new ServiceException(ApiCode.ACCOUNT_NOT_LOGIN);
        }
        if (StringUtils.isBlank(nickname)) {
            throw new ServiceException(ApiCode.PERMISSION_USER_NICKNAME_REQUIRED);
        } else if (nickname.length() > 20) {
            throw new ServiceException(ApiCode.PERMISSION_USER_NICKNAME_LENGTH);        //长度为为 20 字符以内
        }

        if (StringUtils.isNotBlank(email) && !Valid.isMail(email)) {
            throw new ServiceException(ApiCode.PERMISSION_USER_EMAIL_FORMAT);    //请输入正确的邮箱
        }

        if (StringUtils.isBlank(mobile)) {
            throw new ServiceException(ApiCode.PERMISSION_USER_MOBILE_REQUIRED);
        } else if (!Valid.isPhone(mobile)) {
            throw new ServiceException(ApiCode.PERMISSION_USER_MOBILE_FORMAT);
        } else {
            SysUser u = userService.findUserByMobile(mobile);
            if (u != null && !user.getId().equals(u.getId()) && mobile.equals(u.getMobile())) {
                throw new ServiceException(ApiCode.PERMISSION_USER_MOBILE_ONLY);
            }
        }

        user.setNickname(nickname);
        user.setMobile(mobile);
        user.setEmail(email);
        userService.update(user);

        return user;
    }

    /**
     * 修改密码
     *
     * @param password    原密码
     * @param newPassword 新密码
     */
    @Transactional
    public void editPassword(String password, String newPassword) {

        if (StringUtils.isBlank(newPassword)) {
            throw new ServiceException(ApiCode.ACCOUNT_EDIT_NEWPASSWORD_REQUIRED);
        }
        SysUser user = UserUtils.getUser();
        if (user == null) {
            throw new ServiceException(ApiCode.ACCOUNT_NOT_LOGIN);
        }

        if (StringUtils.isBlank(password)) {
            throw new ServiceException(ApiCode.ACCOUNT_EDIT_PASSWORD_REQUIRED);
        } else if (password.length() > 16) {
            throw new ServiceException(ApiCode.ACCOUNT_EDIT_PASSWORD_LENGTH);
        } else if (!(user.getPassword()).equals(Encodes.md5(password, null))) {
            throw new ServiceException(ApiCode.ACCOUNT_EDIT_PASSWORD_VERIFY);
        }
        user.setPassword(Encodes.md5(newPassword, null));
        userService.update(user);
    }
}

