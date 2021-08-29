package com.xiaoxin.experience.download;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseExtractor;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * <p>Simple life, simple work, don't complicate</p>
 *
 * @author 小心
 * @version 1.0.0
 * @date 2021/8/29
 */
public abstract class AbstractDisplayDownloadSpeedResponseExtractor<T> implements ResponseExtractor<T>,DisplayDownloadSpeed {
    private static final Logger log = LoggerFactory.getLogger(AbstractDisplayDownloadSpeedResponseExtractor.class);

    @Override
    public T extractData(ClientHttpResponse response)
            throws IOException
    {

        long contentLength = response.getHeaders().getContentLength();
        this.displaySpeed(Thread.currentThread().getName(),contentLength);
        return this.doExtractData(response);
    }

    protected abstract T doExtractData(ClientHttpResponse response) throws IOException;

    protected abstract long getAlreadyDownloadLength();

    @Override
    public void displaySpeed(String task, long contentLength)
    {
        long totalSize = contentLength / 1024;
        CompletableFuture.runAsync(() -> {
            long tmp = 0;
            long speed;
            while (contentLength - tmp > 0)
            {
                speed = getAlreadyDownloadLength() - tmp;
                tmp = getAlreadyDownloadLength();
                print(task,totalSize,tmp,speed);
                sleep();
            }
        });
    }

    protected void print(String task, long totalSize, long tmp, long speed)
    {
        log.debug("{}文件总大小: {}KB, 已下载： {}KB, 下载速度： {}KB",task,totalSize,tmp/1024,speed/1024);
    }

    private void sleep()
    {
        try
        {
            TimeUnit.SECONDS.sleep(1);
        }
        catch (InterruptedException e)
        {
            log.error("sleep fail: ",e);
        }
    }
}
