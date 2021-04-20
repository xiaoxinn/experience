package com.xiaoxin.experience.config;

import okhttp3.OkHttpClient;
import okhttp3.internal.tls.OkHostnameVerifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * @author xiaoxin
 */
public class HttpsClient extends AcHttpClient
{
    private static final Logger LOG = LoggerFactory.getLogger(HttpsClient.class);

    public static final String KEY_PASS_DEF = "Allcam_https";

    private static final int TIMEOUT_CONNECT = 10;

    private static final int TIMEOUT_READ_WRITE = 10;

    private OkHttpClient client;

    public HttpsClient()
    {
        build(OkHostnameVerifier.INSTANCE, null, null);
    }

    public HttpsClient(String certPath, String keyPass)
    {
        try (InputStream ins = new FileInputStream(certPath))
        {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(ins, keyPass.toCharArray());

            TrustManagerFactory trustManagerFactory =
                    TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(trustStore);
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            if (trustManagers.length == 0 || !(trustManagers[0] instanceof X509TrustManager))
            {
                throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers));
            }
            X509TrustManager trustManager = (X509TrustManager)trustManagers[0];

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[] {trustManager}, new SecureRandom());

            build(OkHostnameVerifier.INSTANCE, sslContext.getSocketFactory(), trustManager);
        }
        catch (Throwable e)
        {
            LOG.error("getSocketFactory fail: ", e);
            throw new IllegalStateException("init with certPath and keyPass fail.");
        }
    }

    public HttpsClient(HostnameVerifier hostnameVerifier, SSLSocketFactory sslSocketFactory)
    {
        build(hostnameVerifier, sslSocketFactory, IgnoredSSLSocketFactory.getX509TrustManager());
    }

    public HttpsClient(HostnameVerifier hostnameVerifier, SSLSocketFactory sslSocketFactory,
                       X509TrustManager trustManager)
    {
        build(hostnameVerifier, sslSocketFactory, trustManager);
    }

    private void build(HostnameVerifier hostnameVerifier, SSLSocketFactory sslSocketFactory,
                       X509TrustManager trustManager)
    {
        OkHttpClient.Builder builder = new OkHttpClient.Builder().connectTimeout(TIMEOUT_CONNECT, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT_READ_WRITE, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT_READ_WRITE, TimeUnit.SECONDS)
                .retryOnConnectionFailure(false);

        if (null != hostnameVerifier)
        {
            builder.hostnameVerifier(hostnameVerifier);
        }

        if (null != sslSocketFactory)
        {
            builder.sslSocketFactory(sslSocketFactory, trustManager);
        }

        this.client = builder.build();
    }

    @Override
    protected OkHttpClient getHttpClient()
    {
        return client;
    }
}
