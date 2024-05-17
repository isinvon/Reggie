package com.itheima.reggie.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.mapper.CategoryMapper;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishService;
import com.itheima.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: Lin
 * @Date: 2023-04-19 18:45
 * 业务层
 **/

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {//实现CategoryService接口

    /**
     * 菜品业务自动注入
     */
    @Autowired
    private DishService dishService;
    /**
     * 套餐业务自动注入
     */
    @Autowired
    private SetmealService setmealService;



    //@Override
    //public void removeCategory(Long id) {
    //    //构建条件构造器
    //    LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
    //    //条件构造器中放入条件
    //    dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);//进行eq方法(查询), 在Dish中根据CategoryId查询id(形参)为id(实参)的记录
    //    int hasDish = dishService.count();//IService中的count用于合计记录数
    //    if (hasDish > 0){
    //        //有记录, 抛出异常
    //        //throw new Exception("当期那分类下包含菜品, 无法进行删除");
    //    }
    //}


    @Override
    public void remove(Long id) {
        //创建条件构造器
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加条件,并且进行查询(eq())
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, id);
        //获取查询到的记录的条数(就是当前分类下关联的菜品数量)
        int count1 = dishService.count(dishLambdaQueryWrapper);
        //查询当前分类下面是或否关联有菜品
        if (count1 > 0) {
            //如果已经关关联了菜品就抛出异常
            throw new CustomException("当前分类下关联了菜品,无法删除!");
        }

        //查询菜品是否关联了套餐
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        //调用套餐业务中的count()进行计数
        int count2 = setmealService.count(setmealLambdaQueryWrapper);
        if (count2 > 0) {
            throw new CustomException("当前分类下关联了套餐,无法删除!");
        }

        super.removeById(id);

    }
}
