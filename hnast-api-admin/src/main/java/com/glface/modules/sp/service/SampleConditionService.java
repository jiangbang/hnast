package com.glface.modules.sp.service;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.glface.base.dto.SampleConditionDto;
import com.glface.base.utils.IdGen;
import com.glface.common.exeception.ServiceException;
import com.glface.common.web.ApiCode;
import com.glface.modules.sp.mapper.BaseMajorCategoryMapper;
import com.glface.modules.sp.mapper.ExpertConditionMapper;
import com.glface.modules.sp.mapper.SampleConditionMapper;
import com.glface.modules.sp.model.BaseCategory;
import com.glface.modules.sp.model.BaseMajorCategory;
import com.glface.modules.sp.model.ExpertCondition;
import com.glface.modules.sp.model.SampleCondition;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.glface.modules.sys.utils.UserUtils;

import javax.annotation.Resource;
import java.util.*;

/*
 * 专家抽取
 * */
@Slf4j
@Service
@Transactional(readOnly = true)
public class SampleConditionService {

    @Resource
    private SampleConditionMapper sampleConditionMapper;
    @Resource
    private ExpertConditionMapper expertConditionMapper;
    @Resource
    private BaseMajorCategoryMapper baseMajorCategoryMapper;


    // 插入
    @Transactional
    public SampleCondition saveCondition(SampleCondition sampleCondition) {
        sampleCondition.setId(IdGen.uuid());
        UserUtils.preAdd(sampleCondition);
        sampleConditionMapper.insert(sampleCondition);
        return sampleCondition;
    }

    //插入
    @Transactional
    public void updateCondition(String id, SampleCondition sampleCondition) {
        UserUtils.preUpdate(sampleCondition);
        sampleConditionMapper.updateById(sampleCondition);
    }

    // 查询所有条件
    @Transactional
    public List<SampleCondition> findAll(String sampleId, String name) {
        return sampleConditionMapper.findAll(sampleId, name);
    }

    /*
     * 根据类别id查询类别名字
     * */

    @Transactional
    public String findCategoryName(String id) {
        return sampleConditionMapper.findCategoryName(id);
    }

    /*
     * 根据行业类别id查询名称
     * */
    public String findMajorName(String id) {
        return sampleConditionMapper.findMajorName(id);
    }


    /*
     * 查询所有专家名称
     * */
    @Transactional
    public List<String> getExpertName() {
        Set<String> experts = sampleConditionMapper.getExpertName();
        List<String> expertNameList = new ArrayList<>();
        expertNameList.addAll(experts);
        //排序
        Collections.sort(expertNameList, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
        return expertNameList;
    }


    /*
     * 查询行业类别
     * */
    @Transactional
    public List<BaseMajorCategory> getmajorName() {
        LambdaQueryWrapper<BaseMajorCategory> queryWrapper = Wrappers.<BaseMajorCategory>query().lambda();
        queryWrapper.eq(BaseMajorCategory::getDelFlag, BaseCategory.DEL_FLAG_NORMAL);
        return baseMajorCategoryMapper.selectList(queryWrapper);
    }


    /*
     * 根据id删除条件
     * */
    @Transactional
    public void delete(String id) {
        sampleConditionMapper.deleteById(id);
    }


    /*
     * 根据条件抽取专家
     * */
    @Transactional
    public List<SampleConditionDto> getExpertList(String categoryId,
                                                  List<String> expertId,
                                                  String majorId,
                                                  List<String> names,
                                                  List<String> orgNames,
                                                  String star,
                                                  Integer num,
                                                  String exProject) {
        return sampleConditionMapper.getExpertList(categoryId,expertId, majorId, names, orgNames, star, num, exProject);
    }

    /*
     * 每次抽取一个五星专家
     * */
    @Transactional
    public List<SampleConditionDto> getExpertOne(String categoryId,
                                                 List<String> expertId,
                                                 String majorId,
                                                 List<String> names,
                                                 List<String> orgNames,
                                                 String exProject) {
        return sampleConditionMapper.getExpertOne(categoryId,expertId, majorId, names, orgNames, exProject);
    }

    /*
     * 根据专指定家名称查询信息
     * */
    @Transactional
    public List<SampleConditionDto> findExpertOne(String name) {
        return sampleConditionMapper.findExpertOne(name);
    }


    /**
     * 根据name和expertNames批量更新is_item为0
     *
     * @return 返回更新的记录数
     */
    @Transactional
    public int updateExpertConditionIsItemByExpertNames(String name, List<String> expertNames) {
        return sampleConditionMapper.updateExpertConditionIsItemByExpertNames(name, expertNames);
    }

    @Transactional
    public int updateExpertConditionNotIsItemByExpertNames(String name, List<String> expertNames) {
        return sampleConditionMapper.updateExpertConditionNotIsItemByExpertNames(name, expertNames);
    }

    /*
     * 查询del_flag=1的专家抽取，来设置抽取轮次
     * */
    @Transactional
    public ExpertCondition selectExpertCondition(String name, String sampleId, String expertName) {
        return sampleConditionMapper.selectExpertCondition(name, sampleId, expertName);
    }

    /**
     * 查询id
     * */

    @Transactional
    public List<String> queryAllExpertId(String id){
        return sampleConditionMapper.queryAllExpertId(id);
    }

}
