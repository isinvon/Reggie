package com.itheima.reggie.common;

/**
 * @author: Lin
 * @Date: 2023-04-21 12:23
 * 自定义异常类
 **/
public class CustomException extends RuntimeException {//runtime为运行时异常

    public CustomException(String message) {
        super(message);//调用父类的构造器
    }

}
