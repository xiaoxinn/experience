package com.xiaoxin.experience;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiaoxin.experience.config.HttpsService;
import com.xiaoxin.experience.m3u8.M3u8Download;
import com.xiaoxin.experience.tree.Device;
import com.xiaoxin.experience.tree.Group;
import com.xiaoxin.experience.tree.GroupTree;
import com.xiaoxin.experience.tree.JsTree;
import com.xiaoxin.experience.util.IOUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.AlgorithmParameterSpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * create by xiaoxin on 2021/3/19
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ExperienceApplication.class},properties = {"application.properties"})
public class ExperienceApplicationTests {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private HttpsService httpsService;

    @Test
    public void restTemplateTest()
    {
        String url  = "https://114.116.223.94:10013/api/system/version";
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> httpEntity  = new HttpEntity<>("{}",header);
        ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
        System.out.println(exchange.getBody());
    }

    @Test
    public void restTemplateTestHttp()
    {
        String url  = "http://114.116.223.94:10010/api/system/version";
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> httpEntity  = new HttpEntity<>("{}",header);
        ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
        System.out.println(exchange.getBody());
    }

    @Test
    public void okhttpClient() throws IOException
    {
        String url  = "https://114.116.223.94:10013/api/system/version";
        String s = httpsService.postJson(url, "{}");
        System.out.println(s);
    }

    @Test
    public void okhttpClientHttp() throws IOException
    {
        String url  = "http://114.116.223.94:10010/api/system/version";
        String s = httpsService.postJson(url, "{}");
        System.out.println(s);
    }

    @Test
    public void download()
    {
        try(BufferedReader bfr = new BufferedReader(new FileReader("F://m3u8JavaDown/download.txt")))
        {
            List<String[]> downloadList = new ArrayList<>();
            bfr.lines().forEach(s -> downloadList.add(s.split(" ")));
            for (String[] strings : downloadList)
            {
                M3u8Download m3U8Download = new M3u8Download(strings[1], "F:\\download", strings[0]);
                m3U8Download.download();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    @Test
    public void downloadAndDecode() throws Exception{
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        //DownloadUtil.downLoadFromUrl("https://tc.xacyhj.com/20210225/f9ujw8up/2000kb/hls/oWI1uW5E.ts","oWI1uW5E.ts","");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
        SecretKeySpec keySpec = new SecretKeySpec("b3c9b9bbee8fd99d".getBytes(StandardCharsets.UTF_8), "AES");
        byte[] ivByte = new byte[16];
        //如果m3u8有IV标签，那么IvParameterSpec构造函数就把IV标签后的内容转成字节数组传进去
        AlgorithmParameterSpec paramSpec = new IvParameterSpec(ivByte);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, paramSpec);
        byte[] bytes = cipher.doFinal(IOUtil.readAllFileToBytes("oWI1uW5E.ts"));
        IOUtil.writeFile("a.ts",bytes);
    }

    @Test
    public void encrypt() throws Exception
    {
        String plaintext = "asdew";
        Cipher cipher = Cipher.getInstance("AES");
        SecretKeySpec key = new SecretKeySpec("b3c9b9bbee8fd99d".getBytes(),"AES");
        cipher.init(Cipher.ENCRYPT_MODE,key);
        byte[] bytes = cipher.doFinal(plaintext.getBytes());
        System.out.println(new String(bytes));
        System.out.println(Base64.getEncoder().encodeToString(bytes));
    }

    @Test
    public void decrypt() throws Exception
    {
        URL url = new URL("https://tc.xacyhj.com/20210225/f9ujw8up/2000kb/hls/oWI1uW5E.ts");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        //设置超时间为3秒
        conn.setConnectTimeout(3 * 1000);
        //防止屏蔽程序抓取而返回403错误
        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        //得到输入流
        InputStream inputStream = conn.getInputStream();
        //获取自己数组
        byte[] getData = readInputStream(inputStream);

        Cipher cipher = Cipher.getInstance("AES");
        SecretKeySpec key = new SecretKeySpec("b3c9b9bbee8fd99d".getBytes(),"AES");
        cipher.init(Cipher.DECRYPT_MODE,key, new IvParameterSpec(new byte[16]));
        byte[] bytes1 = cipher.doFinal(getData);
        System.out.println(new String(bytes1));

//        Cipher cipher = Cipher.getInstance("AES");
//        SecretKeySpec key = new SecretKeySpec("b3c9b9bbee8fd99d".getBytes(),"AES");
//        cipher.init(Cipher.DECRYPT_MODE,key);
//        byte[] bytes = cipher.doFinal(readToString("f://test.ts"));
//        String org = com.xiaoxin.experience.util.Test.decrypt(new String(readToString("f://test.ts"), StandardCharsets.UTF_8), "b3c9b9bbee8fd99d");
        FileWriter fileWriter = new FileWriter("f://oWI1uW5E.ts");
        fileWriter.write(new String(bytes1));
        fileWriter.close();
    }

    private static byte[] readInputStream(InputStream inputStream)
            throws IOException
    {
        byte[] buffer = new byte[1024];
        int len;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = inputStream.read(buffer)) != -1)
        {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }

    public byte[] readToString(String fileName) {
        File file = new File(fileName);
        Long fileLength = file.length();
        System.out.println(fileLength);
        byte[] fileContent = new byte[fileLength.intValue()];
        try {
            FileInputStream in = new FileInputStream(file);
            in.read(fileContent);
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileContent;
    }

    public static byte[] toByteArray(String hexString) {
        if (hexString.isEmpty())
            throw new IllegalArgumentException("this hexString must not be empty");

        hexString = hexString.toLowerCase();
        final byte[] byteArray = new byte[hexString.length() / 2];
        int k = 0;
        for (int i = 0; i < byteArray.length; i++) {//因为是16进制，最多只会占用4位，转换成字节需要两个16进制的字符，高位在先
            byte high = (byte) (Character.digit(hexString.charAt(k), 16) & 0xff);
            byte low = (byte) (Character.digit(hexString.charAt(k + 1), 16) & 0xff);
            byteArray[i] = (byte) (high << 4 | low);
            k += 2;
        }
        return byteArray;
    }

    @Test
    public void jsTreeDemo() throws JsonProcessingException {
        List<Group> groupList = new ArrayList<>();
        Group mainGroup = new Group(0, -1, "0,","主");
        Group group1 = new Group(2,0,"0,","其他1");
        Group group2 = new Group(3, 1, "0,1,", "其他2");
        Group group3 = new Group(4, 1, "0,1,", "其他3");
        Group group4 = new Group(5, 1, "0,1,", "其他4");
        Group group5 = new Group(6, 2, "0,2,","其他5");
        Group group6 = new Group(7, 6, "0,2,6","其他6");



        groupList.add(group1);
        groupList.add(group2);
        groupList.add(group3);
        groupList.add(group4);
        groupList.add(group5);
        groupList.add(group6);


        ObjectMapper objectMapper = new ObjectMapper();

        Group groupRoot = new Group();
        groupRoot.setId(Integer.MAX_VALUE);

        GroupTree groupTree = new GroupTree(groupList,mainGroup,true);
        List<Device> devices = new ArrayList<>();
        Device device = new Device();
        device.setId("1");
        device.setOrganizationId(6);
        device.setName("abc");
        devices.add(device);
        groupTree.decorateLeaves(devices);
        JsTree jsTree = groupTree.getTreeRoot().buildJsTree(branch -> JsTree.asBranch(String.valueOf(branch.getId()), branch.getName(), "1", branch),
                leaf -> JsTree.asLeaf(leaf.getId(), leaf.getName(), "2", leaf));
        List<JsTree> children = jsTree.getChildren();
        String s = objectMapper.writeValueAsString(children);
        System.out.println(s);

        List<Group> groups = new ArrayList<>();
        toGroupList(children,mainGroup.getId(),mainGroup.getParents(),groups);
        String s1 = objectMapper.writeValueAsString(groups);
        System.out.println(s1);
        JsTree specJsTree = getSpecJsTree(6, children);
        String s2 = objectMapper.writeValueAsString(specJsTree);
        System.out.println(s2);
    }

    private static JsTree getSpecJsTree(Integer organizationId,List<JsTree> allJsTree)
    {
        if (CollectionUtils.isEmpty(allJsTree))
        {
            return null;
        }
        for (JsTree jsTree : allJsTree) {
            Group payload = (Group) jsTree.getPayload();
            if(payload.getId().equals(organizationId))
            {
                return jsTree;
            }else {
                JsTree specJsTree = getSpecJsTree(organizationId, jsTree.getChildren());
                if (null == specJsTree)
                {
                    continue;
                }
                return specJsTree;
            }
        }
        return null;
    }

    private static void toGroupList(List<JsTree> jsTreeList,Integer parentId,String parents,List<Group> groupList)
    {
        if (jsTreeList == null)
        {
            return;
        }
        for (JsTree jsTree : jsTreeList)
        {
            if(jsTree.isLeaf()){
                continue;
            }
            Group payload = (Group)jsTree.getPayload();
            payload.setParentId(parentId);
            payload.setParents(parents + payload.getId() + ",");
            groupList.add(payload);
            toGroupList(jsTree.getChildren(),payload.getId(),payload.getParents(),groupList);
        }
    }
}
