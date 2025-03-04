package com.glface.modules.sp.excelOrWord;

import com.glface.base.utils.IdGen;
import com.glface.modules.sp.mapper.ExpertCategoryMapper;
import com.glface.modules.sp.mapper.ExpertMapper;
import com.glface.modules.sp.model.BaseEducation;
import com.glface.modules.sp.model.Expert;
import com.glface.modules.sp.model.ExpertCategory;
import com.glface.modules.sp.service.BaseEducationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Coovright (C), 2020-2023
 * FileName: ExcelService
 * Author: wanluixng
 * Date: 2023/4/21 9:58
 * Description:
 * History:
 * <author>  <time>  <version> <desc>
 * 作者姓名   修改时间    版本号    描述
 */

@Slf4j
@Service
@Transactional(readOnly = true)
public class ExcelService {

    @Resource
    private ExcelMapper excelMapper;

    @Resource
    private ExpertMapper expertMapper;

    @Resource
    private ExpertCategoryMapper expertCategoryMapper;

    @Resource
    private BaseEducationService baseEducationService;

    @Transactional
    public void saveExpert(List<Excel> excelList) {
        Expert expert = new Expert();
        List<String> categories = excelList.stream()
                .map(Excel::getCategory)
                .collect(Collectors.toList());
        List<Excel> categoryIds = excelMapper.searchCategoryId(categories);
        List<String> names = excelList.stream()
                .map(Excel::getName)
                .collect(Collectors.toList());
        for(Excel excel: excelList) {
            expert.setId(IdGen.uuid());
            expert.setName(excel.getName());
            expert.setStar(excel.getStar());
            expert.setMobile(excel.getMobile());
            expert.setApplyDate(new Date(excel.getApplyDate()));
            expert.setOrgName(excel.getOrgName());
            expert.setStatus(excel.getStatus());
            expertMapper.insert(expert);
        }
        List<Excel> expertIds = excelMapper.searchExpertId(names);
        ExpertCategory expertCategory = new ExpertCategory();
        for (int j = 0; j < categoryIds.size(); j++) {
            expertCategory.setId(IdGen.uuid());
            expertCategory.setCategoryId(String.valueOf(categoryIds.get(j)));
            expertCategory.setExpertId(String.valueOf(expertIds.get(j)));
            System.out.println(expertCategory);
//            expertCategoryMapper.insert(expertCategory);
        }
    }

    @Transactional
    public void ExprotAll(String[] ids, String categoryId) {
        for (String value : ids) {
            ExpertCategory expertCategory = new ExpertCategory();
            expertCategory.setId(IdGen.uuid());
            expertCategory.setExpertId(value);
            expertCategory.setCategoryId(categoryId);
            expertCategoryMapper.insert(expertCategory);
        }
    }

    @Transactional
    public void queryIsNull(String expertId) {
        excelMapper.queryIsNull(expertId);
    }

    @Transactional
    public void readWord(String result) {
        // 使用正则表达式提取姓名、性别、出生年月、工作单位、手机
//        String birthDate = extractInfo(result, "出生年月\\s*(\\S+)");
//        String school = extractInfo(result, "毕业学校\\s*(\\S+)");
        String education = extractInfo(result, "最高学历\\s*(\\S+)");
        String name = extractInfo(result, "姓\\s*名\\s*(\\S+)");
        String gender = extractInfo(result, "性别\\s*(\\S+)");
        String workUnit = extractInfo(result, "工作单位\\s*(\\S+)");
        String phone = extractInfo(result, "手机：(\\d{11})");
        String major = extractInfo(result, "专业\\s*(\\S+)");
        Date currentTime = new Date();
        if (gender.equals("男")) {
            gender = "1";
        } else if (gender.equals("女")) {
            gender = "2";
        }
        String educationId = excelMapper.queryEducationId(education);
        if (educationId != null) {
            Expert expert = new Expert();
            expert.setId(IdGen.uuid());
            expert.setApplyDate(currentTime);
            expert.setName(name);
            expert.setSex(gender);
            expert.setOrgName(workUnit);
            expert.setMobile(phone);
            expert.setStudied(major);
            expert.setStatus("1");
            expert.setEducationId(educationId);
//            expertMapper.insert(expert);
        } else {
            log.error("===>{}", "查询专家学历结果为空！！！");
            baseEducationService.create(education, 1000, "");
            String educationIds = excelMapper.queryEducationId(education);
            log.info("===>{}", educationIds + "专家数据");
            Expert expert = new Expert();
            expert.setId(IdGen.uuid());
            expert.setApplyDate(currentTime);
            expert.setName(name);
            expert.setSex(gender);
            expert.setOrgName(workUnit);
            expert.setMobile(phone);
            expert.setStudied(major);
            expert.setStatus("1");
            expert.setEducationId(educationIds);
//            expertMapper.insert(expert);
        }
    }

    // 使用正则表达式提取信息的辅助方法
    private static String extractInfo(String input, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return "";
        }
    }

}

