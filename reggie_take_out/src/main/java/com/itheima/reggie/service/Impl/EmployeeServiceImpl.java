package com.itheima.reggie.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.mapper.EmployeeMapper;
import com.itheima.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author: Lin
 * @Date: 2023-03-22 21:25
 * 业务层: 专为controller服务
 **/
@Service
@Slf4j
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper,Employee> implements EmployeeService {
/**
 * ServiceImpl类是MyBatis-Plus框架提供的一个实现了一些基本的增删改查操作的类，
 * 它实现了IService接口的方法，同时提供了一些额外的方法，例如批量插入、分页查询等等。
 */



}
