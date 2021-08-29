package com.xiaoxin.experience.m3u8;

import com.xiaoxin.experience.util.DownloadUtil;
import com.xiaoxin.experience.util.FileUtil;
import com.xiaoxin.experience.util.IOUtil;
import com.xiaoxin.experience.util.M3u8Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>Simple life, simple work, don't complicate</p>
 *
 * @author 小心
 * @version 1.0.0
 * @date 2021/8/20
 */
public class M3u8Download {

    private static final Logger log = LoggerFactory.getLogger(M3u8Download.class);

    private String fileName;

    private String savePath;

    private String m3u8Url;

    private String method;

    private String key;

    private String iv;

    private final String randomDir;

    private final Map<String,Integer> taskMap = new HashMap<>();

    private String[] tsFileNameArray;

    private final List<String> tsDownloadUrlList = new ArrayList<>();

    private final AtomicInteger downloadCount = new AtomicInteger(0);

    public String getFileName()
    {
        return fileName;
    }

    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    public String getSavePath()
    {
        return savePath;
    }

    public void setSavePath(String savePath)
    {
        this.savePath = savePath;
    }

    public String getM3u8Url()
    {
        return m3u8Url;
    }

    public void setM3u8Url(String m3u8Url)
    {
        this.m3u8Url = m3u8Url;
    }

    public String getMethod()
    {
        return method;
    }

    public void setMethod(String method)
    {
        this.method = method;
    }

    public M3u8Download(String fileName, String savePath, String m3u8Url)
    {
        this.fileName = fileName;
        this.savePath = savePath;
        this.m3u8Url = m3u8Url;
        this.randomDir = UUID.randomUUID().toString().substring(0,8) + "/";
    }

    public void download()
    {
        long start =  System.currentTimeMillis();
        // 通过下载链接获取索引文件名
        String index = m3u8Url.substring(m3u8Url.lastIndexOf("/") + 1);

        byte[] bytes = DownloadUtil.downloadUntilSuccess(m3u8Url);
        String content = new String(bytes, StandardCharsets.UTF_8);
        String[] lines = content.split("[\r\n]");
        fillDownload(lines);
        for (int i = 0; i < tsDownloadUrlList.size(); i++)
        {
            taskMap.put(tsDownloadUrlList.get(i),i);
        }

        // 通过url下载索引文件
        IOUtil.writeFile(DownloadUtil.saveFilePath(DownloadUtil.dirPathComplete(savePath) +  randomDir, index),bytes);

        tsFileNameArray = new String[tsDownloadUrlList.size()];
        int serial = 0;
        for (String tsDownloadUrl : tsDownloadUrlList)
        {
            int finalSerial = serial;
            serial++;
            DownloadThreadPool.execute(() -> {
                decodeAndSave(tsDownloadUrl, finalSerial);
                int i = downloadCount.incrementAndGet();
                log.debug("current download file speed: [{}/{}]",i,tsDownloadUrlList.size());
                taskMap.remove(tsDownloadUrl);
            });
        }
        waitTaskStop(5,60);
        retryTaskIfNeed();
        mergeTsToMp4();
        FileUtil.deleteDir(DownloadUtil.dirPathComplete(savePath) + randomDir);
        long end = System.currentTimeMillis();
        long seconds = TimeUnit.MILLISECONDS.toSeconds(end - start);
        log.debug("下载视频《{}》完成，共耗时： {}分{}秒", fileName, seconds/60, seconds%60);
    }

    public void fillDownload(String[] lines)
    {
        for (String line : lines) {
            //如果含有此字段，则获取加密算法以及获取密钥的链接
            if (line.contains("EXT-X-KEY"))
            {
                String[] split1 = line.split(",");
                for (String s1 : split1)
                {
                    if (s1.contains("METHOD"))
                    {
                        method = s1.split("=", 2)[1];
                    }
                    else if (s1.contains("URI"))
                    {
                        String keyDownloadUrl = s1.split("=", 2)[1];
                        keyDownloadUrl = keyDownloadUrl.replace("\"", "");
                        key = new String(DownloadUtil.downLoadTransToBytes(keyDownloadUrl),StandardCharsets.UTF_8);
                    }
                    else if (s1.contains("IV"))
                    {
                        iv = s1.split("=", 2)[1];
                    }
                }
            }
            if(line.endsWith(".ts"))
            {
                tsDownloadUrlList.add(completeTsUrlIfNeed(line));
            }
        }
    }

    public void waitTaskStop(int sleepTime, int waitTime)
    {
        int last;
        int currentWaitTime = 0;
        do
        {
            last = downloadCount.get();
            try
            {
                TimeUnit.SECONDS.sleep(sleepTime);
            }
            catch (InterruptedException e)
            {
                log.error("sleep fail: ",e);
                Thread.currentThread().interrupt();
            }
            currentWaitTime += sleepTime;
            if (currentWaitTime > waitTime)
            {
                currentWaitTime  = 0;
            }
        }while (notifyVerify(last,currentWaitTime,waitTime));
    }

    public boolean notifyVerify(int last,int currentTime, int waitTime)
    {
        if (taskMap.isEmpty())
        {
            return false;
        }
        if (currentTime >= waitTime)
        {
            return last < downloadCount.get();
        }
        return true;
    }

    public void retryTaskIfNeed()
    {
        Set<String> set = taskMap.keySet();
        if (set.isEmpty())
        {
            return;
        }
        DownloadThreadPool.restart();
        for (String s : set)
        {
            Integer integer = taskMap.get(s);
            decodeAndSave(s, integer);
            int i = downloadCount.incrementAndGet();
            log.debug("current download file speed: [{}/{}]", i ,tsDownloadUrlList.size());
        }
    }

    public String  completeTsUrlIfNeed(String tsDownloadUrl)
    {
        if (tsDownloadUrl.startsWith("http"))
        {
            return tsDownloadUrl;
        }
        return m3u8Url.substring(0,m3u8Url.lastIndexOf("/") + 1) + tsDownloadUrl;
    }

    public void mergeTsToMp4()
    {
        try(FileOutputStream fos = new FileOutputStream(DownloadUtil.saveFilePath(savePath,fileName + ".mp4")))
        {
            Arrays.stream(tsFileNameArray).forEach(tsFileName ->{
                byte[] bytes = IOUtil.readAllFileToBytes(tsFileName);
                IOUtil.outPutWriteUnClose(fos,bytes);
            });
        }
        catch (Exception e)
        {
            log.error("merge ts file fail: ",e);
        }
    }

    public void decodeAndSave(String tsDownloadUrl,int serial)
    {
        byte[] bytes = DownloadUtil.downloadUntilSuccess(tsDownloadUrl);
        byte[] decrypt = M3u8Util.decrypt(iv, key, bytes);
        String tsFileName = decodeFileName(tsDownloadUrl);
        String tsFileSavePath = DownloadUtil.saveFilePath(DownloadUtil.dirPathComplete(savePath) + randomDir, tsFileName);
        tsFileNameArray[serial] = tsFileSavePath;
        IOUtil.writeFile(tsFileSavePath,decrypt);
    }

    public String decodeFileName(String tsDownloadUrl)
    {
        return tsDownloadUrl.substring(tsDownloadUrl.lastIndexOf("/") + 1);
    }
}
