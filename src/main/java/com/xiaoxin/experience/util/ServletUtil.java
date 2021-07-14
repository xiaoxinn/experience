package com.xiaoxin.experience.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Map;

/**
 * @author xiaoxin
 */
public class ServletUtil
{
    private ServletUtil(){}

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

}
