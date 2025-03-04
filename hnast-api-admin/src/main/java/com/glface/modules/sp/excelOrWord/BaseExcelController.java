package com.glface.modules.sp.excelOrWord;

/**
 * Coovright (C), 2020-2023
 * FileName: ExcelController
 * Author: wanluixng
 * Date: 2023/4/21 10:03
 * Description:
 * History:
 * <author>  <time>  <version> <desc>
 * 作者姓名   修改时间    版本号    描述
 */
import com.alibaba.excel.EasyExcel;
import com.glface.base.bean.DynamicBean;
import com.glface.base.bean.R;
import com.glface.base.utils.DateUtils;
import com.glface.base.utils.IdGen;
import com.glface.base.utils.StringUtils;
import com.glface.modules.sp.mapper.ExpertMapper;
import com.glface.modules.sp.model.*;
import com.glface.modules.sp.service.ExpertService;
import com.glface.modules.utils.ExpertStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequestMapping("/excel")
@RestController
@Slf4j
public class BaseExcelController {
    @Autowired
    ExcelService excelService;

    private InputStream inputStream = null;
    @Autowired
    ExcelMapper excelMapper;

    @Autowired
    ExpertMapper expertMapper;

    @Autowired
    ExpertService expertService;

    @RequestMapping("/readword")
    public R<Object> readWord(MultipartFile file) throws IOException{
        // 获取文件名，目的是根据文件名字获取文件后缀
        String originalFilename = file.getOriginalFilename();
        // 获取后缀下标
        int i = originalFilename.lastIndexOf(".");
        // 截取文件后缀
        String suffix = originalFilename.substring(i);
        inputStream = file.getInputStream();
        // 调用工具类
        String result = WordUtil.readWord(suffix, inputStream);
        excelService.readWord(result);
        // 返回结果
        return R.ok(result);
    }


    @RequestMapping("/exprot")
    public R Excel(@RequestParam("file")MultipartFile file) throws IOException {
        InputStream inputStream = file.getInputStream();
        List<Excel> reqCustomerDailyImports = EasyExcel.read(inputStream)
                .head(Excel.class)
                .registerConverter(new StringConverter())
                // 注册监听器，可以在这里校验字段
                .registerReadListener(new CustomerDailyImportListener())
                // 设置sheet,默认读取第一个
                .sheet(0)
                .doReadSync();
        excelService.saveExpert(reqCustomerDailyImports);
        return R.ok(reqCustomerDailyImports);
    }

    @RequestMapping(value = "/exprotAll")
    public R<Object> ExprotAll(@RequestParam(value="ids") String[] ids,
                          @RequestParam(value = "categoryId") String categoryId) {
        excelService.ExprotAll(ids, categoryId);
        return R.ok();
    }

    @RequestMapping(value = "/searchExpert")
    public R<Object> searchExpert(String name, String orgName, String mobile,String status,
                                  @RequestParam(value = "page", defaultValue = "1") int pageNo,
                                  @RequestParam(value = "limit", defaultValue = "10") int limit) {

        //取入库 出库专家
        List<Expert> expertList = expertService.searchByStatus(ExpertStatusEnum.AGREE.getValue());
        expertList.addAll(expertService.searchByStatus(ExpertStatusEnum.OUT.getValue()));

        //依据搜索条件过滤
        List<Expert> filterList = expertList;
        if(StringUtils.isNotBlank(name)){
            List<Expert> tmpList = new ArrayList<>();
            for(Expert expert:filterList){
                if(expert.getName().contains(name)){
                    tmpList.add(expert);
                }
            }
            filterList = tmpList;
        }
        if(StringUtils.isNotBlank(orgName)){
            List<Expert> tmpList = new ArrayList<>();
            for(Expert expert:filterList){
                if(StringUtils.isNotBlank(expert.getOrgName())&&expert.getOrgName().contains(orgName)){
                    tmpList.add(expert);
                }
            }
            filterList = tmpList;
        }
        if(StringUtils.isNotBlank(mobile)){
            List<Expert> tmpList = new ArrayList<>();
            for(Expert expert:filterList){
                if(expert.getMobile().contains(mobile)){
                    tmpList.add(expert);
                }
            }
            filterList = tmpList;
        }
        if(StringUtils.isNotBlank(status)){
            List<Expert> tmpList = new ArrayList<>();
            for(Expert expert:filterList){
                if(status.equals(expert.getStatus())){
                    tmpList.add(expert);
                }
            }
            filterList = tmpList;
        }

        //排序,根据申报时间
        Collections.sort(filterList, new Comparator<Expert>() {
            @Override
            public int compare(Expert o1, Expert o2) {
                return o2.getApplyDate().compareTo(o1.getApplyDate());
            }
        });

        //内存分页
        List<Expert> pageList = new ArrayList<>();
        int start = (pageNo-1)*limit;
        int end = start + limit-1;
        for(int i=start;i>=0&&i<=end&&i<filterList.size();i++){
            System.out.println();
            pageList.add(filterList.get(i));
        }
        // 构造返回数据
        List<Object> results = new ArrayList<>();
        for (Expert expert : pageList) {
            Object o = new DynamicBean.Builder()
                    .setPV("id", expert.getId())
                    .setPV("applyDate", DateUtils.formatDate(expert.getApplyDate(), "yyyy-MM-dd HH:mm:ss"))
                    .setPV("name",  expert.getName())
                    .setPV("orgName", expert.getOrgName())
                    .setPV("star", expert.getStar())
                    .setPV("status", expert.getStatus())
                    .build().getObject();
            results.add(o);
        }

        Object data = new DynamicBean.Builder()
                .setPV("total", filterList.size())
                .setPV("list", results, List.class)
                .build().getObject();
        return R.ok(data);
    }
}
