package com.xiaoxin.experience.config;

/**
 * @author xiaoxin
 */
import com.xiaoxin.experience.util.JsonUtil;
import com.xiaoxin.experience.util.ServletUtil;
import okhttp3.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.util.Map;

public abstract class AcHttpClient
{
    private static final Logger LOG = LoggerFactory.getLogger(AcHttpClient.class);

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private static final MediaType XML = MediaType.parse("application/xml; charset=utf-8");

    protected abstract OkHttpClient getHttpClient();

    public <T> T postJson4Object(String url, String json, Class<T> retClass)
            throws IOException
    {
        String retStr = postJson(url, json);
        if (StringUtils.isEmpty(retStr))
        {
            return null;
        }
        return JsonUtil.fromJson(retStr, retClass);
    }

    public String postJson(String url, String json)
            throws IOException
    {
        return post(url, json, JSON);
    }

    public String postXml(String url, String xml)
            throws IOException
    {
        return post(url, xml, XML);
    }

    public String post(String url, String body, MediaType type)
            throws IOException
    {
        LOG.debug("Http(url,body,type) SYNC post: \n url=[{}] \n body=[{}]", url, body);

        RequestBody reqBody = RequestBody.create(type, body);
        Request request = new Request.Builder()
                .url(url)
                .post(reqBody)
                .build();

        return exeHttp(request);
    }

    public <T> T post4Json(String url, Map<String, String> params, Map<String, String> headers, Class<T> retClass)
            throws IOException
    {
        String retStr = post(url, params, headers);
        if (StringUtils.isEmpty(retStr))
        {
            return null;
        }
        return JsonUtil.fromJson(retStr, retClass);
    }

    public String post(String url, Map<String, String> params, Map<String, String> headers)
            throws IOException
    {
        url = ServletUtil.buildUrlWithParams(url, params);

        Request.Builder reqBuilder = new Request.Builder();
        if (!CollectionUtils.isEmpty(headers))
        {
            headers.forEach(reqBuilder::addHeader);
        }

        RequestBody reqBody = RequestBody.create(null, "");
        LOG.debug("Http(host,port,params,headers) SYNC post: \n url=[{}] \n body=[{}]", url, reqBody);

        return exeHttp(reqBuilder.url(url).post(reqBody).build());
    }

    public String postForm(String url, Map<String, String> formMap, Map<String, String> headers)
            throws IOException
    {
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        if (!CollectionUtils.isEmpty(formMap))
        {
            formMap.forEach(formBodyBuilder::add);
        }
        FormBody formBody = formBodyBuilder.build();

        Request.Builder requestBuilder = new Request.Builder();
        if (!CollectionUtils.isEmpty(headers))
        {
            headers.forEach(requestBuilder::addHeader);
        }

        return exeHttp(requestBuilder.post(formBody).url(url).build());
    }

    public String postFormEncoded(String url, Map<String, String> formMap, Map<String, String> headers,
                                  boolean alreadyEncoded)
            throws IOException
    {
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        if (!CollectionUtils.isEmpty(formMap))
        {
            formMap.forEach(alreadyEncoded ? formBodyBuilder::addEncoded : formBodyBuilder::add);
        }
        FormBody formBody = formBodyBuilder.build();

        Request.Builder requestBuilder = new Request.Builder();
        if (!CollectionUtils.isEmpty(headers))
        {
            headers.forEach(requestBuilder::addHeader);
        }

        return exeHttp(requestBuilder.post(formBody).url(url).build());
    }

    public String postJson(String url, Map<String, String> headers, String json)
            throws IOException
    {
        RequestBody body = RequestBody.create(JSON, json);

        Request.Builder requestBuilder = new Request.Builder();
        if (!CollectionUtils.isEmpty(headers))
        {
            headers.forEach(requestBuilder::addHeader);
        }

        return exeHttp(requestBuilder.post(body).url(url).build());
    }

    public String get(String url)
            throws IOException
    {
        LOG.debug("Http(url) SYNC get: \n url=[{}] ", url);

        return exeHttp(new Request.Builder()
                .url(url)
                .addHeader("Connection", "close")
                .get()
                .build());
    }

    private String exeHttp(Request request)
            throws IOException
    {
        try (Response response = getHttpClient().newCall(request).execute())
        {
            if (!response.isSuccessful())
            {
                //throw new HttpRequestException(response.code());
                throw new RuntimeException(response.code() + "");
            }

            ResponseBody responseBody = response.body();
            if (null == responseBody)
            {
                LOG.warn("response success, but response body is empty.");
                return null;
            }

            String bodyString = responseBody.string();
            LOG.debug("Http Response body:\n {}", bodyString);
            return bodyString;
        }
    }

    public void asyncPostJson(String url, String json, Callback callback)
    {
        asyncPost(url, JSON, json, callback);
    }

    public void asyncPostXml(String url, String xml, Callback callback)
    {
        asyncPost(url, XML, xml, callback);
    }

    public void asyncPost(String url, MediaType type, String body, Callback callback)
    {
        LOG.debug("Http(url,type,body,callback) ASYNC post: \n url=[{}] \n body=[{}]", url, body);

        RequestBody reqBody = RequestBody.create(type, body);
        Request request = new Request.Builder()
                .url(url)
                .post(reqBody)
                .build();

        if (null == callback)
        {
            callback = new Callback()
            {
                @Override
                public void onFailure(Call call, IOException e)
                {
                    LOG.debug("default callback: onFailure()");
                }

                @Override
                public void onResponse(Call call, Response response)
                {
                    LOG.debug("default callback: onResponse()");
                    response.close();
                }
            };
        }

        getHttpClient().newCall(request).enqueue(callback);
    }

    public byte[] download(String url)
    {
        LOG.debug("AcHttpClient down from url:[{}]", url);

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Connection", "close")
                .build();

        try (Response response = getHttpClient().newCall(request).execute())
        {
            ResponseBody body = response.body();
            if (null == body)
            {
                return null;
            }

            if (!response.isSuccessful())
            {
                throw new IllegalStateException("download file error code: " + response);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (InputStream is = body.byteStream())
            {
                IOUtils.copy(is, baos, 1024);
            }

            return baos.toByteArray();
        }
        catch (Exception e)
        {
            LOG.error("download file fail.", e);
            return null;
        }
    }

    public int downloadFile(String url, String filePath)
    {
        LOG.debug("AcHttpClient downFile from [{}] to [{}]", url, filePath);

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Connection", "close")
                .build();

        try (Response response = getHttpClient().newCall(request).execute())
        {
            ResponseBody body = response.body();
            if (null == body)
            {
                return 1;
            }

            if (!response.isSuccessful())
            {
                throw new IllegalStateException("download file error code: " + response);
            }

            File file = new File(filePath);
            try (InputStream is = body.byteStream();
                 FileOutputStream fos = new FileOutputStream(file))
            {
                IOUtils.copy(is, fos, 4096);
            }

            return 0;
        }
        catch (Exception e)
        {
            LOG.error("download file fail.", e);
            return -1;
        }
    }

    public int uploadFile(String url, File file, String fileName, String mediaType, Map<String, String> partMap)
    {
        Assert.hasText(url, "url is empty");
        Assert.notNull(file, "file is null");
        Assert.hasText(fileName, "file name is empty");
        Assert.hasText(mediaType, "media type is empty");

        RequestBody fileBody = RequestBody.create(MediaType.parse(mediaType), file);

        MultipartBody.Builder bodyBuilder = new MultipartBody.Builder();
        bodyBuilder.setType(MultipartBody.FORM)
                .addFormDataPart("file", fileName, fileBody);
        if (null != partMap && !partMap.isEmpty())
        {
            partMap.forEach(bodyBuilder::addFormDataPart);
        }

        Request request = new Request.Builder()
                .url(url)
                .post(bodyBuilder.build())
                .build();

        try (Response response = getHttpClient().newCall(request).execute())
        {
            ResponseBody body = response.body();
            if (null == body)
            {
                return 1;
            }

            if (!response.isSuccessful())
            {
                throw new IllegalStateException("upload file error code: " + response);
            }

            return 0;
        }
        catch (Exception e)
        {
            LOG.error("upload file fail.", e);
            return -1;
        }
    }

    public int uploadFile(String url, byte[] content, String fileName, String mediaType, Map<String, String> partMap)
    {
        Assert.hasText(url, "url is empty");
        Assert.isTrue(content != null && content.length > 0, "content is empty");
        Assert.hasText(fileName, "file name is empty");
        Assert.hasText(mediaType, "media type is empty");

        RequestBody fileBody = RequestBody.create(MediaType.parse(mediaType), content);

        MultipartBody.Builder bodyBuilder = new MultipartBody.Builder();
        bodyBuilder.setType(MultipartBody.FORM)
                .addFormDataPart("file", fileName, fileBody);
        if (null != partMap && !partMap.isEmpty())
        {
            partMap.forEach(bodyBuilder::addFormDataPart);
        }

        Request request = new Request.Builder()
                .url(url)
                .post(bodyBuilder.build())
                .build();

        try (Response response = getHttpClient().newCall(request).execute())
        {
            ResponseBody body = response.body();
            if (null == body)
            {
                return 1;
            }

            if (!response.isSuccessful())
            {
                throw new IllegalStateException("upload file error code: " + response);
            }

            return 0;
        }
        catch (Exception e)
        {
            LOG.error("upload file fail.", e);
            return -1;
        }
    }
}

