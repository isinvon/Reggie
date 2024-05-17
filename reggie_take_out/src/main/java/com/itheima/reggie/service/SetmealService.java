package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author: Lin
 * @Date: 2023-04-20 09:23
 **/
public interface SetmealService extends IService<Setmeal> {
    /**
     * 新增套餐,同时需要保存套餐和菜品的关联关系
     * */
    public void saveWithDish(SetmealDto setmealDto);

    /**
     * 解除套餐和套餐下所有菜品的关联关系
     */
    public void removeWithDish(List<Long> ids);

}
