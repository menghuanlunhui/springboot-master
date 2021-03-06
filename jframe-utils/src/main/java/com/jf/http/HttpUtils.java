package com.jf.http;

import com.jf.exception.HttpTimeoutException;
import com.jf.exception.SysException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.SocketTimeoutException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * Description: HTTP工具类
 * User: admin
 * Date: 2019-05-20
 * Time: 17:16
 */
public class HttpUtils {

    private static Logger log = LoggerFactory.getLogger(HttpUtil.class);

    private static RequestConfig requestConfig = null;

    static {
        // 初始化参数
        RequestConfig.Builder builder = RequestConfig.custom();
        builder.setConnectTimeout(15000); // 设置连接超时时间，单位毫秒
        builder.setSocketTimeout(15000); // 请求获取数据的超时时间(即响应时间)，单位毫秒。 如果访问一个接口，多少时间内无法返回数据，就直接放弃此次调用。
        builder.setCookieSpec(CookieSpecs.STANDARD);
        requestConfig = builder.build();
    }

    /**
     * Get请求
     *
     * @param url
     * @param headers
     * @param params
     * @return http报文
     */
    public static String doGet(String url, Map<String, String> headers, Map<String, String> params) {
        HttpClientContext httpClientContext = HttpClientContext.create();
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String responseStr = "";
        try {
            HttpGet httpGet = doGetObj(url, headers, params);
            response = httpClient.execute(httpGet, httpClientContext);
            if (response.getEntity() != null) {
                responseStr = EntityUtils.toString(response.getEntity(), "UTF-8");
            }
        } catch (Exception e) {
            if (e instanceof SocketTimeoutException) {
                throw new HttpTimeoutException(e.getMessage(), e);
            }
            log.error(e.getMessage(), e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                httpClient.close();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }

        return responseStr;
    }

    /**
     * Get请求
     *
     * @param url
     * @param headers
     * @param params
     * @return HttpGet对象
     * @throws Exception
     */
    public static HttpGet doGetObj(String url, Map<String, String> headers, Map<String, String> params) throws Exception {
        // 创建访问的地址
        URIBuilder uriBuilder = new URIBuilder(url);
        if (params != null) {
            Set<Map.Entry<String, String>> entrySet = params.entrySet();
            for (Map.Entry<String, String> entry : entrySet) {
                uriBuilder.setParameter(entry.getKey(), entry.getValue());
            }
        }
        // 创建http对象
        HttpGet httpGet = new HttpGet(uriBuilder.build());
        httpGet.setConfig(requestConfig);
        // 设置请求头
        packageHeader(headers, httpGet);
        return httpGet;
    }

    /**
     * Post请求
     *
     * @param url
     * @param headers
     * @param params
     * @return http报文
     * @throws Exception
     */
    public static String doPost(String url, Map<String, String> headers, Map<String, String> params) throws Exception {
        HttpClientContext httpClientContext = HttpClientContext.create();
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String responseStr = "";
        try {
            HttpPost httpPost = doPostObj(url, headers, params);
            response = httpClient.execute(httpPost, httpClientContext);
            if (response.getEntity() != null) {
                responseStr = EntityUtils.toString(response.getEntity(), "UTF-8");
            }
        } catch (Exception e) {
            if (e instanceof SocketTimeoutException) {
                throw new HttpTimeoutException(e.getMessage(), e);
            }
            log.error(e.getMessage(), e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                httpClient.close();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }

        return responseStr;
    }

    /**
     * Post请求
     *
     * @param url
     * @param json
     * @return http报文
     * @throws Exception
     */
    public static String doPost(String url, String json) throws Exception {
        HttpClientContext httpClientContext = HttpClientContext.create();
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String responseStr = "";
        try {
            Map<String, String> headers = new HashMap<>();
            headers.put(HttpHeaders.CONTENT_TYPE, "application/json");
            HttpPost httpPost = doPostObj(url, headers, json);
            response = httpClient.execute(httpPost, httpClientContext);
            if (response.getEntity() != null) {
                responseStr = EntityUtils.toString(response.getEntity(), "UTF-8");
            }
        } catch (Exception e) {
            if (e instanceof SocketTimeoutException) {
                throw new HttpTimeoutException(e.getMessage(), e);
            }
            log.error(e.getMessage(), e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                httpClient.close();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }

        return responseStr;
    }

    /**
     * Post请求
     *
     * @param url
     * @param headers
     * @param params
     * @return HttpPost对象
     * @throws Exception
     */
    public static HttpPost doPostObj(String url, Map<String, String> headers, Map<String, String> params) throws Exception {
        // 创建http对象
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);
        // 设置请求头
        packageHeader(headers, httpPost);
        // 封装请求参数
        packageParam(params, httpPost);
        // 执行请求并获得响应结果
        return httpPost;
    }

    /**
     * Post请求
     *
     * @param url
     * @param headers
     * @param body
     * @return HttpPost对象
     * @throws Exception
     */
    public static HttpPost doPostObj(String url, Map<String, String> headers, String body) throws Exception {
        // 创建http对象
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);
        // 设置请求头
        packageHeader(headers, httpPost);
        // 封装请求参数
        StringEntity paramEntity = new StringEntity(body, "UTF-8");
        httpPost.setEntity(paramEntity);
        // 执行请求并获得响应结果
        return httpPost;
    }

    /**
     * 封装头部
     *
     * @param params
     * @param httpMethod
     */
    private static void packageHeader(Map<String, String> params, HttpRequestBase httpMethod) {
        // 封装请求头
        if (params != null) {
            Set<Map.Entry<String, String>> entrySet = params.entrySet();
            for (Map.Entry<String, String> entry : entrySet) {
                // 设置到请求头到HttpRequestBase对象中
                httpMethod.setHeader(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * 封装参数
     *
     * @param params
     * @param httpMethod
     * @throws UnsupportedEncodingException
     */
    private static void packageParam(Map<String, String> params, HttpEntityEnclosingRequestBase httpMethod)
            throws UnsupportedEncodingException {
        // 封装请求参数
        if (params != null) {
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            Set<Map.Entry<String, String>> entrySet = params.entrySet();
            for (Map.Entry<String, String> entry : entrySet) {
                nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }

            // 设置到请求的http对象中
            httpMethod.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
        }
    }

    // 以下为DEMO，请勿直接使用

    /**
     * 登陆
     * <p>
     * // 创建http上下文
     * HttpClientContext httpClientContext = HttpClientContext.create();
     * </p>
     *
     * @param loginUrl
     * @param headers
     * @param params
     * @param httpClientContext
     * @return
     */
    public static CloseableHttpClient login(String loginUrl, Map<String, String> headers, Map<String, String> params,
                                            HttpClientContext httpClientContext) throws Exception {
        // 创建对话
        CookieStore cookieStore = new BasicCookieStore();
        httpClientContext.setCookieStore(cookieStore);
        // SSL配置
        SSLContextBuilder builder = new SSLContextBuilder();
        builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(builder.build());
        CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(sslsf).build();

        HttpPost httpPost = doPostObj(loginUrl, headers, params);
        CloseableHttpResponse response = httpClient.execute(httpPost, httpClientContext);
        String responseStr = EntityUtils.toString(response.getEntity(), "UTF-8");
        log.info("登陆返回: " + responseStr);
        response.close();
        return httpClient;
    }

    /**
     * 从网络上下载文件
     *
     * @param downUrl
     * @param filepath
     * @param filename
     */
    public static void downloadFile(String downUrl, String filepath) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpClientContext httpClientContext = HttpClientContext.create();

        downloadFile(downUrl, filepath, httpClient, httpClientContext);
    }

    /**
     * @param downUrl
     * @param filepath
     * @param httpClient
     * @param httpClientContext 含有Cookie对象
     */
    public static void downloadFile(String downUrl, String filepath,
                                    CloseableHttpClient httpClient, HttpClientContext httpClientContext) {
        InputStream inputStream = null;
        BufferedOutputStream outputStream = null;
        CloseableHttpResponse httpResponse = null;
        try {
            HttpGet httpGet = doGetObj(downUrl, null, null);
            httpResponse = httpClient.execute(httpGet, httpClientContext);
            inputStream = httpResponse.getEntity().getContent();
            inputStream = new BufferedInputStream(inputStream);
            outputStream = new BufferedOutputStream(new FileOutputStream(filepath));
            int byteCount = 0;
            //int filesize = 0;
            byte[] bytes = new byte[4096];
            while ((byteCount = inputStream.read(bytes)) != -1) {
                //filesize += byteCount;
                outputStream.write(bytes, 0, byteCount);
            }
        } catch (Exception e) {
            log.error("文件：" + filepath + "下载失败", e);
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.flush();
                    outputStream.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
                if (httpResponse != null) {
                    httpResponse.close();
                }
                httpClient.close();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    /**
     * 上传文件
     *
     * @param server
     * @param localPath
     * @param extra
     * @param httpClient
     * @param httpClientContext
     */
    private void uploadFile(String server, String localPath, String extra,
                            CloseableHttpClient httpClient, HttpClientContext httpClientContext) {
        CloseableHttpResponse httpResponse = null;
        try {
            File file = new File(localPath);
            if (!file.exists()) {
                throw new SysException("文件不存在：" + localPath);
            }
            HttpPost httpPost = new HttpPost(server);
            MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
            ContentType contentType = ContentType.create(HTTP.PLAIN_TEXT_TYPE, HTTP.UTF_8); // 中文乱码问题
            multipartEntityBuilder.addBinaryBody("file", file);
            multipartEntityBuilder.addTextBody("extra", extra, contentType);
            HttpEntity httpEntity = multipartEntityBuilder.build();
            httpPost.setEntity(httpEntity);

            httpResponse = httpClient.execute(httpPost);
            HttpEntity responseEntity = httpResponse.getEntity();
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                String content = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
                if ("success".equals(content)) {
                    log.info("上传成功");
                } else {
                    log.info("上传失败：" + content);
                }
            } else {
                log.info("上传失败");
            }
        } catch (IOException e) {
            log.info("上传失败", e);
        } finally {
            try {
                if (httpResponse != null) {
                    httpResponse.close();
                }
                httpClient.close();
            } catch (Exception e) {
            }
        }
    }

}
