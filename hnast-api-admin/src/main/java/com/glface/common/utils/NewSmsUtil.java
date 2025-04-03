package com.glface.common.utils;

import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

//如果JDK版本低于1.8,请使用三方库提供Base64类
//import org.apache.commons.codec.binary.Base64;

public class NewSmsUtil {

    public static void main(String[] args) {
//        JSONObject result = sendCode("18229997446", 123455);

        String orgName = "长沙市麓谷信息港";
        String content = "[" +"市本级科协" + " , " +orgName+"]有待实施的项目，请您登录长沙科协项目管理系统查看相关信息。";
        JSONObject result = send("18229997446",  content);
        System.out.println(result);
    }


    /**
     * 发送短信
     * @param content
     * @param phone
     * @return
     */
    public static JSONObject send(String phone, String content) {
        JSONObject jsonObject = new JSONObject();
        String result = "";
        BufferedReader in = null;
        try {//LKSDK0006594 zh9527@
            String formatContent = URLEncoder.encode(content, "GBK");
            String formatParam = "CorpID=LKSDK0006594&Pwd=zh9527@&Mobile=" + phone + "&Content=" + formatContent
                    + "&Cell=&SendTime=";

            String urlNameString = "https://sdk1.mb345.com:6789/ws/BatchSend2.aspx?" + formatParam;
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            jsonObject.put("code", "000000");
            jsonObject.put("description", "Success");
            jsonObject.put("result", result);
        } catch (Exception e) {
            jsonObject.put("code", "E0001");
            jsonObject.put("description", e.getMessage());
            jsonObject.put("result", e.getMessage());
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return jsonObject;
    }

    /**
     * 发送短信
     *
     * @param code
     * @param phone
     * @return
     */
    public static JSONObject sendCode(String phone, Integer code) {
        JSONObject jsonObject = new JSONObject();
        String result = "";
        BufferedReader in = null;
        try {//LKSDK0006594 zh9527@
            String content = String.format("您的注册短信验证码为 %d (泄露有风险),有效时间3分钟【长沙市科技活动中心】", code);
            String formatContent = URLEncoder.encode(content, "GBK");
            String formatParam = "CorpID=LKSDK0006594&Pwd=zh9527@&Mobile=" + phone + "&Content=" + formatContent
                    + "&Cell=&SendTime=";

            String urlNameString = "https://sdk1.mb345.com:6789/ws/BatchSend2.aspx?" + formatParam;
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            jsonObject.put("code", "000000");
            jsonObject.put("description", "Success");
            jsonObject.put("result", result);
        } catch (Exception e) {
            jsonObject.put("code", "E0001");
            jsonObject.put("description", e.getMessage());
            jsonObject.put("result", e.getMessage());
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return jsonObject;
    }
}

