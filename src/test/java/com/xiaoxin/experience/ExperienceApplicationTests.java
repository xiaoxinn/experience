package com.xiaoxin.experience;

import com.xiaoxin.experience.config.HttpsClient;
import com.xiaoxin.experience.config.HttpsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

/**
 * create by xiaoxin on 2021/3/19
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ExperienceApplication.class})
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
        header.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<String> httpEntity  = new HttpEntity<>("{}",header);
        ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
        System.out.println(exchange.getBody());
    }

    @Test
    public void okhttpClient() throws IOException {
        String url  = "https://114.116.223.94:10013/api/system/version";
        String s = httpsService.postJson(url, "{}");
        System.out.println(s);
    }
}
