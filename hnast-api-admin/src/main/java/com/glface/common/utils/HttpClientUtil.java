package com.glface.common.utils;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.http.protocol.HttpCoreContext.HTTP_REQUEST;
import static org.apache.http.protocol.HttpCoreContext.HTTP_TARGET_HOST;

/**
 * Created by Administrator on 2018/12/11 0011.
 */
public class HttpClientUtil {
    private final static Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);

    /**
     * 处理doget请求
     *
     * @param url
     * @return
     */
    public static JSONObject doGet(String url, Map urlParams) {
        url = buildUrl(url, urlParams);
        CloseableHttpClient httpclient = HttpClients.createDefault();
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(10000).build();//设置请求和传输超时时间
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(requestConfig);
        JSONObject jsonObject = null;
        CloseableHttpResponse response = null;
        try {
            response = httpclient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String result = EntityUtils.toString(entity);
                if (result.startsWith("{")) {
                    try {
                        jsonObject = JSONObject.parseObject(result);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            logger.warn(e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return jsonObject;

    }

    /**
     * 处理doget请求
     *
     * @param url
     * @return
     */
    public static String doGetStr(String url, HashMap urlParams) {
        url = buildUrl(url, urlParams);

        CloseableHttpClient httpclient = HttpClients.createDefault();
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(10000).build();//设置请求和传输超时时间
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(requestConfig);

        CloseableHttpResponse response = null;
        try {
            response = httpclient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String result = EntityUtils.toString(entity);
                return result;
            }
        } catch (IOException e) {
            logger.warn(e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;

    }

    public static JSONObject doGet(String url, HashMap<String, String> header, HashMap<String, String> urlParams) {
        url = buildUrl(url, urlParams);

        CloseableHttpClient httpclient = HttpClients.createDefault();
        if (url.startsWith("https://")) {
            sslClient(httpclient);
        }
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(10000).build();//设置请求和传输超时时间
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(requestConfig);
        if (header != null) {
            for (String key : header.keySet()) {
                String value = header.get(key);
                httpGet.addHeader(key, value);
            }
        }

        JSONObject jsonObject = null;
        try {
            CloseableHttpResponse response = httpclient.execute(httpGet);
            String result = EntityUtils.toString(response.getEntity(), "utf-8");
            try {
                jsonObject = JSONObject.parseObject(result);
            } catch (JSONException e) {
                e.printStackTrace();
                logger.info(result);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            logger.warn(e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return jsonObject;
    }

    /**
     * 处理doget请求
     *
     * @param url
     * @return
     */
    public static String getRedirectInfo(String url, HashMap hashMap) {
        if (hashMap != null) {
            StringBuilder stringBuilder = new StringBuilder();
            for (Object key : hashMap.keySet()) {
                Object value = hashMap.get(key);
                try {
                    stringBuilder.append("&").append(key).append("=").append(URLEncoder.encode(value + "", "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    logger.warn(e.getMessage());
                }
            }
            if (stringBuilder.length() > 0) {
                stringBuilder.deleteCharAt(0);
            }
            if (!url.endsWith("?")) {
                url = url + "?";
            }
            url = url + stringBuilder.toString();
        }

        CloseableHttpClient httpclient = HttpClients.createDefault();
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(10000).build();//设置请求和传输超时时间
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(requestConfig);

        HttpContext httpContext = new BasicHttpContext();
        try {
            //将HttpContext对象作为参数传给execute()方法,则HttpClient会把请求响应交互过程中的状态信息存储在HttpContext中
            HttpResponse response = httpclient.execute(httpGet, httpContext);
            //获取重定向之后的主机地址信息,即"http://127.0.0.1:8088"
            HttpHost targetHost = (HttpHost)httpContext.getAttribute(HTTP_TARGET_HOST);
            //获取实际的请求对象的URI,即重定向之后的"/blog/admin/login.jsp"
            HttpUriRequest realRequest = (HttpUriRequest)httpContext.getAttribute(HTTP_REQUEST);
            return targetHost.toString()  + realRequest.getURI().toString();
        } catch (IOException e) {
            logger.warn(e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;

    }

    public static String getRedirectInfo(String url) {

        CloseableHttpClient httpclient = HttpClients.createDefault();
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(10000).build();//设置请求和传输超时时间
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(requestConfig);

        HttpContext httpContext = new BasicHttpContext();
        try {

            //将HttpContext对象作为参数传给execute()方法,则HttpClient会把请求响应交互过程中的状态信息存储在HttpContext中
            HttpResponse response = httpclient.execute(httpGet, httpContext);
            //获取重定向之后的主机地址信息,即"http://127.0.0.1:8088"
            HttpHost targetHost = (HttpHost)httpContext.getAttribute(HTTP_TARGET_HOST);
            //获取实际的请求对象的URI,即重定向之后的"/blog/admin/login.jsp"
            HttpUriRequest realRequest = (HttpUriRequest)httpContext.getAttribute(HTTP_REQUEST);
            return targetHost.toString()  + realRequest.getURI().toString();
        } catch (IOException e) {
            logger.warn(e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;

    }

    /**
     * 处理doget请求
     *
     * @param url
     * @return
     */
    public static JSONObject doGet(String url) {

        CloseableHttpClient httpclient = null;
        JSONObject jsonObject = null;
        try {
            httpclient = HttpClients.createDefault();
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(20000).setConnectTimeout(20000).build();//设置请求和传输超时时间
            HttpGet httpGet = new HttpGet(url);
            httpGet.setConfig(requestConfig);
            CloseableHttpResponse response = httpclient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String result = EntityUtils.toString(entity);
                if (result.startsWith("{")) {
                    jsonObject = JSONObject.parseObject(result);
                }
            }
        } catch (IOException e) {
            logger.warn(e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return jsonObject;

    }
    public static JSONObject doGet(String url,int timeOut) {
        CloseableHttpClient httpclient = null;
        JSONObject jsonObject = null;
        try {
            httpclient = HttpClients.createDefault();
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(timeOut).setConnectTimeout(timeOut).build();//设置请求和传输超时时间
            HttpGet httpGet = new HttpGet(url);
            httpGet.setConfig(requestConfig);
            CloseableHttpResponse response = httpclient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String result = EntityUtils.toString(entity,"UTF-8");
                if (result.startsWith("{")) {
                    jsonObject = JSONObject.parseObject(result);
                }
            }
        } catch (IOException e) {
            logger.warn(e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return jsonObject;

    }

    /**
     * 处理doget请求
     *
     * @param url
     * @return
     */
    public static InputStream doGetInputStream(String url) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(10000).build();//设置请求和传输超时时间
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(requestConfig);
        try {
            CloseableHttpResponse response = httpclient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                return entity.getContent();
            }
        } catch (IOException e) {
            logger.warn(e.getMessage());
            e.printStackTrace();
        }
        return null;

    }

    public static String doGetStr(String url) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(10000).build();//设置请求和传输超时时间
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(requestConfig);

        JSONObject jsonObject = null;
        try {
            CloseableHttpResponse response = httpclient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String result = EntityUtils.toString(entity,"UTF-8");
                return result;
            }
        } catch (IOException e) {
            logger.warn(e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;

    }

    /**
     * 处理post请求
     *
     * @param url
     * @return
     */
    public static JSONObject doPost(String url, String outStr) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(10000).build();//设置请求和传输超时时间
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);
        JSONObject jsonObject = null;
        try {
            httpPost.setEntity(new StringEntity(outStr, "utf-8"));
            CloseableHttpResponse response = httpclient.execute(httpPost);
            String result = EntityUtils.toString(response.getEntity(), "utf-8");
            jsonObject = JSONObject.parseObject(result);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            logger.warn(e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return jsonObject;
    }


    public static JSONObject doPost(String url, JSONObject outStr) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(10000).build();//设置请求和传输超时时间
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);
        JSONObject jsonObject = null;
        try {
            httpPost.setHeader("Content-Type", "application/json;charset=UTF-8");
            httpPost.setEntity(new StringEntity(outStr.toString(), "utf-8"));
            CloseableHttpResponse response = httpclient.execute(httpPost);
            String result = EntityUtils.toString(response.getEntity(), "utf-8");
            jsonObject = JSONObject.parseObject(result);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            logger.warn(e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return jsonObject;
    }

    public static JSONObject doPost(String url, List<NameValuePair> formParams, Map<String, String> header) {
        CloseableHttpClient httpclient = HttpClients.createDefault();

        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(10000).build();//设置请求和传输超时时间
        HttpPost httpPost = new HttpPost(url);
        if (header != null) {
            for (String key : header.keySet()) {
                String value = header.get(key);
                httpPost.setHeader(key, value);
            }
        }
        httpPost.setConfig(requestConfig);
        JSONObject jsonObject = null;
        try {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formParams, "utf-8");
            httpPost.setEntity(entity);
            CloseableHttpResponse response = httpclient.execute(httpPost);
            String result = EntityUtils.toString(response.getEntity(), "utf-8");
            jsonObject = JSONObject.parseObject(result);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            logger.warn(e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return jsonObject;
    }


    public static JSONObject doPost(String url, List<NameValuePair> formParams, Map<String, String> header, Map<String, String> urlParams) {
        url = buildUrl(url, urlParams);
        CloseableHttpClient httpclient = HttpClients.createDefault();

        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(10000).build();//设置请求和传输超时时间
        HttpPost httpPost = new HttpPost(url);
        if (header != null) {
            for (String key : header.keySet()) {
                String value = header.get(key);
                httpPost.setHeader(key, value);
            }
        }
        httpPost.setConfig(requestConfig);
        JSONObject jsonObject = null;
        try {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formParams, "utf-8");
            httpPost.setEntity(entity);
            CloseableHttpResponse response = httpclient.execute(httpPost);
            String result = EntityUtils.toString(response.getEntity(), "utf-8");
            jsonObject = JSONObject.parseObject(result);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            logger.warn(e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return jsonObject;
    }


    public static JSONObject doPost(String url, Map<String, String> bodys, Map<String, String> header, Map<String, String> urlParams) {
        url = buildUrl(url, urlParams);
        CloseableHttpClient httpclient = HttpClients.createDefault();

        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(60000).setConnectTimeout(60000).build();//设置请求和传输超时时间
        HttpPost httpPost = new HttpPost(url);
        if (header != null) {
            for (String key : header.keySet()) {
                String value = header.get(key);
                httpPost.setHeader(key, value);
            }
        }
        httpPost.setConfig(requestConfig);
        JSONObject jsonObject = null;
        try {
            if (bodys != null) {
                List<NameValuePair> nameValuePairList = new ArrayList<>();

                for (String key : bodys.keySet()) {
                    nameValuePairList.add(new BasicNameValuePair(key, bodys.get(key)));
                }
                UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(nameValuePairList, "utf-8");
                formEntity.setContentType("application/x-www-form-urlencoded; charset=UTF-8");
                httpPost.setEntity(formEntity);
            }
            CloseableHttpResponse response = httpclient.execute(httpPost);
            String result = EntityUtils.toString(response.getEntity(), "utf-8");
            jsonObject = JSONObject.parseObject(result);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            logger.warn(e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return jsonObject;
    }

    /**
     * 处理post请求
     *
     * @param url
     * @return
     */
    public static JSONObject doPost(String url, String outStr, HashMap<String, String> header) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(10000).build();//设置请求和传输超时时间
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);
        for (String key : header.keySet()) {
            String value = header.get(key);
            httpPost.setHeader(key, value);
        }
        JSONObject jsonObject = null;
        try {
            httpPost.setEntity(new StringEntity(outStr, "utf-8"));
            CloseableHttpResponse response = httpclient.execute(httpPost);
            String result = EntityUtils.toString(response.getEntity(), "utf-8");
            jsonObject = JSONObject.parseObject(result);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            logger.warn(e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return jsonObject;
    }
    //链接url下载图片
    public static boolean downloadPicture(String picUrl, String path) {
        try {
            URL url = new URL(picUrl);
            DataInputStream dataInputStream = new DataInputStream(url.openStream());
            FileOutputStream fileOutputStream = new FileOutputStream(new File(path));
            ByteArrayOutputStream output = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int length;

            while ((length = dataInputStream.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
            fileOutputStream.write(output.toByteArray());
            dataInputStream.close();
            fileOutputStream.close();
            return true;
        } catch (Exception e) {
            logger.warn(e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    private static void sslClient(HttpClient httpClient) {
        try {
            SSLContext ctx = SSLContext.getInstance("TLS");
            X509TrustManager tm = new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] xcs, String str) {

                }

                public void checkServerTrusted(X509Certificate[] xcs, String str) {

                }
            };
            ctx.init(null, new TrustManager[]{tm}, null);
            SSLSocketFactory ssf = new SSLSocketFactory(ctx);
            ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            ClientConnectionManager ccm = httpClient.getConnectionManager();
            SchemeRegistry registry = ccm.getSchemeRegistry();
            registry.register(new Scheme("https", 443, ssf));
        } catch (KeyManagementException ex) {
            throw new RuntimeException(ex);
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String buildUrl(String url, Map<String, String> urlParams) {
        if (urlParams != null) {
            StringBuilder stringBuilder = new StringBuilder();
            for (Object key : urlParams.keySet()) {
                Object value = urlParams.get(key);
                try {
                    stringBuilder.append("&").append(key).append("=").append(URLEncoder.encode(value + "", "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    logger.warn(e.getMessage());
                }
            }
            if (stringBuilder.length() > 0) {
                stringBuilder.deleteCharAt(0);
            }
            if (!url.endsWith("?")) {
                url = url + "?";
            }
            url = url + stringBuilder.toString();
        }
        return url;
    }
}
