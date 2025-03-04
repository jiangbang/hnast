package com.glface.modules.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.glface.base.bean.Page;
import com.glface.base.utils.Encodes;
import com.glface.base.utils.StringUtils;
import com.glface.base.utils.Valid;
import com.glface.common.exeception.ServiceException;
import com.glface.model.*;
import com.glface.modules.mapper.*;
import com.glface.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

import static com.glface.common.web.ApiCode.*;

@Slf4j
@Service
@Transactional(readOnly = true)
public class UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserRoleMapper userRoleMapper;

    @Resource
    private UserOfficeMapper userOfficeMapper;

    @Resource
    private OfficeMapper officeMapper;

    @Resource
    private MenuService menuService;

    @Resource
    private RoleMenuService roleMenuService;

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private UserRoleService userRoleService;

    /**
     * 新增用户
     *
     * @param account   账号
     * @param officeIds 归属部门
     * @param password  密码
     * @param mobile    手机号
     * @param roleIds   角色
     * @param nickname   昵称
     * @param email      邮箱
     */
    @Transactional
    public void create(String account, List<String> officeIds,
                       String password, String mobile, List<String> roleIds,String nickname,String email) {

//        // 数据验证
        if (StringUtils.isBlank(account)) {
            throw new ServiceException(PERMISSION_USER_ACCOUNT_REQUIRED);
        }
        if (!Valid.isAccount(account)) {
            throw new ServiceException(ACCOUNT_LOGIN_ACCOUNT_FORMAT.getMsg());
        }
        SysUser user = findUserByAccount(account);
        if (user != null) {
            throw new ServiceException(PERMISSION_USER_ACCOUNT_EXIST);
        }
//        if (StringUtils.isBlank(nickname)) {
//            throw new ServiceException(PERMISSION_USER_NICKNAME_REQUIRED);
//        } else if (nickname.length() > 20) {
//            throw new ServiceException(PERMISSION_USER_NICKNAME_LENGTH);        //长度为为 20 字符以内
//        }
//
//        if (StringUtils.isNotBlank(email) && !Valid.isMail(email)) {
//            throw new ServiceException(PERMISSION_USER_EMAIL_FORMAT);    //请输入正确的邮箱
//        }

        if (StringUtils.isBlank(mobile)) {
            throw new ServiceException(PERMISSION_USER_MOBILE_REQUIRED);
        } else if (!Valid.isPhone(mobile)) {
            throw new ServiceException(PERMISSION_USER_MOBILE_FORMAT);
        } else {
            SysUser u = findUserByMobile(mobile);
            if (u != null && mobile.equals(u.getMobile())) {
                throw new ServiceException(PERMISSION_USER_MOBILE_ONLY);
            }
        }

        if (StringUtils.isBlank(password)) {
            throw new ServiceException(PERMISSION_USER_PASSWORD_REQUIRED);
        } else if(!Valid.isPassword(password)){
            throw new ServiceException(PERMISSION_USER_PASSWORD_FORMAT);
        }

        //验证部门 、角色
        List<SysOffice> offices = new ArrayList<>(officeIds!=null?officeIds.size():0);
        if(officeIds!=null){
            for(String officeId:officeIds){
                SysOffice office = officeMapper.selectById(officeId);
                if(office!=null&&!office.isDeleted()){
                    offices.add(office);
                }
            }
        }

        List<SysRole> roles = new ArrayList<>(roleIds!=null?roleIds.size():0);
        if(roleIds!=null){
            for(String roleId:roleIds){
                SysRole role = roleMapper.selectById(roleId);
                if(role!=null&&!role.isDeleted()){
                    roles.add(role);
                }
            }
        }


        // 创建用户
        user = new SysUser();
        user.setAccount(account);
        if(StringUtils.isNotBlank(nickname)){
            user.setNickname(nickname);
        }
        // 设置登录信息
        Date now = new Date();
        user.setPassword(Encodes.md5(password, null));
        user.setLastLoginTime(now);
        user.setLoginCount(0);
        user.setCheckAccount(0);
        if(StringUtils.isNotBlank(email)){
            user.setNickname(email);
        }
        user.setMobile(mobile);
        UserUtils.preAdd(user);
        userMapper.insert(user);

        //创建用户部门
        List<SysUserOffice> userOffices = new ArrayList<>(offices.size());
        for(SysOffice office:offices){
            SysUserOffice userOffice = new SysUserOffice();
            userOffice.setUserId(user.getId());
            userOffice.setOfficeId(office.getId());
            userOffices.add(userOffice);
        }

        // 创建用户角色
        List<SysUserRole> userRoles = new ArrayList<>(roles.size());
        for(SysRole role:roles){
            SysUserRole userRole = new SysUserRole();
            userRole.setUserId(user.getId());
            userRole.setRoleId(role.getId());
            userRoles.add(userRole);
        }

        //存储
        for(SysUserOffice userOffice:userOffices){
            UserUtils.preAdd(userOffice);
            userOfficeMapper.insert(userOffice);
        }
        for(SysUserRole userRole:userRoles){
            UserUtils.preAdd(userRole);
            userRoleMapper.insert(userRole);
        }
    }

    @Transactional
    public void create(SysUser user) {
        UserUtils.preAdd(user);
        userMapper.insert(user);
    }
    /**
     * 编辑用户 同时修改用户角色信息.如果password为空，表示不修改密码
     * @param id        用户id
     * @param officeIds 归属部门
     * @param password  如果密码为空 则表示不修改
     * @param mobile    联系电话
     * @param roleIds   角色
     * @param nickname   昵称
     * @param email      邮箱
     */
    @Transactional
    public void update(String id, List<String> officeIds, String password, String mobile,
                       List<String> roleIds,     String nickname,
                       String email) {

        // 数据验证
        SysUser user = get(id);
        if (user == null) {
            throw new ServiceException(PERMISSION_USER_NOTEXIST);
        }
//        if (StringUtils.isBlank(nickname)) {
//            throw new ServiceException(PERMISSION_USER_NICKNAME_REQUIRED);
//        } else if (nickname.length() > 20) {
//            throw new ServiceException(PERMISSION_USER_NICKNAME_LENGTH);        //长度为为 20 字符以内
//        }
//
//        if (StringUtils.isNotBlank(email) && !Valid.isMail(email)) {
//            throw new ServiceException(PERMISSION_USER_EMAIL_FORMAT);    //请输入正确的邮箱
//        }

        if (StringUtils.isBlank(mobile)) {
            throw new ServiceException(PERMISSION_USER_MOBILE_REQUIRED);
        } else if (!Valid.isPhone(mobile)) {
            throw new ServiceException(PERMISSION_USER_MOBILE_FORMAT);
        } else {
            Set<SysUser> users = findByMobileIncludeDel(mobile);
            if(users!=null&&users.size()>1){
                throw new ServiceException(PERMISSION_USER_MOBILE_ONLY);
            }
            if(users!=null&&users.size()==1){
                SysUser u = (SysUser) users.toArray()[0];
                if (u != null && !user.getId().equals(u.getId())&&mobile.equals(u.getMobile())) {
                    throw new ServiceException(PERMISSION_USER_MOBILE_ONLY);
                }
            }

        }

        if (StringUtils.isNotBlank(password)&&!Valid.isPassword(password)) {
            throw new ServiceException(PERMISSION_USER_PASSWORD_FORMAT);
        }

        //验证部门 、角色
        List<SysOffice> offices = new ArrayList<>(officeIds!=null?officeIds.size():0);
        if(officeIds!=null){
            for(String officeId:officeIds){
                SysOffice office = officeMapper.selectById(officeId);
                if(office!=null&&!office.isDeleted()){
                    offices.add(office);
                }
            }
        }
        List<SysRole> roles = new ArrayList<>(roleIds!=null?roleIds.size():0);
        if(roleIds!=null){
            for(String roleId:roleIds){
                SysRole role = roleMapper.selectById(roleId);
                if(role!=null&&!role.isDeleted()){
                    roles.add(role);
                }
            }
        }

        // 修改用户信息
        user.setMobile(mobile);
        if (StringUtils.isNotBlank(password)) {
            user.setPassword(Encodes.md5(password, null));
        }
        if (StringUtils.isNotBlank(nickname)) {
            user.setNickname(nickname);
        }
        if (StringUtils.isNotBlank(email)) {
            user.setEmail(email);
        }
        //修改用户角色
        List<SysUserRole> addUserRoles = new ArrayList<>(roles.size());//需要新增的
        List<SysUserRole> delUserRoles = new ArrayList<>(roles.size());//需要删除的
        Set<SysUserRole> dbUserRoles = userRoleService.findUserRolesByUserId(user.getId());
        for (SysRole role:roles) {
            boolean has = false;
            for (SysUserRole dbUserRole: dbUserRoles) {
                if(role.getId().equals(dbUserRole.getRoleId())){
                    has = true;
                    break;
                }
            }
            if(!has){
                SysUserRole ur = new SysUserRole();
                ur.setUserId(user.getId());
                ur.setRoleId(role.getId());
                addUserRoles.add(ur);
            }
        }
        for (SysUserRole dbUserRole: dbUserRoles) {
            boolean has = false;
            for (SysRole role:roles) {
                if(role.getId().equals(dbUserRole.getRoleId())){
                    has = true;
                    break;
                }
            }
            if(!has){
                dbUserRole.setDelFlag(SysUserRole.DEL_FLAG_DELETE);
                delUserRoles.add(dbUserRole);
            }
        }
        //修改用户部门
        List<SysUserOffice> addUserOffices = new ArrayList<>(offices.size());//需要新增的
        List<SysUserOffice> delUserOffices = new ArrayList<>(offices.size());//需要删除的
        Set<SysUserOffice> dbUserOffices = userOfficeMapper.findUserOfficesByUserId(user.getId());
        for (SysOffice office:offices) {
            boolean has = false;
            for (SysUserOffice dbUserOffice: dbUserOffices) {
                if(office.getId().equals(dbUserOffice.getOfficeId())){
                    has = true;
                    break;
                }
            }
            if(!has){
                SysUserOffice uo = new SysUserOffice();
                uo.setUserId(user.getId());
                uo.setOfficeId(office.getId());
                addUserOffices.add(uo);
            }
        }
        for (SysUserOffice dbUserOffice: dbUserOffices) {
            boolean has = false;
            for (SysOffice office:offices) {
                if(office.getId().equals(dbUserOffice.getOfficeId())){
                    has = true;
                    break;
                }
            }
            if(!has){
                dbUserOffice.setDelFlag(SysUserOffice.DEL_FLAG_DELETE);
                delUserOffices.add(dbUserOffice);
            }
        }
        //存储
        UserUtils.preUpdate(user);
        userMapper.updateById(user);

        for (SysUserRole userRole: addUserRoles) {
            UserUtils.preAdd(userRole);
            userRoleMapper.insert(userRole);
        }
        for (SysUserRole userRole: delUserRoles) {
            UserUtils.preUpdate(userRole);
            userRoleMapper.updateById(userRole);//无法更新del_flag字段
            userRoleMapper.deleteById(userRole.getId());
        }

        for (SysUserOffice userOffice: addUserOffices) {
            UserUtils.preAdd(userOffice);
            userOfficeMapper.insert(userOffice);
        }
        for (SysUserOffice userOffice: delUserOffices) {
            UserUtils.preUpdate(userOffice);
            userOfficeMapper.updateById(userOffice);//无法更新del_flag字段
            userOfficeMapper.deleteById(userOffice.getId());
        }
    }

    /**
     * 删除用户，同时删除用户部门、用户角色
     */
    @Transactional
    public boolean delete(String id) {
        userMapper.deleteById(id);
        userRoleMapper.delByUserId(id);
        userOfficeMapper.delByUserId(id);
        return true;
    }

    /**
     * 更新用户信息
     */
    @Transactional
    public void update(SysUser user) {
        SysUser select = findUserByAccount(user.getAccount());
        if (null != select && !user.getId().equals(select.getId())) {
            throw new ServiceException(PERMISSION_USER_ACCOUNT_EXIST);
        }
        UserUtils.preUpdate(user);
        userMapper.updateById(user);
    }

    public SysUser get(String id) {
        return userMapper.selectById(id);
    }

    public SysUser findUserByAccount(String account) {
        LambdaQueryWrapper<SysUser> queryWrapper = Wrappers.<SysUser>query().lambda()
                .eq(SysUser::getAccount, account)
                .eq(SysUser::getDelFlag,SysUser.DEL_FLAG_NORMAL);
        return userMapper.selectOne(queryWrapper);
    }

    public SysUser findUserByMobile(String mobile){
        LambdaQueryWrapper<SysUser> queryWrapper = Wrappers.<SysUser>query().lambda()
                .eq(SysUser::getMobile, mobile)
                .eq(SysUser::getDelFlag,SysUser.DEL_FLAG_NORMAL);
        return userMapper.selectOne(queryWrapper);
    }

    public Set<SysUser> findByMobileIncludeDel(String mobile){
        return userMapper.findByMobileIncludeDel(mobile);
    }

    public SysUser findUserByToken(String token) {
        LambdaQueryWrapper<SysUser> queryWrapper = Wrappers.<SysUser>query().lambda()
                .eq(SysUser::getToken, token);
        return userMapper.selectOne(queryWrapper);
    }
    public SysUser findUserByUnionid(String Unionid){
        LambdaQueryWrapper<SysUser> queryWrapper = Wrappers.<SysUser>query().lambda()
                .eq(SysUser::getUnionid, Unionid)
                .eq(SysUser::getDelFlag,SysUser.DEL_FLAG_NORMAL);
        return userMapper.selectOne(queryWrapper);
    }

    /*
    * 插入用户名密码错误的次数
    * */
    @Transactional(noRollbackFor = ServiceException.class)
    public void updateCheckAccout(SysUser user){
        SysUser select = findUserByAccount(user.getAccount());
        if (null != select && !user.getId().equals(select.getId())) {
            throw new ServiceException(PERMISSION_USER_ACCOUNT_EXIST);
        }
        userMapper.updataCheckAccount(user.getLastLoginTime(),user.getLastVisitTime(),user.getCheckAccount(),user.getAccount());
    }

    /**
     * 查找用户菜单
     */
    public List<SysMenu> findMenusByUserId(String userId) {
        List<SysMenu> menuList = Lists.newArrayList();
        Set<SysUserRole> userRoleList = userRoleMapper.findUserRolesByUserId(userId);
        SysUser user = get(userId);
        if (user != null && user.isAdmin()) {
            menuList = menuService.findAll();
        } else {
            for (SysUserRole userRole : userRoleList) {
                String roleId = userRole.getRoleId();
                Set<SysMenu> menus = roleMenuService.findMenusByRoleId(roleId);
                for (SysMenu m : menus) {
                    boolean has = false;
                    for (SysMenu menu : menuList) {
                        if (m.getId().equals(menu.getId())) {
                            has = true;
                            break;
                        }
                    }
                    if (!has && 0 == m.getDelFlag()) {
                        menuList.add(m);
                    }
                }
            }
        }

        return menuList;
    }

    /**
     * 查找用户角色
     */
    public List<SysRole> findRolesByUserId(String userId) {
        List<SysRole> roleList = Lists.newArrayList();
        Set<SysUserRole> userRoleList = userRoleMapper.findUserRolesByUserId(userId);
        for (SysUserRole userRole : userRoleList) {
            String roleId = userRole.getRoleId();
            SysRole role = roleMapper.selectById(roleId);
            if (role != null && !role.isDeleted()) {
                roleList.add(role);
            }
        }
        return roleList;
    }

    /**
     * 查找用户角色
     */
    public List<SysUser> findUserByRoleId(String roleId) {
        List<SysUser> userList = Lists.newArrayList();
        Set<SysUserRole> userRoleList = userRoleMapper.findUserRolesByRoleId(roleId);
        for (SysUserRole userRole : userRoleList) {
            String userId = userRole.getUserId();
            SysUser user = userMapper.selectById(userId);
            if (user != null && !user.isDeleted()) {
                userList.add(user);
            }
        }
        //去重
        Map<String,SysUser> userMap = new HashMap<>();
        for(SysUser user:userList){
            userMap.put(user.getId(),user);
        }
        userList.clear();
        userList.addAll(userMap.values());
        return userList;
    }

    /**
     * 查找用户部门
     */
    public List<SysOffice> findOfficesByUserId(String userId) {
        List<SysOffice> officeList = Lists.newArrayList();
        Set<SysUserOffice> userOfficeList = userOfficeMapper.findUserOfficesByUserId(userId);
        for (SysUserOffice userOffice : userOfficeList) {
            String officeId = userOffice.getOfficeId();
            SysOffice office = officeMapper.selectById(officeId);
            if (office != null && 0 == office.getDelFlag()) {
                officeList.add(office);
            }
        }
        return officeList;
    }

    public Page<SysUser> pageSearch(Page<SysUser> page, SysUser user) {
        page.setCount(userMapper.pageSearchCount(user));
        page.setList(userMapper.pageSearch(page,user));
        return page;
    }

}

