package com.xiaoxin.experience.config;

import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

/**
 * <配置restTemplate支持发送https请求>
 * @author xiaoxin
 *
 * 配置前提: springboot web项目
 * 所需maven依赖为:
 *
 *         <dependency>
 *             <groupId>org.apache.httpcomponents</groupId>
 *             <artifactId>httpclient</artifactId>
 *             <version>4.5.3</version>
 *         </dependency>
 */
@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate httpsRestTemplate(HttpComponentsClientHttpRequestFactory httpsFactory){

        RestTemplate restTemplate = new RestTemplate(httpsFactory);
        restTemplate.setErrorHandler(
                new ResponseErrorHandler()
                {
                    @Override
                    public boolean hasError(ClientHttpResponse clientHttpResponse) {
                        return false;
                    }

                    @Override
                    public void handleError(ClientHttpResponse clientHttpResponse)
                    {
                        // 默认处理非200的返回，会抛异常
                    }
                });
        return restTemplate;
    }

    @Bean
    public HttpComponentsClientHttpRequestFactory httpRequestFactory()
    {
        CloseableHttpClient closeableHttpClient = null;
        try {
            closeableHttpClient = acceptsUntrustedCertsHttpClient();
        } catch (KeyStoreException | NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }
        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        assert closeableHttpClient != null;
        httpRequestFactory.setHttpClient(closeableHttpClient);
        httpRequestFactory.setConnectTimeout(10000);
        httpRequestFactory.setReadTimeout(10000);
        return httpRequestFactory;
    }

    public CloseableHttpClient acceptsUntrustedCertsHttpClient()
            throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException
    {
        HttpClientBuilder b = HttpClientBuilder.create();

        // setup a Trust Strategy that allows all certificates.
        //
        SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, (TrustStrategy) (arg0, arg1) -> true).build();
        b.setSSLContext(sslContext);

        // don't check Hostnames, either.
        //      -- use SSLConnectionSocketFactory.getDefaultHostnameVerifier(), if you don't want to weaken
        HostnameVerifier hostnameVerifier = NoopHostnameVerifier.INSTANCE;

        // here's the special part:
        //      -- need to create an SSL Socket Factory, to use our weakened "trust strategy";
        //      -- and create a Registry, to register it.
        //
        SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext, hostnameVerifier);
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", sslSocketFactory)
                .build();

        // now, we create connection-manager using our Registry.
        //      -- allows multi-threaded use
        PoolingHttpClientConnectionManager connMgr = new PoolingHttpClientConnectionManager( socketFactoryRegistry);
        connMgr.setMaxTotal(200);
        connMgr.setDefaultMaxPerRoute(100);
        b.setConnectionManager( connMgr);

        // finally, build the HttpClient;
        //      -- done!

        return b.build();
    }

}
