package com.xiaoxin.experience.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Map;

/**
 * @author xiaoxin
 */
public class ServletUtil
{
    private static final Logger LOG = LoggerFactory.getLogger(ServletUtil.class);

    public static void onResponseJson(HttpServletResponse response, String respString)
    {
        onResponse(response, respString, "application/json");
    }

    public static void onResponseXml(HttpServletResponse response, String respString)
    {
        onResponse(response, respString, "application/xml");
    }

    public static void onResponse(HttpServletResponse response, String respString, String contentType)
    {
        LOG.debug("send response: {}", respString);
        try
        {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType(contentType);
            PrintWriter out = response.getWriter();
            out.println(respString);
            out.flush();
            out.close();
        }
        catch (Exception e)
        {
            LOG.error("onResponse fail: ", e);
        }
    }

    public static String buildUrlWithParams(String url, Map<String, String> params)
    {
        if (!CollectionUtils.isEmpty(params))
        {
            StringBuilder strBuilder = new StringBuilder(url);
            boolean first = true;
            for (Map.Entry<String, String> entry : params.entrySet())
            {
                strBuilder.append(first ? '?' : '&');
                strBuilder.append(entry.getKey()).append('=').append(entry.getValue());
                first = false;
            }
            url = strBuilder.toString();
        }
        return url;
    }

    public static String appendUrlWithParam(String url, String name, String value)
    {
        if (StringUtils.isBlank(url))
        {
            return url;
        }

        String pChar = url.contains("?") ? "&" : "?";
        return url.concat(pChar).concat(name).concat("=").concat(value);
    }

    private static String getParamValue(String source, String key)
    {
        if (null != source && source.startsWith(key))
        {
            int sepIndex = source.indexOf("=");
            if (sepIndex > 0)
            {
                String value = source.substring(sepIndex + 1);
                return value.replaceAll("\"", "");
            }
        }
        return null;
    }

    public static String getRequestIPAddress(HttpServletRequest request)
    {
        String ip = request.getHeader("x-forwarded-for");
        if (!StringUtil.validate(ip))
        {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (!StringUtil.validate(ip))
        {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (!StringUtil.validate(ip))
        {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

}
