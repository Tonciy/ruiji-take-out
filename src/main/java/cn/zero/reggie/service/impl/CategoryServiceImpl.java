package cn.zero.reggie.service.impl;

import cn.zero.reggie.common.CustomException;
import cn.zero.reggie.entity.Category;
import cn.zero.reggie.entity.Dish;
import cn.zero.reggie.entity.Setmeal;
import cn.zero.reggie.mapper.CategoryMapper;
import cn.zero.reggie.service.CategoryService;
import cn.zero.reggie.service.DishService;
import cn.zero.reggie.service.SetMealService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Zero
 * @Description 描述此类
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetMealService setMealService;


    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 添加查询添加，根据分类 id 进行查询
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, id);
        int count1 = dishService.count(dishLambdaQueryWrapper);
        // 查询当前分类是否关联的菜品，如果已经关联，抛出一个业务异常
        if(count1 > 0){
            // 已经关联菜品，抛出一个业务异常
            throw new CustomException("当前分类下关联了菜品，不能删除");
        }
        // 同理，查询是否跟具体套餐关联
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, id);
        int count2 = setMealService.count(setmealLambdaQueryWrapper);

        if(count2 > 0){
            throw new CustomException("当前分类下关联了套餐，不能删除");
        }

        // 无关联菜品和套餐，可以直接删除
        removeById(id);
    }
}
