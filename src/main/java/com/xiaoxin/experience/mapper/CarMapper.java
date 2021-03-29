package com.xiaoxin.experience.mapper;

import com.xiaoxin.experience.bean.Car;
import com.xiaoxin.experience.bean.CarDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * create by xiaoxin on 2021/3/19
 */
@Mapper
public interface CarMapper {

    CarMapper INSTANCE = Mappers.getMapper( CarMapper.class );


    CarDto carToCarDto(Car car);
}
