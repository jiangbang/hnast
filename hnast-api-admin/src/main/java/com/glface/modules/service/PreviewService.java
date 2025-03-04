package com.glface.modules.service;

import com.glface.base.utils.StringUtils;
import com.glface.common.exeception.ServiceException;
import com.glface.common.utils.OfficeUtil;
import com.glface.modules.model.FileInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.io.File;
import java.nio.file.Paths;
import java.util.Random;

import static com.glface.common.web.ApiCode.*;

/**
 * 预览服务
 */
@Slf4j
@Service
public class PreviewService {

    private final int maxSize=100;//大于100mb不允许预览
    private final String suffixs[]=new String[]{".doc",".docx",".pdf",".xls",".xlsx",".jpg",".jpeg",".png",".bmp"};//支持的格式
    private final static String tmpSuffix = ".tmp";//转换时会先生成临时文件

    @Value("${myself.filePreviewDir}")
    private String filePreviewDir;
    @Resource
    private FileService fileService;

    /**
     * 1 验证是否支持此格式
     * 2 验证文件大小
     * 3 查找是否已生成预览文件
     * 4 如果是图片或pdf则直接返回 否则转换为pdf
     * @param fileId
     */
    public File preview(String fileId){
         FileInfo fileInfo = fileService.get(fileId);
         if(fileInfo==null){
             return null;
         }
         String path = fileInfo.getAbsoluteAddress();
         String suffix = fileInfo.getSuffix();
        if(StringUtils.isBlank(suffix)){
            throw new ServiceException(PREVIEW_SUFFIX_NOT_SUPPORT);
        }
        suffix = suffix.toLowerCase();
         //是否支持的格式
         boolean isSupport = false;
         for(String s:suffixs){
             if(s.equals(suffix)){
                 isSupport = true;
                 break;
             }
         }
         if(!isSupport){
             throw new ServiceException(PREVIEW_SUFFIX_NOT_SUPPORT);
         }
         //大于100m不允许预览
        File file = new File(path);
         String saveName = file.getName();
         saveName = delSuffixName(saveName);//去掉后缀
         if(!file.exists()){
             throw new ServiceException(PREVIEW_FILE_NOT_EXIST);
         }
        if(maxSize < file.length()/1024/1024 ){
            throw new ServiceException(PREVIEW_FILE_LENGTH_OUT);
        }
        //查找是否已生成预览文件 //如果是图片或pdf则不用转换
        File previewFile = null;
        if(suffix.endsWith("jpg")||suffix.endsWith("jpeg")||suffix.endsWith("png")||suffix.endsWith("bmp")||suffix.endsWith("pdf")){
            previewFile = file;
        }else {
            File previewDir = new File(Paths.get(filePreviewDir, fileInfo.getId()).toString());
            if (previewDir.exists()) {
                File[] files = previewDir.listFiles();
                for (File f : files) {
                    if (f.isDirectory()) {
                        continue;
                    }
                    String name = f.getName();
                    if (name.endsWith(tmpSuffix)) {//临时文件
                        continue;
                    }
                    if (name.startsWith(saveName)) {
                        previewFile = f;
                        break;
                    }
                }
            }
        }
        if(previewFile==null){//需要生成预览文件
            try {
                String previewPath = createPreviewFile(fileInfo);
                previewFile = new File(previewPath);
            } catch (Exception e) {
                log.error("预览文件转换异常",e);
                throw new ServiceException(PREVIEW_FILE_TRANSFORM_ERROR);
            }
        }
        return previewFile;
     }

     private String createPreviewFile(FileInfo fileInfo) throws Exception{
         String path = fileInfo.getAbsoluteAddress();
         String suffix = fileInfo.getSuffix();
         suffix = suffix.toLowerCase();
         File file = new File(path);
         String saveName = file.getName();
         saveName = delSuffixName(saveName);//去掉后缀
         File previewDir = new File(Paths.get(filePreviewDir, fileInfo.getId()).toString());
         if(!previewDir.exists()){
             previewDir.mkdirs();
         }
         String outPath = Paths.get(filePreviewDir, fileInfo.getId(),saveName+String.format("%04d",new Random().nextInt(9999))).toString();
         if(suffix.endsWith("doc")||suffix.endsWith("docx")){
             String outPathTmp = outPath+".tmp";
             OfficeUtil.docToPdf(path,outPathTmp);
             //将临时文件转换为正式文件
             File tmpFile = new File(outPathTmp);
             String previewPath = outPath+".pdf";
             File previewFile = new File(previewPath);
             tmpFile.renameTo(previewFile);
             return previewPath;
         }else if(suffix.endsWith("xls")||suffix.endsWith("xlsx")){
             String outPathTmp = outPath+".tmp";
             OfficeUtil.excelToPdf(path,outPathTmp);
             //将临时文件转换为正式文件
             File tmpFile = new File(outPathTmp);
             String previewPath = outPath+".pdf";
             File previewFile = new File(previewPath);
             tmpFile.renameTo(previewFile);
             return previewPath;
         }
         return null;
     }

     private static String delSuffixName(String fileName){
         int index = fileName.lastIndexOf(".");
         if(index>0){
             return fileName.substring(0,index);
         }
         return fileName;
     }
}
