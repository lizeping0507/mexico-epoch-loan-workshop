package com.epoch.loan.workshop.common.util;


import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.*;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @Package com.epoch.remind.util
 * @Description: http请求操作工具类，默认utf-8编码</br>
 * @author: zeping.li
 * @Version V1.0
 * @Date: 2020/11/9 16:44
 */
public class HttpUtils {

    /**
     * JSON格式数据编码
     */
    public static final String CONTENT_TYPE_JSON = "application/json";
    /**
     * httpclient读取内容时使用的字符集
     */
    public static final String CONTENT_CHARSET = "UTF-8";
    public static final String CONTENT_CHARSET_GBK = "GBK";
    public static final Charset UTF_8 = Charset.forName("UTF-8");
    /**
     * 连接超时时间（与服务器建立连接）(5s)
     */
    private static final int CONNECTION_TIMEOUT_MS = 20000;
    /**
     * 读取数据(socket读取数据包之间)超时时间(5s)
     */
    private static final int SO_TIMEOUT_MS = 20000;
    /**
     * 连接池最大连接数
     */
    private static final int POOL_MAX_SIZE = 2000;
    /**
     * 对于某一个目标地址最大的连接数
     */
    private static final int POOL_MAX_PEER_ROUTE = 400;
    /**
     * UserAgent定义
     */
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; rv:40.0) Gecko/20100101 Firefox/40.0";
    /**
     * 键值对字符串数据格式
     */
    private static final String CONTENT_TYPE_FORM = "application/x-www-form-urlencoded";
    /**
     * 键值对字符串数据格式
     */
    private static final String CONTENT_TYPE_FORM_UTF8 = "application/x-www-form-urlencoded;charset=utf-8";
    /**
     * 二进制数据格式
     */
    private static final String CONTENT_TYPE_OCTET_STREAM = "application/octet-stream";
    /**
     * xml格式数据编码
     */
    private static final String CONTENT_TYPE_XML = "application/xml";
    /**
     * 主要用于十六进制数据
     */
    private static final char[] HEXDIGITS = "0123456789abcdef".toCharArray();
    /**
     * 使用http连接池管理
     */
    private static PoolingHttpClientConnectionManager connManager = null;

    /**
     * httpclient是线程安全,共享一个实例接口
     */
    private static CloseableHttpClient client = null;


    static {
        try {
            SSLContext context = SSLContext.getInstance("TLS");
            X509TrustManager tm = new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };
            context.init(null, new TrustManager[]{tm}, null);
            HostnameVerifier verifier = SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(context, verifier);

            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.INSTANCE)
                    .register("https", sslsf)
                    .build();
            // 初始化连接池
            connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);

            // Create socket configuration( nginx 默认也开启这个选项)
            SocketConfig socketConfig = SocketConfig.custom().setTcpNoDelay(true).build();
            connManager.setDefaultSocketConfig(socketConfig);

           /* // Create message constraints
            MessageConstraints messageConstraints = MessageConstraints.custom()
                .setMaxHeaderCount(200)
                .setMaxLineLength(2000)
                .build();

            // Create connection configuration
            ConnectionConfig connectionConfig = ConnectionConfig.custom()
                .setMalformedInputAction(CodingErrorAction.IGNORE)
                .setUnmappableInputAction(CodingErrorAction.IGNORE)
                .setCharset(Consts.UTF_8)
                .setMessageConstraints(messageConstraints)
                .build();

            connManager.setDefaultConnectionConfig(connectionConfig);*/
            connManager.setMaxTotal(POOL_MAX_SIZE);
            connManager.setDefaultMaxPerRoute(POOL_MAX_PEER_ROUTE);

            // 将指定的目标主机的最大连接数增加到50
            // HttpHost hostxxx = new HttpHost("http://xxxx.com",80);
            // connManager.setMaxPerRoute(new HttpRoute(hostxxx), 50);

            // 初始化http客户端
            client = HttpClients.custom().setConnectionManager(connManager).build();
        } catch (Exception e) {
            LogUtil.sysError("[init httpUtils exception]", e);
        }
    }


    private HttpUtils() {

    }

    /**
     * 以HTTPS的方式调用GET
     *
     * @param url
     * @param params
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     * @throws URISyntaxException
     */
    public static String GET_HTTPS(String url, Map<String, String> params)
            throws Exception {
        return getInvoke(url, params, CONTENT_CHARSET, true);
    }

    /**
     * 简单get调用
     *
     * @param url
     * @param params
     * @return
     * @throws Exception
     */
    public static String GET(String url, Map<String, String> params) throws Exception {
        return simpleGetInvoke(url, params, CONTENT_CHARSET);
    }


    /**
     * 简单get调用
     *
     * @param url
     * @param params
     * @return
     * @throws Exception
     */
    public static String GET_PROXY(String proxyIp, int proxyPort, String url, Map<String, String> params) throws Exception {
        LogUtil.sysInfo("[PROXY: {} PORT: {}]", proxyIp, proxyPort);
        return simpleGetProxyInvoke(proxyIp, proxyPort, url, params, CONTENT_CHARSET);
    }


    /**
     * 简单get调用
     *
     * @param url
     * @param params
     * @return
     * @throws Exception
     */
    public static String GET(String url, Map<String, String> params, String charSet) throws Exception {
        return simpleGetInvoke(url, params, charSet);
    }


    /**
     * 简单get调用
     *
     * @param url
     * @param params
     * @return
     * @throws Exception
     */
    public static String GET_WITH_HEADER(String url, Map<String, String> params, Map<String, String> headers) throws Exception {
        return getWithHeaderInvoke(url, params, CONTENT_CHARSET, headers);
    }


    /**
     * http get下载文件
     *
     * @param url      远程http地址(eg: http://xxxx.zip)
     * @param filePath 本地保存全路径(eg: /home/1.zip)
     * @return
     * @throws Exception
     */
    public static boolean GET_FILE(String url, Map<String, String> params, String filePath) throws Exception {
        return getFileInvoke(url, params, filePath);
    }


    /**
     * HTTP GET 下载文件
     *
     * @param url      http文件地址
     * @param filePath 本地保存全路径
     * @return 是否正常
     * @throws Exception
     */
    public static boolean getFileInvoke(String url, Map<String, String> params, String filePath) throws Exception {
        CloseableHttpResponse response = null;
        HttpGet get = buildHttpGet(url, params);
        InputStream in = null;
        FileOutputStream out = null;
        try {
            response = client.execute(get);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    in = entity.getContent();
                    out = new FileOutputStream(new File(filePath));
                    int l = -1;
                    byte[] tmp = new byte[1024];
                    while ((l = in.read(tmp)) != -1) {
                        out.write(tmp, 0, l);
                    }
                    out.flush();
                }
            } else {
                get.abort();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            // 关闭连接及资源
            if (null != out) {
                out.close();
            }
            if (null != in) {
                in.close();
            }
            HttpClientUtils.closeQuietly(response);
            get.releaseConnection();
        }
        return true;
    }

    /**
     * 简单get调用，指定返回内容字符集
     *
     * @param url
     * @param params
     * @param charset
     * @return
     * @throws Exception
     */
    public static String simpleGetInvoke(String url, Map<String, String> params, String charset) throws Exception {
        CloseableHttpResponse response = null;
        HttpGet get = buildHttpGet(url, params);
        try {
            response = client.execute(get);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    // 输入流关闭，同时会自动触发http连接的release
                    String returnStr = EntityUtils.toString(entity, charset);
                    return returnStr;
                }
            } else {
                get.abort();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e);
        } finally {
            HttpClientUtils.closeQuietly(response);
            get.releaseConnection();
        }
        return null;
    }

    /**
     * 简单get调用，指定返回内容字符集
     *
     * @param url
     * @param params
     * @param charset
     * @return
     * @throws Exception
     */
    public static String simpleGetProxyInvoke(String proxyIp, int proxyPort, String url, Map<String, String> params, String charset) throws Exception {
        CloseableHttpResponse response = null;
        HttpGet get = buildHttpProxyGet(proxyIp, proxyPort, url, params);
        try {
            response = client.execute(get);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    // 输入流关闭，同时会自动触发http连接的release
                    String returnStr = EntityUtils.toString(entity, charset);
                    return returnStr;
                }
            } else {
                get.abort();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e);
        } finally {
            HttpClientUtils.closeQuietly(response);
            get.releaseConnection();
        }
        return null;
    }


    /**
     * 简单get调用，指定返回内容字符集
     *
     * @param url
     * @param params
     * @param charset
     * @return
     * @throws Exception
     */
    public static String getWithHeaderInvoke(String url, Map<String, String> params, String charset, Map<String, String> headers) throws Exception {
        CloseableHttpResponse response = null;
        HttpGet get = buildHttpGetWithHeader(url, params, headers);
        try {
            response = client.execute(get);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    // 输入流关闭，同时会自动触发http连接的release
                    String returnStr = EntityUtils.toString(entity, charset);
                    return returnStr;
                }
            } else {
                get.abort();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e);
        } finally {
            HttpClientUtils.closeQuietly(response);
            get.releaseConnection();
        }
        return null;
    }


    public static String getInvoke(String url, Map<String, String> params, String charset, boolean isHttps)
            throws Exception {
        CloseableHttpResponse response = null;
        HttpGet get = buildHttpGet(url, params);
        try {
            response = client.execute(get);
            int status = response.getStatusLine().getStatusCode();
            if (status == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    // 输入流关闭，同时会自动触发http连接的release
                    String returnStr = EntityUtils.toString(entity, charset);
                    return returnStr;
                }
            } else {
                get.abort();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e);
        } finally {
            HttpClientUtils.closeQuietly(response);
            get.releaseConnection();
        }
        return null;
    }

    /**
     * POST 发送键值对
     *
     * @param url
     * @param params
     * @return
     * @throws URISyntaxException
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static String POST(String url, Map<String, String> params)
            throws Exception {
        return simplePostInvoke(url, params, CONTENT_CHARSET);
    }

    /**
     * POST 发送键值对
     *
     * @param url
     * @param params
     * @return
     * @throws URISyntaxException
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static String POST_WITH_HEADER(String url, Map<String, String> params, Map<String, String> headers)
            throws Exception {
        return postWithHeaderInvoke(url, params, headers);
    }

    /**
     * POST 发送键值对
     *
     * @param url
     * @param json
     * @return
     * @throws URISyntaxException
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static String POST_WITH_HEADER(String url, String json, Map<String, String> headers)
            throws Exception {
        return simplePostInvoke(url, json, CONTENT_CHARSET, headers);
    }

    /**
     * POST 发送json 不设置字符集
     *
     * @param url
     * @param json
     * @return
     * @throws URISyntaxException
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static String POST_WITH_HEADER_NOCHARSET(String url, String json, Map<String, String> headers)
            throws Exception {
        return simplePostInvoke(url, json, headers);
    }


    /**
     * 以HTTP方式POST发送json串
     *
     * @param url
     * @param json
     * @return
     * @throws URISyntaxException
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static String POST(String url, String json) throws Exception {
        return simplePostInvoke(url, json, CONTENT_CHARSET);
    }


    /**
     * 以HTTP方式POST发送json串
     *
     * @param url
     * @param json
     * @return
     * @throws URISyntaxException
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static String POST_PROXY(String proxyIp, int proxyPort, String url, String json) throws Exception {
        LogUtil.sysInfo("[PROXY: {} PORT: {}]", proxyIp, proxyPort);
        return simplePostProxyInvoke(proxyIp, proxyPort, url, json, CONTENT_CHARSET);
    }

    /**
     * 以HTTP方式POST发送键值对字符串(类似表单提交)
     *
     * @param url
     * @param params JSON
     * @return
     * @throws URISyntaxException
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static String POST_FORM(String url, String params) throws Exception {
        return paramsPostInvoke(url, params, CONTENT_CHARSET);
    }


    /**
     * 以HTTP方式POST发送键值对及二进制文件
     *
     * @param url
     * @param params
     * @return
     * @throws URISyntaxException
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static String POST_FORM_FILE(String url, Map<String, String> params, Map<String, File> files) throws Exception {
        return paramsFilesPostInvoke(url, params, files, CONTENT_CHARSET);
    }

    /**
     * 以HTTP方式POST发送键值对及二进制文件
     *
     * @param url
     * @param params
     * @param heardMap
     * @return
     * @throws URISyntaxException
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static String POST_WITH_HEADER_FORM_FILE(String url, Map<String, String> params, Map<String, String> heardMap, Map<String, File> files) throws Exception {
        return paramsWithHeaderFilesPostInvoke(url, params, heardMap, files, CONTENT_CHARSET);
    }


    /**
     * 以HTTP方式POST发送键值对字符串(类似表单提交)
     *
     * @param url
     * @param params
     * @return
     * @throws URISyntaxException
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static String POST_FORM_UTF8(String url, String params) throws Exception {
        return paramsPostInvokeHeaderUtf8(url, params, CONTENT_CHARSET);
    }

    /**
     * 以HTTP方式POST发送byte数组
     *
     * @param url
     * @param params
     * @return
     * @throws URISyntaxException
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static byte[] POST_BYTES(String url, byte[] params) throws Exception {
        return bytesPostInvoke(url, params);
    }

    /**
     * 以HTTP方式POST发送xml串
     *
     * @param url
     * @return
     * @throws URISyntaxException
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static String POST_XML(String url, String xml) throws Exception {
        return simplePostXMLInvoke(url, xml, CONTENT_CHARSET);
    }


    /**
     * 以HTTPS的方式POST发送json
     *
     * @param url
     * @param json
     * @return
     * @throws URISyntaxException
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static String POST_HTTPS(String url, String json)
            throws Exception {
        return postInvoke(url, json, CONTENT_CHARSET, true);
    }


    /**
     * 以HTTPS的方式POST发送json并接收文件
     *
     * @param url
     * @param json
     * @param filename 文件全路径
     * @return
     * @throws URISyntaxException
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static String POST_HTTPS_FILE(String url, String json, String filename)
            throws Exception {
        return postInvokeFile(url, json, filename, true);
    }


    /**
     * 以http的方式，发送json及图片两个对象
     *
     * @param url
     * @param json
     * @param filePath
     * @return
     * @throws URISyntaxException
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static String POST_JSON_AND_FILE(String url, String json, String filePath)
            throws Exception {
        return simplePostInvoke(url, json, filePath, CONTENT_CHARSET, false);
    }


    /**
     * 以 https的方式，发送json及图片两个对象
     *
     * @param url
     * @param json
     * @param filePath
     * @return
     * @throws URISyntaxException
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static String POST_HTTPS_JSON_AND_FILE(String url, String json, String filePath)
            throws Exception {
        return simplePostInvoke(url, json, filePath, CONTENT_CHARSET, true);
    }


    public static String simplePostInvoke(String url, String json, String filePath, String charset, boolean isHttps)
            throws Exception {
        CloseableHttpResponse response = null;
        HttpPost post = buildHttpPost(url, json, filePath);
        try {
            response = client.execute(post);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    // 输入流关闭，同时会自动触发http连接的release
                    String returnStr = EntityUtils.toString(entity, charset);
                    return returnStr;
                }
            } else {
                post.abort();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e);
        } finally {
            HttpClientUtils.closeQuietly(response);
            post.releaseConnection();
        }
        return null;
    }

    public static HttpPost buildHttpPost(String url, String json, String filePath)
            throws UnsupportedEncodingException, URISyntaxException {
        HttpPost post = new HttpPost(url);

        // 设置超时时间
        post.setConfig(buildRequestConfig());

        // 创建一个多参数的builder
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();

        // 设置提交类型
        //builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

        // json非空判断
        if (null != json && json.length() > 0) {
            builder.addPart("json", new StringBody(json, ContentType.APPLICATION_JSON.withCharset(UTF_8)));
        }

        // file非空判断
        if (null != filePath && filePath.length() > 0) {
            builder.addPart("file", new FileBody(new File(filePath), ContentType.APPLICATION_OCTET_STREAM));
        }

        // 设置参数
        post.setEntity(builder.build());

        return post;
    }


    /**
     * 构建http form表单参数及文件共同提交 post请求
     *
     * @param url
     * @param params
     * @param files
     * @return
     * @throws Exception
     */
    public static HttpPost buildHttpParamFilePost(String url, Map<String, String> params, Map<String, File> files)
            throws Exception {
        HttpPost post = new HttpPost(url);

        // Httpbody体 boundary分隔符
        String boundary = "----" + System.currentTimeMillis() + "----";

        // 设置超时时间
        post.setConfig(buildRequestConfig());

        // 设置头
        post.setHeader(HTTP.CONTENT_TYPE, "multipart/form-data;boundary=" + boundary);
        post.setHeader(HTTP.USER_AGENT, USER_AGENT);

        // 创建一个多参数的builder
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();

        // 设置基本参数
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        builder.setBoundary(boundary);
        builder.setContentType(ContentType.MULTIPART_FORM_DATA);
        builder.setCharset(UTF_8);

        // 设置form表单参数
        for (Entry<String, String> entry : params.entrySet()) {
            builder.addPart(entry.getKey(), new StringBody(entry.getValue(), ContentType.TEXT_PLAIN.withCharset(UTF_8)));
        }

        // 设置文件参数
        for (Entry<String, File> entry : files.entrySet()) {
            File temp = entry.getValue();
            builder.addPart(entry.getKey(), new FileBody(temp, ContentType.APPLICATION_OCTET_STREAM, temp.getName()));
        }

        // 设置参数
        post.setEntity(builder.build());

        return post;
    }

    /**
     * 构建http form表单参数及文件共同提交 post请求
     *
     * @param url
     * @param params
     * @param heardMap
     * @param files
     * @return
     * @throws Exception
     */
    public static HttpPost buildHttpWithHeaderParamFilePost(String url, Map<String, String> params, Map<String, String> heardMap, Map<String, File> files)
            throws Exception {
        HttpPost post = new HttpPost(url);

        // Httpbody体 boundary分隔符
        String boundary = "----------ThIs_Is_tHe_bouNdaRY_$";

        // 设置超时时间
        post.setConfig(buildRequestConfig());

        // 设置头
        // 设置请求头参数
        if (ObjectUtils.isNotEmpty(heardMap)) {
            for (String key : heardMap.keySet()) {
                post.setHeader(key, heardMap.get(key));
            }
        }

        // 创建一个多参数的builder
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();

        // 设置基本参数
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        builder.setBoundary(boundary);
        builder.setContentType(ContentType.MULTIPART_FORM_DATA);
        builder.setCharset(UTF_8);

        // 设置form表单参数
        for (Entry<String, String> entry : params.entrySet()) {
            builder.addPart(entry.getKey(), new StringBody(entry.getValue(), UTF_8));
        }

        // 设置文件参数
        for (Entry<String, File> entry : files.entrySet()) {
            File temp = entry.getValue();
            builder.addPart(entry.getKey(), new FileBody(temp, ContentType.APPLICATION_OCTET_STREAM, temp.getName()));
        }

        // 设置参数
        post.setEntity(builder.build());

        return post;
    }


    /**
     * POST发送键值对，并指定返回内容字符集
     *
     * @param url
     * @param params
     * @param charset
     * @return
     * @throws Exception
     */
    public static String simplePostInvoke(String url,
                                          Map<String, String> params, String charset) throws Exception {
        CloseableHttpResponse response = null;
        HttpPost post = buildHttpPost(url, params);
        try {
            response = client.execute(post);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    // 输入流关闭，同时会自动触发http连接的release
                    String returnStr = EntityUtils.toString(entity, charset);
                    return returnStr;
                }
            } else {
                post.abort();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e);
        } finally {
            HttpClientUtils.closeQuietly(response);
            post.releaseConnection();
        }
        return null;
    }


    /**
     * POST发送键值对，并指定返回内容字符集
     *
     * @param url
     * @param params
     * @return
     * @throws Exception
     */
    public static String postWithHeaderInvoke(String url, Map<String, String> params, Map<String, String> headers) throws Exception {
        CloseableHttpResponse response = null;
        HttpPost post = buildHttpPostWithHeader(url, params, headers);
        try {
            response = client.execute(post);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    // 输入流关闭，同时会自动触发http连接的release
                    String returnStr = EntityUtils.toString(entity, UTF_8);
                    return returnStr;
                }
            } else {
                post.abort();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e);
        } finally {
            HttpClientUtils.closeQuietly(response);
            post.releaseConnection();
        }
        return null;
    }

    /**
     * 使用POST 发送json，并指定返回内容字符集
     *
     * @param url
     * @param json
     * @param charset
     * @return
     * @throws Exception
     */
    public static String simplePostInvoke(String url, String json, String charset, Map<String, String> headers) throws Exception {
        CloseableHttpResponse response = null;
        HttpPost post = buildHttpPost(url, json, headers);
        try {
            response = client.execute(post);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    // 输入流关闭，同时会自动触发http连接的release
                    String returnStr = EntityUtils.toString(entity, charset);
                    return returnStr;
                }
            } else {
                post.abort();
                return response.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e);
        } finally {
            HttpClientUtils.closeQuietly(response);
            post.releaseConnection();
        }
        return null;
    }

    /**
     * 使用POST 发送json，并指定返回内容字符集
     * 不设置字符集
     *
     * @param url
     * @param json
     * @return
     * @throws Exception
     */
    public static String simplePostInvoke(String url, String json, Map<String, String> headers) throws Exception {
        CloseableHttpResponse response = null;
        HttpPost post = buildHttpPostNoCharset(url, json, headers);
        try {
            response = client.execute(post);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    // 输入流关闭，同时会自动触发http连接的release
                    String returnStr = EntityUtils.toString(entity, CONTENT_CHARSET);
                    return returnStr;
                }
            } else {
                post.abort();
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    // 输入流关闭，同时会自动触发http连接的release
                    String returnStr = EntityUtils.toString(entity, CONTENT_CHARSET);
                    return returnStr;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e);
        } finally {
            HttpClientUtils.closeQuietly(response);
            post.releaseConnection();
        }
        return null;
    }

    /**
     * 使用POST 发送json，并指定返回内容字符集
     *
     * @param url
     * @param json
     * @param charset
     * @return
     * @throws Exception
     */
    public static String simplePostInvoke(String url, String json, String charset) throws Exception {
        CloseableHttpResponse response = null;
        HttpPost post = buildHttpPost(url, json);
        try {
            response = client.execute(post);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    // 输入流关闭，同时会自动触发http连接的release
                    String returnStr = EntityUtils.toString(entity, charset);
                    return returnStr;
                }
            } else {
                post.abort();
                return response.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e);
        } finally {
            HttpClientUtils.closeQuietly(response);
            post.releaseConnection();
        }
        return null;
    }


    /**
     * 使用POST 发送json，并指定返回内容字符集
     *
     * @param url
     * @param json
     * @param charset
     * @return
     * @throws Exception
     */
    public static String simplePostProxyInvoke(String proxyIp, int proxyPort, String url, String json, String charset) throws Exception {
        CloseableHttpResponse response = null;
        HttpPost post = buildHttpProxyPost(proxyIp, proxyPort, url, json);
        try {
            response = client.execute(post);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    // 输入流关闭，同时会自动触发http连接的release
                    String returnStr = EntityUtils.toString(entity, charset);
                    return returnStr;
                }
            } else {
                post.abort();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e);
        } finally {
            HttpClientUtils.closeQuietly(response);
            post.releaseConnection();
        }
        return null;
    }


    /**
     * 使用POST 发送键值对字符串，并指定返回内容字符集
     *
     * @param url
     * @param charset
     * @return
     * @throws Exception
     */
    public static String paramsPostInvoke(String url, String params, String charset) throws Exception {
        CloseableHttpResponse response = null;
        HttpPost post = buildParamsHttpPost(url, params);
        try {
            response = client.execute(post);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    // 输入流关闭，同时会自动触发http连接的release
                    String returnStr = EntityUtils.toString(entity, charset);
                    return returnStr;
                }
            } else {
                post.abort();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e);
        } finally {
            HttpClientUtils.closeQuietly(response);
            post.releaseConnection();
        }
        return null;
    }


    /**
     * 使用POST 发送键值对字符串及二进制文件，并指定返回内容字符集
     *
     * @param url
     * @param params   表单普通参数
     * @param heardMap 请求头
     * @param files    文件参数
     * @param charset  编码格式
     * @return
     * @throws Exception
     */
    public static String paramsWithHeaderFilesPostInvoke(String url, Map<String, String> params, Map<String, String> heardMap, Map<String, File> files, String charset) throws Exception {
        CloseableHttpResponse response = null;
        HttpPost post = buildHttpWithHeaderParamFilePost(url, params, heardMap, files);
        try {
            response = client.execute(post);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    // 输入流关闭，同时会自动触发http连接的release
                    String returnStr = EntityUtils.toString(entity, charset);
                    return returnStr;
                }
            } else {
                post.abort();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e);
        } finally {
            HttpClientUtils.closeQuietly(response);
            post.releaseConnection();
        }
        return null;
    }

    /**
     * 使用POST 发送键值对字符串及二进制文件，并指定返回内容字符集
     *
     * @param url
     * @param params  表单普通参数
     * @param files   文件参数
     * @param charset 编码格式
     * @return
     * @throws Exception
     */
    public static String paramsFilesPostInvoke(String url, Map<String, String> params, Map<String, File> files, String charset) throws Exception {
        CloseableHttpResponse response = null;
        HttpPost post = buildHttpParamFilePost(url, params, files);
        try {
            response = client.execute(post);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    // 输入流关闭，同时会自动触发http连接的release
                    String returnStr = EntityUtils.toString(entity, charset);
                    return returnStr;
                }
            } else {
                post.abort();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e);
        } finally {
            HttpClientUtils.closeQuietly(response);
            post.releaseConnection();
        }
        return null;
    }

    /**
     * 使用POST 发送键值对字符串，并指定返回内容字符集
     * 在header的content-Type中指定 form;charset=utf8
     *
     * @param url
     * @param charset
     * @return
     * @throws Exception
     */
    public static String paramsPostInvokeHeaderUtf8(String url, String params, String charset) throws Exception {
        CloseableHttpResponse response = null;
        HttpPost post = buildParamsHttpPostHeaderUtf8(url, params);
        try {
            response = client.execute(post);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    // 输入流关闭，同时会自动触发http连接的release
                    String returnStr = EntityUtils.toString(entity, charset);
                    return returnStr;
                }
            } else {
                post.abort();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e);
        } finally {
            HttpClientUtils.closeQuietly(response);
            post.releaseConnection();
        }
        return null;
    }

    /**
     * 使用POST 发送键值对字符串，并指定返回内容字符集
     *
     * @param url
     * @return
     * @throws Exception
     */
    public static byte[] bytesPostInvoke(String url, byte[] params) throws Exception {
        CloseableHttpResponse response = null;
        HttpPost post = buildBytesHttpPost(url, params);
        try {
            response = client.execute(post);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    // 输入流关闭，同时会自动触发http连接的release
                    return EntityUtils.toByteArray(entity);
                }
            } else {
                post.abort();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e);
        } finally {
            HttpClientUtils.closeQuietly(response);
            post.releaseConnection();
        }
        return null;
    }


    /**
     * 使用POST 发送xml，并指定返回内容字符集
     *
     * @param url
     * @param charset
     * @return
     * @throws Exception
     */
    public static String simplePostXMLInvoke(String url, String xml, String charset) throws Exception {
        CloseableHttpResponse response = null;
        HttpPost post = buildXMLHttpPost(url, xml);
        try {
            response = client.execute(post);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    // 输入流关闭，同时会自动触发http连接的release
                    String returnStr = EntityUtils.toString(entity, charset);
                    return returnStr;
                }
            } else {
                post.abort();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e);
        } finally {
            HttpClientUtils.closeQuietly(response);
            post.releaseConnection();
        }
        return null;
    }


    /**
     * 使用POST 发送json，并指定返回内容字符集
     *
     * @param url
     * @param json
     * @param charset
     * @return
     * @throws URISyntaxException
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static String postInvoke(String url, String json, String charset, boolean isHttps) throws Exception {
        CloseableHttpResponse response = null;
        HttpPost post = buildHttpPost(url, json);
        try {
            response = client.execute(post);

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    // 输入流关闭，同时会自动触发http连接的release
                    String returnStr = EntityUtils.toString(entity, charset);
                    return returnStr;
                }
            } else {
                post.abort();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e);
        } finally {
            HttpClientUtils.closeQuietly(response);
            post.releaseConnection();
        }
        return null;
    }


    /**
     * 通过post的方式发送json,返回文件
     *
     * @param url      请求地址
     * @param json     请求参数json
     * @param fileName 文件保存全路径(eg:/usr/1.log)
     * @param isHttps
     * @return
     * @throws URISyntaxException
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static String postInvokeFile(String url, String json, String fileName, boolean isHttps)
            throws Exception {
        CloseableHttpResponse response = null;
        // 创建post请求
        HttpPost post = buildHttpPost(url, json);
        try {
            // 执行请求
            response = client.execute(post);
            // 状态正常的时候，开始接收文件
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    InputStream in = null;
                    FileOutputStream out = null;
                    try {
                        in = entity.getContent();
                        out = new FileOutputStream(new File(fileName));
                        int i = -1;
                        byte[] temp = new byte[1024];
                        while ((i = in.read(temp)) != -1) {
                            out.write(temp, 0, i);
                        }
                        out.flush();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (null != out) {
                            out.close();
                        }
                        if (null != in) {
                            in.close();
                        }
                    }
                }
            } else {
                post.abort();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e);
        } finally {
            HttpClientUtils.closeQuietly(response);
            post.releaseConnection();
        }
        return null;
    }


    /**
     * 创建HttpClient
     *
     * @param isHttps 是否https地址
     * @return
     */
    public static CloseableHttpClient buildHttpClient(boolean isHttps) {
        CloseableHttpClient client = null;
        try {
            if (isHttps) {
                SSLContext context = SSLContext.getInstance("TLS");
                X509TrustManager tm = new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                };
                context.init(null, new TrustManager[]{tm}, null);
                HostnameVerifier verifier = SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
                SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(context, verifier);
                client = HttpClients.custom().setSSLSocketFactory(sslsf).build();
            } else {
                client = HttpClientBuilder.create().build();
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        // 设置代理服务器地址和端口
        // client.getHostConfiguration().setProxy("proxy_host_addr",proxy_port);
        return client;
    }

    /**
     * 构建httpPost对象
     *
     * @param url
     * @return
     * @throws UnsupportedEncodingException
     * @throws URISyntaxException
     */
    public static HttpPost buildHttpPost(String url, Map<String, String> params)
            throws UnsupportedEncodingException, URISyntaxException {
        HttpPost post = new HttpPost(url);
        setHttpFormHeader(post);
        // 设置超时时间
        post.setConfig(buildRequestConfig());
        HttpEntity he = null;
        if (params != null) {
            List<NameValuePair> formparams = new ArrayList<NameValuePair>();
            for (String key : params.keySet()) {
                formparams.add(new BasicNameValuePair(key, params.get(key)));
            }
            he = new UrlEncodedFormEntity(formparams, UTF_8);
            post.setEntity(he);
        }
        return post;
    }


    /**
     * 构建httpPost对象
     *
     * @param url
     * @param headers
     * @return
     * @throws UnsupportedEncodingException
     * @throws URISyntaxException
     */
    public static HttpPost buildHttpPostWithHeader(String url, Map<String, String> params, Map<String, String> headers)
            throws UnsupportedEncodingException, URISyntaxException {
        HttpPost post = new HttpPost(url);
        setHttpHeader(post, headers);
        // 设置超时时间
        post.setConfig(buildRequestConfig());
        HttpEntity he = null;
        if (params != null) {
            List<NameValuePair> formparams = new ArrayList<NameValuePair>();
            for (String key : params.keySet()) {
                formparams.add(new BasicNameValuePair(key, params.get(key)));
            }
            he = new UrlEncodedFormEntity(formparams, UTF_8);
            post.setEntity(he);
        }
        return post;
    }

    /**
     * 构建httpPost对象
     *
     * @param url
     * @param headers
     * @return
     * @throws UnsupportedEncodingException
     * @throws URISyntaxException
     */
    public static HttpPost buildHttpPost(String url, String json, Map<String, String> headers) throws Exception {
        HttpPost post = new HttpPost(url);
        setHttpHeader(post, headers);
        // 设置超时时间
        post.setConfig(buildRequestConfig());
        // 非空判断
        if (null != json && json.length() > 0) {
            StringEntity se = new StringEntity(json, CONTENT_CHARSET);
            se.setContentEncoding(CONTENT_CHARSET);
            se.setContentType("application/json");
            post.setEntity(se);
        }
        return post;
    }

    /**
     * 构建httpPost对象 不设置字符集
     *
     * @param url
     * @param headers
     * @return
     * @throws UnsupportedEncodingException
     * @throws URISyntaxException
     */
    public static HttpPost buildHttpPostNoCharset(String url, String json, Map<String, String> headers) throws Exception {
        HttpPost post = new HttpPost(url);
        setHttpHeader(post, headers);
        // 设置超时时间
        post.setConfig(buildRequestConfig());
        // 非空判断
        if (null != json && json.length() > 0) {
            StringEntity se = new StringEntity(json, CONTENT_CHARSET);
            se.setContentType("application/json");
            post.setEntity(se);
        }
        return post;
    }

    /**
     * 构建httpPost对象
     *
     * @param url
     * @return
     * @throws UnsupportedEncodingException
     * @throws URISyntaxException
     */
    public static HttpPost buildHttpPost(String url, String json) throws Exception {
        HttpPost post = new HttpPost(url);
        setHttpHeader(post);
        // 设置超时时间
        post.setConfig(buildRequestConfig());
        // 非空判断
        if (null != json && json.length() > 0) {
            StringEntity se = new StringEntity(json, CONTENT_CHARSET);
            se.setContentEncoding(CONTENT_CHARSET);
            se.setContentType("application/json");
            post.setEntity(se);
        }
        return post;
    }


    /**
     * 构建httpPost对象
     *
     * @param url
     * @return
     * @throws UnsupportedEncodingException
     * @throws URISyntaxException
     */
    public static HttpPost buildHttpProxyPost(String ip, int port, String url, String json) throws Exception {
        HttpPost post = new HttpPost(url);
        setHttpHeader(post);
        // 设置超时时间及代理
        post.setConfig(buildProxyRequestConfig(ip, port));
        // 非空判断
        if (null != json && json.length() > 0) {
            StringEntity se = new StringEntity(json, CONTENT_CHARSET);
            se.setContentEncoding(CONTENT_CHARSET);
            se.setContentType("application/json");
            post.setEntity(se);
        }
        return post;
    }


    /**
     * 构建httpPost对象
     *
     * @param url
     * @param params
     * @return
     * @throws UnsupportedEncodingException
     * @throws URISyntaxException
     */
    public static HttpPost buildParamsHttpPost(String url, String params) throws Exception {
        HttpPost post = new HttpPost(url);
        setHttpFormHeader(post);
        // 设置超时时间
        post.setConfig(buildRequestConfig());
        // 非空判断
        if (null != params && params.length() > 0) {
            List<NameValuePair> formparams = new ArrayList<NameValuePair>();
            Map<String, String> paramsMap = JSONObject.parseObject(params, Map.class);
            for (String key : paramsMap.keySet()) {
                formparams.add(new BasicNameValuePair(key, paramsMap.get(key)));
            }

            UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");
            uefEntity.setContentEncoding(CONTENT_CHARSET);
            uefEntity.setContentType(CONTENT_TYPE_FORM);
            post.setEntity(uefEntity);
        }
        return post;
    }


    /**
     * 构建httpPost对象
     *
     * @param url
     * @param params
     * @return
     * @throws UnsupportedEncodingException
     * @throws URISyntaxException
     */
    public static HttpPost buildParamsHttpPostHeaderUtf8(String url, String params) throws Exception {
        HttpPost post = new HttpPost(url);
        setHttpFormHeaderUtf8(post);
        // 设置超时时间
        post.setConfig(buildRequestConfig());
        // 非空判断
        if (null != params && params.length() > 0) {
            StringEntity se = new StringEntity(params, CONTENT_CHARSET);
            se.setContentEncoding(CONTENT_CHARSET);
            se.setContentType(CONTENT_TYPE_FORM);
            post.setEntity(se);
        }
        return post;
    }


    /**
     * 构建httpPost对象
     *
     * @param url
     * @param params
     * @return
     * @throws UnsupportedEncodingException
     * @throws URISyntaxException
     */
    public static HttpPost buildBytesHttpPost(String url, byte[] params) throws Exception {
        HttpPost post = new HttpPost(url);
        setHttpFormHeader(post);
        // 设置超时时间
        post.setConfig(buildRequestConfig());
        // 非空判断
        if (null != params) {
            ByteArrayEntity bae = new ByteArrayEntity(params);
            bae.setContentType(CONTENT_TYPE_OCTET_STREAM);
            post.setEntity(bae);
        }
        return post;
    }

    /**
     * 构建httpPost对象
     *
     * @param url
     * @return
     * @throws UnsupportedEncodingException
     * @throws URISyntaxException
     */
    public static HttpPost buildXMLHttpPost(String url, String xml)
            throws UnsupportedEncodingException, URISyntaxException {
        HttpPost post = new HttpPost(url);
        setHttpHeader(post);
        // 设置超时时间
        post.setConfig(buildRequestConfig());
        // 非空判断
        if (null != xml && xml.length() > 0) {
            StringEntity se = new StringEntity(xml, CONTENT_CHARSET);
            se.setContentEncoding(CONTENT_CHARSET);
            se.setContentType(CONTENT_TYPE_XML);
            post.setEntity(se);
        }
        return post;
    }

    /**
     * 构建httpGet对象
     *
     * @param url
     * @return
     * @throws URISyntaxException
     */
    public static HttpGet buildHttpGet(String url, Map<String, String> params)
            throws URISyntaxException {
        HttpGet get = new HttpGet(buildGetUrl(url, params));
        // 设置超时时间
        get.setConfig(buildRequestConfig());
        // 设置请求头
        get.setHeader(HTTP.USER_AGENT, USER_AGENT);
        return get;
    }


    /**
     * 构建httpGet对象
     *
     * @param url
     * @return
     * @throws URISyntaxException
     */
    public static HttpGet buildHttpProxyGet(String proxyIp, int proxyPort, String url, Map<String, String> params)
            throws URISyntaxException {
        HttpGet get = new HttpGet(buildGetUrl(url, params));
        // 设置超时时间及代理
        get.setConfig(buildProxyRequestConfig(proxyIp, proxyPort));
        // 设置请求头
        get.setHeader(HTTP.USER_AGENT, USER_AGENT);
        return get;
    }

    /**
     * 构建httpGet对象
     *
     * @param url
     * @return
     * @throws URISyntaxException
     */
    public static HttpGet buildHttpGetWithHeader(String url, Map<String, String> params, Map<String, String> header)
            throws URISyntaxException {
        HttpGet get = new HttpGet(buildGetUrl(url, params));
        // 设置超时时间
        get.setConfig(buildRequestConfig());
        // 设置agent
        get.setHeader(HTTP.USER_AGENT, USER_AGENT);
        // 设置请求头参数
        if (null != header) {
            for (String key : header.keySet()) {
                get.setHeader(key, header.get(key));
            }
        }
        return get;
    }


    /**
     * build getUrl str
     *
     * @param url
     * @param params
     * @return
     */
    private static String buildGetUrl(String url, Map<String, String> params) {
        StringBuffer uriStr = new StringBuffer(url);
        if (params != null) {
            List<NameValuePair> ps = new ArrayList<NameValuePair>();
            for (String key : params.keySet()) {
                ps.add(new BasicNameValuePair(key, params.get(key)));
            }
            uriStr.append("?");
            uriStr.append(URLEncodedUtils.format(ps, UTF_8));
        }
        return uriStr.toString();
    }

    /**
     * 设置HttpMethod通用配置
     *
     * @param httpMethod
     */
    public static void setHttpHeader(HttpRequestBase httpMethod) {
        // 设置内容编码格式
        httpMethod.setHeader(HTTP.CONTENT_ENCODING, CONTENT_CHARSET);
        // 设置头部数据类型及编码
        httpMethod.setHeader(HTTP.CONTENT_TYPE, CONTENT_TYPE_JSON);
    }

    /**
     * 设置HttpMethod form表单提交方式头
     *
     * @param httpMethod
     */
    public static void setHttpFormHeader(HttpRequestBase httpMethod) {
        // 设置内容编码格式
        httpMethod.setHeader(HTTP.CONTENT_ENCODING, CONTENT_CHARSET);
        // 设置头部数据类型及编码
        httpMethod.setHeader(HTTP.CONTENT_TYPE, CONTENT_TYPE_FORM);
    }


    /**
     * 设置HttpMethod form表单提交方式头
     *
     * @param httpMethod
     */
    public static void setHttpFormHeaderUtf8(HttpRequestBase httpMethod) {
        // 设置内容编码格式
        httpMethod.setHeader(HTTP.CONTENT_ENCODING, CONTENT_CHARSET);
        // 设置头部数据类型及编码
        httpMethod.setHeader(HTTP.CONTENT_TYPE, CONTENT_TYPE_FORM_UTF8);
    }

    /**
     * 设置HttpMethod通用配置
     *
     * @param httpMethod
     * @param headers
     */
    public static void setHttpHeader(HttpRequestBase httpMethod, Map<String, String> headers) {
        if (null != headers) {
            for (String key : headers.keySet()) {
                httpMethod.setHeader(key, headers.get(key));
            }
        }
    }

    /**
     * 设置成消息体的长度 setting MessageBody length
     *
     * @param httpMethod
     * @param he
     */
    public static void setContentLength(HttpRequestBase httpMethod,
                                        HttpEntity he) {
        if (he == null) {
            return;
        }
        httpMethod.setHeader(HTTP.CONTENT_LEN,
                String.valueOf(he.getContentLength()));
    }

    /**
     * 构建公用RequestConfig
     *
     * @return
     */
    public static RequestConfig buildRequestConfig() {
        // 设置请求和传输超时时间
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(SO_TIMEOUT_MS)
                .setConnectTimeout(CONNECTION_TIMEOUT_MS).build();
        return requestConfig;
    }


    /**
     * 构建公用RequestConfig
     * 指定代理服务的IP 及 端口
     *
     * @return
     */
    public static RequestConfig buildProxyRequestConfig(String proxyIp, int proxyPort) {

        // 代理地址
        HttpHost proxy = new HttpHost(proxyIp, proxyPort);

        // 设置请求和传输超时时间
        RequestConfig requestConfig = RequestConfig.custom()
                .setProxy(proxy)
                .setSocketTimeout(SO_TIMEOUT_MS)
                .setConnectTimeout(CONNECTION_TIMEOUT_MS).build();
        return requestConfig;
    }

    /**
     * 强验证必须是200状态否则报异常
     *
     * @param res
     * @throws HttpException
     */
    public static void assertStatus(HttpResponse res) throws IOException {
        switch (res.getStatusLine().getStatusCode()) {
            case HttpStatus.SC_OK:
                /* case HttpStatus.SC_CREATED:
                 * case HttpStatus.SC_ACCEPTED:
                 * case HttpStatus.SC_NON_AUTHORITATIVE_INFORMATION:
                 * case HttpStatus.SC_NO_CONTENT:
                 * case HttpStatus.SC_RESET_CONTENT:
                 * case HttpStatus.SC_PARTIAL_CONTENT:
                 * case HttpStatus.SC_MULTI_STATUS:
                 */
                break;
            default:
                //throw new IOException("服务器响应状态异常");
        }
    }

    /**
     * 获取key对应的字符串value
     *
     * @param map
     * @param key
     * @return
     */
    public static String getParameterByMap(Map<String, Object> map, String key) {
        return (String) (map.get(key) != null
                && map.get(key).getClass().isArray() ? ((String[]) map.get(key))[0]
                : map.get(key));
    }

    /**
     * 获取key对应的数字value
     *
     * @param map
     * @param key
     * @return
     */
    public static Integer getParameterIntegerByMap(Map<String, Object> map,
                                                   String key) {
        String v = getParameterByMap(map, key);
        return StringUtils.isNumeric(v) && !StringUtils.isEmpty(v) ? Integer.valueOf(v) : 0;
    }


    /**
     * 将 ip地址进行运算成为数字类型
     *
     * @param strIp
     * @return
     */
    public static long ipToLong(String strIp) {

        long[] ip = new long[4];

        // 先找到IP地址字符串中.的位置
        int position1 = strIp.indexOf(".");
        int position2 = strIp.indexOf(".", position1 + 1);
        int position3 = strIp.indexOf(".", position2 + 1);

        // 将每个.之间的字符串转换成整型
        ip[0] = Long.parseLong(strIp.substring(0, position1));
        ip[1] = Long.parseLong(strIp.substring(position1 + 1, position2));
        ip[2] = Long.parseLong(strIp.substring(position2 + 1, position3));
        ip[3] = Long.parseLong(strIp.substring(position3 + 1));
        return (ip[0] << 24) + (ip[1] << 16) + (ip[2] << 8) + ip[3];
    }


    /**
     * 将byte数组变为16进制字符串
     *
     * @param bytes
     * @return
     */
    private static String toHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 3);
        for (int b : bytes) {
            b &= 0xff;
            sb.append(HEXDIGITS[b >> 4]);
            sb.append(HEXDIGITS[b & 15]);
            sb.append(' ');
        }
        return sb.toString();
    }

    /**
     * 有的时候,调用https方法会返回证书不可信的错误,可以使用此方法将对应的
     * 网站的https证书下载下来
     * <p>
     * 自动下载https网站的证书,执行main方法后，会在项目根目录下生成证书文件
     * 1.将证书文件，copy到  ${JAVA_HOME}\jre\lib\security 目录下
     * 2.然后重启程序访问对应https地址，不会再产生没有可信证书而无法访问的问题了
     * <p>
     * 注意：使用的时候，先将args[0] 的值修改为本次需要的域名，main执行即可
     */
    public static void downloadCertFile() throws Exception {

        // 手动设置需要下载证书的域名,如果不是默认443端口，那么域名后面冒号端口
        String[] args = new String[1];
        args[0] = "api.nciic.com.cn";

        String host;
        int port;
        char[] passphrase;
        if ((args.length == 1) || (args.length == 2)) {
            String[] c = args[0].split(":");
            host = c[0];
            port = (c.length == 1) ? 443 : Integer.parseInt(c[1]);
            String p = (args.length == 1) ? "changeit" : args[1];
            passphrase = p.toCharArray();
        } else {
            System.out.println("Usage: java InstallCert <host>[:port] [passphrase]");
            return;
        }

        File file = new File("jssecacerts");
        if (file.isFile() == false) {
            char SEP = File.separatorChar;
            File dir = new File(System.getProperty("java.home") + SEP + "lib"
                    + SEP + "security");
            file = new File(dir, "jssecacerts");
            if (file.isFile() == false) {
                file = new File(dir, "cacerts");
            }
        }
        System.out.println("Loading KeyStore " + file + "...");
        InputStream in = new FileInputStream(file);
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(in, passphrase);
        in.close();

        SSLContext context = SSLContext.getInstance("TLS");
        TrustManagerFactory tmf = TrustManagerFactory
                .getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(ks);
        X509TrustManager defaultTrustManager = (X509TrustManager) tmf
                .getTrustManagers()[0];
        SavingTrustManager tm = new SavingTrustManager(defaultTrustManager);
        context.init(null, new TrustManager[]{tm}, null);
        SSLSocketFactory factory = context.getSocketFactory();

        System.out.println("Opening connection to " + host + ":" + port + "...");
        SSLSocket socket = (SSLSocket) factory.createSocket(host, port);
        socket.setSoTimeout(10000);
        try {
            System.out.println("Starting SSL handshake...");
            socket.startHandshake();
            socket.close();
            System.out.println();
            System.out.println("No errors, certificate is already trusted");
        } catch (SSLException e) {
            System.out.println();
            e.printStackTrace(System.out);
        }

        X509Certificate[] chain = tm.chain;
        if (chain == null) {
            System.out.println("Could not obtain server certificate chain");
            return;
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        System.out.println();
        System.out.println("Server sent " + chain.length + " certificate(s):");
        System.out.println();
        MessageDigest sha1 = MessageDigest.getInstance("SHA1");
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        for (int i = 0; i < chain.length; i++) {
            X509Certificate cert = chain[i];
            System.out.println(" " + (i + 1) + " Subject " + cert.getSubjectDN());
            System.out.println("   Issuer  " + cert.getIssuerDN());
            sha1.update(cert.getEncoded());
            System.out.println("   sha1    " + toHexString(sha1.digest()));
            md5.update(cert.getEncoded());
            System.out.println("   md5     " + toHexString(md5.digest()));
            System.out.println();
        }

        System.out.println("Enter certificate to add to trusted keystore or 'q' to quit: [1]");
        String line = reader.readLine().trim();
        int k;
        try {
            k = (line.length() == 0) ? 0 : Integer.parseInt(line) - 1;
        } catch (NumberFormatException e) {
            System.out.println("KeyStore not changed");
            return;
        }

        X509Certificate cert = chain[k];
        String alias = host + "-" + (k + 1);
        ks.setCertificateEntry(alias, cert);

        OutputStream out = new FileOutputStream("jssecacerts");
        ks.store(out, passphrase);
        out.close();

        System.out.println();
        System.out.println(cert);
        System.out.println();
        System.out.println("Added certificate to keystore 'jssecacerts' using alias '" + alias + "'");
    }

    private static class SavingTrustManager implements X509TrustManager {

        private final X509TrustManager tm;
        private X509Certificate[] chain;

        SavingTrustManager(X509TrustManager tm) {
            this.tm = tm;
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
            this.chain = chain;
            tm.checkServerTrusted(chain, authType);
        }
    }
}
