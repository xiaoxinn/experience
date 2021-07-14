package com.xiaoxin.experience.util.http;

import sun.net.www.protocol.https.Handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

/**
 * @author xiaoxin
 */
public class HttpsUtil {

    private HttpsUtil() {}

    public static String postJson(String url, String data)
    {
        //发送https前的准备工作
        TrustManager[] tm = {new MyX509TrustManager()};
        HttpsURLConnection conn;
        OutputStream out = null;
        String rsp = null;
        try {
            byte[] byteArray = data.getBytes(StandardCharsets.UTF_8);
            SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
            sslContext.init(null, tm, new java.security.SecureRandom());
            SSLSocketFactory ssf = sslContext.getSocketFactory();
            URL uri = new URL(null, url, new Handler());
            conn = (HttpsURLConnection) uri.openConnection();
            //忽略证书验证--Begin
            conn.setHostnameVerifier((hostname, session) -> true);
            conn.setSSLSocketFactory(ssf);
            //忽略证书验证--End
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Host", uri.getHost());
            conn.setRequestProperty("Content-Type", "application/json");
            out = conn.getOutputStream();
            out.write(byteArray);
            out.close();
            if (conn.getResponseCode() == 200)
            {
                rsp = getStreamAsString(conn.getInputStream(), "utf-8");
            }
            else
            {
                rsp = getStreamAsString(conn.getErrorStream(), "utf-8");
            }
        } catch (Exception e) {
            if (null != out)
            {
                try
                {
                    out.close();
                } catch (IOException ioException)
                {
                    ioException.printStackTrace();
                }
            }
            e.printStackTrace();
        }
        return rsp;
    }


    private static String getStreamAsString(InputStream stream, String charset) throws IOException
    {
        try (Reader reader = new InputStreamReader(stream, charset))
        {
            StringBuilder response = new StringBuilder();

            final char[] buff = new char[1024];
            int read = 0;
            while ((read = reader.read(buff)) > 0)
            {
                response.append(buff, 0, read);
            }
            return response.toString();
        } finally
        {
            if (stream != null)
            {
                stream.close();
            }
        }
    }
}
