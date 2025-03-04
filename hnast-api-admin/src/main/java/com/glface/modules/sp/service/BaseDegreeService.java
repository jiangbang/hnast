package com.glface.modules.sp.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.glface.base.bean.Page;
import com.glface.base.utils.StringUtils;
import com.glface.common.exeception.ServiceException;
import com.glface.modules.sp.mapper.BaseDegreeMapper;
import com.glface.modules.sp.model.BaseDegree;
import com.glface.modules.sys.utils.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

import static com.glface.common.web.ApiCode.*;

/**
 * 学位信息
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class BaseDegreeService {
    @Resource
    private BaseDegreeMapper degreeMapper;

    public BaseDegree get(String id){
        return degreeMapper.selectById(id);
    }

    public List<BaseDegree> all(){
        LambdaQueryWrapper<BaseDegree> queryWrapper = Wrappers.<BaseDegree>query().lambda();
        queryWrapper.eq(BaseDegree::getDelFlag,BaseDegree.DEL_FLAG_NORMAL).orderByAsc(BaseDegree::getSort);
        return degreeMapper.selectList(queryWrapper);
    }


    public Page<BaseDegree> pageSearch(Page<BaseDegree> page, BaseDegree bean) {
        page.setCount(degreeMapper.pageSearchCount(bean));
        page.setList(degreeMapper.pageSearch(page,bean));
        return page;
    }

    /**
     * 新增
     */
    @Transactional
    public void create(String name, int sort, String remark) {
        name = StringUtils.trim(name);

        if (StringUtils.isBlank(name)) {
            throw new ServiceException(SP_DEGREE_NAME_REQUIRED);
        }

        BaseDegree model = findByName(name);
        if(model!=null){
            throw new ServiceException(SP_DEGREE_NAME_EXIST);
        }

        // 创建
        model = new BaseDegree();
        model.setName(name);
        model.setSort(sort);
        model.setRemark(remark);
        UserUtils.preAdd(model);
        degreeMapper.insert(model);
    }

    /**
     * 编辑
     */
    @Transactional
    public void update(String id, String name, int sort,String remark) {
        name = StringUtils.trim(name);
        // 数据验证
        BaseDegree model = get(id);
        if (model == null) {
            throw new ServiceException(SP_DEGREE_NOTEXIST);
        }
        if (StringUtils.isBlank(name)) {
            throw new ServiceException(SP_DEGREE_NAME_REQUIRED);
        }

        BaseDegree mode2 = findByName(name);
        if(mode2!=null&&!model.getId().equals(mode2.getId())){
            throw new ServiceException(SP_DEGREE_NAME_EXIST);
        }

        // 修改
        model.setName(name);
        model.setSort(sort);
        model.setRemark(remark);
        //存储
        UserUtils.preUpdate(model);
        degreeMapper.updateById(model);
    }

    public BaseDegree findByName(String name){
        LambdaQueryWrapper<BaseDegree> queryWrapper = Wrappers.<BaseDegree>query().lambda()
                .eq(BaseDegree::getName, name)
                .eq(BaseDegree::getDelFlag,BaseDegree.DEL_FLAG_NORMAL);
        return degreeMapper.selectOne(queryWrapper);
    }

    @Transactional
    public void delete(String id) {
        degreeMapper.deleteById(id);
    }

 }
