package com.xiaoxin.experience.util;

import sun.net.www.protocol.https.Handler;

import javax.net.ssl.*;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>Simple life, simple work, don't complicate</p>
 *
 * @author 小心
 * @version 1.0.0
 * @date 2021/7/14
 */


public class DownFileUtil {

    private static final Pattern PATTERN = Pattern.compile(".*ts");

    private static String rootPath = "F:\\m3u8dir";

    public static void main(String[] args)
    {

        String indexPath = "https://video.hcyunshang.cn/20210225/f9ujw8up/index.m3u8";
        String prePath = indexPath.substring(0, indexPath.lastIndexOf("/") + 1);
        System.out.println(prePath);
        //下载索引文件
        String indexStr = getIndexFile(indexPath);
        //解析索引文件
        List<String> videoUrlList = analysisIndex(indexStr);

        //生成文件下载目录
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        String fileRootPath = rootPath + File.separator + uuid;
        File fileDir = new File(fileRootPath);
        if (!fileDir.exists())
        {
            fileDir.mkdirs();
        }
        //下载视频片段，分成50个线程切片下载
        HashMap<Integer,String> keyFileMap = new HashMap<>();
        int downForThreadCount = videoUrlList.size() / 50;
        for (int i = 0; i < videoUrlList.size(); i += downForThreadCount) {
            int end = i + downForThreadCount - 1;
            if (end > videoUrlList.size()) {
                end = videoUrlList.size() - 1;
            }
            new DownFileUtil().new DownLoadNode(videoUrlList, i, end, keyFileMap, prePath, fileRootPath).start();
        }
        //等待下载
        while (keyFileMap.size() < videoUrlList.size()) {
            System.out.println("当前下载数量" + keyFileMap.size());
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //合成视频片段
        composeFile(fileRootPath + File.separator + uuid + ".mp4", keyFileMap);
    }


    /**
     * 视频片段合成
     *
     * @param fileOutPath
     * @param keyFileMap
     */
    public static void composeFile(String fileOutPath, HashMap<Integer, String> keyFileMap) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(new File(fileOutPath));
            byte[] bytes = new byte[1024];
            int length = 0;
            for (int i = 0; i < keyFileMap.size(); i++) {
                String nodePath = keyFileMap.get(i);
                File file = new File(nodePath);
                if (!file.exists())
                {
                    continue;
                }
                FileInputStream fis = new FileInputStream(file);
                while ((length = fis.read(bytes)) != -1) {
                    fileOutputStream.write(bytes, 0, length);
                }
            }
        } catch (Exception e) {

        }
    }


    public static List<String> analysisIndex(String content) {
        Matcher ma = PATTERN.matcher(content);

        List<String> list = new ArrayList<>();

        while (ma.find()) {
            String s = ma.group();
            list.add(s);
            System.out.println(s);
        }
        return list;
    }


    class DownLoadNode extends Thread {
        private List<String> list;
        private int start;
        private int end;
        HashMap<Integer,String> keyFileMap;
        private String preUrlPath;
        private String fileRootPath;

        DownLoadNode(List<String> list, int start, int end, HashMap<Integer, String> keyFileMap, String preUrlPath, String fileRootPath) {
            this.list = list;
            this.end = end;
            this.start = start;
            this.keyFileMap = keyFileMap;
            this.preUrlPath = preUrlPath;
            this.fileRootPath = fileRootPath;
        }

        @Override
        public void run() {
            try {
                for (int i = start; i <= end; i++) {
                    String urlpath = list.get(i);
                    URL url = new URL(preUrlPath + urlpath);
                    //下在资源
                    DataInputStream dataInputStream = new DataInputStream(url.openStream());
                    String fileOutPath = fileRootPath + File.separator + urlpath;
                    FileOutputStream fileOutputStream = new FileOutputStream(new File(fileOutPath));
                    byte[] bytes = new byte[1024];
                    int length = 0;
                    while ((length = dataInputStream.read(bytes)) != -1) {
                        fileOutputStream.write(bytes, 0, length);
                    }
                    dataInputStream.close();
                    keyFileMap.put(i, fileOutPath);
                }
                System.out.println("第" + start / (end - start) + "组完成，" + "开始位置" + start + ",结束位置" + end);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static String getIndexFile(String urlpath) {
        try {

            // 创建SSLContext
            SSLContext sslContext = SSLContext.getInstance("SSL");
            TrustManager[] tm = { new MyX509TrustManager() };
            // 初始化
            sslContext.init(null, tm, new java.security.SecureRandom());
            // 获取SSLSocketFactory对象
            SSLSocketFactory ssf = sslContext.getSocketFactory();
            // url对象
            URL url = new URL(null,urlpath,new Handler());
            // 打开连接
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            /**
             * 这一步的原因: 当访问HTTPS的网址。您可能已经安装了服务器证书到您的JRE的keystore
             * 但是服务器的名称与证书实际域名不相等。这通常发生在你使用的是非标准网上签发的证书。
             *
             * 解决方法：让JRE相信所有的证书和对系统的域名和证书域名。
             *
             * 如果少了这一步会报错:java.io.IOException: HTTPS hostname wrong: should be <localhost>
             */
            conn.setHostnameVerifier((hostname,session) -> true);
            // 设置一些参数
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("GET");
            // 设置当前实例使用的SSLSoctetFactory
            conn.setSSLSocketFactory(ssf);
            conn.connect();

            //下在资源
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                content.append(line).append("\n");
            }
            in.close();
            System.out.println("content: " + content);
            return content.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

   class MyX509TrustManager implements X509TrustManager {

    @Override
    public void checkClientTrusted(X509Certificate certificates[],String authType) throws CertificateException {
    }

    @Override
    public void checkServerTrusted(X509Certificate[] ax509certificate,String s) throws CertificateException {
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        // TODO Auto-generated method stub
        return null;
    }
}



