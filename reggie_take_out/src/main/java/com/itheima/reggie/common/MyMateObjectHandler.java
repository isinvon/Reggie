package com.itheima.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author: Lin
 * @Date: 2023-04-16 15:00
 * 元数据对象处理器
 * 公共字段自动填充服务
 **/

@Slf4j
@Component
public class MyMateObjectHandler implements MetaObjectHandler {//MetaObjectHandler: 元对象处理程序
    /**
     * 插入操作的时候自动填充
     * */
    @Override
    public void insertFill(MetaObject metaObject) {//insertFill:在执行insert语句的时候就会执行
        log.info("公共字段自动填充[insert]...");
        log.info(metaObject.toString());
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("createUser",BaseContext.getCurrentId());
        metaObject.setValue("updateUser",BaseContext.getCurrentId());
    }

    /**
     * 更新操作的时候自动填充
     * */
    @Override
    public void updateFill(MetaObject metaObject) {//updateFill是在执行update语句的时候就会执行
        //mateObject中含有originObject原始对象, 原始对象里面含有id,username,name,password,phone,sex,idNumber等等数据
        log.info("公共字段自动填充[insert]...");
        log.info(metaObject.toString());

        long id = Thread.currentThread().getId();
        log.info("线程的id: {}",id);

        metaObject.setValue("updateTime",LocalDateTime.now());
        metaObject.setValue("updateUser",BaseContext.getCurrentId());
    }
}
