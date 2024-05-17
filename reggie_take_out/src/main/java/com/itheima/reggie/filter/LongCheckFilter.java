package com.itheima.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * @author: Lin
 * @Date: 2023-03-24 21:29
 * 过虑器
 * 检查用户是否已经完成登录
 * filterName 过滤器的名字
 * urlPatterns 拦截器的请求, 这里是拦截所有的请求(想要拦截的路径)
 **/
@WebFilter(filterName = "LongCheckFilter", urlPatterns = "/*")//所有路径
@Slf4j
public class LongCheckFilter implements Filter {

    //路径匹配器, 支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    //拦截器
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;//转型为http是为了能够用request对象去获取requestURI
        //获取本次请求的uri
        String requestURI = request.getRequestURI();// /backend/index.html

        log.info("拦截到的请求：{}", requestURI);

        //定义不需要处理的请求路径, 比如静态资源(静态页面我们不需要拦截,因为此时的静态页面是没有数据的)
        String[] urls = new String[]{
                "/employee/login",//登录不拦截
                "/employee/logout",//推出登录不拦截
                "/backend/**",//backend静态资源不拦截
                "/front/**",//front的静态资源不拦截
                "/common/**",//运行common路径下的不拦截
                //给登陆页面放行:
                "/user/sendMsg",//移动端发送短信
                "/user/login",//移动端登录
        };

        //做调试用的
        //log.info("拦截到的请求:{}",requestURL);


        //2. 判断本次请求是否需要处理
        boolean check = check(urls, requestURI);

        //3. 如果不需要处理, 则直接放行
        if (check) {
            log.info("本次请求{}不需要处理", requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        //4-1. 判断登录状态(PC端), 如果已登录, 就直接放行
        //PC端操作的数据库表是employee
        if (request.getSession().getAttribute("employee") != null) {
            Long empId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);//公共字段需求的工具类所需要的参数值
            //调试用的:
            //log.info("用户已登录, 用户id为: {}",request.getSession().getAttribute("employee"));
            //放行
            filterChain.doFilter(request, response);
            return;
        }


        //4-2. 判断登录状态(移动端), 如果已登录, 就直接放行
        //因为移动端操作的数据库表是user而不是employee了
        if (request.getSession().getAttribute("user") != null) {
            Long userId = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);//公共字段需求的工具类所需要的参数值
            //调试用的:
            //log.info("用户已登录, 用户id为: {}",request.getSession().getAttribute("employee"));
            //放行
            filterChain.doFilter(request, response);
            return;
        }

        log.info("用户未登录");
        //5、如果未登录则返回未登录结果，通过输出流方式向客户端页面响应数据,具体响应什么数据，看前端的需求，然后前端会根据登陆状态做页面跳转        log.info("");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;

    }



    /**
     * @param urls
     * @param requestURI
     * @return
     */
    public boolean check(String[] urls, String requestURI) {
        //逐个匹配
        for (String url : urls) {
            //把浏览器发过来的请求和我们定义的不拦截的url作比较, 匹配则放行
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match) {
                return true;
            }
        }
        return false;
    }

}
