package com.xiaoxin.experience.system.properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;

import java.util.Properties;

/**
 * @author xiaoxin
 * 加载外部配置文件
 */
public class LoadEnvironmentPropertiesProcessor implements EnvironmentPostProcessor, Ordered
{
    private static final Logger LOG = LoggerFactory.getLogger(LoadEnvironmentPropertiesProcessor.class);

    @Override
    public int getOrder()
    {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application)
    {
        //此时日志组件还没有初始化，暂时同时使用标准输出打印
        LOG.debug("postProcessEnvironment Begin.");

        PropertiesHelper.createCacheFile();

        Properties cacheProperties = PropertiesHelper.loadPropertiesFromDisk();
        if (null == cacheProperties)
        {
            LOG.error("load cache properties fail.");
            return;
        }

        LOG.debug("postProcessEnvironment cache properties is: {}", cacheProperties);

        MutablePropertySources propertySources = environment.getPropertySources();
        //addLast 结合下面的 getOrder() 保证顺序
        propertySources.addLast(new PropertiesPropertySource("applicationConfig", cacheProperties));

        LOG.debug("postProcessEnvironment Finish.");
    }

}

