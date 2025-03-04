package com.glface.modules.sp.controller;

import com.glface.base.bean.DynamicBean;
import com.glface.base.bean.R;
import com.glface.base.utils.StringUtils;
import com.glface.log.LoggerMonitor;
import com.glface.modules.sp.model.Expert;
import com.glface.modules.sp.model.SampleAvoid;
import com.glface.modules.sp.service.ExpertService;
import com.glface.modules.sp.service.SampleAvoidService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 专家抽取回避条件
 */
@Slf4j
@RestController
@RequestMapping("/specialist/avoid")
public class SampleAvoidController {

    @Resource
    private SampleAvoidService sampleAvoidService;
    @Resource
    private ExpertService expertService;

    @RequestMapping(value = "/get")
    public R<Object> get(String id) {
        SampleAvoid bean = sampleAvoidService.get(id);
        String expertId = bean.getExpertId();
        String expertName = "";//专家姓名
        if(StringUtils.isNotBlank(expertId)){
            Expert expert = expertService.get(expertId);
            if(expert!=null){
                expertName = expert.getName();
            }
        }
        //构造返回数据
        Object object = new DynamicBean.Builder().setPV("id", bean.getId())
                .setPV("sampleId", bean.getSampleId())
                .setPV("type", bean.getType())
                .setPV("orgName", bean.getOrgName())
                .setPV("orgCode", bean.getOrgCode())
                .setPV("expertId", bean.getExpertId())
                .setPV("expertName", expertName)
                .setPV("remark", bean.getRemark())
                .build().getObject();
        return R.ok(object);
    }

    /**
     * 查询屏蔽信息
     */
    @PreAuthorize("hasAuthority('specialist:avoid:view')")
    @RequestMapping(value = "/findBySample")
    public R<Object> findBySample(String sampleId) {
        List<SampleAvoid> list = sampleAvoidService.findBySampleId(sampleId);
        List<Object> result = new ArrayList<>();
        for (SampleAvoid bean : list) {
            String expertId = bean.getExpertId();
            String expertName = "";//专家姓名
            if(StringUtils.isNotBlank(expertId)){
                Expert expert = expertService.get(expertId);
                if(expert!=null){
                    expertName = expert.getName();
                }
            }
            Object batchBean = new DynamicBean.Builder().setPV("id", bean.getId())
                    .setPV("sampleId", bean.getSampleId())
                    .setPV("type", bean.getType())
                    .setPV("orgName", bean.getOrgName())
                    .setPV("orgCode", bean.getOrgCode())
                    .setPV("expertId", bean.getExpertId())
                    .setPV("expertName", expertName)
                    .setPV("remark", bean.getRemark())
                    .build().getObject();
            result.add(batchBean);
        }
        return R.ok(result);
    }

    /**
     * 查询指定信息 只有管理员可以查看
     */
    @PreAuthorize("hasAuthority('specialist:avoid:manager')")
    @RequestMapping(value = "/findAppointsBySample")
    public R<Object> findAppointsBySample(String sampleId) {
        List<SampleAvoid> list = sampleAvoidService.findAppointsBySampleId(sampleId);
        List<Object> result = new ArrayList<>();
        for (SampleAvoid bean : list) {
            String expertId = bean.getExpertId();
            String expertName = "";//专家姓名
            if(StringUtils.isNotBlank(expertId)){
                Expert expert = expertService.get(expertId);
                if(expert!=null){
                    expertName = expert.getName();
                }
            }
            Object batchBean = new DynamicBean.Builder().setPV("id", bean.getId())
                    .setPV("sampleId", bean.getSampleId())
                    .setPV("type", bean.getType())
                    .setPV("orgName", bean.getOrgName())
                    .setPV("orgCode", bean.getOrgCode())
                    .setPV("expertId", bean.getExpertId())
                    .setPV("expertName", expertName)
                    .setPV("remark", bean.getRemark())
                    .build().getObject();
            result.add(batchBean);
        }
        return R.ok(result);
    }


    /**
     * 新增
     * @param type          0:工作单位  1:专家姓名
     * @return
     */
    @LoggerMonitor(value = "【专家库】专家抽取回避条件-新增")
    @PreAuthorize("hasAuthority('specialist:avoid:add')")
    @RequestMapping(value = "/create")
    public R<Object> create(String sampleId, String type, String orgName,String orgCode,String expertId,String remark) {
        sampleAvoidService.create(sampleId, type,orgName, orgCode,expertId,remark);
        return R.ok();
    }

    /**
     * 编辑
     */
    @LoggerMonitor(value = "【专家库】专家抽取回避条件-编辑")
    @PreAuthorize("hasAuthority('specialist:avoid:edit')")
    @RequestMapping(value = "/update")
    public R<Object> update(String id, String sampleId, String type, String orgName,String orgCode,String expertId,String remark) {
        sampleAvoidService.update(id, sampleId, type,orgName, orgCode,expertId,remark);
        return R.ok();
    }

    /**
     * 只有管理员可以指定
     * @return
     */
    @LoggerMonitor(value = "【专家库】专家抽取回避条件-指定专家")
    @PreAuthorize("hasAuthority('specialist:avoid:manager')")
    @RequestMapping(value = "/appoint")
    public R<Object> appoint(String sampleId,String expertIds,String remark) {
        sampleAvoidService.appoint(sampleId,expertIds,remark);
        return R.ok();
    }

    /**
     * 查询星级信息 只有管理员可以查看
     */
    @PreAuthorize("hasAuthority('specialist:avoid:manager')")
    @RequestMapping(value = "/findStars")
    public R<Object> findStarsBySample(String sampleId) {
        List<String> list = sampleAvoidService.findStarsBySampleId(sampleId);
        return R.ok(list);
    }

    /**
     * 只有管理员可以设置星级
     * @param star 星级  如果批量设置使用英文逗号隔开
     * @return
     */
    @LoggerMonitor(value = "【专家库】专家抽取回避条件-设置星级")
    @PreAuthorize("hasAuthority('specialist:avoid:manager')")
    @RequestMapping(value = "/stars")
    public R<Object> star(String sampleId, String star) {
        sampleAvoidService.star(sampleId,star);
        return R.ok();
    }

    /**
     * 只有管理员可以移除星级
     * @return
     */
    @LoggerMonitor(value = "【专家库】专家抽取回避条件-移除星级")
    @PreAuthorize("hasAuthority('specialist:avoid:manager')")
    @RequestMapping(value = "/removeStar")
    public R<Object> removeStar(String sampleId, String star) {
        sampleAvoidService.removeStar(sampleId,star);
        return R.ok();
    }

    /**
     * 删除
     */
    @LoggerMonitor(value = "【专家库】专家抽取回避条件-删除")
    @PreAuthorize("hasAuthority('specialist:avoid:del')")
    @RequestMapping(value = "/delete")
    public R<Object> delete(String id) {
        sampleAvoidService.delete(id);
        return R.ok();
    }


}
