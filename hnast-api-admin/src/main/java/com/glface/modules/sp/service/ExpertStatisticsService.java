package com.glface.modules.sp.service;

import com.glface.base.utils.StringUtils;
import com.glface.modules.sp.model.*;
import com.glface.modules.utils.ExpertStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * 统计
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class ExpertStatisticsService {

    @Resource
    private ExpertService expertService;
    @Resource
    private SampleService sampleService;
    @Resource
    private BaseCategoryService baseCategoryService;
    /**
     * 专家汇总
     */
    public ExpertSummary expertSummary(){
        List<Expert> list = expertService.findAll();

        List<Expert> applyList = new ArrayList<>();
        //去掉未申请的项目
        for(Expert expert:list){
            if(expert.getApplyDate()!=null){
                applyList.add(expert);
            }
        }
        list = applyList;
        //统计
        ExpertSummary summary = new ExpertSummary();
        summary.setExpertNum(list.size());//申报专家数量
         int validNum=0;	// 入库专家数量
         int outNum=0;//出库专家数量
         int rejectNum=0;//审核不通过数量
        for(Expert expert:list){
            String status = expert.getStatus();
            if(ExpertStatusEnum.AGREE.getValue().equals(status)){
                validNum++;
            }
            if(ExpertStatusEnum.OUT.getValue().equals(status)){
                outNum++;
            }
            if(ExpertStatusEnum.REJECT.getValue().equals(status)){
                rejectNum++;
            }
        }
        summary.setValidNum(validNum);
        summary.setOutNum(outNum);
        summary.setRejectNum(rejectNum);
        return summary;
    }

    /**
     * 项目统计
     */
    public SampleSummary sampleSummary(){
        SampleSummary summary = new SampleSummary();
        Map<String, Set<Sample>> categorySampleMap = summary.getCategorySampleMap();
        if(categorySampleMap==null){
            categorySampleMap = new HashMap<>();
            summary.setCategorySampleMap(categorySampleMap);
        }
        List<Sample> list = sampleService.findAll();
        List<BaseCategory> allBaseCategories = baseCategoryService.all();
        Map<String,BaseCategory> baseCategoryMap = new HashMap<>();
        for(BaseCategory baseCategory:allBaseCategories){
            baseCategoryMap.put(baseCategory.getId(),baseCategory);
        }
        for(Sample sample:list){
            String baseCategoryIds = sample.getBaseCategoryIds();
            if(StringUtils.isBlank(baseCategoryIds)){
                continue;
            }
            String ids[]= baseCategoryIds.split(",");
            for(String baseCategoryId:ids){
                if(StringUtils.isBlank(baseCategoryId)){
                    continue;
                }
                BaseCategory baseCategory = baseCategoryMap.get(baseCategoryId);
                if(baseCategory==null){
                    continue;
                }
                Set<Sample> sampleSet = categorySampleMap.get(baseCategoryId);
                if(sampleSet==null){
                    sampleSet = new HashSet<>();
                    categorySampleMap.put(baseCategoryId,sampleSet);
                }
                sampleSet.add(sample);
            }
        }
        return summary;
    }

    /**
     * 项目搜索
     * @param baseCategoryId 类别
     * @param status 状态 0未抽取  1已抽取
     * @return
     */
    public List<Sample> search(String baseCategoryId,String status){
        List<Sample> list = sampleService.findAll();

        if(StringUtils.isNotBlank(baseCategoryId)){
            List<Sample> tmpList = new ArrayList<>();
            for(Sample sample:list){
                if(sample.getBaseCategoryIds()==null){
                    continue;
                }
                String ids[] =sample.getBaseCategoryIds().split(",");
                boolean has = false;
                for(String categoryId:ids){
                    if (categoryId.equals(baseCategoryId)){
                        has = true;
                        break;
                    }
                }
                if(has){
                    tmpList.add(sample);
                }
            }
            list = tmpList;
        }
        if(StringUtils.isNotBlank(status)){
            List<Sample> tmpList = new ArrayList<>();
            for(Sample sample:list){
                if(status.equals(sample.getStatus())){
                    tmpList.add(sample);
                }
            }
            list = tmpList;
        }
        return list;
    }

}
