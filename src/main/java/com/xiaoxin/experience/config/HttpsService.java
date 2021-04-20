package com.xiaoxin.experience.config;

import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author xiaoxin
 */
@Component
public class HttpsService extends AcHttpClient
{
    private static final Logger LOG = LoggerFactory.getLogger(HttpsClient.class);

    @Override
    protected OkHttpClient getHttpClient()
    {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true);
        builder.sslSocketFactory(IgnoredSSLSocketFactory.getSocketFactory(), IgnoredSSLSocketFactory.getX509TrustManager());
        builder.hostnameVerifier((hostname,session)->true);
        return builder.build();
    }

}
