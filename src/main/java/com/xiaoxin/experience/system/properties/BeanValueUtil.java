package com.xiaoxin.experience.system.properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Properties;

/**
 * @author xiaoxin
 */
public class BeanValueUtil {

    private static final Logger log = LoggerFactory.getLogger(BeanValueUtil.class);

    private BeanValueUtil() {}

    /**
     * 刷新ioc容器中bean对象包含有@value注解的字段值内容
     * @param applicationContext 上下文对象
     * @param properties 要更新的@value值对应的键值对对象
     */
    public static void refreshValue(ApplicationContext applicationContext, Properties properties)
    {
        if (Objects.isNull(applicationContext) || Objects.isNull(properties))
        {
            return;
        }
        for (String beanName : applicationContext.getBeanDefinitionNames())
        {
            Object bean = applicationContext.getBean(beanName);
            for (Field f : bean.getClass().getDeclaredFields())
            {
                Value valueAnnotation = f.getAnnotation(Value.class);
                if (valueAnnotation == null)
                {
                    continue;
                }
                String key = valueAnnotation.value().replace("${", "").replace("}", "").split(":")[0];
                if (properties.containsKey(key))
                {
                    f.setAccessible(true);
                    try
                    {
                        filedSet(f,bean, properties.get(key));
                    }
                    catch (Exception e)
                    {
                        log.error(e.getMessage(), e);
                    }
                }
            }
        }
    }

    private static void filedSet(Field field, Object bean, Object value)
            throws IllegalAccessException
    {
        Class<?> type = field.getType();
        if(type.isAssignableFrom(String.class))
        {
            field.set(bean,value.toString());
        }
        else if (type.isAssignableFrom(Boolean.class) || "boolean".equals(type.getName()))
        {
            field.set(bean,Boolean.parseBoolean(value.toString()));
        }
        else if (type.isAssignableFrom(Integer.class) || "int".equals(type.getName()))
        {
            field.set(bean,Integer.parseInt(value.toString()));
        }
        else if (type.isAssignableFrom(Long.class) || "long".equals(type.getName()))
        {
            field.set(bean,Long.parseLong(value.toString()));
        }
        else if (type.isAssignableFrom(Double.class) || "double".equals(type.getName()))
        {
            field.set(bean,Double.parseDouble(value.toString()));
        }
        else
        {
            field.set(bean,value);
        }
    }
}
