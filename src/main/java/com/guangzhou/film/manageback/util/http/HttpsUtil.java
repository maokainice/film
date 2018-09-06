package com.guangzhou.film.manageback.util.http;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class HttpsUtil {

    private static final Log logger = LogFactory.getLog(HttpsUtil.class);

    public static final String CHARACTER_ENCODING = "UTF-8";

    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int SO_TIMEOUT = 60000;
    public static final int SO_TIMEOUT_60S = 60000;

    /**
     * HttpResponse包装类
     *
     * @author qiuguobin
     */
    public static class HttpResponseWrapper {
        private HttpResponse httpResponse;
        private HttpClient httpClient;
        private HttpRequestBase httpRequest;

        public HttpResponseWrapper(HttpClient httpClient) {
            this.httpClient = httpClient;
        }

        public HttpResponseWrapper(HttpClient httpClient, HttpResponse httpResponse) {
            this.httpClient = httpClient;
            this.httpResponse = httpResponse;
        }

        public HttpResponseWrapper(HttpClient httpClient, HttpResponse httpResponse, HttpRequestBase httpRequest) {
            this(httpClient, httpResponse);
            this.httpRequest = httpRequest;
        }

        public HttpRequestBase getHttpRequest() {
            return httpRequest;
        }

        public void setHttpRequest(HttpRequestBase httpRequest) {
            this.httpRequest = httpRequest;
        }

        public HttpResponse getHttpResponse() {
            return httpResponse;
        }

        public void setHttpResponse(HttpResponse httpResponse) {
            this.httpResponse = httpResponse;
        }

        /**
         * 获得流类型的响应
         */
        public InputStream getResponseStream() throws IllegalStateException, IOException {
            return httpResponse.getEntity().getContent();
        }

        /**
         * 获得字符串类型的响应
         */
        public String getResponseString(String responseCharacter) throws ParseException, IOException {
            HttpEntity entity = getEntity();
            String responseStr = EntityUtils.toString(entity, responseCharacter);
            if (entity.getContentType() == null) {
                responseStr = new String(responseStr.getBytes("iso-8859-1"), responseCharacter);
            }
            EntityUtils.consume(entity);
            return responseStr;
        }

        public String getResponseString() throws ParseException, IOException {
            return getResponseString(CHARACTER_ENCODING);
        }

        /**
         * 获得响应状态码
         */
        public int getStatusCode() {
            return httpResponse.getStatusLine().getStatusCode();
        }

        /**
         * 获得响应状态码并释放资源
         */
        public int getStatusCodeAndClose() {
            close();
            return getStatusCode();
        }

        public HttpEntity getEntity() {
            return httpResponse.getEntity();
        }

        /**
         * 释放资源
         */
        public void close() {
            if (httpRequest != null) {
                httpRequest.releaseConnection();
            }
            httpClient.getConnectionManager().shutdown();
        }
    }

    private static class TrustAnyTrustManager implements X509TrustManager {

        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[] {};
        }
    }
    /**
     * GET方式提交URL请求，会自动重定向
     */
    public static String requestGet(String url) {
        return requestGet(url, CHARACTER_ENCODING, null);
    }

    /**
     * GET方式提交URL请求，会自动重定向
     */
    public static String requestGet(String url, String responseCharacter, String xForWardedFor) {
        HttpResponseWrapper httpResponseWrapper = null;
        try {
            httpResponseWrapper = requestGetResponse(url, xForWardedFor);
            return httpResponseWrapper.getResponseString(responseCharacter);
        } catch (Exception e) {
            logger.error(e);
        } finally {
            if (httpResponseWrapper != null) {
                httpResponseWrapper.close();
            }
        }
        return null;
    }

    public static HttpResponseWrapper requestGetResponse(String url, String xForWardedFor) throws ClientProtocolException, IOException {
        HttpClient client = null;
        if (url.startsWith("https")) {
            client = createHttpsClient();
        } else {
            client = createHttpClient();
        }
        HttpGet httpGet = new HttpGet(url);
        if (null != xForWardedFor && !"".equalsIgnoreCase(xForWardedFor)) {
            httpGet.addHeader("X-Forwarded-For", xForWardedFor);
        }

        try {
            HttpResponse httpResponse = client.execute(httpGet);
            return new HttpResponseWrapper(client, httpResponse, httpGet);
        } catch (Exception e) {
            logger.error(e);
        }
        return null;
    }

    public static HttpClient createHttpsClient() {
        return createHttpsClient(CONNECTION_TIMEOUT, SO_TIMEOUT);
    }

    public static HttpClient createHttpClient() {
        return createHttpClient(CONNECTION_TIMEOUT, SO_TIMEOUT);
    }

    public static HttpClient createHttpClient(int connectionTimeout, int soTimeout) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpParams params = httpClient.getParams();
        HttpConnectionParams.setConnectionTimeout(params, connectionTimeout);
        HttpConnectionParams.setSoTimeout(params, soTimeout);
        return httpClient;
    }

    public static HttpClient createHttpsClient(int connectionTimeout, int soTimeout) {
        try {
            HttpClient httpClient = new DefaultHttpClient(); // 创建默认的httpClient实例
            HttpParams params = httpClient.getParams();
            HttpConnectionParams.setConnectionTimeout(params, connectionTimeout);
            HttpConnectionParams.setSoTimeout(params, soTimeout);
            // TLS1.0与SSL3.0基本上没有太大的差别，可粗略理解为TLS是SSL的继承者，但它们使用的是相同的SSLContext
            SSLContext ctx = SSLContext.getInstance("TLS");
            // 使用TrustManager来初始化该上下文，TrustManager只是被SSL的Socket所使用
            ctx.init(null, new TrustManager[] { new TrustAnyTrustManager() }, null);
            // 创建SSLSocketFactory
            SSLSocketFactory socketFactory = new SSLSocketFactory(ctx);
            //socketFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            //SSLSocketFactory socketFactory = new MYSSLSocketFactory(ctx,SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            // 通过SchemeRegistry将SSLSocketFactory注册到我们的HttpClient上
            httpClient.getConnectionManager().getSchemeRegistry().register(new Scheme("https", 443, socketFactory));

            return httpClient;
        } catch (Exception e) {
            logger.error(e);
        }
        return null;
    }
}
