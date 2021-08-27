package com.xiaoxin.experience.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.util.Collections;
import java.util.List;

/**
 * <p>Simple life, simple work, don't complicate</p>
 *
 * @author 小心
 * @version 1.0.0
 * @date 2021/8/16
 */
@Slf4j
public class IOUtil {

    private IOUtil() {}

    public static byte[] readAllFileToBytes(String filePath)
    {
        File file = new File(filePath);
        int length = (int) file.length();
        byte[] bytes = new byte[length];
        try(FileInputStream fis =  new FileInputStream(filePath))
        {
            fis.read(bytes, 0, length);
        }
        catch (Exception e)
        {
            log.error("read file fail :",e);
            return new byte[0];
        }
        return bytes;
    }

    public static void writeFile(String fileName,byte[] bytes)
    {
        try(FileOutputStream fos = new FileOutputStream(fileName))
        {
            fos.write(bytes);
            fos.flush();
        }
        catch (Exception e)
        {
            log.error("write file fail: ",e);
        }
    }

    public static void outPutWriteUnClose(OutputStream outputStream, byte[] bytes)
    {
        if (bytes.length == 0)
        {
            return;
        }
        try
        {
            outputStream.write(bytes);
            outputStream.flush();
        }
        catch (IOException e)
        {
            log.error("write bytes fail: ",e);
        }
    }

    public static void writeListToFile(String fileName, List<String> list)
    {
        if (CollectionUtils.isEmpty(list))
        {
            return;
        }
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName))))
        {
            for (String s : list)
            {
                bw.write(s);
                bw.newLine();
                bw.flush();
            }
        }
        catch (Exception e)
        {
            log.error("write string list fail :",e);
        }

    }
}
