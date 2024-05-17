package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.mapper.CategoryMapper;

/**
 * @author: Lin
 * @Date: 2023-04-19 18:43
 **/

public interface CategoryService extends IService<Category> {
    /**
     * IService接口通常定义了一些基本的增删查改操作, 而EmployeeService可能是为了实现对Employee的增删查改的基本操作
     * */
    public void remove(Long id);
}
