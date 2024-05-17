package com.itheima.reggie.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.mapper.DishMapper;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 业务层
 * @author: Lin
 * @Date: 2023-04-20 09:18
 **/
@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper,Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;//用于操作口味
    /**
     * 新增菜品, 同时保存对应的口味数据
     * */
    @Override
    @Transactional // 因为涉及多张表,所以需要用到事务,如果没有开启事务管理，那么在操作多张表时可能会出现数据不一致的情况。在Java中操控多张表时需要开启事务管理，以确保数据的一致性和完整性。
    public void saveWithFlavor(DishDto dishDto) {
        this.save(dishDto);//this指的是当前类(DishServiceImpl)的类对象

        //获取到dishID菜品Id并顺便保存到表中
        Long dishId = dishDto.getId();

        //菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();

        //通过便利Dishflavor里面的所有属性值,然后相应的, 给对应的属性赋值
        //这个代码片段的目的是将集合（flavors）中的每个元素的dishId属性设置为dishId，并将修改后的元素收集到一个列表中。
        flavors = flavors.stream().map((item)->{
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        //保存菜品口味数据到菜品口味表dish_flavor
        dishFlavorService.saveBatch(flavors);//saveBatch()指的是批量保存,保存dishDto中的口味到口味表中

    }


    /**
     * 根据id查询菜品信息和对应的口味信息
     * */
    //此方法主要信息摘要: 1查询菜品基本信息2根据条件查询菜品对应的口味3将已经查到的菜品信息拷贝到新创建的dishdto对象中，最后将通过条件查询的菜品口味也set到新的dishdto中，然后一并将这个新对象数据返回
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        //查询菜品的基本信息,从dish表查询
        Dish dish = this.getById(id);

        DishDto dishDto = new DishDto();
        //将已经查到的菜品信息拷贝到新创建的dishdto对象中
        BeanUtils.copyProperties(dish, dishDto);
        //查询当前菜品对应的口味信息,从dish_flavor表查询
        //构造一个条件构造器
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        //进行查询
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        //将查询到的flavors口味赋值给dishDto(即:将通过条件查询的菜品口味也set到新的dishdto中)
        dishDto.setFlavors(flavors);

        return dishDto;
    }

    /**
     * 修改菜品信息,并且更新菜品口味
     * */
    @Override
    @Transactional//开启事务注解,保证数据的一致性
    public void updateWithFlavor(DishDto dishDto) {
        //更新dish表的基本信息
        this.updateById(dishDto);//因为DishDto是Dish的子类(继承关系),所以是一样的

        //清理当前菜品对应的口味数据---dish_flavor表的delete操作
        //构建查询条件对象
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());

        //先清理原先的口味数据
        dishFlavorService.remove(queryWrapper);


        //添加当前提交过来的口味数据---dish_flavor表的insert操作,批量保存
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item)->{
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);//批量保存

        //更新口味表的基本信息
    }
}
