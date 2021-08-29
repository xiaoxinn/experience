package com.xiaoxin.experience.download;

import org.springframework.http.client.ClientHttpResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * <p>Simple life, simple work, don't complicate</p>
 *
 * @author 小心
 * @version 1.0.0
 * @date 2021/8/29
 */
public class FileResponseExtractor extends AbstractDisplayDownloadSpeedResponseExtractor<File> {

    private long byteCount;

    private String filePath;

    public FileResponseExtractor(String filePath)
    {
        this.filePath = filePath;
    }

    @Override
    protected File doExtractData(ClientHttpResponse response)
            throws IOException
    {
        InputStream in = response.getBody();
        File file = new File(filePath);
        FileOutputStream out = new FileOutputStream(file);
        int bytesRead;
        for (byte[] buffer = new byte[4096];(bytesRead = in.read(buffer))  != -1; byteCount += bytesRead)
        {
            out.write(buffer,0,bytesRead);
        }
        out.flush();
        out.close();
        return file;
    }

    @Override
    protected long getAlreadyDownloadLength()
    {
        return byteCount;
    }
}
