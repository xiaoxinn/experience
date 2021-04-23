package com.xiaoxin.experience;

import com.xiaoxin.experience.config.HttpsService;
import com.xiaoxin.experience.service.demo.DemoService;
import com.xiaoxin.experience.service.demo.model.Demo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Autowired
    private DemoService demoService;

    @Test
    public void restTemplateTest()
    {
        String url  = "https://114.116.223.94:10013/api/system/version";
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON_UTF8);
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
    public void mybatisPlusTest()
    {
        Demo demo = new Demo();
        demo.setId("de");
        demo.setName("demo");
        Timestamp timestamp = new Timestamp(new Date().getTime());
        demo.setCreateTime(timestamp);
        demoService.insertDemo(demo);
    }

    @Test
    public void mybatisMapSelect()
    {
        Map<String,Object> map = new HashMap<>();
        map.put("name","demo");
        List<Demo> demoList = demoService.getDemoList(map);
        Assert.notNull(demoList,"domeList is null");
    }
}
