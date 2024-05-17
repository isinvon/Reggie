package com.itheima.reggie;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author: Lin
 * @Date: 2023-03-20 15:05
 * springboot启动类
 **/
@Slf4j//用于日志传输. 添加了之后就可以进行log.info()的操作来输出日志了
@SpringBootApplication
@ServletComponentScan//servlet组件扫描, 为了能够去扫描关于有web相关注解的类
@EnableTransactionManagement//开启事务管理
public class ReggieApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReggieApplication.class,args);
        log.info("项目启动成功...");

    }
}
