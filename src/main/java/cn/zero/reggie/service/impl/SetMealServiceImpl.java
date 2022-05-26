package cn.zero.reggie.service.impl;


import cn.zero.reggie.common.CustomException;
import cn.zero.reggie.common.R;
import cn.zero.reggie.dto.SetmealDto;
import cn.zero.reggie.entity.Category;
import cn.zero.reggie.entity.Setmeal;
import cn.zero.reggie.entity.SetmealDish;
import cn.zero.reggie.mapper.SetMealMapper;
import cn.zero.reggie.service.CategoryService;
import cn.zero.reggie.service.DishFlavorService;
import cn.zero.reggie.service.SetMealService;
import cn.zero.reggie.service.SetmealDishService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Zero
 * @Description 描述此类
 */
@Slf4j
@Service
public class SetMealServiceImpl extends ServiceImpl<SetMealMapper, Setmeal> implements SetMealService{

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;

    @Override
    @Transactional
    public void addSetMealWithDish(SetmealDto setmealDto) {
        // 添加套餐信息
        this.save(setmealDto);
        // 获取新增的套餐id
        Long setmealId = setmealDto.getId();
       // 补充setmeal_dish中的套餐id信息
        List<SetmealDish> collect = setmealDto.getSetmealDishes().stream().map((item) -> {
            item.setSetmealId(setmealId);
            return item;
        }).collect(Collectors.toList());
        // 新增套餐对应菜品信息
        setmealDishService.saveBatch(collect);
    }

    @Override
    public R<Page> pageWithCategory(int page, int pageSize, String name) {
        // 正常查询流程
        Page<Setmeal> pageInfo = new Page<Setmeal>(page, pageSize);
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.like(StringUtils.isNotEmpty(name), Setmeal::getName, name);
        this.page(pageInfo, setmealLambdaQueryWrapper);
        // Setmeal 有个属性封装不了，得用 SetmealDto
        Page<SetmealDto> pageRes = new Page<>();
        BeanUtils.copyProperties(pageInfo, pageRes);
        List<Setmeal> records = pageInfo.getRecords();
        List<SetmealDto> collect = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            setmealDto.setCategoryName(category.getName());
            return setmealDto;
        }).collect(Collectors.toList());
        pageRes.setRecords(collect);
        return R.success(pageRes);
    }

    @Override
    public void deleteWithDish(List<Long> ids) {
        // 查询套餐信息，看是否可以删除
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId, ids);
        queryWrapper.eq(Setmeal::getStatus, 1);
        int count = this.count();
        if(count > 0){
            throw new CustomException("含套餐处于在售状态，删除失败");
        }
        // 删除套餐表中的数据 -- setmeal
        this.removeByIds(ids);
        // 删除关系表中的数据
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.in(SetmealDish::getSetmealId, ids);
        setmealDishService.remove(setmealDishLambdaQueryWrapper);
    }
}
