package com.glface.modules.sp.excelOrWord;

import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Word 操作工具类
 */
public class WordUtil {
    // 定义静态的文件后缀
    public static final String SUFFIX_DOC = ".doc";
    public static final String SUFFIX_DOCX = ".docx";

    /**
     * 读取 Word 入口方法，根据后缀，调用方法
     * @param suffix 文件后缀
     * @param inputStream 文件输入流
     * @return
     */
    public static String readWord(String suffix, InputStream inputStream) throws IOException {
        try{
            // docx 类型
            if (SUFFIX_DOCX.equals(suffix)) {
                return readDocx(inputStream);
                // doc 类型
            } else if (SUFFIX_DOC.equals(suffix)) {
                return readDoc(inputStream);
            } else {
                return "";
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            inputStream.close();
        }
        return "";
    }

    /**
     * 读取 doc 类型，使用 WordExtractor 对象，传递输入流
     *
     * @param inputStream
     * @return
     */
    private static String readDoc(InputStream inputStream) {
        try {
            String content = "";
            WordExtractor ex = new WordExtractor(inputStream);
            content = ex.getText();
            return content;
        } catch (Exception e) {
            return null;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    return null;
                }
            }
        }
    }

    /**
     * 读取 docx 类型，使用 XWPFDocument 对象，传递输入流
     *
     * @param inputStream
     * @return
     */
    private static String readDocx(InputStream inputStream) {
        try {
            String content = "";
            XWPFDocument xdoc = new XWPFDocument(inputStream);
            XWPFWordExtractor extractor = new XWPFWordExtractor(xdoc);
            content = extractor.getText();
            extractor.close();

            return content;
        } catch (Exception e) {
            return null;
        }
    }
}

