package com.glface.modules.sp.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.glface.base.bean.Page;
import com.glface.base.utils.StringUtils;
import com.glface.common.exeception.ServiceException;
import com.glface.modules.sp.mapper.BaseMajorCategoryMapper;
import com.glface.modules.sp.mapper.BaseMajorMapper;
import com.glface.modules.sp.model.BaseMajorCategory;
import com.glface.modules.sys.utils.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

import static com.glface.common.web.ApiCode.*;

/**
 * 行业(专业分类)
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class BaseMajorCategoryService {
    @Resource
    private BaseMajorCategoryMapper majorCategoryMapperMapper;
    @Resource
    private BaseMajorMapper majorMapper;

    public BaseMajorCategory get(String id){
        return majorCategoryMapperMapper.selectById(id);
    }

    public List<BaseMajorCategory> all(){
        LambdaQueryWrapper<BaseMajorCategory> queryWrapper = Wrappers.<BaseMajorCategory>query().lambda();
        queryWrapper.eq(BaseMajorCategory::getDelFlag,BaseMajorCategory.DEL_FLAG_NORMAL).orderByAsc(BaseMajorCategory::getSort);
        return majorCategoryMapperMapper.selectList(queryWrapper);
    }


    public Page<BaseMajorCategory> pageSearch(Page<BaseMajorCategory> page, BaseMajorCategory bean) {
        page.setCount(majorCategoryMapperMapper.pageSearchCount(bean));
        page.setList(majorCategoryMapperMapper.pageSearch(page,bean));
        return page;
    }

    /**
     * 新增
     */
    @Transactional
    public void create(String name, int sort, String remark) {
        name = StringUtils.trim(name);

        if (StringUtils.isBlank(name)) {
            throw new ServiceException(SP_MAJOR_CATEGORY_NAME_REQUIRED);
        }

        BaseMajorCategory model = findByName(name);
        if(model!=null){
            throw new ServiceException(SP_MAJOR_CATEGORY_NAME_EXIST);
        }

        // 创建
        model = new BaseMajorCategory();
        model.setName(name);
        model.setSort(sort);
        model.setRemark(remark);
        UserUtils.preAdd(model);
        majorCategoryMapperMapper.insert(model);
    }

    /**
     * 编辑
     */
    @Transactional
    public void update(String id, String name,int sort,String remark) {
        name = StringUtils.trim(name);
        // 数据验证
        BaseMajorCategory model = get(id);
        if (model == null) {
            throw new ServiceException(SP_MAJOR_CATEGORY_NOTEXIST);
        }
        if (StringUtils.isBlank(name)) {
            throw new ServiceException(SP_MAJOR_CATEGORY_NAME_REQUIRED);
        }

        BaseMajorCategory mode2 = findByName(name);
        if(mode2!=null&&!model.getId().equals(mode2.getId())){
            throw new ServiceException(SP_MAJOR_CATEGORY_NAME_EXIST);
        }

        // 修改
        model.setName(name);
        model.setSort(sort);
        model.setRemark(remark);
        //存储
        UserUtils.preUpdate(model);
        majorCategoryMapperMapper.updateById(model);
    }

    public BaseMajorCategory findByName(String name){
        LambdaQueryWrapper<BaseMajorCategory> queryWrapper = Wrappers.<BaseMajorCategory>query().lambda()
                .eq(BaseMajorCategory::getName, name)
                .eq(BaseMajorCategory::getDelFlag,BaseMajorCategory.DEL_FLAG_NORMAL);
        return majorCategoryMapperMapper.selectOne(queryWrapper);
    }

    @Transactional
    public void delete(String id) {
        majorCategoryMapperMapper.deleteById(id);
        majorMapper.delByMajorCategoryId(id);
    }

 }
