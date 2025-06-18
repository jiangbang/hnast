package com.glface.modules.service;

import com.glface.base.utils.StringUtils;
import com.glface.common.exeception.ServiceException;
import com.glface.common.utils.SpringContextUtil;
import com.glface.modules.model.*;
import com.glface.modules.model.json.*;
import com.glface.modules.utils.ProjectNodeEnum;
import com.glface.modules.utils.ProjectStatusEnum;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities;
import org.jsoup.select.Elements;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.annotation.Resource;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;

import static com.glface.common.web.ApiCode.PROJECT_FTL_NOTEXIST;
import static com.glface.common.web.ApiCode.PROJECT_NOT_EXIST;

@Slf4j
@Service
@Transactional(readOnly = true)
public class ProjectJsonService {
    @Resource
    private ProjectService projectService;
    @Resource
    private ProjectOrgService orgService;
    @Resource
    private ProjectPlanTypeService projectPlanTypeService;
    @Resource
    private ProjectContentService contentService;
    @Resource
    private ProjectStageService stageService;
    @Resource
    private ProjectFundsService fundsService;
    @Resource
    private ProjectProcessService projectProcessService;

    public ProjectJson getProjectJson(String projectId) {
        ProjectJson projectJson = new ProjectJson();
        if (StringUtils.isBlank(projectId)) {
            return projectJson;
        }
        Project project = projectService.get(projectId);
        if (project == null) {
            throw new ServiceException(PROJECT_NOT_EXIST);
        }
        //查询申报单位信息
        String orgId = project.getOrgId();
        ProjectOrg projectOrg = null;
        if (StringUtils.isNotBlank(orgId)) {
            projectOrg = orgService.get(orgId);
        }
        //
        ProjectPlanType projectPlanType = projectPlanTypeService.get(project.getPlanTypeId());
        //查询项目内容
        ProjectContent projectContent = contentService.findByProjectId(projectId);
        //查询项目实施阶段 进度计划
        List<ProjectStage> dbStages = stageService.findByProjectId(projectId);
        //查询经费预算明细
        List<ProjectFunds> dbFunds = fundsService.findByProjectId(projectId);
        //执行过程
        //List<ProjectProcess> processList = projectProcessService.findByProjectId(projectId);
        List<ProjectProcess> processList = new ArrayList<>();
        ProjectProcess districtProcess = null;//区县评审
        ProjectProcess firstProcess = null;//初审
        ProjectProcess recommendProcess = null;//推荐审核
        ProjectProcess expertProcess = null;//专家评审
        for (ProjectProcess process : processList) {
            if (ProjectNodeEnum.DISTRICT_REVIEW.getValue().equals(process.getNodeValue())) {//区县审核
                if (districtProcess == null || process.getCreateDate().after(districtProcess.getCreateDate())) {
                    districtProcess = process;
                }
            } else if (ProjectNodeEnum.FIRST_REVIEW.getValue().equals(process.getNodeValue())) {//初审
                if (firstProcess == null || process.getCreateDate().after(firstProcess.getCreateDate())) {
                    firstProcess = process;
                }
            } else if (ProjectNodeEnum.RECOMMEND_REVIEW.getValue().equals(process.getNodeValue())) {//推荐审核
                if (recommendProcess == null || process.getCreateDate().after(recommendProcess.getCreateDate())) {
                    recommendProcess = process;
                }
            } else if (ProjectNodeEnum.EXPERT_REVIEW.getValue().equals(process.getNodeValue())) {//专家评审
                if (expertProcess == null || process.getCreateDate().after(expertProcess.getCreateDate())) {
                    expertProcess = process;
                }
            }
        }
        if (districtProcess != null) {
            if (firstProcess != null && firstProcess.getCreateDate().before(districtProcess.getCreateDate())) {
                firstProcess = null;
                recommendProcess = null;
                expertProcess = null;
            }
        }
        if (firstProcess != null && recommendProcess != null && recommendProcess.getCreateDate().before(firstProcess.getCreateDate())) {
            recommendProcess = null;
            expertProcess = null;
        }
        if (recommendProcess != null && expertProcess != null && expertProcess.getCreateDate().before(recommendProcess.getCreateDate())) {
            expertProcess = null;
        }
        //组装数据
        projectJson.setName(project.getName());
        projectJson.setCode(project.getCode());
        projectJson.setOrg(ProjectOrgJson.fromProjectOrg(projectOrg));
        projectJson.setPlanType(ProjectPlanTypeJson.fromProjectPlanType(projectPlanType));
        projectJson.setContent(ProjectContentJson.fromProjectContent(projectContent));
        projectJson.setApplyDate(project.getApplyDate());
        projectJson.setStartDate(project.getStartDate());
        projectJson.setEndDate(project.getEndDate());

        projectJson.setStages(ProjectStageJson.fromProjectStageList(dbStages));//实施阶段
        projectJson.setBudget(project.getBudget());
        projectJson.setFunds(project.getFunds());
        projectJson.setFundsList(ProjectFundsJson.fromProjectFundsList(dbFunds));//预算明细

        projectJson.setBank(project.getBank());
        projectJson.setCardNo(project.getCardNo());
        projectJson.setAccounts(project.getAccounts());

        //设置评审意见
        if (districtProcess != null) {
            ProjectStatusEnum statusEnum = ProjectStatusEnum.getProjectStatusEnumByValue(districtProcess.getResult());
            if (statusEnum != null) {
                projectJson.setDistrictResult(statusEnum.getShortLabel());
            }
            projectJson.setDistrictOpinion(districtProcess.getResultOpinion());
        }
        if (firstProcess != null) {
            ProjectStatusEnum statusEnum = ProjectStatusEnum.getProjectStatusEnumByValue(firstProcess.getResult());
            if (statusEnum != null) {
                projectJson.setFirstResult(statusEnum.getShortLabel());
            }
            projectJson.setFirstOpinion(firstProcess.getResultOpinion());
        }
        if (recommendProcess != null) {
            ProjectStatusEnum statusEnum = ProjectStatusEnum.getProjectStatusEnumByValue(recommendProcess.getResult());
            if (statusEnum != null) {
                projectJson.setRecommendResult(statusEnum.getShortLabel());
            }
            projectJson.setRecommendOpinion(recommendProcess.getResultOpinion());
        }
        if (expertProcess != null) {
            ProjectStatusEnum statusEnum = ProjectStatusEnum.getProjectStatusEnumByValue(expertProcess.getResult());
            if (statusEnum != null) {
                projectJson.setExpertResult(statusEnum.getShortLabel());
            }
            projectJson.setExpertOpinion(expertProcess.getResultOpinion());
        }
        return projectJson;
    }

    public String getHtmlContent(String id, boolean hasFix) throws ServiceException {
        ProjectJson projectJson = getProjectJson(id);
        List<ProjectPlanType> planTypeList = projectPlanTypeService.all();
        List<ProjectPlanTypeJson> planTypes = new ArrayList<>();
        for(ProjectPlanType planType:planTypeList){
            planTypes.add(ProjectPlanTypeJson.fromProjectPlanType(planType));
        }
        Map<String, Object> root = new HashMap<>();
        root.put("project", projectJson);
        root.put("planTypes", planTypes);
        StringWriter swContent = new StringWriter();
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_22);
        cfg.setDefaultEncoding("UTF-8");
        File reportFile = null;
        String fixDocSize = null;
        try {
            fixDocSize = SpringContextUtil.getProperty("myself.fixDocSize");
            reportFile = new File(Paths.get(SpringContextUtil.getProperty("myself.classpath"),"reports").toString());
            cfg.setDirectoryForTemplateLoading(reportFile);
            Template t = cfg.getTemplate("project.ftl");
            t.process(root, swContent);
        } catch (FileNotFoundException ex) {
            log.error(ex.getMessage(), ex);
            throw new ServiceException(PROJECT_FTL_NOTEXIST);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceException(e.getMessage());
        }

        Document.OutputSettings set = new Document.OutputSettings();
        set.escapeMode(Entities.EscapeMode.base);
        set.syntax(Document.OutputSettings.Syntax.xml);
        set.prettyPrint(false);
        Document doc = Jsoup.parse(swContent.toString());
        if (hasFix) {
            doc = fixDoc(doc,fixDocSize);
        }
        doc.outputSettings(set);
        return doc.toString();
    }

    private Document fixDoc(Document doc, String fixDocSizeStr) {
        int fixDocSize = 1080;
        if(StringUtils.isNotBlank(fixDocSizeStr)){
            fixDocSize = Integer.parseInt(fixDocSizeStr);
        }
        Elements els = doc.getElementsByClass("wrap1");
        for (int i = 0; i < els.size(); i++) {
            Element el = (Element) els.get(i);
            int size = el.text().trim().length();
            int len = size / fixDocSize + 1;
            int hen = 951 * len;
            if (len > 1) {
                hen += 12 * (len - 1);
            }
            el.attr("style", "height:" + hen + "px;");
        }
        return doc;
    }


    public byte[] genPdf(String id) throws ServiceException {
        String content = getHtmlContent(id, true);
        content = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">" + content;
        content = content.replaceAll("/pms/static/", "");
        // 处理未转义的&符号
        content = content.replaceAll("&(?![a-zA-Z]+;|#[0-9]+;|#x[0-9a-fA-F]+;)", "&amp;");

        ByteArrayOutputStream os = new ByteArrayOutputStream();//不需要路径，写入内存
        ByteArrayOutputStream shuiyinPdfOutputStream = new ByteArrayOutputStream();//不需要路径，写入内存
        try {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(content);

            // 字体处理
            configureFontsAndResources(renderer);

            String shuiyinPath = new File(Paths.get(SpringContextUtil.getProperty("myself.classpath"),"reports","shuiyin.png").toString()).getAbsolutePath();
            String simsunPath = new File(Paths.get(SpringContextUtil.getProperty("myself.classpath"),"fonts","SIMSUN.TTC").toString()).getAbsolutePath();
            renderer.layout();
            renderer.createPDF(os);
            os.flush();
            //添加水印
            BaseFont base = BaseFont.createFont(simsunPath + ",1", "Identity-H", true);
            PdfReader reader = new PdfReader(os.toByteArray());
            PdfStamper stamper = new PdfStamper(reader, shuiyinPdfOutputStream);
            int total = reader.getNumberOfPages() + 1;
            Image shuiyin = Image.getInstance(shuiyinPath);
            shuiyin.setAbsolutePosition(0.0F, 0.0F);
            char c = Character.MIN_VALUE;
            int rise = 0;
            for (int i = 1; i < total; i++) {
                rise = 400;
                PdfContentByte under = stamper.getOverContent(i);
                under.beginText();
                under.setFontAndSize(base, 12.0F);
                under.setTextMatrix(275.0F, 11.0F);
                under.showText("第"+ i + "页");
                under.endText();
                under.addImage(shuiyin);
                under.stroke();
            }
            stamper.close();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceException(e.getMessage());
        }
        return shuiyinPdfOutputStream.toByteArray();
    }

    private void configureFontsAndResources(ITextRenderer renderer) throws IOException {
        ITextFontResolver fontResolver = renderer.getFontResolver();

        String sourceHanSansSCPath = new File(Paths.get(SpringContextUtil.getProperty("myself.classpath"),"fonts","SourceHanSansSC-Regular-2.otf").toString()).getAbsolutePath();
        String simsunPath = new File(Paths.get(SpringContextUtil.getProperty("myself.classpath"),"fonts","SIMSUN.TTC").toString()).getAbsolutePath();
//        String wenQuanYiFontPath = new File(Paths.get(SpringContextUtil.getProperty("myself.classpath"),"fonts","wqy-microhei.ttc").toString()).getAbsolutePath();

        // 定义字体优先级列表（按全面性排序）
        List<String> fontPaths = Arrays.asList(
                simsunPath   ,         // 备选：宋体
                sourceHanSansSCPath          // 思源黑体
//                wenQuanYiFontPath     // 首选：开源全面中文字体
        );

        // 尝试加载可用的字体
        for (String fontPath : fontPaths) {
            File fontFile = new File(fontPath);
            if (fontFile.exists()) {
                try {
                    fontResolver.addFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                    log.info("成功加载字体: {}", fontPath);
                } catch (Exception e) {
                    log.warn("字体加载失败: {}", fontPath, e);
                }
            }
        }
        // 设置基础URL
        String staticPath = new File(Paths.get(SpringContextUtil.getProperty("myself.classpath"),"static").toString()).getPath();
        //解决图片相对路径问题
        String system = System.getProperty("os.name").toLowerCase();
        //renderer.getSharedContext().setBaseURL("file:/E:/workspace/java/xmgl2022/code/hnast/hnast-api-admin/target/classes/static/");
        if (system!=null&&system.indexOf("windows") >= 0) {
            staticPath = staticPath.replaceAll("\\\\", "\\/");
            String f = "file:/" + staticPath + "/";
            renderer.getSharedContext().setBaseURL(f);
        } else {
            renderer.getSharedContext().setBaseURL("file:" + staticPath + "/");
        }
    }
}
