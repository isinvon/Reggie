package com.itheima.reggie.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.mapper.SetmealMapper;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: Lin
 * @Date: 2023-04-20 09:24
 **/
@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 新增套餐,同时需要保存套餐和菜品的关联关系
     *
     * @param setmealDto
     */
    @Transactional//保证数据的一致性
    @Override
    public void saveWithDish(@RequestBody SetmealDto setmealDto) {
        //1.保存setmeal表
        //保存套餐的基本信息，操作setmeal,执行insert操作
        this.save(setmealDto);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();

        setmealDishes = setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());


        //(批量操作)保存套餐和莱品的关联信息，操作setmeal_dish,执行insert操作
        setmealDishService.saveBatch(setmealDishes);
    }


    /**
     * 解除套餐和套餐下所有菜品的关联关系
     *
     * @param ids
     */
    @Override
    public void removeWithDish(List<Long> ids) {
        /** 此方法的sql语句为: select count(*) from setmeal where id in (1,2,3) and status =1
         * 解释: from 是从哪张表, where是条件, id in (1,2,3)是查询这些id列表, status是售卖状态为'起售'的
         */


        //1查询套餐状态,确定是否可用删除
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId, ids);
        queryWrapper.eq(Setmeal::getStatus, 1);
        //统计关联的菜品数量,count()是ServiceImpl中的方法
        int count = this.count(queryWrapper);
        if (count > 0) {
            //2如果不能删除,抛出业务异常
            throw new CustomException("套餐正在售卖中, 不能删除");
        }
        //3如果可以删除,先删除套餐表中的数据---setmeal
        this.removeByIds(ids);

        //成功解除关系之后就正式删除套餐信息
        //删除关系表中的数据(此时的ids不是套餐表setmeal中的ids,而是菜品setmealDish表中的的ids),所以要另外用其他方法
        /**删除套餐的sql语句为delete from setmeal dish where setmeal id in (1,2,3)*/
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //从setmealdish表中查询到套餐的id:setmeal_id,然后通过这个id从setmeal表中查询套餐
        lambdaQueryWrapper.in(SetmealDish::getSetmealId, ids);
        setmealDishService.remove(lambdaQueryWrapper);



    }
}