package com.itheima.reggie.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 员工实体
 */
@Data
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String username;

    private String name;

    private String password;

    private String phone;

    private String sex;

    private String idNumber;//身份证号码

    private Integer status;

    //设置并且标记需要被自动填充的属性
    //Table就是表格的意思
    @TableField(fill = FieldFill.INSERT)//表示在插入的时候会进行自动填充字段
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)//在插入和更新的时候会进行自动填充字段
    private LocalDateTime updateTime;


    @TableField(fill = FieldFill.INSERT)//在插入的时候自动填充
    private Long createUser;

    @TableField(fill = FieldFill.INSERT_UPDATE)//插入和更新的时候自动填充字段
    private Long updateUser;

}
