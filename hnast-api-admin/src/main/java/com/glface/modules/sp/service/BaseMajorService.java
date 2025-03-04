package com.glface.modules.sp.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.glface.base.bean.Page;
import com.glface.base.utils.StringUtils;
import com.glface.common.exeception.ServiceException;
import com.glface.modules.sp.mapper.BaseMajorCategoryMapper;
import com.glface.modules.sp.mapper.BaseMajorMapper;
import com.glface.modules.sp.model.BaseMajor;
import com.glface.modules.sp.model.BaseMajorCategory;
import com.glface.modules.sys.utils.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

import static com.glface.common.web.ApiCode.*;

/**
 * 专业信息
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class BaseMajorService {
    @Resource
    private BaseMajorMapper majorMapper;

    @Resource
    private BaseMajorCategoryMapper majorCategoryMapper;

    public BaseMajor get(String id){
        return majorMapper.selectById(id);
    }

    public BaseMajorCategory getByMajorId(String majorId){
        BaseMajor major = get(majorId);
        if(major!=null&&StringUtils.isNotBlank(major.getMajorCategoryId())){
            BaseMajorCategory category = majorCategoryMapper.selectById(major.getMajorCategoryId());
            return category;
        }
        return null;
    }

    public List<BaseMajor> all(){
        LambdaQueryWrapper<BaseMajor> queryWrapper = Wrappers.<BaseMajor>query().lambda();
        queryWrapper.eq(BaseMajor::getDelFlag,BaseMajor.DEL_FLAG_NORMAL).orderByAsc(BaseMajor::getSort);
        return majorMapper.selectList(queryWrapper);
    }

    public List<BaseMajor> findByMajorCategoryId(String majorCategoryId){
        LambdaQueryWrapper<BaseMajor> queryWrapper = Wrappers.<BaseMajor>query().lambda();
        queryWrapper.eq(BaseMajor::getMajorCategoryId, majorCategoryId)
                .eq(BaseMajor::getDelFlag,BaseMajor.DEL_FLAG_NORMAL);
        return majorMapper.selectList(queryWrapper);
    }


    public Page<BaseMajor> pageSearch(Page<BaseMajor> page, BaseMajor bean) {
        page.setCount(majorMapper.pageSearchCount(bean));
        page.setList(majorMapper.pageSearch(page,bean));
        return page;
    }

    /**
     * 新增
     */
    @Transactional
    public void create(String name, String majorCategoryId,int sort, String remark) {
        name = StringUtils.trim(name);

        if (StringUtils.isBlank(name)) {
            throw new ServiceException(SP_MAJOR_NAME_REQUIRED);
        }

        if (StringUtils.isBlank(majorCategoryId)) {
            throw new ServiceException(SP_MAJOR_CATEGORY_REQUIRED);
        }
        BaseMajorCategory majorCategory = majorCategoryMapper.selectById(majorCategoryId);
        if(majorCategory==null){
            throw new ServiceException(SP_MAJOR_CATEGORY_NOTEXIST);
        }

        BaseMajor model = findByName(name);
        if(model!=null){
            throw new ServiceException(SP_MAJOR_NAME_EXIST);
        }

        // 创建
        model = new BaseMajor();
        model.setName(name);
        model.setMajorCategoryId(majorCategoryId);
        model.setSort(sort);
        model.setRemark(remark);
        UserUtils.preAdd(model);
        majorMapper.insert(model);
    }

    /**
     * 编辑
     */
    @Transactional
    public void update(String id, String name,String majorCategoryId,int sort,String remark) {
        name = StringUtils.trim(name);
        // 数据验证
        BaseMajor model = get(id);
        if (model == null) {
            throw new ServiceException(SP_MAJOR_NOTEXIST);
        }
        if (StringUtils.isBlank(name)) {
            throw new ServiceException(SP_MAJOR_NAME_REQUIRED);
        }

        if (StringUtils.isBlank(majorCategoryId)) {
            throw new ServiceException(SP_MAJOR_CATEGORY_REQUIRED);
        }
        BaseMajorCategory majorCategory = majorCategoryMapper.selectById(majorCategoryId);
        if(majorCategory==null){
            throw new ServiceException(SP_MAJOR_CATEGORY_NOTEXIST);
        }

        BaseMajor mode2 = findByName(name);
        if(mode2!=null&&!model.getId().equals(mode2.getId())){
            throw new ServiceException(SP_MAJOR_NAME_EXIST);
        }

        // 修改
        model.setName(name);
        model.setMajorCategoryId(majorCategoryId);
        model.setSort(sort);
        model.setRemark(remark);
        //存储
        UserUtils.preUpdate(model);
        majorMapper.updateById(model);
    }

    public BaseMajor findByName(String name){
        LambdaQueryWrapper<BaseMajor> queryWrapper = Wrappers.<BaseMajor>query().lambda()
                .eq(BaseMajor::getName, name)
                .eq(BaseMajor::getDelFlag,BaseMajor.DEL_FLAG_NORMAL);
        return majorMapper.selectOne(queryWrapper);
    }

    @Transactional
    public void delete(String id) {
        majorMapper.deleteById(id);
    }

 }
