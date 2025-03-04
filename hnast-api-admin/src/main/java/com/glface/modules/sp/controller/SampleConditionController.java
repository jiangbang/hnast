package com.glface.modules.sp.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.glface.base.bean.DynamicBean;
import com.glface.base.bean.R;
import com.glface.base.dto.SampleConditionDto;
import com.glface.base.utils.DateUtils;
import com.glface.base.utils.IdGen;
import com.glface.common.utils.SpringContextUtil;
import com.glface.modules.sp.mapper.*;
import com.glface.modules.sp.model.*;
import com.glface.modules.sp.service.ExpertConditionService;
import com.glface.modules.sp.service.SampleConditionService;
import com.glface.modules.sp.service.SampleNameService;
import com.glface.modules.sp.service.SampleService;
import lombok.extern.slf4j.Slf4j;
import net.sf.jxls.transformer.XLSTransformer;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.file.Paths;
import java.util.*;

/*
 * 专家抽取
 * */
@Slf4j
@RestController
@RequestMapping("/specialist/sampleCondition")
public class SampleConditionController {

    @Resource
    private SampleConditionService sampleConditionService;
    @Resource
    private SampleConditionMapper sampleConditionMapper;
    @Resource
    private ExpertConditionMapper expertConditionMapper;
    @Resource
    private ExpertConditionService expertConditionService;
    @Resource
    private SampleMapper sampleMapper;
    @Resource
    private SampleService sampleService;
    @Resource
    private BaseMajorCategoryMapper baseMajorCategoryMapper;
    @Resource
    private SampleNameMapper sampleNameMapper;
    @Resource
    private SampleNameService sampleNameService;

    SampleCondition ids = null;
    String names = "";

    //添加抽取组别名称
    @RequestMapping("/createSampleName")
    public R<Object> createSampleName(SampleCondition sampleCondition) {
        SampleName sampleName = new SampleName();
        sampleName.setSampleId(sampleCondition.getSampleId());
        sampleName.setName(sampleCondition.getName());
        QueryWrapper<SampleName> selectSampleName = new QueryWrapper<SampleName>()
                .eq("sample_id", sampleCondition.getSampleId())
                .eq("name", sampleCondition.getName());
        QueryWrapper<SampleCondition> selectNum = new QueryWrapper<SampleCondition>()
                .eq("name", sampleCondition.getName())
                .eq("sample_id", sampleCondition.getSampleId())
                .eq("base_category_id", sampleCondition.getBaseCategoryId());
        if (sampleNameMapper.selectCount(selectSampleName) > 0 || sampleConditionMapper.selectCount(selectNum) > 0) {
            return R.fail("已存在");
        }
        sampleNameService.createSampleName(sampleName);
        sampleConditionService.saveCondition(sampleCondition);
        return R.ok();
    }

    // 删除组别名称及关联数据
    // name:组别名称 sampleId：抽取id conditionId：条件id
    @RequestMapping("/delSampleName")
    public R<Object> delSampleName(String name, String sampleId, String conditionId) {
        // 查询删除的是否是最后一个组别,如果是的则将抽取状态更新为未抽取
        /*QueryWrapper<SampleName> sampleNameQueryWrapper = new QueryWrapper<>();
        sampleNameQueryWrapper.eq("sample_id",sampleId);
        List<SampleName> sampleNameCount = sampleNameMapper.selectList(sampleNameQueryWrapper);
        if (sampleNameCount.size() < 1) {
            //更新已抽取状态
            UpdateWrapper<Sample> sampleUpdateWrapper = new UpdateWrapper<>();
            sampleUpdateWrapper.eq("id",sampleId);
            sampleUpdateWrapper.set("status",0);
            sampleMapper.update(null,sampleUpdateWrapper);
        }*/
        //删除组别名称
        UpdateWrapper<SampleName> sampleNameUpdateWrapper = new UpdateWrapper<>();
        sampleNameUpdateWrapper.eq("name", name);
        sampleNameUpdateWrapper.eq("sample_id", sampleId);
        sampleNameUpdateWrapper.set("del_flag", 1);
        //删除组别名称对应条件
        UpdateWrapper<SampleCondition> sampleConditionUpdateWrapper = new UpdateWrapper<>();
        sampleConditionUpdateWrapper.eq("name", name);
        sampleConditionUpdateWrapper.eq("sample_id", sampleId);
        sampleConditionUpdateWrapper.set("del_flag", 1);
        //删除抽取条件
        UpdateWrapper<ExpertCondition> expertConditionUpdateWrapper = new UpdateWrapper<>();
        expertConditionUpdateWrapper.eq("sample_id", sampleId);
        expertConditionUpdateWrapper.eq("name", name);
        expertConditionUpdateWrapper.set("del_flag", 1);
        sampleNameMapper.update(null, sampleNameUpdateWrapper);
        sampleConditionMapper.update(null, sampleConditionUpdateWrapper);
        expertConditionMapper.update(null, expertConditionUpdateWrapper);
        return R.ok();
    }

    // 添加条件
    @RequestMapping("/create")
    public R<Object> create(SampleCondition sampleCondition) {
        QueryWrapper<SampleCondition> selectNum = new QueryWrapper<SampleCondition>()
                .eq("name", sampleCondition.getName())
                .eq("sample_id", sampleCondition.getSampleId())
                .eq("base_category_id", sampleCondition.getBaseCategoryId());
        if (sampleConditionMapper.selectCount(selectNum) > 0) {
            return R.fail("已存在");
        }
        sampleConditionService.saveCondition(sampleCondition);
        return R.ok();
    }

    // 查询组别名称
    @RequestMapping("/getSampleName")
    public R<Object> getSampleName(String sampleId) {
        QueryWrapper<SampleName> queryWrapper = new QueryWrapper<SampleName>()
                .select("id", "name").eq("sample_id", sampleId);
        List<SampleName> sampleNames = sampleNameMapper.selectList(queryWrapper);
        return R.ok(sampleNames);
    }

    //编辑抽取条件
    @RequestMapping(value = "/update")
    public R<Object> update(SampleCondition sampleCondition) {
        sampleConditionService.updateCondition(sampleCondition.getId(), sampleCondition);
        return R.ok();
    }

    // 根据条件查询条件
    @RequestMapping("/findAll")
    public R<Object> findAll(String sampleId, String name) {
        List<SampleCondition> sampleConditionList = sampleConditionService.findAll(sampleId, name);
        for (SampleCondition sampleCondition : sampleConditionList) {
            String baseCategoryName = sampleConditionService.findCategoryName(sampleCondition.getBaseCategoryId());
            sampleCondition.setBseCategoryName(baseCategoryName);
            String majorName = sampleConditionService.findMajorName(sampleCondition.getHideMajorName());
            sampleCondition.setBaseMajorName(majorName);
        }
        return R.ok(sampleConditionList);
    }

    // 根据组别名称查询
    @RequestMapping("/findName")
    public R<Object> findName(String sampleId, String name) {
        QueryWrapper<SampleCondition> wrapper = new QueryWrapper<>();
        wrapper.select("name").eq("sample_id", sampleId).eq("name", name).eq("del_flag", 0);
        List<SampleCondition> sampleConditions = sampleConditionMapper.selectList(wrapper);
        return R.ok(sampleConditions);
    }

    // 查询所有专家名称
    @RequestMapping("/getExpertName")
    public R<Object> getExpertName() {
        List<String> experts = sampleConditionService.getExpertName();
        return R.ok(experts);
    }

    //查询所有行业类别名称
    @RequestMapping("/getmajorName")
    public R<Object> getmajorName() {
        List<BaseMajorCategory> majors = sampleConditionService.getmajorName();
        //排序
        Collections.sort(majors, new Comparator<BaseMajorCategory>() {
            @Override
            public int compare(BaseMajorCategory o1, BaseMajorCategory o2) {
                if (o1.getCreateDate().before(o2.getCreateDate())) {
                    return 1;
                } else if (o1.getCreateDate().after(o2.getCreateDate())) {
                    return -1;
                }
                return 0;
            }
        });
        List<Object> dataList = new ArrayList<>();
        for (BaseMajorCategory bean : majors) {
            Object object = new DynamicBean.Builder().setPV("id", bean.getId())
                    .setPV("name", bean.getName())
                    .setPV("sort", bean.getSort())
                    .setPV("remark", bean.getRemark())
                    .build().getObject();
            dataList.add(object);
        }
        return R.ok(dataList);
    }

    //删除条件
    @RequestMapping("/delete")
    public R<Object> delete(String id, String sampleId, String name, String conditionId) {
        //同步删除抽取结果的数据
        if (sampleId != null && conditionId != null) {
            UpdateWrapper<ExpertCondition> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("name", name);
            updateWrapper.eq("condition_id", conditionId);
            updateWrapper.eq("sample_id", sampleId);
            updateWrapper.eq("pre_flag", 1);
            updateWrapper.set("del_flag", 1);
            expertConditionMapper.update(null, updateWrapper);
        }
        sampleConditionService.delete(id);
        return R.ok();
    }


    //专家抽取
    @RequestMapping(value = "/reqData")
    public R<Object> reqData(@RequestParam("isType") String isType,
                             @RequestBody List<SampleCondition> sampleCondition) {
        ExpertCondition expertCondition = new ExpertCondition();
        List<SampleConditionDto> strings = new ArrayList<>();
        String conditionNames = "";
        String expertNames = "";
        // 重新抽取根据sampleId将之前抽取的结果删除
        //获取sampleId
        SampleCondition sampleList = null;
        for (SampleCondition sampleCondition1 : sampleCondition) {
            sampleList = sampleCondition1;
        }
        LambdaQueryWrapper<SampleCondition> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(SampleCondition::getSampleId, SampleCondition::getName)
                .eq(SampleCondition::getName, sampleList.getName());
        List<SampleCondition> sampleIdList = sampleConditionMapper.selectList(wrapper);
        SampleCondition sampleIds = null;
        for (SampleCondition sampleId1 : sampleIdList) {
            sampleIds = sampleId1;
            ids = sampleId1;
        }
        String sampleId = sampleIds.getSampleId();
        String sampleName = sampleIds.getName();
        QueryWrapper<ExpertCondition> expertConditionQueryWrapper = new QueryWrapper<>();
        expertConditionQueryWrapper.select("*");
        List<ExpertCondition> expertConditionList = expertConditionMapper.selectList(expertConditionQueryWrapper);
        if (expertConditionList.size() != 0) {
            for (SampleCondition conditions : sampleCondition) {
                UpdateWrapper<ExpertCondition> expertConditionUpdateWrapper = new UpdateWrapper<>();
                expertConditionUpdateWrapper.eq("sample_id", sampleId);
                expertConditionUpdateWrapper.eq("name", sampleName);
                expertConditionUpdateWrapper.eq("condition_id", conditions.getId());
                expertConditionUpdateWrapper.eq("pre_flag", "1");
                expertConditionUpdateWrapper.set("del_flag", 1);
                // expertConditionMapper.update(null,expertConditionUpdateWrapper);
            }
        }

        for (SampleCondition condition : sampleCondition) {
            System.out.println("isType" + isType);
            //设置本次查询之前查过的专家id
            List<String> expertId = sampleConditionService.queryAllExpertId(condition.getId());
            String ex = "";
            for (String id:expertId){
                ex += id + ",";
            }
            String[] expert = ex.split(",");
            List<String> expertIds = Arrays.asList(expert);
            //设置屏蔽单位
            conditionNames = condition.getHideCondition();
            String[] conditionName = conditionNames.split(",");
            List<String> orgNames = Arrays.asList(conditionName);
            //设置屏蔽专家
            expertNames = condition.getHideExpertName();
            String[] expertName = expertNames.split(",");
            List<String> names = Arrays.asList(expertName);
            //设置抽取条件
            String categoryId = condition.getBaseCategoryId();
            String star = condition.getStar();
            Integer num = condition.getNum();
            Integer nums = num - 1;
            String name = condition.getName();
            String conditionId = condition.getId();
            //设置屏蔽行业类别
            String majorId = condition.getHideMajorName();
            //设置指定制裁项目
            String exProject = condition.getHideExpertProject();
            //抽取五星级专家
            List<SampleConditionDto> expertOnes =
                    sampleConditionService.getExpertOne(categoryId,expertIds, majorId, names, orgNames, exProject);
            //判断是否有五星级专家
            if (expertOnes.size() <= 0) {
                List<SampleConditionDto> expertLists1 =
                        sampleConditionService.getExpertList(categoryId,expertIds, majorId, names ,orgNames, star,num,exProject);
                if (expertLists1.size() < num) {
                    return R.fail("抽取专家不足");
                } else {
                    strings.addAll(expertLists1);
                    //将抽取到的数据添加进数据库
                    for (SampleConditionDto dto : expertLists1) {
                        expertCondition.setId(IdGen.uuid());
                        expertCondition.setName(name);
                        expertCondition.setExpertId(dto.getExpertId());
                        expertCondition.setSampleId(sampleId);
                        expertCondition.setConditionId(conditionId);
                        expertCondition.setExpertName(dto.getName());
                        expertCondition.setOrgName(dto.getOrgName());
                        expertCondition.setStudiedName(dto.getStudied());
                        expertCondition.setPositionalName(dto.getPositional());
                        expertCondition.setStudiedName(dto.getStudied());
                        expertCondition.setMobile(dto.getMobile());
                        expertCondition.setStar(dto.getStar());
                        expertCondition.setConfirmFlag("0");
                        expertCondition.setPreFlag("1");
                        expertCondition.setIsItem("1");
                        if (isType.equals("01")) {
                            expertCondition.setRounds(1);
                        } else {
                            expertCondition.setRounds(2);
                        }
                        expertConditionMapper.insert(expertCondition);
                        //更新已抽取状态
                        UpdateWrapper<Sample> sampleUpdateWrapper = new UpdateWrapper<>();
                        sampleUpdateWrapper.eq("id", sampleId);
                        sampleUpdateWrapper.set("status", 1);
                        sampleMapper.update(null, sampleUpdateWrapper);
                    }
                }
            } else {
                List<SampleConditionDto> expertOne = sampleConditionService.getExpertOne(categoryId, expertIds,majorId, names, orgNames, exProject);
                List<SampleConditionDto> expertLists =
                        sampleConditionService.getExpertList(categoryId,expertIds, majorId, names, orgNames, star,nums,exProject);
                strings.addAll(expertLists);
                strings.addAll(expertOne);
                if (strings.size() < num) {
                    return R.fail("抽取专家不足");
                } else {
                    //将抽取到的数据添加进数据库
                    for (SampleConditionDto dto : expertLists) {
                        expertCondition.setId(IdGen.uuid());
                        expertCondition.setName(name);
                        expertCondition.setExpertId(dto.getExpertId());
                        expertCondition.setSampleId(sampleId);
                        expertCondition.setConditionId(conditionId);
                        expertCondition.setExpertName(dto.getName());
                        expertCondition.setOrgName(dto.getOrgName());
                        expertCondition.setStudiedName(dto.getStudied());
                        expertCondition.setPositionalName(dto.getPositional());
                        expertCondition.setStudiedName(dto.getStudied());
                        expertCondition.setMobile(dto.getMobile());
                        expertCondition.setStar(dto.getStar());
                        expertCondition.setConfirmFlag("0");
                        expertCondition.setPreFlag("1");
                        expertCondition.setIsItem("1");
                        if (isType.equals("01")) {
                            expertCondition.setRounds(1);
                        } else {
                            expertCondition.setRounds(2);
                        }
                        expertConditionMapper.insert(expertCondition);
                        //更新已抽取状态
                        UpdateWrapper<Sample> sampleUpdateWrapper = new UpdateWrapper<>();
                        sampleUpdateWrapper.eq("id", sampleId);
                        sampleUpdateWrapper.set("status", 1);
                        sampleMapper.update(null, sampleUpdateWrapper);
                    }
                }
                //插入五星专家
                for (SampleConditionDto dto : expertOne) {
                    expertCondition.setId(IdGen.uuid());
                    expertCondition.setName(name);
                    expertCondition.setExpertId(dto.getExpertId());
                    expertCondition.setSampleId(sampleId);
                    expertCondition.setConditionId(conditionId);
                    expertCondition.setExpertName(dto.getName());
                    expertCondition.setOrgName(dto.getOrgName());
                    expertCondition.setStudiedName(dto.getStudied());
                    expertCondition.setPositionalName(dto.getPositional());
                    expertCondition.setStudiedName(dto.getStudied());
                    expertCondition.setMobile(dto.getMobile());
                    expertCondition.setStar(dto.getStar());
                    expertCondition.setConfirmFlag("0");
                    expertCondition.setPreFlag("1");
                    expertCondition.setIsItem("1");
                    if (isType.equals("01")) {
                        expertCondition.setRounds(1);
                    } else {
                        expertCondition.setRounds(2);
                    }
                    expertConditionMapper.insert(expertCondition);
                    //更新已抽取状态
                    UpdateWrapper<Sample> sampleUpdateWrapper = new UpdateWrapper<>();
                    sampleUpdateWrapper.eq("id", sampleId);
                    sampleUpdateWrapper.set("status", 1);
                    sampleMapper.update(null, sampleUpdateWrapper);
                }
            }
        }
        return R.ok(strings);
    }

    // 刷新专家替换
    @RequestMapping(value = "/reqDatas")
    public R<Object> reqDatas(@RequestParam("isType") String isType, @RequestBody List<SampleCondition> sampleCondition) {
        ExpertCondition expertCondition = new ExpertCondition();
        List<SampleConditionDto> strings = new ArrayList<>();
        String conditionNames = "";
        String expertNames = "";
        // 重新抽取根据sampleId将之前抽取的结果删除
        //获取sampleId
        SampleCondition sampleList = null;
        for (SampleCondition sampleCondition1 : sampleCondition) {
            sampleList = sampleCondition1;
        }
        LambdaQueryWrapper<SampleCondition> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(SampleCondition::getSampleId, SampleCondition::getName)
                .eq(SampleCondition::getName, sampleList.getName());
        List<SampleCondition> sampleIdList = sampleConditionMapper.selectList(wrapper);
        SampleCondition sampleIds = null;
        for (SampleCondition sampleId1 : sampleIdList) {
            sampleIds = sampleId1;
            ids = sampleId1;
        }
        String sampleId = sampleIds.getSampleId();
        String sampleName = sampleIds.getName();
        QueryWrapper<ExpertCondition> expertConditionQueryWrapper = new QueryWrapper<>();
        expertConditionQueryWrapper.select("*");
        List<ExpertCondition> expertConditionList = expertConditionMapper.selectList(expertConditionQueryWrapper);
        if (expertConditionList.size() != 0) {
            for (SampleCondition conditions : sampleCondition) {
                UpdateWrapper<ExpertCondition> expertConditionUpdateWrapper = new UpdateWrapper<>();
                expertConditionUpdateWrapper.eq("sample_id", sampleId);
                expertConditionUpdateWrapper.eq("name", sampleName);
                expertConditionUpdateWrapper.eq("condition_id", conditions.getId());
                expertConditionUpdateWrapper.set("del_flag", 1);
                expertConditionMapper.update(null, expertConditionUpdateWrapper);
            }
        }

        for (SampleCondition condition : sampleCondition) {
            String ex = "";
            String[] expert = ex.split(",");
            List<String> expertIds = Arrays.asList(expert);
            //设置屏蔽单位
            conditionNames = condition.getHideCondition();
            String[] conditionName = conditionNames.split(",");
            List<String> orgNames = Arrays.asList(conditionName);
            //设置屏蔽专家
            expertNames = condition.getHideExpertName();
            String[] expertName = expertNames.split(",");
            List<String> names = Arrays.asList(expertName);
            //设置抽取条件
            String categoryId = condition.getBaseCategoryId();
            String star = condition.getStar();
            Integer num = condition.getNum();
            Integer nums = num - 1;
            String name = condition.getName();
            String conditionId = condition.getId();
            //设置屏蔽行业类别
            String majorId = condition.getHideMajorName();
            //设置指定制裁项目
            String exProject = condition.getHideExpertProject();
            //抽取五星级专家
            List<SampleConditionDto> expertOnes = sampleConditionService.getExpertOne(categoryId,expertIds, majorId, names, orgNames, exProject);
            //判断是否有五星级专家
            if (expertOnes.size() <= 0) {
                List<SampleConditionDto> expertLists1 =
                        sampleConditionService.getExpertList(categoryId,expertIds, majorId, names, orgNames, star, num,exProject);
                if (expertLists1.size() < num) {
                    return R.fail("抽取专家不足");
                } else {
                    strings.addAll(expertLists1);
                    //将抽取到的数据添加进数据库
                    for (SampleConditionDto dto : expertLists1) {
                        expertCondition.setId(IdGen.uuid());
                        expertCondition.setExpertId(dto.getExpertId());
                        expertCondition.setName(name);
                        expertCondition.setSampleId(sampleId);
                        expertCondition.setConditionId(conditionId);
                        expertCondition.setExpertName(dto.getName());
                        expertCondition.setOrgName(dto.getOrgName());
                        expertCondition.setStudiedName(dto.getStudied());
                        expertCondition.setPositionalName(dto.getPositional());
                        expertCondition.setStudiedName(dto.getStudied());
                        expertCondition.setMobile(dto.getMobile());
                        expertCondition.setStar(dto.getStar());
                        expertCondition.setConfirmFlag("0");
                        expertCondition.setPreFlag("1");
                        expertCondition.setIsItem("1");
                        if (isType.equals("01")) {
                            expertCondition.setRounds(1);
                        } else {
                            expertCondition.setRounds(2);
                        }
                        expertConditionMapper.insert(expertCondition);
                    }
                }
            } else {
                List<SampleConditionDto> expertOne = sampleConditionService.getExpertOne(categoryId, expertIds,majorId, names, orgNames, exProject);
                List<SampleConditionDto> expertLists =
                        sampleConditionService.getExpertList(categoryId, expertIds,majorId, names, orgNames, star, nums, exProject);
                strings.addAll(expertLists);
                strings.addAll(expertOne);
                if (strings.size() < num) {
                    return R.fail("抽取专家不足");
                } else {
                    //将抽取到的数据添加进数据库
                    for (SampleConditionDto dto : expertLists) {
                        expertCondition.setId(IdGen.uuid());
                        expertCondition.setExpertId(dto.getExpertId());
                        expertCondition.setName(name);
                        expertCondition.setSampleId(sampleId);
                        expertCondition.setConditionId(conditionId);
                        expertCondition.setExpertName(dto.getName());
                        expertCondition.setOrgName(dto.getOrgName());
                        expertCondition.setStudiedName(dto.getStudied());
                        expertCondition.setPositionalName(dto.getPositional());
                        expertCondition.setStudiedName(dto.getStudied());
                        expertCondition.setMobile(dto.getMobile());
                        expertCondition.setStar(dto.getStar());
                        expertCondition.setConfirmFlag("0");
                        expertCondition.setPreFlag("1");
                        expertCondition.setIsItem("1");
                        if (isType.equals("01")) {
                            expertCondition.setRounds(1);
                        } else {
                            expertCondition.setRounds(2);
                        }
                        expertConditionMapper.insert(expertCondition);
                    }
                }
                //插入五星专家
                for (SampleConditionDto dto : expertOne) {
                    expertCondition.setId(IdGen.uuid());
                    expertCondition.setExpertId(dto.getExpertId());
                    expertCondition.setName(name);
                    expertCondition.setSampleId(sampleId);
                    expertCondition.setConditionId(conditionId);
                    expertCondition.setExpertName(dto.getName());
                    expertCondition.setOrgName(dto.getOrgName());
                    expertCondition.setStudiedName(dto.getStudied());
                    expertCondition.setPositionalName(dto.getPositional());
                    expertCondition.setStudiedName(dto.getStudied());
                    expertCondition.setMobile(dto.getMobile());
                    expertCondition.setStar(dto.getStar());
                    expertCondition.setConfirmFlag("0");
                    expertCondition.setPreFlag("1");
                    expertCondition.setIsItem("1");
                    if (isType.equals("01")) {
                        expertCondition.setRounds(1);
                    } else {
                        expertCondition.setRounds(2);
                    }
                    expertConditionMapper.insert(expertCondition);
                }
            }
        }
        return R.ok();
    }


    //根据组别名称查询抽取结果
    @RequestMapping("/findConditionName")
    public R<Object> findCondittionName(String name, String sampleId) {
        if (name != null) {
            names = name;
            QueryWrapper<ExpertCondition> expertConditionQueryWrapper = new QueryWrapper<>();
            expertConditionQueryWrapper.eq("name", name);
            expertConditionQueryWrapper.eq("sample_id", sampleId);
            List<ExpertCondition> expertConditionList = expertConditionMapper.selectList(expertConditionQueryWrapper);
            return R.ok(expertConditionList);
        } else {
            SampleCondition names = null;
            QueryWrapper<SampleCondition> sampleConditionQueryWrapper = new QueryWrapper<>();
            sampleConditionQueryWrapper.select("name").eq("sample_id", sampleId).last("LIMIT 1");
            List<SampleCondition> sampleConditionList = sampleConditionMapper.selectList(sampleConditionQueryWrapper);
            for (SampleCondition s : sampleConditionList) {
                names = s;
            }
            if (names != null) {
                QueryWrapper<ExpertCondition> expertConditionQueryWrapper = new QueryWrapper<>();
                expertConditionQueryWrapper.eq("name", names.getName());
                List<ExpertCondition> expertConditionList = expertConditionMapper.selectList(expertConditionQueryWrapper);
                return R.ok(expertConditionList);
            }
        }
        return R.ok();
    }

    // 根据组别名称查询以保存的数据
    @RequestMapping("/findPreName")
    public R<Object> findPreName(String sampleId) {
        QueryWrapper<ExpertCondition> wrapper = new QueryWrapper<>();
        wrapper.eq("sample_id", sampleId)
                .eq("del_flag", 0);
        List<ExpertCondition> expertConditionList = expertConditionMapper.selectList(wrapper);
        //排序,根据组别名称
        Collections.sort(expertConditionList, new Comparator<ExpertCondition>() {
            @Override
            public int compare(ExpertCondition o1, ExpertCondition o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        return R.ok(expertConditionList);
    }


    // 保存查询数据
    @RequestMapping("/updatePre")
    public R<Object> updatePre(String sampleId) {
        //更新保存状态
        UpdateWrapper<ExpertCondition> expertConditionUpdateWrapper = new UpdateWrapper<>();
        expertConditionUpdateWrapper.eq("sample_id", sampleId);
        expertConditionUpdateWrapper.eq("del_flag", 0);
        expertConditionUpdateWrapper.set("pre_flag", 0);
        expertConditionMapper.update(null, expertConditionUpdateWrapper);
        //更新已抽取状态
        UpdateWrapper<Sample> sampleUpdateWrapper = new UpdateWrapper<>();
        sampleUpdateWrapper.eq("id", sampleId);
        sampleUpdateWrapper.set("status", 1);
        sampleMapper.update(null, sampleUpdateWrapper);
        return R.ok();
    }

    //查询抽取状态禁用抽取按钮
    @RequestMapping("/findStatus")
    public R<Object> findStatus(String sampleId) {
        // 查询抽取状态
        QueryWrapper<Sample> sampleQueryWrapper = new QueryWrapper<>();
        sampleQueryWrapper.select("status");
        sampleQueryWrapper.eq("id", sampleId);
        List<Sample> sampleList = sampleMapper.selectList(sampleQueryWrapper);
        Sample sample = null;
        for (Sample s : sampleList) {
            sample = s;
        }
        String status = sample.getStatus();
        return R.ok(status);
    }

    // 根据sampleId，name插入指定专家；name：组别名称
    @RequestMapping("/insertExpert")
    public R<Object> insertExpert(String expertName, String sampleId, String name, String conditionId) {
        QueryWrapper<ExpertCondition> expertConditionQueryWrapper = new QueryWrapper<>();
        expertConditionQueryWrapper.eq("name", name);
        expertConditionQueryWrapper.eq("expert_name", expertName);
        if (expertConditionMapper.selectCount(expertConditionQueryWrapper) > 0) {
            return R.fail("当前组别已存在当前专家");
        } else {
            /*QueryWrapper<SampleCondition> queryWrapperConditionId = new QueryWrapper<>();
            queryWrapperConditionId.select("id");
            queryWrapperConditionId.eq("name",name);
            List<SampleCondition> sampleConditionList = sampleConditionMapper.selectList(queryWrapperConditionId);*/
            List<SampleConditionDto> expertList = sampleConditionService.findExpertOne(expertName);
            ExpertCondition expertCondition = new ExpertCondition();
            for (SampleConditionDto dto : expertList) {
                expertCondition.setId(IdGen.uuid());
                expertCondition.setSampleId(sampleId);
                expertCondition.setConditionId(conditionId);
                expertCondition.setName(name);
                expertCondition.setExpertName(dto.getName());
                expertCondition.setPositionalName(dto.getPositional());
                expertCondition.setStudiedName(dto.getStudied());
                expertCondition.setOrgName(dto.getOrgName());
                expertCondition.setMobile(dto.getMobile());
                expertCondition.setConfirmFlag("0");
                expertCondition.setStar(dto.getStar());
                expertCondition.setDelFlag(0);
                expertCondition.setPreFlag("1");
                expertCondition.setIsItem("1");
                expertCondition.setRounds(1);
                expertConditionMapper.insert(expertCondition);
            }
            return R.ok();
        }
    }


    // 根据组别名字查询专家名称
    @RequestMapping("/findExpertName")
    public R<Object> findExpertName(String name) {
        QueryWrapper<ExpertCondition> expertConditionQueryWrapper = new QueryWrapper<>();
        expertConditionQueryWrapper.select("expert_name");
        expertConditionQueryWrapper.eq("name", name);
        List<ExpertCondition> expertConditionList = expertConditionMapper.selectList(expertConditionQueryWrapper);
        return R.ok(expertConditionList);
    }


    //设置组长
    @RequestMapping("/setItem")
    public R<Object> setItem(@RequestBody List<String> expertNames) {
        int updateIsTeam = sampleConditionService.updateExpertConditionIsItemByExpertNames(names, expertNames);
        int updateIsNotTeam = sampleConditionService.updateExpertConditionNotIsItemByExpertNames(names, expertNames);
        return R.ok(updateIsTeam);
    }


    //根据组别名称导出抽取结果
    @RequestMapping(value = "/SampleExcel")
    public void jsExcel(@RequestBody String sampleId, HttpServletResponse response) {
        //查询抽取结果
        QueryWrapper<ExpertCondition> expertConditionQueryWrapper = new QueryWrapper<>();
        expertConditionQueryWrapper.eq("sample_id", sampleId);
        List<ExpertCondition> expertConditionList = expertConditionMapper.selectList(expertConditionQueryWrapper);
        //查询会议名称
        String projectName = sampleService.findProjectName(sampleId);
        //排序,根据组别名称
        Collections.sort(expertConditionList, new Comparator<ExpertCondition>() {
            @Override
            public int compare(ExpertCondition o1, ExpertCondition o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        //构造数据
        Map<String, Object> root = new HashMap<String, Object>();
        List<Map<String, Object>> list = new ArrayList<>();
        for (ExpertCondition condition : expertConditionList) {
            String names = condition.getName();
            String expertName = condition.getExpertName();
            String positionalName = condition.getPositionalName();
            String studied = condition.getStudiedName();
            String orgName = condition.getOrgName();
            String moblie = condition.getMobile();
            String rounds = "";
            if (condition.getRounds() == 1) {
                rounds = "首轮抽取";
            } else {
                rounds = "补抽";
            }
            String isItem = "";
            if (condition.getIsItem().equals("0")) {
                isItem = "组长";
            } else {
                isItem = "";
            }
            String confirmFlag = "";
            if (condition.getConfirmFlag().equals("0")) {
                confirmFlag = "未确认";
            } else if (condition.getConfirmFlag().equals("1")) {
                confirmFlag = "参加";
            } else if (condition.getConfirmFlag().equals("2")) {
                confirmFlag = "不参加";
            }

            Map<String, Object> entityMap = new HashMap<String, Object>();
            entityMap.put("name", names);
            entityMap.put("expertName", expertName);
            entityMap.put("positionalName", positionalName);
            entityMap.put("studied", studied);
            entityMap.put("orgName", orgName);
            entityMap.put("mobile", moblie);
            entityMap.put("rounds", rounds);
            entityMap.put("isItem", isItem);
            entityMap.put("confirmFlag", confirmFlag);
            list.add(entityMap);
        }

        root.put("list", list);
        Map<String, Map<String, Object>> beanParams = new HashMap<String, Map<String, Object>>();
        beanParams.put("statistics", root);
        XLSTransformer former = new XLSTransformer();
        InputStream in = null;
        OutputStream out = null;
        try {
            String fileName = projectName + DateUtils.getDate("yyyyMMddHHmm") + ".xlsx";
            File statisticsFile = new File(Paths.get(SpringContextUtil.getProperty("myself.classpath"), "reports", "xmpszj.xlsx").toString());
            in = new FileInputStream(statisticsFile);
            Workbook workbook = former.transformXLS(in, beanParams);
            response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
            response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            response.setCharacterEncoding("UTF-8");
            out = response.getOutputStream();
            workbook.write(out);
            out.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
