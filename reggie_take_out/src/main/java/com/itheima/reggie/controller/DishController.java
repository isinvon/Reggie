package com.itheima.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.util.IgnorePropertiesUtil;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import com.itheima.reggie.service.Impl.DishServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    //@Autowired
    //private DishController controller;
    @Autowired
    private DishService dishService;
    @Autowired
    private DishServiceImpl dishServiceImpl;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;//管理菜品的业务对象



    /**
     * 新增菜品
     * */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){ //因为客户端向服务端提交数据是json,所以此处@RequestBody
        //这方法需要操作dish和dishflavor这两张表
        log.info("新增的菜品为: {}",dishDto.toString());

        dishService.saveWithFlavor(dishDto);

        return R.success("新增菜品成功");
    }


    /**
     * 菜品信息的分页
     * */
    //eg:
    // 请求 URL: http://localhost:8080/dish/page?page=1&pageSize=10
    //请求方法: GET
    //在这个URL中， page和pageSize都是查询参数，因此应使用@RequestParam注释它们。
    //http://localhost:8080/dish/{id} ，这里的id就是一个动态路径变量，应该使用@PathVariable进行访问。
    @GetMapping("/page")
    public R<Page> page(@RequestParam int page,int pageSize,String name){
        //分页构造器
        Page<Dish> pageInfo = new Page<>(page, pageSize);//Dish中缺少categoryName,所以需要使用DishDto扩展更多属性
        Page<DishDto> dishDtoPage = new Page<>();

        //创建条件构造器对象(查询的是菜品Dish表)
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件,like是模糊查询
        queryWrapper.like(name != null,Dish::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);//降序排序

        //执行分页查询
        dishService.page(pageInfo,queryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo,dishService, "records");//拷贝dishService的属性给pageInfo,但是需要把其他不需要拷贝的属性给忽略掉,不然就会影响到pageInfo本身的其他属性
        /*recorde是页面拷贝过去显示的数据/记录*/

        //拿到了records的list集合
        List<Dish> records = pageInfo.getRecords();

        List<DishDto> list = records.stream().map((item)->{

            //创建DishDto对象
            DishDto dishDto = new DishDto();

            //将dishDto合并到item中
            BeanUtils.copyProperties(item,dishDto);

            Long categoryId = item.getCategoryId();//找到分类的Id
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);

            if (category != null){//避免查询到的数据有问题得不到数据, 所以用于避免空指针
                //获取到菜品的名字
                String categoryName = category.getName();
                //终于可以把菜品对象返回给DishDto了
                dishDto.setCategoryName(categoryName);
            }

            //返回dishDto给list作为集合
            return dishDto;
        }).collect(Collectors.toList());

        //覆盖原来的records记录(对页面)
        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }

    /**
     * 对菜品信息的回显:根据id拆线呢菜品信息和对应的口味信息
     * */
    //eg:
    //请求 URL: http://localhost:8080/dish/1663526547312734209
    //请求方法: GET
    //http://localhost:8080/dish/{id} ，这里的id就是一个动态路径变量，应该使用@PathVariable进行访问。
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){
        DishDto dishDto = dishServiceImpl.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    /**
     * 修改菜品
     * */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){ //因为客户端向服务端提交数据是json,所以此处@RequestBody
        //这方法需要操作dish和dishflavor这两张表
        log.info("新增的菜品为: {}",dishDto.toString());

        dishService.updateWithFlavor(dishDto);//更新菜品

        return R.success("新增菜品成功");
    }



    /**
     * 查询菜品数据
     * 将菜品列表映射到新建套餐中的"添加菜品"列表中
     * 请求 URL:http://localhost:8080/dish/list?categoryId=1397844263642378242
     * 请求方法:GET
     * */
    @GetMapping("/list")
    public R<List<DishDto>> getlist(Dish dish) {//使用list<Dish>是为了返回菜品Dish的列表
        //构建条件构造器
        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
        //查询
        lqw.eq(dish.getCategoryId() != null ,Dish::getCategoryId,dish.getCategoryId());
        //选取起售的状态status为1
        lqw.eq(Dish::getStatus,1);
        //排序(升序)
        lqw.orderByAsc(Dish::getStatus).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(lqw);


        List<DishDto> dishDtosList = list.stream().map((item)->{
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId,dishId);
            //SQL:select * from dish_flavor where dish_id = ?
            List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());
        //返回菜品列表
        return R.success(dishDtosList);
    }








}
