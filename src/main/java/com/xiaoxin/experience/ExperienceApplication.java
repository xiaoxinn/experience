package com.xiaoxin.experience;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author xiaoxin
 * @version 2021/2/23
 */
@SpringBootApplication(scanBasePackages = {"com.xiaoxin.experience"})
public class ExperienceApplication {

    public static void main(String[] args)
    {
        SpringApplication.run(ExperienceApplication.class,args);
    }
}
