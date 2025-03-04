package com.glface.modules.controller;

import com.glface.base.bean.DynamicBean;
import com.glface.base.bean.R;
import com.glface.base.utils.StringUtils;
import com.glface.common.exeception.ServiceException;
import com.glface.common.utils.OfficeUtil;
import com.glface.common.web.ApiCode;
import com.glface.modules.model.FileInfo;
import com.glface.modules.service.FileService;
import com.glface.modules.service.PreviewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;

import static com.glface.common.web.ApiCode.PREVIEW_FILE_ERROR;

@Slf4j
@RestController
@RequestMapping("/file")
public class FileController {

    @Resource
    private FileService fileService;

    @Resource
    private PreviewService previewService;

    /**
     * 上传接口，登录用户可用
     * @param file
     * @return
     */
    @PreAuthorize("hasAuthority('permission:user:view')")
    @PostMapping("/upload")
    public R<Object> upload(@RequestParam("file") MultipartFile file) throws Exception{
        FileInfo fileInfo = fileService.uploadFile(file);
        //构造返回数据
        Object object = new DynamicBean.Builder().setPV("fileId", fileInfo.getId())
                .build().getObject();
        return R.ok(object);
    }

    @PreAuthorize("hasAuthority('permission:user:view')")
    @PostMapping("/info")
    public R<Object> info(String id){
        FileInfo fileInfo = fileService.get(id);
        if(fileInfo==null){
            return R.fail(ApiCode.FILE_NOT_EXIST.getMsg());
        }
        //构造返回数据
        Object object = new DynamicBean.Builder().setPV("id", fileInfo.getId())
                .setPV("name", fileInfo.getName())
                .build().getObject();
        return R.ok(object);
    }

    /**
     * 文件下载
     * @param id      文件id
     * @param request
     * @param response
     */
    @PostMapping("/down")
    public void downLoad(String id, HttpServletRequest request, HttpServletResponse response){
        if(StringUtils.isBlank(id)){
            return;
        }
        FileInfo fileInfo = fileService.get(id);
        if(fileInfo==null){
            return;
        }
        BufferedInputStream bufferedInputStream = null;
        ServletOutputStream outputStream = null;
        try {
            response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
            response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(fileInfo.getName(), "UTF-8"));
            response.setCharacterEncoding("UTF-8");
            File file = new File(fileInfo.getAbsoluteAddress());
            bufferedInputStream =new BufferedInputStream( new FileInputStream(file));
            outputStream = response.getOutputStream();
            writeBytes(bufferedInputStream, outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(outputStream!=null){
                    outputStream.flush();
                    outputStream.close();
                }
                if(bufferedInputStream!=null){
                    bufferedInputStream.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 预览(文件下载)
     * @param id      文件id
     * @param request
     * @param response
     */
    @PostMapping("/preview")
    public void preview(String id, HttpServletRequest request, HttpServletResponse response){
        if(StringUtils.isBlank(id)){
            return;
        }
        FileInfo fileInfo = fileService.get(id);
        if(fileInfo==null){
            return;
        }
        File file = previewService.preview(id);
        if(file==null){
            throw new ServiceException(PREVIEW_FILE_ERROR);
        }
        BufferedInputStream bufferedInputStream = null;
        ServletOutputStream outputStream = null;
        try {
            response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
            response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(fileInfo.getName(), "UTF-8"));
            response.setCharacterEncoding("UTF-8");
            bufferedInputStream =new BufferedInputStream( new FileInputStream(file));
            outputStream = response.getOutputStream();
            writeBytes(bufferedInputStream, outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(outputStream!=null){
                    outputStream.flush();
                    outputStream.close();
                }
                if(bufferedInputStream!=null){
                    bufferedInputStream.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @PostMapping("/testHtmlToPdf")
    public R<Object> testHtmlToPdf(String html) throws Exception{
        OfficeUtil.htmlToPdf(html.getBytes(),"E:\\tmp\\html.pdf");
        return R.ok();
    }

    private void writeBytes(InputStream in, OutputStream out) throws IOException {
        byte[] buffer= new byte[1024];
        int length = -1;
        while ((length = in.read(buffer))!=-1){
            out.write(buffer,0,length);
        }
    }
}
