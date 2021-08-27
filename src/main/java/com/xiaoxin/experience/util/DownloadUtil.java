package com.xiaoxin.experience.util;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

/**
 * <p>Simple life, simple work, don't complicate</p>
 *
 * @author 小心
 * @version 1.0.0
 * @date 2021/7/17
 */
@Slf4j
public class DownloadUtil {

    private DownloadUtil() {}

    /**
     * 从网络Url中下载文件
     */
    public static void downLoadFromUrl(String sourceFileUrl, String fileName, String savePath)
    {
        log.debug("start download ...");
        FileOutputStream fos = null;
        try
        {
            byte[] getData = downLoadTransToBytes(sourceFileUrl);
            if (getData.length == 0)
            {
                log.error("down load fail ...");
                return;
            }
            fos = new FileOutputStream(new File(saveFilePath(savePath, fileName)));
            fos.write(getData);
            log.debug("down load file success !");
        }
        catch (IOException e)
        {
            e.printStackTrace();
            log.error("down load fail ...");
        }
        finally
        {
            if (Objects.nonNull(fos))
            {
                try
                {
                    fos.close();
                }
                catch (IOException e)
                {
                    log.error("close write stream fail");
                }
            }
        }
    }

    public static byte[] downLoadTransToBytes(String sourceFileUrl)
    {
        log.debug("start download ...");
        InputStream inputStream = null;
        try
        {
            URL url = new URL(sourceFileUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //设置超时间为3秒
            conn.setConnectTimeout(3 * 1000);
            //防止屏蔽程序抓取而返回403错误
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            //得到输入流
            inputStream = conn.getInputStream();
            //获取自己数组
            log.debug("downLoad trans success !");
            return readInputStream(inputStream);
        }
        catch (Exception e)
        {
            log.error("down fail :",e);
        }
        finally
        {
            if (Objects.nonNull(inputStream))
            {
                try
                {
                    inputStream.close();
                }
                catch (IOException e)
                {
                    log.error("close input stream fail");
                }
            }
        }
        return new byte[0];
    }

    public static String dirPathComplete(String dirPath)
    {
        if (dirPath.endsWith("/"))
        {
            return dirPath;
        }
        return dirPath + File.separator;
    }

    public static String saveFilePath(String dirPath, String fileName)
    {
        if (dirPath == null || dirPath.trim().length() == 0)
        {
            return fileName;
        }
        File saveDir = new File(dirPath);
        if (!saveDir.exists())
        {
            boolean mkdirs = saveDir.mkdirs();
            if (!mkdirs)
            {
                log.error("create dir fail,save in current dir");
                return fileName;
            }
        }

        return dirPathComplete(dirPath) + fileName;
    }

    /**
     * 从输入流中获取字节数组
     */
    private static byte[] readInputStream(InputStream inputStream)
            throws IOException
    {
        byte[] buffer = new byte[1024];
        int len;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = inputStream.read(buffer)) != -1)
        {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }

    public static byte[] downloadUntilSuccess(String sourceFileUrl)
    {
        byte[] bytes = downLoadTransToBytes(sourceFileUrl);
        while (bytes.length == 0)
        {
            bytes = downLoadTransToBytes(sourceFileUrl);
        }
        return bytes;
    }

    public static byte[] downloadLimitRetry(String sourceFileUrl, int retry)
    {
        byte[] bytes = downLoadTransToBytes(sourceFileUrl);
        while (retry  > 0)
        {
            retry--;
            if (bytes.length == 0)
            {
                bytes = downLoadTransToBytes(sourceFileUrl);
            }
        }
        return bytes;
    }
}
