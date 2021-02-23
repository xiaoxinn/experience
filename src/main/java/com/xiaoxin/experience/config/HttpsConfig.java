package com.xiaoxin.experience.config;

import org.apache.catalina.connector.Connector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.apache.catalina.Context;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;

import javax.annotation.Resource;

/**
 * <将服务配置成同时支持http服务和https服务>
 * @author xiaoxin
 * @version 2021/2/23
 */
@Configuration
public class HttpsConfig {

    @Resource
    private ServerProperties serverProperties;

    @Value("${http.server.port}")
    private int httpPort;

    @Bean
    public TomcatServletWebServerFactory servletContainer()
    {
        return servletContainer(httpConnector());
    }

    @Bean
    public Connector httpConnector()
    {
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setScheme("http");

        //Connector监听的http的端口号
        connector.setPort(httpPort);
        connector.setSecure(true);

        //监听到http的端口号后转向到的https的端口号
        connector.setRedirectPort(serverProperties.getPort());
        return connector;
    }

    private TomcatServletWebServerFactory servletContainer(Connector httpConnector)
    {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory()
        {
            @Override
            protected void postProcessContext(Context context)
            {
                SecurityConstraint constraint = new SecurityConstraint();
                constraint.setUserConstraint("CONFIDENTIAL");
                SecurityCollection collection = new SecurityCollection();
                collection.addPattern("/*");
                constraint.addCollection(collection);
                context.addConstraint(constraint);
            }
        };
        tomcat.addAdditionalTomcatConnectors(httpConnector);
        return tomcat;
    }
}
