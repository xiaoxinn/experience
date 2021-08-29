package com.xiaoxin.experience.download;

import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.concurrent.CompletableFuture;
import com.xiaoxin.experience.config.RestTemplateConfig;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <p>Simple life, simple work, don't complicate</p>
 *
 * @author 小心
 * @version 1.0.0
 * @date 2021/8/29
 */
public class DownloadMain {

    public static void main(String[] args) throws IOException
    {
        multiThreadDownload("http://139.9.183.199:10608/release/allmedia-x86_64-v1.18.0-gcc4.8.5-20210828165217.tar.gz","f:/allmedia-x86.tar.gz",50);
    }

    public static void downloadToMemory(String fileUrl, String filePath)
        throws IOException
    {
        long start = System.currentTimeMillis();
        RestTemplateConfig restTemplateConfig = new RestTemplateConfig();
        RestTemplate restTemplate = restTemplateConfig.httpsRestTemplate(restTemplateConfig.httpRequestFactory());
        byte[] body = restTemplate.execute(fileUrl, HttpMethod.GET, null, new ByteArrayResponseExtractor());
        Files.write(Paths.get(filePath), Objects.requireNonNull(body));
        System.out.println("总共下载文件耗时： " + (System.currentTimeMillis() - start) / 1000 + "秒");
    }

    public static void downloadToFile(String fileUrl, String filePath)
            throws IOException
    {
        long start = System.currentTimeMillis();
        RestTemplateConfig restTemplateConfig = new RestTemplateConfig();
        RestTemplate restTemplate = restTemplateConfig.httpsRestTemplate(restTemplateConfig.httpRequestFactory());
        File tmpFile = restTemplate.execute(fileUrl, HttpMethod.GET, null, new FileResponseExtractor(filePath + ".download"));
        assert tmpFile != null;
        tmpFile.renameTo(new File(filePath));
        System.out.println("总共下载文件耗时： " + (System.currentTimeMillis() - start) / 1000 + "秒");
    }

    public static void multiThreadDownload(String fileUrl,String filePath, int threadNum) throws IOException
    {
        System.out.println("开始下载文件...");
        ExecutorService executorService = Executors.newFixedThreadPool(threadNum);

        long startTime = System.currentTimeMillis();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> requestEntity = new HttpEntity<>(null,headers);
        RestTemplateConfig restTemplateConfig = new RestTemplateConfig();
        RestTemplate restTemplate = restTemplateConfig.httpsRestTemplate(restTemplateConfig.httpRequestFactory());
        ResponseEntity<String> entity = restTemplate.exchange(fileUrl, HttpMethod.HEAD, requestEntity, String.class);

        long contentLength = entity.getHeaders().getContentLength();

        long step = contentLength / threadNum;

        List<CompletableFuture<File>> futures = new ArrayList<>();
        for (int index = 0; index < threadNum; index++)
        {
            String start = step + index + "";
            String end = index == threadNum - 1 ? "": (step * (index + 1) -1) + "";
            FileResponseExtractor extractor = new FileResponseExtractor(filePath + ".download." + index);

            CompletableFuture<File> future = CompletableFuture.supplyAsync(() -> {
                RequestCallback callback = request -> {
                    request.getHeaders().add(HttpHeaders.RANGE, "bytes=" + start + "-" + end);
                };
                return restTemplate.execute(fileUrl, HttpMethod.GET, callback, extractor);
            }, executorService).exceptionally(e -> {
                e.printStackTrace();
                return null;
            });
            futures.add(future);
        }

        FileChannel outChannel = new FileOutputStream(new File(filePath)).getChannel();
        futures.forEach(future ->{
            try {
                File tmpFile = future.get();
                FileChannel tmpIn = new FileOutputStream(tmpFile).getChannel();
                outChannel.transferFrom(tmpIn,outChannel.size(),tmpIn.size());
                tmpIn.close();
                tmpFile.delete();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        });
        outChannel.close();
        executorService.shutdown();
        System.out.println("下载文件完成，总共耗时：" + (System.currentTimeMillis() - startTime)/1000 + "s");
    }
}
