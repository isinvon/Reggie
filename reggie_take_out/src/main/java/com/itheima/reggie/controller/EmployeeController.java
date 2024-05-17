package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.mapper.EmployeeMapper;
import com.itheima.reggie.service.EmployeeService;
import com.itheima.reggie.service.Impl.EmployeeServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * @author: Lin
 * @Date: 2023-03-22 21:22
 * 实现类
 **/

/**
 * 需求:
 * 1、将页面提交的密码password进行md5加密处理
 * 2、根据页面提交的用户名username查询数据库
 * 3、如果没有查询到则返回登录失败结果
 * 4、密码比对，如果不一致则返回登录失败结果
 * 5、查看员工状态，如果为已禁用状态，则返回员工已禁用结果
 * 6、登录成功，将员工id存入Session并返回登录成功结果
 */

@Slf4j
@RequestMapping("/employee")
@RestController
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    //登陆页面: http://localhost:8080/backend/page/login/login.html
    @PostMapping("/login")//类似于create, 创建一个资源, 调用insert
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        /*
         * 此处HttpServletRequest request的作用:
         * 把从employee中得到的参数传入request中,
         * 然后就能通过request对象来get到session
         * */

        //1. 将页面提交的密码password进行MD5加密处理
        String password = employee.getPassword();//从前端用户登录拿到的用户密码
        password = DigestUtils.md5DigestAsHex(password.getBytes());//对用户进行MD5加密

        //2. 根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        /*
          简化版:
         定制LambdaQueryWrapper的条件,然后调用ge()方法指定查询
         LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>().ge(Employee::getUsername,employee.getUsername());
         */
        //在设计数据库的时候我们对username使用了唯一索引,所以这里可以使用getOne方法
        Employee emp = employeeService.getOne(queryWrapper);

        //3.如果没有查询到则返回登录失败的结果
        if (emp == null) {
            return R.error("登录失败,用户名不存在");
        }

        //4. 密码比对. 如果不一致则返回登录失败的结果
        if (!emp.getPassword().equals(password)) {
            //emp.getPassword();//用户存在后从数据库查询到的密码(加密状态的)  password是前端用户自己输入的密码(已经加密处理)
            return R.error("密码不正确");
        }

        //5. 查看员工状态, 如果为已禁用的状态, 返回员工已禁止的结果
        if (emp.getStatus() == 0) {
            return R.error("账号已禁用");
        }

        //6. 登录成功, 将员工的id传入session中, 并返回登陆成功的结果
        request.getSession().setAttribute("employee", emp.getId());
        //把从数据库中查询到的用户id返回出去
        return R.success(emp);
    }

    /**
     * 退出功能
     * ①在controller中创建对应的处理方法来接受前端的请求，请求方式为post；
     * ②清理session中的用户id
     * ③返回结果（前端页面会进行跳转到登录页面）
     *
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        //清理session中的用户id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * 新建员工
     *
     * @param employee
     * @return
     */
    @PostMapping()//因为请求就是 /employee 在类上已经写了，所以咱俩不用再写了
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee) {
        //对新增的员工设置初始化密码123456,需要进行md5加密处理，后续员工可以直接修改密码
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));


        //以下数据都由公共字段自动填充

        //employee.setCreateTime(LocalDateTime.now());
        //employee.setUpdateTime(LocalDateTime.now());
        //
        ////获取当前登陆用户的id
        //Long empId = (Long) request.getSession().getAttribute("employee");
        ////创建这个员工
        //employee.setCreateUser(empId);//创建人的id,就是当前用户的id（在进行添加操作的id）
        //employee.setUpdateUser(empId);//最后的更新人是谁


        //保存这个员工到数据库中
        try {
            employeeService.save(employee);
        } catch (Exception e) {
            R.error("新增员工失败");
            e.printStackTrace();
        }
        //返回提示信息
        return R.success("新增员工成功");
    }


    /**
     * 员工信息分页
     *
     * @param page     当前页数
     * @param pageSize 当前页最多存放数据条数,就是这一页查几条数据
     * @param name     根据name查询员工的信息
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        //这里之所以是返回page对象(mybatis-plus的page对象)，是因为前端需要这些分页的数据(比如当前页，总页数)
        //在编写前先测试一下前端传过来的分页数据有没有被我们接受到
        log.info("page = {},pageSize = {},name = {}", page, pageSize, name);//诊断日志

        //构造分页构造器  就是page对象
        Page pageInfo = new Page(page, pageSize);
        //构造条件构造器  就是动态的封装前端传过来的过滤条件  记得加泛型
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        //根据条件查询  注意这里的条件是不为空
        queryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name);//查询条件: 根据name查询Employee中的值, 所查的条件是: name
        //添加一个排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        //执行查询 这里不用封装了mybatis-plus帮我们做好了
        employeeService.page(pageInfo, queryWrapper);

        return R.success(pageInfo);
        //功能测试：分页的三个时机，①用户登录成功时，分页查询一次 ②用户使用条件查询的时候分页一次 ③跳转页面的时候分页查询一次
    }

    /**
     * 根据id修改员工信息
     *
     * @param employee
     * @return
     */
    @PutMapping//更新资源
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee) {
        //诊断日志
        log.info(employee.toString());

        //获取操作者的id
        Long empId = (Long) request.getSession().getAttribute("employee");
        //更新最后操作的人
        employee.setUpdateUser(empId);
        //更新最后操作的时间
        employee.setUpdateTime(LocalDateTime.now());

        //通过id修改
        employeeService.updateById(employee);
        //返回结果类
        return R.success("员工信息修改成功");

    }
    /**
     * 编辑员工信息
     *  需求:
     * 1. 点击编辑按钮时，页面跳转到add.html，并且在url中携带参数【员工id】
     * 2. 在add.html页面获取url中的参数【员工id】
     * 3. 发送ajax请求，请求服务端，痛死后提交员工id参数
     * 4. 服务端接收请求，根据员工id查询员工信息，将员工信息以json形式响应给页面
     * 5. 页面接收服务端响应的json数据，通过VUE的数据绑定进行员工信息回显
     * 6. 点击保存按钮，发送ajax请求，将页面中的员工信息以json方式提交给服务端
     * 7. 服务端接收员工信息，并进行处理，完成后给页面响应
     * 8. 页面接收到服务端响应信息后进行响应处理
     * 注意add.html页面为公共页面，新增员工和编辑员工都是在此也页面操作
     * */
    /**
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id) {
        log.info("根据id查询员工信息...");
        Employee employee = employeeService.getById(id);
        if (employee != null) {//员工存在...
            return R.success(employee);
        }
        return R.error("没有查询到员工信息");
    }
    /**
     * 这是一个使用Spring MVC注解的Java方法，用于处理GET请求。
     * 该方法接收一个名为"id"的PathVariable参数，表示请求的URL中的"id"部分。
     * 方法返回一个R<Employee>类型的对象，其中R表示自定义的响应类，Employee表示员工信息实体类。
     * 该方法的作用是根据id查询员工信息，并将查询结果封装在R对象中返回给客户端。
     * 如果查询成功，则返回R.success(employee)，其中success表示响应状态为成功，employee表示查询到的员工信息。
     * 如果查询失败，则返回R.error("没有查询到员工信息")，其中error表示响应状态为失败，"没有查询到员工信息"表示错误信息。
     * */

}



