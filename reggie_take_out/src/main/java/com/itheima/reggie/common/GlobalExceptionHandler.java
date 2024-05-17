package com.itheima.reggie.common;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * @author: Lin
 * @Date: 2023-03-30 16:15
 * 全局异常处理
 * SQLIntegrityConstraintViolationException异常: 即数据库中的完整性约束异常
 * 如果异常信息中包含“Duplicate entry”，则说明该用户名已经存在，将其封装为一个错误信息返回；否则返回一个“未知错误”的错误信息。
 *      Duplicate entry: 重复条目
 **/
//通知注解
@ControllerAdvice(annotations = {RestController.class, Controller.class})//表示带有这两个注解的就会被通知
@ResponseBody//因为我们等下还要写一个方法, 这个方法需要返回JSON
@Slf4j//日志注解
public class GlobalExceptionHandler {

    /**
     * 处理SQLIntegrityConstraintViolationException异常的方法
     * */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)//用于标注在方法上表示该方法用来处理SQLIntegrityConstraintViolationException类型的异常。当系统中抛出了该类型的异常时，就会调用该方法进行处理。
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException e){
        //报错记得打印日志a
        log.error(e.getMessage());
        if (e.getMessage().contains("Duplicate entry")) {
            //获取已经存在的用户名, 这里是从报错的异常信息中获取的
            String[] split = e.getMessage().split(" ");
            String msg = split[2] + "已存在";
            return R.error(msg);
        }
        return R.error("未知错误");
    }

    /**
     * 异常处理方法(异常通知类)
     * @param e
     * @return
     */
    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandler(CustomException e){
        //报错打印日志信息
        log.info(e.getMessage());
        return R.error(e.getMessage());//返回结果集
    }

}
