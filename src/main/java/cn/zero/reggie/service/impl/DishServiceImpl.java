package cn.zero.reggie.service.impl;


import cn.zero.reggie.dto.DishDto;
import cn.zero.reggie.entity.Dish;
import cn.zero.reggie.entity.DishFlavor;
import cn.zero.reggie.mapper.DishMapper;
import cn.zero.reggie.service.DishFlavorService;
import cn.zero.reggie.service.DishService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
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
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 新增菜品，同时保存对应的口味数据
     * @param dishDto
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        // 保存菜品的基本信息到菜品表 dish
        this.save(dishDto);
        // 获取菜品id
        Long id = dishDto.getId();
        // 设置口味对应的菜品id值
        List<DishFlavor> collect = dishDto.getFlavors().stream().map((item) -> {
            item.setDishId(id);
            return item;
        }).collect(Collectors.toList());
        // 保存菜品口味到数据库中
        dishFlavorService.saveBatch(collect);
    }

    @Override
    @Transactional
    public DishDto getByIdWithFlavor(Long id) {
        Dish dish = this.getById(id);
        DishDto dishDto = new DishDto();
        // 拷贝基本信息
        BeanUtils.copyProperties(dish, dishDto);
        // 查询当前菜品对应的口味信息
        LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId, id);
        List<DishFlavor> flavors = dishFlavorService.list(dishFlavorLambdaQueryWrapper);
        dishDto.setFlavors(flavors);
        return dishDto;
    }

    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        // 更新对应的菜品信息
        this.updateById(dishDto);
        // 清理当前菜品对应口味数据
        LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId, dishDto.getId());
        dishFlavorService.remove(dishFlavorLambdaQueryWrapper);
        // 添加当前提交过来的口味数据
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) ->{
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
    }
}
