package com.glface.modules.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.glface.base.utils.StringUtils;
import com.glface.common.exeception.ServiceException;
import com.glface.common.web.ApiCode;
import com.glface.model.SysArea;
import com.glface.model.SysOffice;
import com.glface.modules.mapper.AreaMapper;
import com.glface.modules.mapper.OfficeMapper;
import com.glface.modules.mapper.UserOfficeMapper;
import com.glface.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

@Slf4j
@Service
@Transactional(readOnly = true)
public class OfficeService {
    @Resource
    private OfficeMapper officeMapper;

    @Resource
    private UserOfficeMapper userOfficeMapper;

    @Resource
    private AreaMapper areaMapper;

    public List<SysOffice> findAll() {
        LambdaQueryWrapper<SysOffice> queryWrapper = Wrappers.<SysOffice>query().lambda()
                .eq(SysOffice::getDelFlag, 0);
        return officeMapper.selectList(queryWrapper);
    }

    public SysOffice get(String id) {
        return officeMapper.selectById(id);
    }

    /**
     * 新增部门
     * @param pid 上级部门
     * @param type 机构类型
     * @param sort 排序(从小到大)
     * @param name 名称
     */
    @Transactional
    public void create(String pid, String name,String type,String areaId,int sort) {
        if (StringUtils.isBlank(pid)) {
            pid = "0";
        }
        if (StringUtils.isBlank(name)) {
            throw new ServiceException(ApiCode.PERMISSION_OFFICE_NAME_REQUIRED);
        }

        if(StringUtils.isBlank(areaId)){
            throw new ServiceException(ApiCode.PERMISSION_OFFICE_AREAID_REQUIRED);
        }

        SysArea area = areaMapper.selectById(areaId);
        if (area == null) {
            throw new ServiceException(ApiCode.PERMISSION_AREA_NOT_EXIST);
        }

        // 查找上级部门
        SysOffice parent = officeMapper.selectById(pid);
        if (parent == null) {
            throw new ServiceException(ApiCode.PERMISSION_OFFICE_NOT_EXIST);
        }

        // 设置code值
        String code = parent.getCode();
        String maxCode = officeMapper.findMaxCodeById(parent.getId());
        if (maxCode == null) {
            code = code + "000";
        } else {
            String childCode = maxCode.substring(maxCode.length() - 3);
            int cCode = Integer.valueOf(childCode);
            cCode = cCode +1;
            if(cCode<=9){
                code = code +"00"+cCode;
            }else if(cCode<=99){
                code = code +"0"+cCode;
            }else{
                code = code + cCode;
            }
        }

        SysOffice newOffice = new SysOffice();
        newOffice.setCode(code);
        newOffice.setName(name);
        newOffice.setType(type);
        newOffice.setPid(pid);
        newOffice.setAreaId(areaId);
        newOffice.setSort(sort);

        UserUtils.preAdd(newOffice);
        officeMapper.insert(newOffice);

    }

    /**
     * 更新部门信息，如果更新上级部门，则必须修改code和所有子部门code
     *
     * @param id  部门id
     * @param name 名称
     * @param sort 排序
     * @param pid 如果为空则不修改
     */
    @Transactional
    public void update(String pid, String id, String name, String type,String areaId,int sort) {

        // 验证
        if (StringUtils.isBlank(id)) {
            throw new ServiceException(ApiCode.PERMISSION_OFFICE_ID_REQUIRED);
        }

        if (StringUtils.isBlank(name)) {
            throw new ServiceException(ApiCode.PERMISSION_OFFICE_NAME_REQUIRED);
        }
        if(StringUtils.isBlank(areaId)){
            throw new ServiceException(ApiCode.PERMISSION_OFFICE_AREAID_REQUIRED);
        }

        SysArea area = areaMapper.selectById(areaId);
        if (area == null) {
            throw new ServiceException(ApiCode.PERMISSION_AREA_NOT_EXIST);
        }

        SysOffice office = officeMapper.selectById(id);
        if (office == null) {
            throw new ServiceException(ApiCode.PERMISSION_OFFICE_NOT_EXIST);
        }

        // 是否修改上级部门
        boolean updateParent = false;
        String oldPid = office.getPid();
        if (StringUtils.isNotBlank(pid) && !pid.equals(oldPid)) {
            updateParent = true;
        }
        String oldCode = office.getCode();
        String code = office.getCode();
        if (updateParent) {
            SysOffice parent = officeMapper.selectById(pid);
            if (parent == null) {
                throw new ServiceException(ApiCode.PERMISSION_OFFICE_NOT_EXIST);
            }
            String maxCode = officeMapper.findMaxCodeByCode(parent.getCode());
            if (maxCode == null) {
                code = parent.getCode() + "000";
            } else {
                String childCode = maxCode.substring(maxCode.length() - 3);
                int cCode = Integer.valueOf(childCode);
                cCode = cCode +1;
                if(cCode<=9){
                    code = parent.getCode()+"00"+cCode;
                }else if(cCode<=99){
                    code = parent.getCode()+"0"+cCode;
                }else{
                    code = parent.getCode()+ cCode;
                }
            }
        }
        // 更新office信息
        office.setName(name);
        office.setType(type);
        office.setAreaId(areaId);
        office.setSort(sort);
        if (updateParent) {
            office.setCode(code);
            office.setPid(pid);
        }
        UserUtils.preUpdate(office);
        officeMapper.updateById(office);
        // 更新子部门office信息
        if (updateParent) {
            List<SysOffice> children = officeMapper.findAllChildrenByCode(oldCode);
            if (children != null) {
                for (SysOffice child : children) {
                    String childCode = child.getCode();
                    childCode = childCode.replaceFirst(oldCode, code);
                    child.setCode(childCode);
                }
                for (SysOffice child : children) {
                    UserUtils.preUpdate(child);
                    officeMapper.updateById(child);
                }
            }
        }

    }


    /**
     * 删除部门，并删除关联的用户部门，如果有未删除的子部门，则不允许删除
     */
    @Transactional
    public boolean delete(String id)
    {
        SysOffice office = officeMapper.selectById(id);
        if(office!=null){
            Set<SysOffice> childrens = officeMapper.childrens(office.getId());
            if(childrens.size()>0){
                throw new ServiceException(ApiCode.PERMISSION_OFFICE_CHILDREN_EXIST);
            }
            officeMapper.deleteById(office.getId());
            userOfficeMapper.delByOfficeId(id);
        }
        return true;
    }

    /**
     * 返回树形结构的office，最上面的节点为pid=0的节点
     */
    public List<SysOffice> allListTree(){
        List<SysOffice> officeList = Lists.newArrayList();
        LambdaQueryWrapper<SysOffice> queryWrapper = Wrappers.<SysOffice>query().lambda()
                .eq(SysOffice::getDelFlag, 0).orderByAsc(SysOffice::getSort);
        List<SysOffice>  all=officeMapper.selectList(queryWrapper);

        List<SysArea> areaList = areaMapper.selectList(Wrappers.<SysArea>query().lambda().eq(SysArea::getDelFlag, 0));
        Map<String,SysArea> areaMap = new HashMap<>();
        for(SysArea area:areaList){
            areaMap.put(area.getId(),area);
        }

        Map<String, SysOffice> menuMap = new HashMap<>();
        for(SysOffice office:all){
            menuMap.put(office.getId(), office);
            SysArea area = areaMap.get(office.getAreaId());
            if(area!=null){
                office.setAreaName(area.getName());
            }
        }
        for(SysOffice office:all){
            if("0".equals(office.getPid())){
                officeList.add(office);
            }else{
                SysOffice parent = menuMap.get(office.getPid());
                if(parent!=null){
                    parent.addChild(office);
                }
            }
        }
        return officeList;
    }

    /**
     * 返回树形结构的归口部门，最上面的节点为pid=0的节点
     */
    public List<SysOffice> gkbmTree(){
        List<SysOffice> officeList = Lists.newArrayList();
        LambdaQueryWrapper<SysOffice> queryWrapper = Wrappers.<SysOffice>query().lambda()
                .eq(SysOffice::getDelFlag, 0).orderByAsc(SysOffice::getSort);
        List<SysOffice>  all=officeMapper.selectList(queryWrapper);

        Map<String, SysOffice> menuMap = new HashMap<>();
        for(SysOffice menu:all){
            menuMap.put(menu.getId(), menu);
        }
        for(SysOffice office:all){
            if("0".equals(office.getPid())){
                officeList.add(office);
            }else{
                if("2".equals(office.getType())){
                    continue;
                }
                SysOffice parent = menuMap.get(office.getPid());
                if(parent!=null){
                    parent.addChild(office);
                }
            }
        }
        return officeList;
    }

    public List<SysOffice> qxkxTree(){
        List<SysOffice> officeList = Lists.newArrayList();
        LambdaQueryWrapper<SysOffice> queryWrapper = Wrappers.<SysOffice>query().lambda()
                .eq(SysOffice::getDelFlag, 0).orderByAsc(SysOffice::getSort);
        List<SysOffice>  all=officeMapper.selectList(queryWrapper);

        Map<String, SysOffice> menuMap = new HashMap<>();
        for(SysOffice menu:all){
            menuMap.put(menu.getId(), menu);
        }
        for(SysOffice office:all){
            if("0".equals(office.getPid())){
                officeList.add(office);
            }else{
                if("1".equals(office.getType())){
                    continue;
                }
                SysOffice parent = menuMap.get(office.getPid());
                if(parent!=null){
                    parent.addChild(office);
                }
            }
        }
        return officeList;
    }
    /**
     * 获取部门id层级数组
     */
    public List<String> findOneOffice(String officeId) {
        SysOffice office = get(officeId);
        List<String> officeIdList = new ArrayList<>();
        while (true&&office!=null) {
            officeIdList.add(office.getId());
            if (!"0".toString().equals(office.getPid())) {
                office = get(office.getPid());
            } else {
                break;
            }
        }

        // 反转数组元素
        Collections.reverse(officeIdList);
        return officeIdList;
    }

    /**
     * 获取部门id层级
     * @return
     */
    public List<SysOffice> findAllChildrenByCode(String code) {
        return officeMapper.findAllChildrenByCode(code);
    }


    /*
    * 根据项目id获取name
    * */
    public String findOfficeName(String id){
        return officeMapper.findOfficeName(id);
    }
}

