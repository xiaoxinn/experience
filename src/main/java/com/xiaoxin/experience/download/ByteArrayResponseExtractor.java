package com.xiaoxin.experience.download;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * <p>Simple life, simple work, don't complicate</p>
 *
 * @author 小心
 * @version 1.0.0
 * @date 2021/8/29
 */
public class ByteArrayResponseExtractor extends AbstractDisplayDownloadSpeedResponseExtractor<byte[]> {

    private long byteCount;
    @Override
    protected byte[] doExtractData(ClientHttpResponse response)
            throws IOException
    {
        long contentLength = response.getHeaders().getContentLength();
        ByteArrayOutputStream out = new ByteArrayOutputStream(contentLength >= 0 ? (int) contentLength : StreamUtils.BUFFER_SIZE);
        InputStream in = response.getBody();
        int bytesRead;
        for (byte[] buffer = new byte[4096];(bytesRead = in.read(buffer))  != -1; byteCount += bytesRead)
        {
            out.write(buffer,0,bytesRead);
        }
        out.flush();
        return out.toByteArray();
    }

    @Override
    protected long getAlreadyDownloadLength()
    {
        return byteCount;
    }
}
