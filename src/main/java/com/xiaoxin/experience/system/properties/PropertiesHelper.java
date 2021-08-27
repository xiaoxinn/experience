package com.xiaoxin.experience.system.properties;

import com.xiaoxin.experience.system.exception.SystemException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * @author chenjing
 */
public class PropertiesHelper
{
    private PropertiesHelper(){}

    private static final Logger LOG = LoggerFactory.getLogger(PropertiesHelper.class);

    static final String CACHE_PATH = "cache";

    static final String PROP_CACHE_NAME = "self-modifying.properties";

    static final String BASE_PATH = System.getProperty("user.dir");

    public static void cachePropertiesToDisk(Properties properties)
    {
        String cacheName = basePath() + File.separator + CACHE_PATH + File.separator + PROP_CACHE_NAME;
        cachePropertiesToDisk(properties, cacheName);
    }

    static void cachePropertiesToDisk(Properties properties, String cacheName)
    {
        LOG.debug("cache properties to disk Begin.");

        if (StringUtils.isEmpty(cacheName))
        {
            return;
        }

        try (FileOutputStream fos = new FileOutputStream(cacheName))
        {
            properties.store(fos, "cache properties from user change.");
            LOG.debug("cache properties to disk Finish.");
        }
        catch (IOException e)
        {
            LOG.error("cache properties to disk fail.", e);
        }
    }

    public static Properties loadPropertiesFromDisk()
    {
        String cacheName = basePath() + File.separator + CACHE_PATH + File.separator + PROP_CACHE_NAME;

        LOG.debug("cacheName : {}" , cacheName);

        return loadPropertiesFromDisk(cacheName);
    }

    static Properties loadPropertiesFromDisk(String cacheName)
    {
        LOG.debug("load properties from disk Begin.");

        if (StringUtils.isEmpty(cacheName))
        {
            return null;
        }

        File cacheFile = new File(cacheName);
        if (!cacheFile.exists())
        {
            LOG.warn("cache properties not exist on disk.");
            return null;
        }

        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(cacheFile), StandardCharsets.UTF_8))
        {
            Properties properties = new Properties();
            properties.load(reader);
            LOG.debug("load properties from disk Finish.");
            return properties;
        }
        catch (IOException e)
        {
            LOG.error("load properties from disk fail.", e);
            return null;
        }
    }

    static void createCacheFile()
    {
        File path = new File(basePath() + File.separator + CACHE_PATH);
        if (!path.exists())
        {
            boolean isSuccess = path.mkdir();

            if (!isSuccess)
            {
                throw new SystemException("create directory fail");
            }
        }

        try(OutputStreamWriter ignored = new OutputStreamWriter(new FileOutputStream(path + File.separator + PROP_CACHE_NAME, true), StandardCharsets.UTF_8);)
        {
            LOG.debug("create file success");
        }
        catch (IOException e)
        {
            LOG.error("create file fail.", e);
        }
    }

    static String basePath()
    {
        String lastBasePath = BASE_PATH;
        String containsPath = File.separator + "app";
        if (lastBasePath.contains(containsPath))
        {
            lastBasePath = lastBasePath.substring(0, lastBasePath.lastIndexOf(containsPath));
        }
        return lastBasePath;
    }
}
