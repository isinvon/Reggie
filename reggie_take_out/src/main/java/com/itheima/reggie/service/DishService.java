package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;

/**
 * @author: Lin
 * @Date: 2023-04-20 09:17
 **/
public interface DishService extends IService<Dish> {
    /**
     * 新增菜品, 同时保存对应的口味数据,需要操作两张表: dish, dish_flavor
     * */
    public void saveWithFlavor(DishDto dishDto);

    //根据id查询
    public DishDto getByIdWithFlavor(Long id);

    //更新菜品信息,同时更新口味信息
    public void updateWithFlavor(DishDto dishDto);

}
