package com.itheima.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.itheima.reggie.entity.Employee;
import org.apache.ibatis.annotations.Mapper;
/**
 * @author: Lin
 * @Date: 2023-03-20 17:28
 *
 * mapper文件夹一般用于访问数据库的操作,
 *
 * 接口, 用于实现Mybatis的mapper接口,用于定义数据库的访问操作,
 **/
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {


}   