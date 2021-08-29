package com.xiaoxin.experience.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * <p>Simple life, simple work, don't complicate</p>
 *
 * @author 小心
 * @version 1.0.0
 * @date 2021/8/29
 */
public class FileUtil {

    private static final Logger log = LoggerFactory.getLogger(FileUtil.class);

    private FileUtil()  {}

    public static void deleteDir(String dir)
    {
        File dirFile = new File(dir);
        File[] files = dirFile.listFiles();
        if (files != null)
        {
            for (File file : files)
            {
                if (file.isDirectory())
                {
                    deleteDir(file.getAbsolutePath());
                    continue;
                }
                deleteFile(file);
            }
        }
        // 目录为空或者只是一个文件
        deleteFile(dirFile);
    }

    public static void deleteFile(File file)
    {
        boolean delete = file.delete();
        if (!delete)
        {
            log.error("delete file[{}] fail",file.getName());
        }
    }
}
