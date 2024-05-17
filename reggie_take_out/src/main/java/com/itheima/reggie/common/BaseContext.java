package com.itheima.reggie.common;

/**
 * @author: Lin
 * @Date: 2023-04-17 00:53
 * 工具类
 *  需求:
 *      用户保存和获取当前登陆用户的id(基于线程的封装类)
 *
 *  实现步骤：
     * 1、编写BaseContext工具类，基于ThreadLocal封装的工具类
     * 2、在LoginCheckFilter的doFilter方法中调用BaseContext:来设置当前登录用户的id
     * 3、在MyMetaObjectHandler的方法中调用BaseContext获取登录用户的id
 **/
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();
    /**
     * 设置属性
     * */
    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    /**
     * 获取属性值
     * */
    public static Long getCurrentId(){
        return threadLocal.get();//把刚刚塞进去(设置的)所有值都取出来
    }
}

