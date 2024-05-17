package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.Impl.CategoryServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author: Lin
 * @Date: 2023-04-19 18:50
 * 控制层controller
 **/
@RestController
@RequestMapping("/category")//设置请求路径
@Slf4j
public class CategoryController {
    //自动注入
    @Autowired
    private CategoryService categoryService;

    /**
     * @param category
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Category category){
        log.info("category:{}",category);
        categoryService.save(category);
        return R.success("新增分类成功");
    }

    /**
     * 菜品分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize){
        //分页构造器
        Page<Category> pageInfo = new Page<>(page,pageSize);
        //条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //添加排序条件, 根据sort进行排序
        queryWrapper.orderByAsc(Category::getSort);
        /**
         * 这是一个使用 MyBatis-Plus 提供的查询构造器 QueryWrapper 的方法，
         * 意思是按照 Category 实体类中 sort 字段的升序进行排序。
         * 其中，Category::getSort 是一个 lambda 表达式，表示获取 Category 实体类中的 sort 字段。
         */
        //进行分页查询
        categoryService.page(pageInfo,queryWrapper);
        //返回结果集
        return R.success(pageInfo);
    }

    @DeleteMapping
    public R<String> delete(Long id){
        log.info("删除分类, id为:{}",id);
        //categoryService.removeById(id);
        //需要判断当前菜品分类是否关联了相应的菜品, 如果关联的话是不能删除的, 反之可以删除
        categoryService.remove(id);//categoryService是categoryServiceImpl的接口,所以也持有Impl的所有方法
        return R.success("分类信息删除成功");
    }

    /**
     * 修改菜品分类
     * @param category
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Category category){
        log.info("修改菜品类:{},id:{}",category.getName(),category.getId());
        categoryService.updateById(category);
        return R.success("修改分类信息成功");
    }

    //请求 URL: http://localhost:8080/category/list?type=1
    @GetMapping("/list")
    public R<List<Category>> list(Category category){//本来说形参应该写String type的,但是Category里面也包装有type,同时写category方便后期变换不同的参数(例如id,name,sort,time等等)
        //Create an object for the conditional constructor
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //Add condition for query based on type
        queryWrapper.eq(category.getType() != null,Category::getType,category.getType());
        //Add condition for sort
        queryWrapper.orderByAsc(Category::getType).orderByDesc(Category::getUpdateTime);//orderByAsc: 按升降排序,orderByDesc:按描述排序
        List<Category> categories = categoryService.list(queryWrapper);
        return R.success(categories);
    }
}
