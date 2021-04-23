package com.xiaoxin.experience.service.demo.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaoxin.experience.mapper.DemoMapper;
import com.xiaoxin.experience.service.demo.DemoService;
import com.xiaoxin.experience.service.demo.model.Demo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author xiaoxin
 */
@Service
public class DemoServiceImpl extends ServiceImpl<DemoMapper,Demo> implements DemoService {

    @Override
    public void insertDemo(Demo demo)
    {
        save(demo);
    }

    @Override
    public List<Demo> getDemoList(Map<String, Object> map) {
        return baseMapper.selectByMap(map);
    }
}
