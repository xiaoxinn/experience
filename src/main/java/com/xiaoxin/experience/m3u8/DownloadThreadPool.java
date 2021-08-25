package com.xiaoxin.experience.m3u8;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * <p>Simple life, simple work, don't complicate</p>
 *
 * @author 小心
 * @version 1.0.0
 * @date 2021/8/21
 */
public class DownloadThreadPool {

    private static final Logger log = LoggerFactory.getLogger(DownloadThreadPool.class);

    private DownloadThreadPool() {}

    private static ThreadPoolExecutor threadPoolExecutor;

    static {
        threadPoolExecutor = new ThreadPoolExecutor(
                12,
                12,
                60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy()
        );
        threadPoolExecutor.allowCoreThreadTimeOut(true);
    }

    public static void execute(Runnable r)
    {
        threadPoolExecutor.execute(r);
    }

    public static void stop()
    {
        threadPoolExecutor.shutdownNow();
    }

    public static void restart()
    {
        log.debug("重新启动线程池");
        stop();
        threadPoolExecutor = new ThreadPoolExecutor(
                12,
                12,
                60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy()
        );
        threadPoolExecutor.allowCoreThreadTimeOut(true);
    }
}
