package com.glface.modules.sp.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.glface.base.bean.BaseEntity;
import com.glface.base.utils.StringUtils;
import com.glface.common.exeception.ServiceException;
import com.glface.modules.sp.mapper.SampleAvoidMapper;
import com.glface.modules.sp.mapper.SampleMapper;
import com.glface.modules.sp.model.BaseCategory;
import com.glface.modules.sp.model.Sample;
import com.glface.modules.sp.model.SampleAvoid;
import com.glface.modules.sys.utils.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

import static com.glface.common.web.ApiCode.*;

/**
 * 专家抽取回避条件
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class SampleAvoidService {
    @Resource
    private SampleAvoidMapper sampleAvoidMapper;
    @Resource
    private SampleMapper sampleMapper;

    public SampleAvoid get(String id) {
        return sampleAvoidMapper.selectById(id);
    }


    /**
     * 查询屏蔽信息
     *
     * @param sampleId
     * @return
     */
    public List<SampleAvoid> findBySampleId(String sampleId) {
        LambdaQueryWrapper<SampleAvoid> queryWrapper = Wrappers.<SampleAvoid>query().lambda();
        queryWrapper.eq(SampleAvoid::getDelFlag, SampleAvoid.DEL_FLAG_NORMAL)
                .eq(SampleAvoid::getSampleId, sampleId)
                .ne(SampleAvoid::getAppointFlag, "1")
                .orderByDesc(SampleAvoid::getCreateDate);
        return sampleAvoidMapper.selectList(queryWrapper);
    }

    /**
     * 查询指定信息
     *
     * @param sampleId
     * @return
     */
    public List<SampleAvoid> findAppointsBySampleId(String sampleId) {
        LambdaQueryWrapper<SampleAvoid> queryWrapper = Wrappers.<SampleAvoid>query().lambda();
        queryWrapper.eq(SampleAvoid::getDelFlag, SampleAvoid.DEL_FLAG_NORMAL)
                .eq(SampleAvoid::getSampleId, sampleId)
                .eq(SampleAvoid::getAppointFlag, "1")
                .orderByDesc(SampleAvoid::getCreateDate);
        return sampleAvoidMapper.selectList(queryWrapper);
    }

    /**
     * 查询星级
     *
     * @param sampleId
     * @return
     */
    public List<String> findStarsBySampleId(String sampleId) {
        List<String> stars = new ArrayList<>();
        Sample sample = sampleMapper.selectById(sampleId);
        if(sample!=null&&StringUtils.isNotBlank(sample.getStars())){
            stars = Arrays.asList(sample.getStars().split(","));
        }
        return stars;
    }

    public SampleAvoid findBySampleIdAndExpertId(String sampleId,String expertId,String appointFlag){
        LambdaQueryWrapper<SampleAvoid> queryWrapper = Wrappers.<SampleAvoid>query().lambda()
                .eq(SampleAvoid::getSampleId, sampleId)
                .eq(SampleAvoid::getExpertId, expertId)
                .eq(SampleAvoid::getAppointFlag, appointFlag)
                .eq(SampleAvoid::getDelFlag,SampleAvoid.DEL_FLAG_NORMAL);
        return sampleAvoidMapper.selectOne(queryWrapper);
    }

    public SampleAvoid findBySampleIdAndOrgName(String sampleId,String orgName){
        LambdaQueryWrapper<SampleAvoid> queryWrapper = Wrappers.<SampleAvoid>query().lambda()
                .eq(SampleAvoid::getSampleId, sampleId)
                .eq(SampleAvoid::getOrgName, orgName)
                .eq(SampleAvoid::getDelFlag,SampleAvoid.DEL_FLAG_NORMAL);
        return sampleAvoidMapper.selectOne(queryWrapper);
    }

    /**
     * 新增屏蔽信息
     *
     * @param sampleId
     * @param type     0:工作单位  1:专家姓名
     */
    @Transactional
    public void create(String sampleId, String type, String orgName, String orgCode, String expertId, String remark) {
        if (StringUtils.isNotBlank(orgName)) {
            orgName = orgName.trim();
        }
        if (StringUtils.isNotBlank(orgCode)) {
            orgCode = orgCode.trim();
            orgCode = orgCode.toUpperCase();
        }

        if (StringUtils.isBlank(sampleId)) {
            throw new ServiceException(SP_SAMPLE_NOTEXIST);
        }

        if (StringUtils.isBlank(type)) {
            throw new ServiceException(SP_SAMPLE_AVOID_TYPE_REQUIRED);
        }
        if ("0".equals(type)) {//工作单位
            if (StringUtils.isBlank(orgName)) {
                throw new ServiceException(SP_SAMPLE_AVOID_ORG_NAME_REQUIRED);
            }
//            if (StringUtils.isBlank(orgCode)) {
//                throw new ServiceException(SP_SAMPLE_AVOID_ORG_CODE_REQUIRED);
//            }
            SampleAvoid dbData = findBySampleIdAndOrgName(sampleId,orgName);
            if(dbData!=null){
                throw new ServiceException(SP_SAMPLE_AVOID_EXIST);
            }
        } else if ("1".equals(type)) {
            if (StringUtils.isBlank(expertId)) {
                throw new ServiceException(SP_SAMPLE_AVOID_EXPERTID_REQUIRED);
            }
            SampleAvoid dbData = findBySampleIdAndExpertId(sampleId,expertId, "1");
            if(dbData!=null){
                throw new ServiceException(SP_SAMPLE_AVOID_EXIST);
            }
        } else {
            throw new ServiceException(SP_SAMPLE_AVOID_TYPE_ERROR);
        }

        // 创建
        SampleAvoid model = new SampleAvoid();
        model.setSampleId(sampleId);
        model.setType(type);
        model.setOrgName(orgName);
        model.setOrgCode(orgCode);
        model.setExpertId(expertId);
        model.setRemark(remark);
        model.setAppointFlag("0");
        UserUtils.preAdd(model);
        sampleAvoidMapper.insert(model);
    }

    /**
     * 编辑屏蔽信息
     */
    @Transactional
    public void update(String id, String sampleId, String type, String orgName, String orgCode, String expertId, String remark) {
        if (StringUtils.isNotBlank(orgName)) {
            orgName = orgName.trim();
        }
        if (StringUtils.isNotBlank(orgCode)) {
            orgCode = orgCode.trim();
            orgCode = orgCode.toUpperCase();
        }
        // 数据验证
        SampleAvoid model = get(id);
        if (model == null) {
            throw new ServiceException(SP_SAMPLE_AVOID_NOTEXIST);
        }
        if (StringUtils.isBlank(sampleId)) {
            throw new ServiceException(SP_SAMPLE_NOTEXIST);
        }

        if (StringUtils.isBlank(type)) {
            throw new ServiceException(SP_SAMPLE_AVOID_TYPE_REQUIRED);
        }
        if ("0".equals(type)) {//工作单位
            if (StringUtils.isBlank(orgName)) {
                throw new ServiceException(SP_SAMPLE_AVOID_ORG_NAME_REQUIRED);
            }
//            if (StringUtils.isBlank(orgCode)) {
//                throw new ServiceException(SP_SAMPLE_AVOID_ORG_CODE_REQUIRED);
//            }
            SampleAvoid dbData = findBySampleIdAndOrgName(sampleId,orgName);
            if(dbData!=null&&!id.equals(dbData.getId())){
                throw new ServiceException(SP_SAMPLE_AVOID_EXIST);
            }
        } else if ("1".equals(type)) {
            if (StringUtils.isBlank(expertId)) {
                throw new ServiceException(SP_SAMPLE_AVOID_EXPERTID_REQUIRED);
            }
            SampleAvoid dbData = findBySampleIdAndExpertId(sampleId,expertId,"1");
            if(dbData!=null&&!id.equals(dbData.getId())){
                throw new ServiceException(SP_SAMPLE_AVOID_EXIST);
            }
        } else {
            throw new ServiceException(SP_SAMPLE_AVOID_TYPE_ERROR);
        }
        // 修改
        model.setSampleId(sampleId);
        model.setType(type);
        model.setOrgName(orgName);
        model.setOrgCode(orgCode);
        model.setExpertId(expertId);
        model.setRemark(remark);
        model.setAppointFlag("0");
        //存储
        UserUtils.preUpdate(model);
        sampleAvoidMapper.updateById(model);
    }

    /**
     * 指定
     *
     * @param sampleId
     */
    @Transactional
    public void appoint(String sampleId, String expertIds, String remark) {

        if (StringUtils.isBlank(sampleId)) {
            throw new ServiceException(SP_SAMPLE_NOTEXIST);
        }
//        if (StringUtils.isBlank(expertIds)) {
//            throw new ServiceException(SP_SAMPLE_AVOID_EXPERTID_REQUIRED);
//        }
        List<SampleAvoid> appoints = findAppointsBySampleId(sampleId);
        String[] expertList = expertIds.split(",");
        for (SampleAvoid s : appoints) {
            boolean hasExpert = false;
            for (String expert : expertList) {
                if(s.getExpertId().equals(expert)){
                    hasExpert = true;
                }
            }
            if(!hasExpert){
                sampleAvoidMapper.deleteById(s);
            }
        }
        for(String expertId:expertList){
            if(StringUtils.isNotBlank(expertId)){
                SampleAvoid sampleAvoid = findBySampleIdAndExpertId(sampleId,expertId, "1");
                if(sampleAvoid==null){
                    // 创建
                    SampleAvoid model = new SampleAvoid();
                    model.setSampleId(sampleId);
                    model.setType("1");
                    model.setExpertId(expertId);
                    model.setRemark(remark);
                    model.setAppointFlag("1");
                    UserUtils.preAdd(model);
                    sampleAvoidMapper.insert(model);
                }
            }

        }

    }

    /**
     * 指定星级
     */
    @Transactional
    public void star(String sampleId, String star) {
        if(StringUtils.isNotBlank(star)){
            star = star.trim();
            if(star.startsWith(",")){
                star = star.substring(1);
            }
            if(star.endsWith(",")){
                star = star.substring(0,star.length()-1);
            }
        }
        if (StringUtils.isBlank(sampleId)) {
            throw new ServiceException(SP_SAMPLE_NOTEXIST);
        }
        Sample sample = sampleMapper.selectById(sampleId);
        if (sample == null) {
            throw new ServiceException(SP_SAMPLE_NOTEXIST);
        }
        if (StringUtils.isBlank(star)) {
            throw new ServiceException(SP_SAMPLE_AVOID_STAR_REQUIRED);
        }
        String stars = sample.getStars();
        if(star.indexOf(",")>0){
            stars = star;
        }else{
            Set<String> starSet = new HashSet<>();
            if(stars!=null){
                starSet.addAll(Arrays.asList(sample.getStars().split(",")));
            }
            starSet.add(star);
            List<String> starList = new ArrayList<>();
            starList.addAll(starSet);

            Collections.sort(starList);
            String separator = ",";
            StringBuilder sb = new StringBuilder();
            for (String s:starList) {
                sb.append(s).append(separator);
            }
            stars = sb.toString();
            if(starList.size()>0){
                stars = stars.substring(0, stars.length() - 1);
            }
        }
        sample.setStars(stars);
        sampleMapper.updateById(sample);
    }

    /**
     * 移除星级
     */
    @Transactional
    public void removeStar(String sampleId, String star) {
        if (StringUtils.isBlank(sampleId)) {
            throw new ServiceException(SP_SAMPLE_NOTEXIST);
        }
        Sample sample = sampleMapper.selectById(sampleId);
        if (sample == null) {
            throw new ServiceException(SP_SAMPLE_NOTEXIST);
        }
        if (StringUtils.isBlank(star)) {
            throw new ServiceException(SP_SAMPLE_AVOID_STAR_REQUIRED);
        }
        String stars = sample.getStars();
        Set<String> starSet = new HashSet<>();
        if(stars!=null){
            starSet.addAll(Arrays.asList(sample.getStars().split(",")));
        }
        starSet.remove(star);
        List<String> starList = new ArrayList<>();
        starList.addAll(starSet);

        Collections.sort(starList);
        String separator = ",";
        StringBuilder sb = new StringBuilder();
        for (String s:starList) {
            sb.append(s).append(separator);
        }
        stars = sb.toString();
        if(starList.size()>0){
            stars = stars.substring(0, stars.length() - 1);
        }
        sample.setStars(stars);
        sampleMapper.updateById(sample);
    }

    @Transactional
    public void delete(String id) {
        sampleAvoidMapper.deleteById(id);
    }

}
