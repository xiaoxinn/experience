package com.xiaoxin.experience.util;

import org.springframework.beans.BeanUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * @author xiaoxin
 */
public class ConvertUtil {

    public static <T> T convert(Object source, Class<T> target)
    {
        T t;
        try {
             t = target.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        BeanUtils.copyProperties(source,t);
        return t;
    }

    public static <T> List<T> convertList(List<?> source, Class<T> target)
    {
        List<T> list = new LinkedList<>();
        for (Object o : source) {
            T convert = convert(o, target);
            list.add(convert);
        }
        return list;
    }
}
