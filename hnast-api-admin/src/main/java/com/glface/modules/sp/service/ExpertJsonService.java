package com.glface.modules.sp.service;

import com.glface.base.utils.StringUtils;
import com.glface.common.exeception.ServiceException;
import com.glface.common.utils.SpringContextUtil;
import com.glface.modules.model.*;
import com.glface.modules.model.json.*;
import com.glface.modules.service.*;
import com.glface.modules.sp.model.Expert;
import com.glface.modules.sp.model.json.*;
import com.glface.modules.utils.ProjectNodeEnum;
import com.glface.modules.utils.ProjectStatusEnum;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.StringWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.glface.common.web.ApiCode.*;

@Slf4j
@Service
@Transactional(readOnly = true)
public class ExpertJsonService {
    @Resource
    private ExpertService expertService;
    @Resource
    private BaseEducationService baseEducationService;
    @Resource
    private BasePartyService basePartyService;
    @Resource
    private BaseDegreeService baseDegreeService;
    @Resource
    private BaseMajorCategoryService baseMajorCategoryService;
    @Resource
    private BaseMajorService baseMajorService;
    @Resource
    private BasePositionalService basePositionalService;
    @Resource
    private ExpertExtService expertExtService;
    @Resource
    private ExpertCategoryService expertCategoryService;

    public ExpertJson getExpertJson(String expertId) {
        ExpertJson projectJson = new ExpertJson();
        if (StringUtils.isBlank(expertId)) {
            return projectJson;
        }
        Expert project = expertService.get(expertId);
        if (project == null) {
            throw new ServiceException(SP_EXPERT_NOT_EXIST);
        }

        //组装数据
        projectJson.setName(project.getName());
        projectJson.setApplyDate(project.getApplyDate());
        projectJson.setBirthday(project.getBirthday());
        projectJson.setEmail(project.getEmail());
        projectJson.setExpertExt(ExpertExtJson.fromExpertExt(expertExtService.get(project.getExtId())));
        projectJson.setDegree(DegreeJson.fromBaseDegree(baseDegreeService.get(project.getDegreeId())));
        projectJson.setEducation(EducationJson.fromBaseEducation(baseEducationService.get(project.getEducationId())));
        projectJson.setMajorCategory(MajorCategoryJson.fromBaseMajorCategory(baseMajorCategoryService.get(project.getMajorCategoryId())));
        projectJson.setPositional(PositionalJson.fromBasePositional(basePositionalService.get(project.getPositionalId())));
        projectJson.setParty(PartyJson.fromBaseParty(basePartyService.get(project.getParties())));

        projectJson.setIdentityCard(project.getIdentityCard());
        projectJson.setEmail(project.getEmail());
        projectJson.setJob(project.getJob());
        projectJson.setMobile(project.getMobile());
        projectJson.setOrgName(project.getOrgName());
        projectJson.setPictureFileId(project.getPictureFileId());
        projectJson.setAddress(project.getAddress());
        projectJson.setPost(project.getPost());
        projectJson.setQq(project.getQq());
        projectJson.setSex(project.getSex());
        projectJson.setStar(project.getStar());
        projectJson.setStatus(project.getStatus());
        projectJson.setStudied(project.getStudied());
        projectJson.setWx(project.getWx());
        projectJson.setSpecialty(project.getSpecialty());
        projectJson.setCategories(expertCategoryService.findByExpertId(expertId));
        return projectJson;
    }
}
