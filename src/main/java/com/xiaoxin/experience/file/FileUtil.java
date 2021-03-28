package com.xiaoxin.experience.file;

import org.springframework.util.Assert;

import java.io.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>Simple life, simple work, don't complicate</p>
 *
 * @author 小心
 * @version 1.0.0
 * @date 2021/3/28
 */
public class FileUtil {

    /**
     * 读取TXT文件内容
     * @param path TXT文件路径
     * @return 返回TXT文件每行文本内容集合
     */
    public static List<String> readTxt(String path){
        Assert.notNull(path,"file path can not be null");
        List<String> txtFileMessage = new LinkedList<>();
        try(Reader reader = new FileReader(path);
            BufferedReader bufferedReader = new BufferedReader(reader)) {
            bufferedReader.lines().forEach(txtFileMessage::add);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("file not exist");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("file read fail");
        }
        return txtFileMessage;
    }

    /**
     * 写文本内容到指定TXT文件
     * @param path 文件路径
     * @param list  文本内容集合
     */
    public static void writeTxt(String path, List<String> list)
    {
        File file = new File(path);
        if(file.exists())
        {
            try(FileWriter fileWriter = new FileWriter(file)){
                for (String s : list) {
                    String message = completeWord("map.put", s);
                    fileWriter.write(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static String completeWord(String pre, String word)
    {
        String reduce = word.replaceAll(",", "").replace(" ","").trim();
        String code = reduce.substring(0, 6);
        String range = reduce.substring(6);
        return pre + "(\"" + code + "\", \"" + range + "\");\r\n";
    }

    public static void main(String[] args) {
        List<String> list = readTxt("D:\\project\\experience\\main\\experience\\省市区行政编码.txt");
        writeTxt("D:\\project\\experience\\main\\experience\\rangeMap.txt",list);
    }
}
