package com.xiaoxin.experience.util;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;

/**
 * @author xiaoxin
 */
public class XmlUtil {

    private static final Logger LOG = LoggerFactory.getLogger(XmlUtil.class);

    public static String toXml(Object object)
    {
        if (null == object)
        {
            return "";
        }

        try
        {
            Serializer serializer = new Persister();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(8);

            serializer.write(object, byteArrayOutputStream);
            return new String(byteArrayOutputStream.toByteArray());
        }
        catch (Exception e)
        {
            LOG.error("toXml fail.", e);
            return "";
        }
    }
}
