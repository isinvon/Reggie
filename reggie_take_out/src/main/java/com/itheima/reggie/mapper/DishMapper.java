package com.itheima.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.entity.Dish;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author: Lin
 * @Date: 2023-04-20 09:20
 * 数据库操作接口
 **/
@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
