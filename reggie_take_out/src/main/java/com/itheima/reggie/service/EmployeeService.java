package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.Employee;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author: Lin
 * @Date: 2023-03-22 21:37
 **/

public interface EmployeeService extends IService<Employee> {
    /**
     * IService接口通常定义了一些基本的增删查改操作, 而EmployeeService可能是为了实现对Employee的增删查改的基本操作
     * */

}
