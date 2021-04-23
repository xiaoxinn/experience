package com.xiaoxin.experience.service.demo;

import com.xiaoxin.experience.service.demo.model.Demo;

import java.util.List;
import java.util.Map;

/**
 * @author xiaoxin
 */
public interface DemoService {

    void insertDemo(Demo demo);

    List<Demo> getDemoList(Map<String,Object> map);
}
