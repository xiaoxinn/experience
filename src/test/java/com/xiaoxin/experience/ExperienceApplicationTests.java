package com.xiaoxin.experience;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiaoxin.experience.config.HttpsService;
import com.xiaoxin.experience.tree.Device;
import com.xiaoxin.experience.tree.Group;
import com.xiaoxin.experience.tree.GroupTree;
import com.xiaoxin.experience.tree.JsTree;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
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
