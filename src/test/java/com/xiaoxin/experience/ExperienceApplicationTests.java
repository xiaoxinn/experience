package com.xiaoxin.experience;

import com.xiaoxin.experience.bean.Car;
import com.xiaoxin.experience.bean.CarDto;
import com.xiaoxin.experience.mapper.CarMapper;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * create by xiaoxin on 2021/3/19
 */
@SpringBootTest
public class ExperienceApplicationTests {


    @Test
    public void mapstructStart()
    {
        //given
        Car car = new Car( "Morris", "5", System.currentTimeMillis() + "" );

        //when
        CarDto carDto = CarMapper.INSTANCE.carToCarDto( car );

        //then
        Assert.assertNotNull(carDto);
        Assert.assertEquals("Morris",carDto.getName());
        Assert.assertEquals("5",carDto.getPrice());
        Assert.assertNotNull(carDto.getCreateTime());
    }
}
