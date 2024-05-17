package com.itheima.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.Impl.SetmealDishServiceImpl;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/setmeal")
public class SertmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private CategoryService categoryService;

    @PostMapping
    //创建一个"保存"方法
    public R<String> save(@RequestBody SetmealDto setmealDto){//setmealDto返回的是json数据,所以加上注解@Rquestbody
        log.info("新增套餐信息为:{}",setmealDto);//不加tostring()的话结果会显示数据类型,比较好识别
        setmealService.saveWithDish(setmealDto);
        return R.success("新增套餐成功");
    }

    /**
     * 显示套餐分页信息
     */
    /**
     * 菜品分页查询
     * 请求 URL:http://localhost:8080/setmeal/page?page=1&pageSize=10
     * 请求方法:GET
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(@RequestParam int page, int pageSize,String name){
        //分页构造器
        Page<Setmeal> pageInfo = new Page<>(page,pageSize);
        Page<SetmealDto> dtoPage = new Page<>();

        //条件构造器
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件, 根据like进行模糊查询
        queryWrapper.like(name != null,Setmeal::getName,name);
        //添加排序条件,根据更新时间降序排序
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        //将查询条件先交给业务类托管
        setmealService.page(pageInfo,queryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo,dtoPage,"records");

        //查询Setmeal的records
        List<Setmeal> records = pageInfo.getRecords();

        //查询查询菜品信息,并且返回records给数据类型为SetmealDto的集合中
        List<SetmealDto> dtoRecordsList = records.stream().map((item)->{
            SetmealDto setmealDto = new SetmealDto();
            //先将item的全部信息赋值给SetmealDto对象
            BeanUtils.copyProperties(item,setmealDto);
            //获取菜品分类的id
            Long categoryId = item.getCategoryId();
            //通过菜品id从数据库中查询到菜品的所有信息(即菜品对象)
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                //将查询到的菜品的名字categoryName赋值到'setmeaDto对象'中()
                setmealDto.setCategoryName(category.getName());
            }
            return setmealDto;
        }).collect(Collectors.toList());

        //将得到的SetmealDto类型的数据'setmealDtoPageRecords'塞入到setmealDtoPage中并且返回到前端
        dtoPage.setRecords(dtoRecordsList);
        //返回结果集
        return R.success(dtoPage);
    }


    /**
     * url: http://localhost:8080/setmeal?ids=1665591566107975681
     * 删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    @Transactional
    public R<String> delete(@RequestParam List<Long> ids){//List<Long>是因为删除多个套餐的时候前端会返回多个id,且用逗号隔开
        log.info("删除的套餐id为:{}",ids);
        //难点: 删除套餐的时候,需要先提前解除全部菜品跟套餐的关联关系
        //removeWithDish(): 解除菜品和套餐的关联关系,并且删除套餐
        setmealService.removeWithDish(ids);
        return R.success("套餐信息删除成功");
    }


    /**
     * 查询套餐信息
     * @param setmeal
     * @return
     */
    public R<List<Setmeal>> list(Setmeal setmeal){//这里的setmeal不加@RequestBody是因为返回值不是json数据,而是key-value形式的数据
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(setmeal.getCategoryId() != null,Setmeal::getCategoryId,setmeal.getCategoryId());
        lambdaQueryWrapper.eq(setmeal.getStatus() != null,Setmeal::getStatus,setmeal.getStatus());
        lambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> list = setmealService.list(lambdaQueryWrapper);
        return R.success(list);
    }




}
