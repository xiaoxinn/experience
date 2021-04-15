package com.xiaoxin.experience.util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

/**
 * @author xiaoxin
 */
public class Base64ChangeUtil {

    public static byte[] readInputStream(InputStream inputStream)
    {
        byte[] result = new byte[0];
        byte[] buffer = new byte[1024];
        int len;
        try(ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            while ((len=inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            result = outputStream.toByteArray();
        }catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String base64Change(String pUrl)
    {
        String result = "";
        try(FileInputStream inputStream = new FileInputStream(pUrl)){
            byte[] getData = readInputStream(inputStream);
            result = Base64.getEncoder().encodeToString(getData);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String base64Change1(String pUrl)
    {
        String result = "";
        try
        {
            URL url = new URL(pUrl);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setConnectTimeout(5 * 1000);
            InputStream inputStream = conn.getInputStream();
            byte[] getData = readInputStream(inputStream);
            result = Base64.getEncoder().encodeToString(getData);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return result;
    }

    public static String readByteToBase64(byte[] data)
    {
        return Base64.getEncoder().encodeToString(data);
    }

    public static String readInputStreamToBase64(InputStream in)
    {
        return readByteToBase64(readInputStream(in));
    }
}
