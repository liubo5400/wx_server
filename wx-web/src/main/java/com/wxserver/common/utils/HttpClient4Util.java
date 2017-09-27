package com.wxserver.common.utils;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.*;
import javax.servlet.ServletOutputStream;
import java.io.*;
import java.net.URL;
import java.security.cert.CertificateException;

public class HttpClient4Util {

    private static HttpClient httpClient = new HttpClient(new MultiThreadedHttpConnectionManager());

    private static HttpClient getHttpClient() {
        if (httpClient == null) {
            httpClient = new HttpClient(new MultiThreadedHttpConnectionManager());
        }
        return httpClient;
    }

    private static HttpClient getNewHttpClient() {
        return new HttpClient();
    }

    public static String GetContentForNew(String url, String charset) {
        GetMethod get = new GetMethod(url);
        try {

            System.out.println("======>HttpClient4Util.GetContent(), url=" + url + ", start......");
            int res = getNewHttpClient().executeMethod(get);
            String resCharset = get.getResponseCharSet();
            String content = get.getResponseBodyAsString();
            if (!charset.equalsIgnoreCase(resCharset)) {
                byte[] bytearray = content.getBytes(resCharset);
                return new String(bytearray, charset);
            }
            System.out.println("======>HttpClient4Util.GetContent(), url=" + url + ", end......");

            get.abort();
            get.releaseConnection();
            return content;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            get.releaseConnection();
        }

        return null;
    }

    public static String GetContent(String url, String charset) {
        GetMethod get = new GetMethod(url);
        try {
            System.out.println("======>HttpClient4Util.GetContent(), url=" + url + ", start......");
            int res = getHttpClient().executeMethod(get);
            String resCharset = get.getResponseCharSet();
            String content = get.getResponseBodyAsString();
//            if (!charset.equalsIgnoreCase(resCharset)) {
//                byte[] bytearray = content.getBytes(resCharset);
//                return new String(bytearray, charset);
//            }
            System.out.println("======>HttpClient4Util.GetContent(), url=" + url + ", end......");
            return content;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            get.releaseConnection();
        }

        return null;
    }
    
    
    //����url״̬��
    public static int GetContentForImage(String url, String charset) {
      GetMethod get = new GetMethod(url);
      int res = 200;
      try {
          res = getHttpClient().executeMethod(get);
      } catch (Exception e) {
          e.printStackTrace();
          return 0;
      } finally {
          get.releaseConnection();
      }
      return res;
  }

    /**
     * ֱ�ӻ�ȡget�������Ӧ���
     */
    public static void getResponse(String url, ServletOutputStream output) {

        GetMethod get = new GetMethod(url);
        try {
            int res = getHttpClient().executeMethod(get);
            byte[] bytearray = get.getResponseBody();
            output.write(bytearray);
            output.flush();
            output.close();
        } catch (HttpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            get.releaseConnection();
        }
    }

    /**
     * ��ȡget�������Ӧ����������浽�ļ�
     */
    public static void saveResponseToFile(String url, String filename) {

        GetMethod get = new GetMethod(url);
        try {
            int res = getHttpClient().executeMethod(get);
            byte[] bytearray = get.getResponseBody();
            FileUtils.writeByteArrayToFile(new File(filename), bytearray);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            get.releaseConnection();
        }
    }

    /**
     * ��ȡget�������Ӧ������浽�ֽ�����
     */
    public static byte[] getResponseToByteArray(String url) {

        GetMethod get = new GetMethod(url);
        try {
            int res = getHttpClient().executeMethod(get);
            byte[] bytearray = get.getResponseBody();
            return bytearray;
        } catch (HttpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            get.releaseConnection();
        }
        return null;
    }

    public static void postContent(String url, String body, String charset) {
        try {

            SSLSocketFactory ssf = getSSLSocketFactory();
            URL uri = new URL(url);
            HttpsURLConnection httpUrlConn = (HttpsURLConnection) uri.openConnection();
            httpUrlConn.setSSLSocketFactory(ssf);
            httpUrlConn.setDoOutput(true);
            httpUrlConn.setDoInput(true);
            httpUrlConn.setUseCaches(false);
            httpUrlConn.setRequestMethod("POST");
            OutputStream outputStream = httpUrlConn.getOutputStream();
            // ע������ʽ����ֹ��������  
            outputStream.write(body.getBytes(charset));
            outputStream.close();
            // �����ص�������ת�����ַ���  
            InputStream inputStream = httpUrlConn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, charset);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String str = null;
            StringBuffer buffer = new StringBuffer();
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            bufferedReader.close();
            inputStreamReader.close();
            // �ͷ���Դ  
            inputStream.close();
            inputStream = null;
            httpUrlConn.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * �������ı���ʽ��Ӧ������
     * @param responseEntity
     * @param charset
     * @return
     * @throws Exception
     */
    public static String getResponseContent(HttpEntity responseEntity, String charset) throws Exception {
        byte[] bytes = EntityUtils.toByteArray(responseEntity);
        return new String(bytes, charset);
    }

    private static SSLSocketFactory getSSLSocketFactory() {

        X509TrustManager tm = new X509TrustManager() {

            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
                    throws CertificateException {
                // TODO Auto-generated method stub

            }

            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
                    throws CertificateException {
                // TODO Auto-generated method stub

            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                // TODO Auto-generated method stub
                return null;
            }

        };

        TrustManager[] tms = { tm };

        try {
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, tms, new java.security.SecureRandom());
            // ������SSLContext�����еõ�SSLSocketFactory����  
            SSLSocketFactory ssf = sslContext.getSocketFactory();
            return ssf;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static String postContentUseHttpClient3(String url, String body, String charset) {
        try {

            PostMethod post = new PostMethod(url);
            post.setRequestBody(new ByteArrayInputStream(body.getBytes(charset)));
            getHttpClient().executeMethod(post);
            String content = post.getResponseBodyAsString();
            System.out.println("==>res=" + content);
            return content;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //    private static void enableSSL(HttpClient httpclient) {
    //        //����ssl  
    //        try {
    //            SSLContext sslcontext = SSLContext.getInstance("TLS");
    //            sslcontext.init(null, new TrustManager[] { truseAllManager }, null);
    //            SSLSocketFactory sf = new SSLSocketFactory(sslcontext);
    //            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
    //            Scheme https = new Scheme("https", sf, 443);
    //            httpclient.getConnectionManager().getSchemeRegistry().register(https);
    //            httpclient.getParams().setParameter("http.protocol.content-charset", HTTP.UTF_8);
    //            httpclient.getParams().setParameter(HTTP.CONTENT_ENCODING, HTTP.UTF_8);
    //            httpclient.getParams().setParameter(HTTP.CHARSET_PARAM, HTTP.UTF_8);
    //            httpclient.getParams().setParameter(HTTP.DEFAULT_PROTOCOL_CHARSET, HTTP.UTF_8);
    //        } catch (Exception e) {
    //            e.printStackTrace();
    //        }
    //    }

    /** 
     * ��д��֤������ȡ�����ssl 
     */
    private static TrustManager truseAllManager = new X509TrustManager() {

        public void checkClientTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
                throws CertificateException {
            // TODO Auto-generated method stub  

        }

        public void checkServerTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
                throws CertificateException {
            // TODO Auto-generated method stub  

        }

        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            // TODO Auto-generated method stub  
            return null;
        }

    };
}
