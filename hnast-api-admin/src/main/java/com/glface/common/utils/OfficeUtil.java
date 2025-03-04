package com.glface.common.utils;
import cn.hutool.system.OsInfo;
import cn.hutool.system.SystemUtil;
import com.aspose.cad.imageoptions.CadRasterizationOptions;
import com.aspose.cad.imageoptions.PdfCompliance;
import com.aspose.cad.imageoptions.PdfDocumentOptions;
import com.aspose.cad.imageoptions.PdfOptions;
import com.aspose.cells.Workbook;
import com.aspose.slides.Presentation;
import com.aspose.words.Document;
import com.aspose.words.FontSettings;
import com.aspose.words.SaveFormat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.util.Date;

/**
 * 将Word转为PDF工具类
 */
@Slf4j
public class OfficeUtil {
    /**
     * 获取授权码防止出现水印
     */
    public static boolean getLicense(String type) {
        boolean result = false;
        try {
            InputStream is = new ClassPathResource("aspose-license.xml").getInputStream();
            if (type.equals("excel")) {
                com.aspose.cells.License aposeLic = new com.aspose.cells.License();
                aposeLic.setLicense(is);
            } else if (type.equals("ppt")) {
                com.aspose.slides.License aposeLic = new com.aspose.slides.License();
                aposeLic.setLicense(is);
            } else {
                com.aspose.words.License aposeLic = new com.aspose.words.License();
                aposeLic.setLicense(is);
            }
            //linux 下设置字体文件目录 /usr/share/fonts
            OsInfo osInfo = SystemUtil.getOsInfo();
            if(osInfo.isLinux()){
                FontSettings.getDefaultInstance().setFontsFolder
                        (File.separator + "usr"
                                + File.separator + "share" + File.separator + "fonts", true);
            }
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 将word转为pdf
     * @param docPath  word路径
     * @param pdfPath  pdf路径
     * @throws IOException
     */
    public static void docToPdf(String docPath, String pdfPath) throws Exception {
        if (!getLicense("")) {
            return;
        }
        FileOutputStream os=null;
        try {
            long old = System.currentTimeMillis();
            File file = new File(pdfPath);
            os = new FileOutputStream(file);
            Document doc = new Document(docPath);
            doc.save(os, SaveFormat.PDF);//全面支持DOC, DOCX, OOXML, RTF HTML, OpenDocument, PDF, EPUB, XPS, SWF 相互转换
            long now = System.currentTimeMillis();
            log.info("共耗时：%d秒",((now - old) / 1000.0));
        } catch (Exception e) {
            throw e;
        }finally {
            if(os!=null) os.close();
        }
    }
    public static void excelToPdf(String address,String toAddress) throws Exception {
        if (!getLicense("excel")) {
            return;
        }
        FileOutputStream fileOS=null;
        try {
            long old = System.currentTimeMillis();
            Workbook wb = new Workbook(address);// 原始excel路径
//            com.aspose.cells.PdfSaveOptions pdfSaveOptions = new com.aspose.cells.PdfSaveOptions();
//            pdfSaveOptions.setOnePagePerSheet(true);
//            int[] autoDrawSheets={3};
//            //当excel中对应的sheet页宽度太大时，在PDF中会拆断并分页。此处等比缩放。
////            autoDraw(wb,autoDrawSheets);
//            int[] showSheets={0};
//            //隐藏workbook中不需要的sheet页。
//            printSheetPage(wb,showSheets);

            File pdfFile = new File(toAddress);// 输出路径
            fileOS = new FileOutputStream(pdfFile);
            //com.aspose.cells.SaveFormat.PDF
            wb.save(fileOS, com.aspose.cells.SaveFormat.PDF);
            fileOS.flush();
            long now = System.currentTimeMillis();
            log.info("共耗时：%d秒",((now - old) / 1000.0));
        } catch (Exception e) {
            throw e;
        }finally {
            if(fileOS!=null)  fileOS.close();
        }
    }

    /**
     * 设置打印的sheet 自动拉伸比例
     * @param wb
     * @param page 自动拉伸的页的sheet数组
     */
    public static void autoDraw(Workbook wb,int[] page){
        if(null!=page&&page.length>0){
            for (int i = 0; i < page.length; i++) {
                wb.getWorksheets().get(i).getHorizontalPageBreaks().clear();
                wb.getWorksheets().get(i).getVerticalPageBreaks().clear();
            }
        }
    }


    /**
     * 隐藏workbook中不需要的sheet页。
     * @param wb
     * @param page 显示页的sheet数组
     */
    public static void printSheetPage(Workbook wb,int[] page){
        for (int i= 1; i < wb.getWorksheets().getCount(); i++)  {
            wb.getWorksheets().get(i).setVisible(false);
        }
        if(null==page||page.length==0){
            wb.getWorksheets().get(0).setVisible(true);
        }else{
            for (int i = 0; i < page.length; i++) {
                wb.getWorksheets().get(i).setVisible(true);
            }
        }
    }
    // ppt to pdf
    public static void pptToPdf(String oldUrl, String pdfUrl) throws Exception {
        if (!getLicense("ppt")) {
            return;
        }
        try {
            long old = System.currentTimeMillis();
            File file = new File(pdfUrl);// 输出pdf路径
            Presentation pres = new Presentation(oldUrl);// 输入pdf路径
            FileOutputStream fileOS = new FileOutputStream(file);
            pres.save(fileOS, com.aspose.slides.SaveFormat.Pdf);
            fileOS.close();

            long now = System.currentTimeMillis();
            log.info("共耗时：%d秒",((now - old) / 1000.0));
        } catch (Exception e) {
            throw e;
        }
    }
    //cad dwg 文件转 pdf
    public static void dwg2pdf(String oldUrl, String pdfUrl) {
        if (!getLicense("")) {
            return;
        }
        // TODO Auto-generated method stub
        com.aspose.cad.Image objImage = com.aspose.cad.Image.load(oldUrl);
        // Create an instance of PdfOptions
        PdfOptions pdfOptions = new PdfOptions();
        pdfOptions.setVectorRasterizationOptions(new CadRasterizationOptions());

        pdfOptions.setCorePdfOptions(new PdfDocumentOptions());
        pdfOptions.getCorePdfOptions().setCompliance(PdfCompliance.PdfA1a);
        long curTime = new Date().getTime();
        // 初始化pdf文件 dataDir（本地） urlPrefix:http
        objImage.save(pdfUrl, pdfOptions);
    }

    public static void htmlToPdf(byte[] html,String pdfPath) throws Exception{
        if (!getLicense("")) {
            return;
        }
        FileOutputStream os=null;
        try {
            long old = System.currentTimeMillis();
            File file = new File(pdfPath);
            os = new FileOutputStream(file);
            Document doc = new Document(new ByteArrayInputStream(html));
            doc.save(os, SaveFormat.PDF);//全面支持DOC, DOCX, OOXML, RTF HTML, OpenDocument, PDF, EPUB, XPS, SWF 相互转换
            long now = System.currentTimeMillis();
            log.info("共耗时：%d秒",((now - old) / 1000.0));
        } catch (Exception e) {
            throw e;
        }finally {
            if(os!=null) os.close();
        }
    }
}
