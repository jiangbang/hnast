package com.glface.modules.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.glface.base.utils.StringUtils;
import com.glface.common.exeception.ServiceException;
import com.glface.common.web.ApiCode;
import com.glface.model.SysArea;
import com.glface.model.SysOffice;
import com.glface.modules.mapper.AreaMapper;
import com.glface.modules.mapper.UserOfficeMapper;
import com.glface.modules.model.SubjectContent;
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
public class AreaService {
    @Resource
    private AreaMapper areaMapper;


    public List<SysArea> findAll() {
        LambdaQueryWrapper<SysArea> queryWrapper = Wrappers.<SysArea>query().lambda()
                .eq(SysArea::getDelFlag, 0);
        return areaMapper.selectList(queryWrapper);
    }

    public SysArea get(String id) {
        return areaMapper.selectById(id);
    }

    /**
     * 新增区域
     * @param pid 上级区域
     * @param sort 排序(从小到大)
     * @param name 名称
     */
    @Transactional
    public void create(String pid, String name,String type,int sort) {
        if (StringUtils.isBlank(pid)) {
            pid = "0";
        }
        if (StringUtils.isBlank(name)) {
            throw new ServiceException(ApiCode.PERMISSION_AREA_NAME_REQUIRED);
        }
        if (StringUtils.isBlank(type)) {
            throw new ServiceException(ApiCode.PERMISSION_AREA_TYPE_REQUIRED);
        }
        // 查找上级区域
        SysArea parent = areaMapper.selectById(pid);
        if (parent == null) {
            throw new ServiceException(ApiCode.PERMISSION_AREA_NOT_EXIST);
        }

        // 设置code值
        String code = parent.getCode();
        String maxCode = areaMapper.findMaxCodeById(parent.getId());
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

        SysArea newArea = new SysArea();
        newArea.setCode(code);
        newArea.setName(name);
        newArea.setType(type);
        newArea.setPid(pid);
        newArea.setSort(sort);

        UserUtils.preAdd(newArea);
        areaMapper.insert(newArea);

    }

    /**
     * 更新区域信息，如果更新上级区域，则必须修改code和所有子区域code
     *
     * @param id  区域id
     * @param name 名称
     * @param sort 排序
     * @param pid 如果为空则不修改
     */
    @Transactional
    public void update(String pid, String id, String name,String type, int sort) {

        // 验证
        if (StringUtils.isBlank(id)) {
            throw new ServiceException(ApiCode.PERMISSION_AREA_ID_REQUIRED);
        }

        if (StringUtils.isBlank(name)) {
            throw new ServiceException(ApiCode.PERMISSION_AREA_NAME_REQUIRED);
        }

        if (StringUtils.isBlank(type)) {
            throw new ServiceException(ApiCode.PERMISSION_AREA_TYPE_REQUIRED);
        }

        SysArea office = areaMapper.selectById(id);
        if (office == null) {
            throw new ServiceException(ApiCode.PERMISSION_AREA_NOT_EXIST);
        }
        // 是否修改上级区域
        boolean updateParent = false;
        String oldPid = office.getPid();
        if (StringUtils.isNotBlank(pid) && !pid.equals(oldPid)) {
            updateParent = true;
        }
        String oldCode = office.getCode();
        String code = office.getCode();
        if (updateParent) {
            SysArea parent = areaMapper.selectById(pid);
            if (parent == null) {
                throw new ServiceException(ApiCode.PERMISSION_AREA_NOT_EXIST);
            }
            String maxCode = areaMapper.findMaxCodeByCode(parent.getCode());
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
        office.setSort(sort);
        if (updateParent) {
            office.setCode(code);
            office.setPid(pid);
        }
        UserUtils.preUpdate(office);
        areaMapper.updateById(office);
        // 更新子区域office信息
        if (updateParent) {
            List<SysArea> children = areaMapper.findAllChildrenByCode(oldCode);
            if (children != null) {
                for (SysArea child : children) {
                    String childCode = child.getCode();
                    childCode = childCode.replaceFirst(oldCode, code);
                    child.setCode(childCode);
                }
                for (SysArea child : children) {
                    UserUtils.preUpdate(child);
                    areaMapper.updateById(child);
                }
            }
        }

    }


    /**
     * 删除区域，并删除关联的用户区域，如果有未删除的子区域，则不允许删除
     */
    @Transactional
    public boolean delete(String id)
    {
        SysArea area = areaMapper.selectById(id);
        if(area!=null){
            Set<SysArea> childrens = areaMapper.childrens(area.getId());
            if(childrens.size()>0){
                throw new ServiceException(ApiCode.PERMISSION_AREA_CHILDREN_EXIST);
            }
            areaMapper.deleteById(area.getId());
        }
        return true;
    }

    /**
     * 返回树形结构的office，最上面的节点为pid=0的节点
     */
    public List<SysArea> allListTree(){
        List<SysArea> areaList = Lists.newArrayList();
        LambdaQueryWrapper<SysArea> queryWrapper = Wrappers.<SysArea>query().lambda()
                .eq(SysArea::getDelFlag, 0).orderByAsc(SysArea::getSort);
        List<SysArea>  all= areaMapper.selectList(queryWrapper);

        Map<String, SysArea> map = new HashMap<>();
        for(SysArea menu:all){
            map.put(menu.getId(), menu);
        }
        for(SysArea area:all){
            if("0".equals(area.getPid())){
                areaList.add(area);
            }else{
                SysArea parent = map.get(area.getPid());
                if(parent!=null){
                    parent.addChild(area);
                }
            }
        }
        return areaList;
    }
    /**
     * 获取区域id层级数组
     */
    public List<String> findOneOffice(String officeId) {
        SysArea office = get(officeId);
        List<String> officeIdList = new ArrayList<>();
        while (true) {
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
}

