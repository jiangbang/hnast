package com.glface.modules.sp.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.glface.base.bean.Page;
import com.glface.base.utils.StringUtils;
import com.glface.common.exeception.ServiceException;
import com.glface.model.SysOffice;
import com.glface.modules.mapper.OfficeMapper;
import com.glface.modules.sp.mapper.BaseCategoryMapper;
import com.glface.modules.sp.model.BaseCategory;
import com.glface.modules.sys.utils.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

import static com.glface.common.web.ApiCode.*;

/**
 * 专家库类别信息
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class BaseCategoryService {
    @Resource
    private BaseCategoryMapper categoryMapper;

    @Resource
    private OfficeMapper officeMapper;


    public BaseCategory get(String id){
        return categoryMapper.selectById(id);
    }

    public List<BaseCategory> all(){
        LambdaQueryWrapper<BaseCategory> queryWrapper = Wrappers.<BaseCategory>query().lambda();
        queryWrapper.eq(BaseCategory::getDelFlag,BaseCategory.DEL_FLAG_NORMAL).orderByAsc(BaseCategory::getSort);
        return categoryMapper.selectList(queryWrapper);
    }


    public Page<BaseCategory> pageSearch(Page<BaseCategory> page, BaseCategory bean) {
        page.setCount(categoryMapper.pageSearchCount(bean));
        page.setList(categoryMapper.pageSearch(page,bean));
        return page;
    }

    /**
     * 新增
     */
    @Transactional
    public void create(String name, String officeId, int sort, String remark) {
        name = StringUtils.trim(name);

        if (StringUtils.isBlank(name)) {
            throw new ServiceException(SP_CATEGORY_NAME_REQUIRED);
        }
        if (StringUtils.isBlank(officeId)) {
            throw new ServiceException(SP_CATEGORY_OFFICEID_REQUIRED);
        }

        SysOffice office = officeMapper.selectById(officeId);
        if(office==null){
            throw new ServiceException(SP_CATEGORY_OFFICE_NOTEXIST);
        }

        BaseCategory model = findByName(name);
        if(model!=null){
            throw new ServiceException(SP_CATEGORY_NAME_EXIST);
        }

        // 创建
        model = new BaseCategory();
        model.setName(name);
        model.setOfficeId(officeId);
        model.setSort(sort);
        model.setRemark(remark);
        UserUtils.preAdd(model);
        categoryMapper.insert(model);
    }

    /**
     * 编辑
     */
    @Transactional
    public void update(String id, String name, String officeId, int sort,String remark) {
        name = StringUtils.trim(name);
        // 数据验证
        BaseCategory model = get(id);
        if (model == null) {
            throw new ServiceException(SP_CATEGORY_NOTEXIST);
        }
        if (StringUtils.isBlank(name)) {
            throw new ServiceException(SP_CATEGORY_NAME_REQUIRED);
        }
        if (StringUtils.isBlank(officeId)) {
            throw new ServiceException(SP_CATEGORY_OFFICEID_REQUIRED);
        }

        SysOffice office = officeMapper.selectById(officeId);
        if(office==null){
            throw new ServiceException(SP_CATEGORY_OFFICE_NOTEXIST);
        }

        BaseCategory mode2 = findByName(name);
        if(mode2!=null&&!model.getId().equals(mode2.getId())){
            throw new ServiceException(SP_CATEGORY_NAME_EXIST);
        }

        // 修改
        model.setName(name);
        model.setOfficeId(officeId);
        model.setSort(sort);
        model.setRemark(remark);
        //存储
        UserUtils.preUpdate(model);
        categoryMapper.updateById(model);
    }

    public BaseCategory findByName(String name){
        LambdaQueryWrapper<BaseCategory> queryWrapper = Wrappers.<BaseCategory>query().lambda()
                .eq(BaseCategory::getName, name)
                .eq(BaseCategory::getDelFlag,BaseCategory.DEL_FLAG_NORMAL);
        return categoryMapper.selectOne(queryWrapper);
    }

    @Transactional
    public void delete(String id) {
        categoryMapper.deleteById(id);
    }

 }
