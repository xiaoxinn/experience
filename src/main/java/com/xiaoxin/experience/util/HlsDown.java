package com.xiaoxin.experience.util;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>Simple life, simple work, don't complicate</p>
 *
 * @author 小心
 * @version 1.0.0
 * @date 2021/7/14
 */
public class HlsDown {

    private final Pattern pattern = Pattern.compile(".*ts");
    public String getIndexFile()
            throws Exception
    {
        String originUrlPath = "https://video.hcyunshang.cn/20210225/f9ujw8up/index.m3u8";
        URL url = new URL(originUrlPath);
        //下载资源
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(),"UTF-8"));

        StringBuilder content = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null)
        {
            content.append(line).append("\n");
        }
        in.close();

        return content.toString();
    }

    public List analysisIndex(String content)
            throws Exception
    {
        Matcher ma = pattern.matcher(content);

        List<String> list = new ArrayList<String>();

        while(ma.find()){
            list.add(ma.group());
        }

        return list;
    }

    public HashMap downLoadIndexFile(List<String> urlList){
        HashMap<Integer,String> keyFileMap = new HashMap<>();

        for(int i =0;i<urlList.size();i++){
            String subUrlPath = urlList.get(i);
            String folderPath = "";
            String fileOutPath = folderPath + File.separator + i + ".ts";
            keyFileMap.put(i, fileOutPath);
            try{
                String preUrlPath = "";
                downloadNet(preUrlPath + subUrlPath, fileOutPath);

                System.out.println("成功："+ (i + 1) +"/" + urlList.size());
            }catch (Exception e){
                System.err.println("失败："+ (i + 1) +"/" + urlList.size());
            }
        }

        return  keyFileMap;
    }

    private void downloadNet(String fullUrlPath, String fileOutPath) throws Exception {

        //int bytesum = 0;
        int byteread = 0;

        URL url = new URL(fullUrlPath);
        URLConnection conn = url.openConnection();
        InputStream inStream = conn.getInputStream();
        FileOutputStream fs = new FileOutputStream(fileOutPath);

        byte[] buffer = new byte[1204];
        while ((byteread = inStream.read(buffer)) != -1) {
            //bytesum += byteread;
            fs.write(buffer, 0, byteread);
        }
    }

    public void downLoadIndexFileAsync(List<String> urlList, HashMap<Integer,String> keyFileMap) throws Exception{
        int threadQuantity = 3;
        int downloadForEveryThread = (urlList.size() + threadQuantity - 1)/threadQuantity;
        if(downloadForEveryThread == 0)
        {
            downloadForEveryThread = urlList.size();
        }

        for(int i=0; i<urlList.size();i+=downloadForEveryThread){
            int endIndex = i + downloadForEveryThread - 1;
            if(endIndex >= urlList.size())
            {
                endIndex = urlList.size() - 1;
            }

            new DownloadThread(urlList, i, endIndex, keyFileMap).start();
        }
    }

    class DownloadThread extends Thread{
        private List<String> urlList;
        private int startIndex;
        private int endIndex;
        private HashMap<Integer,String> keyFileMap;

        public DownloadThread(List<String> urlList, int startIndex, int endIndex, HashMap<Integer,String> keyFileMap){
            this.urlList = urlList;
            this.startIndex = startIndex;
            this.endIndex = endIndex;
            this.keyFileMap = keyFileMap;
        }
        @Override
        public void run(){
            for(int i=startIndex;i<=endIndex;i++){
                String subUrlPath = urlList.get(i);
                String folderPath = "";
                String fileOutPath = folderPath + File.separator + i + ".ts";
                keyFileMap.put(i, fileOutPath);
                String message = "%s: 线程 " + (startIndex/(endIndex - startIndex) + 1)
                        + ", "+ (i + 1) +"/" + urlList.size() +", 合计: %d";
                try{
                    String preUrlPath = "";
                    downloadNet(preUrlPath + subUrlPath, fileOutPath);

                    System.out.println(String.format(message, "成功", keyFileMap.size()));
                }catch (Exception e){
                    System.err.println(String.format(message, "失败", keyFileMap.size()));
                }
            }
        }
    }

    public String composeFile(HashMap<Integer,String> keyFileMap) throws Exception{

        if(keyFileMap.isEmpty())
        {
            return null;
        }

        String rootPath = "";
        String fileName = "";
        String fileOutPath = rootPath + File.separator + fileName;
        FileOutputStream fileOutputStream = new FileOutputStream(new File(fileOutPath));
        byte[] bytes = new byte[1024];
        int length = 0;
        for(int i=0; i<keyFileMap.size(); i++){
            String nodePath = keyFileMap.get(i);
            File file = new File(nodePath);
            if(!file.exists())
            {
                continue;
            }

            FileInputStream fis = new FileInputStream(file);
            while ((length = fis.read(bytes)) != -1) {
                fileOutputStream.write(bytes, 0, length);
            }
        }

        return fileName;
    }
}
