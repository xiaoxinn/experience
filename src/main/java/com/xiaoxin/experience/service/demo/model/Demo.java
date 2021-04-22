package com.xiaoxin.experience.service.demo.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * @author xiaoxin
 */
@Setter
@Getter
@TableName("tbl_demo")
public class Demo {

    private String id;

    private String name;

    private Timestamp createTime;
}
