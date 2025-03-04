package com.glface.modules.sp.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.glface.base.bean.Page;
import com.glface.base.utils.StringUtils;
import com.glface.common.exeception.ServiceException;
import com.glface.modules.sp.mapper.ExpertConditionMapper;
import com.glface.modules.sp.mapper.ExpertMapper;
import com.glface.modules.sp.mapper.SampleExpertMapper;
import com.glface.modules.sp.mapper.SampleMapper;
import com.glface.modules.sp.model.*;
import com.glface.modules.sys.utils.UserUtils;
import com.glface.modules.utils.ExpertStatusEnum;
import com.glface.modules.utils.SampleStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

import static com.glface.common.web.ApiCode.*;

/**
 * 专家抽取
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class SampleService {
    @Resource
    private SampleMapper sampleMapper;
    @Resource
    private SampleExpertMapper sampleExpertMapper;
    @Resource
    private ExpertMapper expertMapper;
    @Resource
    private SampleAvoidService sampleAvoidService;
    @Resource
    private ExpertAvoidOrgService expertAvoidOrgService;
    @Resource
    private SampleCategoryNumService sampleCategoryNumService;
    @Resource
    private ExpertCategoryService expertCategoryService;
    @Resource
    private BaseCategoryService baseCategoryService;
    @Resource
    private ExpertConditionMapper expertConditionMapper;

    public Sample get(String id) {
        return sampleMapper.selectById(id);
    }

    public List<Sample> findAll() {
        LambdaQueryWrapper<Sample> queryWrapper = Wrappers.<Sample>query().lambda();
        queryWrapper.eq(Sample::getDelFlag, Sample.DEL_FLAG_NORMAL);
        return sampleMapper.selectList(queryWrapper);
    }

    public Page<Sample> pageSearch(Page<Sample> page, Sample bean) {
        page.setCount(sampleMapper.pageSearchCount(bean));
        page.setList(sampleMapper.pageSearch(page, bean));
        return page;
    }

    /**
     * 新增
     *
     * @param projectName     项目名称
     // * @param baseCategoryIds 分类
     * @param reviewDate      评审时间
     * @param remark          简要描述
     */
    @Transactional
    public void create(String projectName, Date reviewDate, String remark) {
        projectName = StringUtils.trim(projectName);

        if (StringUtils.isBlank(projectName)) {
            throw new ServiceException(SP_SAMPLE_NAME_REQUIRED);
        }

        /*if (StringUtils.isBlank(baseCategoryIds)) {
            throw new ServiceException(SP_SAMPLE_CATEGORY_REQUIRED);
        }*/
        if (reviewDate == null) {
            throw new ServiceException(SP_SAMPLE_DATE_REQUIRED);
        }

        // 创建
        Sample model = new Sample();
        model.setProjectName(projectName);
        // model.setBaseCategoryIds(baseCategoryIds);
        model.setReviewDate(reviewDate);
        model.setStatus(SampleStatusEnum.NO.getValue());
        model.setStatusDate(new Date());
        model.setRemark(remark);
        UserUtils.preAdd(model);
        sampleMapper.insert(model);
    }

    /**
     * 编辑
     */
    @Transactional
    public void update(String id, String projectName,Date reviewDate, String remark) {
        projectName = StringUtils.trim(projectName);
        // 数据验证
        Sample model = get(id);
        if (model == null) {
            throw new ServiceException(SP_SAMPLE_NOTEXIST);
        }
        if (StringUtils.isBlank(projectName)) {
            throw new ServiceException(SP_SAMPLE_NAME_REQUIRED);
        }

        /*if (StringUtils.isBlank(baseCategoryIds)) {
            throw new ServiceException(SP_SAMPLE_CATEGORY_REQUIRED);
        }*/
        if (reviewDate == null) {
            throw new ServiceException(SP_SAMPLE_DATE_REQUIRED);
        }
        // 修改
        model.setProjectName(projectName);
       // model.setBaseCategoryIds(baseCategoryIds);
        model.setReviewDate(reviewDate);
        if (StringUtils.isBlank(model.getStatus())) {
            model.setStatus(SampleStatusEnum.NO.getValue());
            model.setStatusDate(new Date());
        }
        model.setRemark(remark);
        //存储
        UserUtils.preUpdate(model);
        sampleMapper.updateById(model);
    }

    /**
     * 计划抽取人数
     */
    @Transactional
    public void setNumber(String id, Integer number) {
        // 数据验证
        Sample model = get(id);
        if (model == null) {
            throw new ServiceException(SP_SAMPLE_NOTEXIST);
        }
        if (number == null || number <= 0) {
            throw new ServiceException(SP_SAMPLE_NUMBER_ERROR);
        }
        model.setNumber(number);
        UserUtils.preUpdate(model);
        sampleMapper.updateById(model);
    }

    /**
     * 确认是否参加评审
     * @param expertConditionId  评审抽取专家id
     * @param confirmFlag  1参加 2：不参加
     */
    @Transactional
    public void expertConfirm(String expertConditionId,String confirmFlag,Date confirmDate){
        if(confirmDate==null){
            confirmDate = new Date();
        }
        ExpertCondition expertCondition = expertConditionMapper.selectById(expertConditionId);
        if(expertCondition==null){
            throw new ServiceException(SP_SAMPLE_EXPERT_NOTEXIST);
        }
        expertCondition.setConfirmFlag(confirmFlag);
        if(confirmFlag.equals("2")){
            confirmDate = null;
        }
        expertCondition.setConfirmDate(confirmDate);
        UserUtils.preUpdate(expertCondition);
        expertConditionMapper.updateById(expertCondition);
    }

    /**
     * 抽取
     */
    @Transactional
    public void sampleExpert(String id) {
        Sample sample = get(id);
        if (sample == null) {
            throw new ServiceException(SP_SAMPLE_NOTEXIST);
        }
        //查询已抽取人数
        LambdaQueryWrapper<SampleExpert> queryWrapper = Wrappers.<SampleExpert>query().lambda();
        queryWrapper.eq(SampleExpert::getDelFlag, SampleExpert.DEL_FLAG_NORMAL)
                .eq(SampleExpert::getSampleId, id);
        List<SampleExpert> dbSampleExperts = sampleExpertMapper.selectList(queryWrapper);
        List<SampleExpert> dbNotParticipates = new ArrayList<>();//确认不参加的专家
        //查询指定专家
        List<SampleAvoid> appoints = sampleAvoidService.findAppointsBySampleId(sample.getId());

        //查询抽取专家类别人数设置
        List<SampleCategoryNum> categoryNumList = sampleCategoryNumService.findBySampleId(id);
        if(categoryNumList.size()==0){
            throw new ServiceException(SP_SAMPLE_CATEGORY_NUMBER_REQUIRED);
        }
        //通过map存储本次对应类别下抽取的专家
        Map<String,Set<String>> categoryExpertMap = new HashMap<>();
        Map<String,Integer> remainderMap = new HashMap<>();//每一个分类剩余的待抽取人数

        int maxRounds = 0;//查询已抽取的最大抽取轮次
        for (SampleExpert sampleExpert : dbSampleExperts) {
            if ("2".equals(sampleExpert.getConfirmFlag())) {
                dbNotParticipates.add(sampleExpert);
            }
            if(sampleExpert.getRounds()!=null&&sampleExpert.getRounds()>maxRounds){
                maxRounds = sampleExpert.getRounds();
            }
        }
        //查询每一个分类剩余的待抽取人数
        for(SampleCategoryNum categoryNum:categoryNumList) {
            String baseCategoryId = categoryNum.getBaseCategoryId();
            int number = categoryNum.getNum();
            int categoryDbNum = 0;
            for(SampleExpert sampleExpert:dbSampleExperts){
                if(baseCategoryId.equals(sampleExpert.getBaseCategoryId())){
                    categoryDbNum++;
                }
            }
            int dbNotNum = 0;
            for(SampleExpert sampleExpert:dbNotParticipates){
                if(baseCategoryId.equals(sampleExpert.getBaseCategoryId())){
                    dbNotNum++;
                }
            }
            //剩余待抽取专家数
            int remainder = number - categoryDbNum + dbNotNum;
            if(remainder<0){
                remainder = 0;
            }
            remainderMap.put(baseCategoryId,remainder);
        }
        int remainderTotal = 0;//总待抽取人数
        for(String cId:remainderMap.keySet()){
            remainderTotal = remainderTotal + remainderMap.get(cId);
        }
        if(remainderTotal<=0){
            sample.setResult("已全部抽取");
            sampleMapper.updateById(sample);
            return;
        }

        //开始分类抽取
        for(SampleCategoryNum categoryNum:categoryNumList){
            String baseCategoryId = categoryNum.getBaseCategoryId();
            int remainder = remainderMap.get(baseCategoryId);//待抽取人数
            if(remainder<=0){
                continue;
            }
            //本次已抽取的专家
            Set<String> selecteds = new HashSet<>();
            for(String cId:categoryExpertMap.keySet()){
                Set<String> selectedList = categoryExpertMap.get(cId);
                selecteds.addAll(selectedList);
            }

            //设置专家池
            List<Expert> experts = new ArrayList<>();
            experts.addAll(expertMapper.findByCategoryId(baseCategoryId, ExpertStatusEnum.AGREE.getValue()));
            //踢出回避单位后的专家池
            experts = kickOutExperts(sample, experts);
            //从专家池中踢出已选定和指定的专家
            List<Expert> expertPool = new ArrayList<>();
            for (Expert expert : experts) {
                boolean has = false;
                for (SampleExpert sampleExpert : dbSampleExperts) {
                    if (expert.getId().equals(sampleExpert.getExpertId())) {
                        has = true;
                        break;
                    }
                }
                if (!has) {
                    for (SampleAvoid appoint : appoints) {
                        if (expert.getId().equals(appoint.getExpertId())) {
                            has = true;
                            break;
                        }
                    }
                }
                if (!has) {
                    expertPool.add(expert);
                }
            }
            //剔除本次已抽取的专家
            List<Expert> tmpPool = new ArrayList<>();
            for (Expert expert : expertPool) {
                boolean has = false;
                for(String select:selecteds){
                    if(expert.getId().equals(select)){
                        has = true;
                        break;
                    }
                }
                if(!has){
                    tmpPool.add(expert);
                }
            }
            expertPool = tmpPool;
            //去重
            Set<String> expertIdPool = new HashSet<>();
            for(Expert expert:expertPool){
                expertIdPool.add(expert.getId());
            }

            //开始抽取
            List<String> expertIds = new ArrayList<>();
            expertIds.addAll(sample(new ArrayList<>(expertIdPool),remainder));
            Set<String> categoryExpertIds = categoryExpertMap.get(baseCategoryId);
            if(categoryExpertIds==null){
                categoryExpertIds = new HashSet<>();
                categoryExpertMap.put(baseCategoryId,categoryExpertIds);
            }
            categoryExpertIds.addAll(expertIds);
        }

        //添加指定专家 排除已选或确认不参加的专家
        List<SampleAvoid> filterAppoints = new ArrayList<>();
        for(SampleAvoid sampleAvoid:appoints){
            boolean has = false;
            for (SampleExpert sampleExpert : dbSampleExperts) {
                if (sampleAvoid.getExpertId().equals(sampleExpert.getExpertId())) {
                    has = true;
                    break;
                }
            }
            if(!has){
                filterAppoints.add(sampleAvoid);
            }
        }
        for(SampleAvoid sampleAvoid:filterAppoints){//需要补充的
            String expertId = sampleAvoid.getExpertId();
            List<ExpertCategory> expertCategories = expertCategoryService.findByExpertId(expertId);
            Map<String,ExpertCategory> expertCategoryMap = new HashMap<>();
            for(ExpertCategory expertCategory:expertCategories){
                expertCategoryMap.put(expertCategory.getCategoryId(),expertCategory);
            }
            boolean hasInsert = false;
            //先补充到未选满的分类中
            for(SampleCategoryNum categoryNum:categoryNumList) {
                String baseCategoryId = categoryNum.getBaseCategoryId();
                if(expertCategoryMap.get(baseCategoryId)==null){
                    continue;
                }
                int remainder = remainderMap.get(baseCategoryId);//待抽取人数
                Set<String> categoryExpertIds = categoryExpertMap.get(baseCategoryId);//当前抽取情况
                if(categoryExpertIds==null){
                    categoryExpertIds = new HashSet<>();
                    categoryExpertMap.put(baseCategoryId,categoryExpertIds);
                }
                if(remainder>categoryExpertIds.size()){//未选满
                    categoryExpertIds.add(expertId);
                    hasInsert = true;
                    break;
                }
            }
            if(hasInsert){
                continue;
            }
            //替换已补满的专家 不能替补指定的专家
            for(SampleCategoryNum categoryNum:categoryNumList) {
                String baseCategoryId = categoryNum.getBaseCategoryId();
                if(expertCategoryMap.get(baseCategoryId)==null){
                    continue;
                }
                Set<String> categoryExpertIds = categoryExpertMap.get(baseCategoryId);//当前抽取情况
                if(categoryExpertIds==null){
                    categoryExpertIds = new HashSet<>();
                    categoryExpertMap.put(baseCategoryId,categoryExpertIds);
                }
                if(categoryExpertIds.size()>0){
                    String removeId = "";
                    for(String categoryExpertId:categoryExpertIds){
                        boolean isAppoint=false;
                        for(SampleAvoid sampleAvoid2:appoints){//不能替补指定的专家
                            if(categoryExpertId.equals(sampleAvoid2.getExpertId())){
                                isAppoint = true;
                                break;
                            }
                        }
                        if(!isAppoint){
                            removeId = categoryExpertId;
                            break;
                        }
                    }
                    if(StringUtils.isNotBlank(removeId)){
                        categoryExpertIds.remove(removeId);
                        categoryExpertIds.add(expertId);
                        hasInsert = true;
                        break;
                    }
                }else{
                    categoryExpertIds.add(expertId);
                    hasInsert = true;
                    break;
                }
            }
            if(hasInsert){
                continue;
            }
            //没有找到对应的分类 不做处理
        }

        //查询是否都已抽满
        String msg = "";
        for(SampleCategoryNum categoryNum:categoryNumList) {
            String baseCategoryId = categoryNum.getBaseCategoryId();
            int remainder = remainderMap.get(baseCategoryId);//待抽取人数
            Set<String> categoryExpertIds = categoryExpertMap.get(baseCategoryId);//当前抽取情况
            if(categoryExpertIds==null){
                categoryExpertIds = new HashSet<>();
                categoryExpertMap.put(baseCategoryId,categoryExpertIds);
            }
            if(remainder>categoryExpertIds.size()){//未选满
                BaseCategory baseCategory = baseCategoryService.get(baseCategoryId);
                String baseCategoryName = baseCategory!=null?baseCategory.getName():"";
                if(StringUtils.isNotBlank(baseCategoryName)){
                    msg = msg + baseCategoryName + "未抽满，缺"+(remainder-categoryExpertIds.size())+"个。\n";
                }
            }
        }
        if(StringUtils.isBlank(msg)){
            msg = "抽取成功";
        }

        //存储
        List<SampleExpert> sampleExperts = new ArrayList<>();
        for(String categoryId:categoryExpertMap.keySet()){
            Set<String> categoryExpertIds = categoryExpertMap.get(categoryId);
            for(String expertId:categoryExpertIds){
                SampleExpert sampleExpert = new SampleExpert();
                sampleExpert.setSampleId(sample.getId());
                sampleExpert.setBaseCategoryId(categoryId);
                sampleExpert.setExpertId(expertId);
                sampleExpert.setRounds(maxRounds+1);
                sampleExpert.setConfirmFlag("0");
                sampleExperts.add(sampleExpert);
            }
        }

        if(!"1".equals(sample.getStatus())&&sampleExperts.size()>0){
            sample.setStatus("1");
            sample.setStatusDate(new Date());
        }

        sample.setResult(msg);
        sampleMapper.updateById(sample);

        for(SampleExpert add:sampleExperts){
            UserUtils.preAdd(add);
            sampleExpertMapper.insert(add);
        }

    }

    private List<String> sample(List<String> pool,int number){
        if(number>=pool.size()){
            return pool;
        }
        List<Integer> indexList = new ArrayList<>();
        for(int i=0;i<1000;i++){
            if(indexList.size()>=number||indexList.size()>=pool.size()){
                break;
            }
            int rand = StringUtils.randNumber(0,pool.size()-1);
            if(!indexList.contains(rand)){
                indexList.add(rand);
            }
        }
        List<String> list = new ArrayList<>();
        for(Integer index:indexList){
            list.add(pool.get(index));
        }
        return list;
    }

    /**
     * 踢出回避单位的专家
     *
     * @param sample
     * @param experts
     * @return
     */
    private List<Expert> kickOutExperts(Sample sample, List<Expert> experts) {
        List<Expert> remainders = new ArrayList<>();
        List<SampleAvoid> avoidList = sampleAvoidService.findBySampleId(sample.getId());
        List<String> avoidOrgNames = new ArrayList<>();
        List<String> avoidOrgCodes = new ArrayList<>();
        List<String> avoidExperts = new ArrayList<>();
        for (SampleAvoid avoid : avoidList) {
            //0:工作单位  1:专家姓名
            if ("0".equals(avoid.getType())) {
                if (StringUtils.isNotBlank(avoid.getOrgName())) {
                    avoidOrgNames.add(avoid.getOrgName());
                }
                if (StringUtils.isNotBlank(avoid.getOrgCode())) {
                    avoidOrgCodes.add(avoid.getOrgCode().toUpperCase());
                }
            } else if ("1".equals(avoid.getType())) {
                if (StringUtils.isNotBlank(avoid.getExpertId())) {
                    avoidExperts.add(avoid.getExpertId());
                }
            }
        }
        for (Expert expert : experts) {
            String orgName = expert.getOrgName();//工作单位
            //List<ExpertAvoidOrg> expertAvoidOrgs = expertAvoidOrgService.findByExpertId(expert.getId());//专家主动填写的回避单位
            boolean has = false;
            if (StringUtils.isNotBlank(orgName) && avoidOrgNames.contains(orgName)) {
                has = true;
            }
//            if (!has) {
//                if (expertAvoidOrgs != null && expertAvoidOrgs.size() > 0) {
//                    for (ExpertAvoidOrg expertAvoidOrg : expertAvoidOrgs) {
//                        if (StringUtils.isNotBlank(expertAvoidOrg.getOrgCode()) && avoidOrgCodes.contains(expertAvoidOrg.getOrgCode().toUpperCase())) {
//                            has = true;
//                            break;
//                        } else if (StringUtils.isNotBlank(expertAvoidOrg.getOrgName()) && avoidOrgNames.contains(expertAvoidOrg.getOrgName())) {
//                            has = true;
//                            break;
//                        }
//                    }
//                }
//            }
            if (!has) {
                if (avoidExperts.contains(expert.getId())) {
                    has = true;
                }
            }
            if (!has) {
                remainders.add(expert);
            }
        }
        //是否设置了好评度条件
        String stars = sample.getStars();
        if (StringUtils.isNotBlank(stars)) {
            String[] starArray = stars.split(",");
            if (starArray != null && starArray.length > 0) {
                List<String> starList = new ArrayList<>();
                for (String star : starArray) {
                    star = star.replaceAll("星", "");
                    starList.add(star);
                }
                List<Expert> tmp = new ArrayList<>();
                for (Expert expert : remainders) {
                    String expertStar = expert.getStar();
                    if (StringUtils.isNotBlank(expertStar)) {
                        expertStar = expertStar.replaceAll("星", "");
                        if (!starList.contains(expertStar)) {
                            tmp.add(expert);
                        }
                    } else {
                        tmp.add(expert);
                    }
                }
                remainders = tmp;
            }
        }
        return remainders;
    }

    @Transactional
    public void delete(String id) {
        sampleMapper.deleteById(id);
    }

    @Transactional
    /**
    * 查询会议名称
    * @param id
    * */
    public String findProjectName(String id){
        String projectName = sampleMapper.findProjectName(id);
        return projectName;
    }



}
