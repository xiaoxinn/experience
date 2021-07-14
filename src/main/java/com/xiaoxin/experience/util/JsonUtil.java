package com.xiaoxin.experience.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xiaoxin
 */
public class JsonUtil {

    private JsonUtil(){}

    private static final Logger LOG = LoggerFactory.getLogger(JsonUtil.class);

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static String toJson(Object obj)
    {
        String json = "";
        try {
            json = OBJECT_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            LOG.error("Convert object to JSON string fail",e);
        }
        return json;
    }

    public static <T> T fromJson(String jsonStr, Class<T> clz)
    {
        try
        {
            return OBJECT_MAPPER.readValue(jsonStr, clz);
        }
        catch (Exception e)
        {
            LOG.error("Conversion JSON string to " + clz.getName() + " fail",e);
            return null;
        }
    }

    public static Map<String, String> jsonToMap(String jsonStr)
    {
        try
        {
            JavaType jvt =
                    OBJECT_MAPPER.getTypeFactory().constructParametricType(HashMap.class, String.class, String.class);
            return OBJECT_MAPPER.readValue(jsonStr, jvt);
        }
        catch (Exception e)
        {
            return new HashMap<>();
        }
    }

    public static <T> List<T> jsonToList(String jsonStr, Class<T> clz)
    {
        try
        {
            JavaType jt = OBJECT_MAPPER.getTypeFactory().constructParametricType(ArrayList.class, clz);
            return OBJECT_MAPPER.readValue(jsonStr, jt);
        }
        catch (IOException e)
        {
            return new ArrayList<>();
        }
    }
}
