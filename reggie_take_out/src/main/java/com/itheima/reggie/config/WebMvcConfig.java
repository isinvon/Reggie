package com.itheima.reggie.config;

import com.itheima.reggie.common.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

/**
 * @author: Lin
 * @Date: 2023-03-20 15:27
 * 用配置类做Mvc框架关于静态资源的映射, 为了告诉springmvc框架在resource下两个文件夹template和front中放的就是我们的静态资源文件, 直接放行即可
 **/
@Slf4j
@Configuration//配置类
public class WebMvcConfig extends WebMvcConfigurationSupport {
    /*
     *设置静态资源映射
     *@param registry
     *前面表示的是浏览器访问的请求
     *后面表示的是要把请求映射到哪里去
     * */
    //添加资源监管器
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("开始静态资源映射...");
        log.info("开始进行静态资源映射...");
        //向注册表中添加映射,  让拦截器给某文件开放, 使得mvc能够有权限访问这些文件
        registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
        registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");
    }

    /**
     * 扩展消息转换器
     * @param converters
     * */
    /**
     * 1)提供对象转换器JacksonObjectMapper,基于Jackson进行]ava对象到json数据的转换（资料中已经提供，直接复制到
     * 项目中使用)
     * 2)在WebMvcConfig配置类中扩展Spring mvc的消息转换器，在此消息转换器中使用提供的对象转换器进行]ava对象到
     * json数据的转换
     */
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.info("扩展消息转换器...");
        //创建消息转换器的对象
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        //设置具体的对象映射器
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        //通过设置索引, 让自己的转换器放在最前面 ,否则默认的jackson转换器就会放在前面,用不上我们的这个转换器
        converters.add(0, messageConverter);//设置0即设置了优先级为第一,优先使用该转换器
    }


}
