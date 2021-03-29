package com.xiaoxin.experience.bean;


import lombok.Data;

/**
 * create by xiaoxin on 2021/3/19
 */
@Data
public class Car {

    private String name;

    private String price;

    private String createTime;

    public Car() {
    }

    public Car(String name, String price, String createTime) {
        this.name = name;
        this.price = price;
        this.createTime = createTime;
    }
}
