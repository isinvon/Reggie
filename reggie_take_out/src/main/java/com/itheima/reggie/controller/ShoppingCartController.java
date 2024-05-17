package com.itheima.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.ShoppingCart;
import com.itheima.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加购物车
     *
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {
        log.info("购物车数据:{}",shoppingCart);

        //1.设置用户id,指定当前是哪个用户的购物车数据
        Long currentUserId = BaseContext.getCurrentId();//获取当前用户的id
        //用户id设置到购物车中(即设置表shopping_cart中的user_id)
        shoppingCart.setUserId(currentUserId);

        Long dishId = shoppingCart.getDishId();

        //创建条件构造器
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, currentUserId);

        if (dishId != null) {
            //添加到购物车的是菜品
            //通过shoppingCart表中的user_id和dish_id联合判断菜品,这样可以唯一的锁定菜品
            queryWrapper.eq(ShoppingCart::getDishId, dishId);

        } else {//即setmealId != null
            //添加到购物车的是套餐
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }

        //2.查询当前莱品或者套餐是否在购物车中
        /*但添加购物车菜品的时候前端会返回给服务端一个dishId,而添加套餐的时候会返回一个setmealId,通过判读两个id是否存在来判断菜品或者套餐是否存在于购物车后中*/
        //SQL:select * from shopping_cart where user_id = ? and dish_id.setmeal_id = ?
        ShoppingCart cartServiceOne = shoppingCartService.getOne(queryWrapper);//根据 Wrapper，查询一条记录

        //3.如果已经存在，就在原来数量基础上加一
        if (cartServiceOne != null) {
            Integer number = cartServiceOne.getNumber();
            //菜品存在,加1
            cartServiceOne.setNumber(number + 1);
            //更新数据库
            shoppingCartService.updateById(cartServiceOne);//cartServiceOne是一个对象,封装有了Id
        }else {
            //4.如果不存在，则添加到购物车，数量默认就是一
            //第一次入库,number设置成1(数据库表这个字段默认是1，不设置也可以),creatTime也设置一下
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            //保存菜品到购物车(数据库表)中
            shoppingCartService.save(shoppingCart);
        }
        //返回购物车对象给用户端
        return R.success(cartServiceOne);
    }


    /**
     * 查看购物车
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        log.info("查看购物车...");
        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        //拿到当前用户的id来进行查找当前用户的购物车后中的菜品/套餐
        lqw.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        //排序(后加入的菜品最前面展示-->即根据时间升序排列)
        lqw.orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(lqw);
        return R.success(list);
    }


    /**
     * 清空购物车
     * @return
     */
    //以user_id做为删除的依据清空shopping_cart表中的所有行
    @DeleteMapping("/clean")
    public R<String> delete(){
        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        shoppingCartService.remove(lqw);
        return R.success("购物车清空成功");
    }

}
