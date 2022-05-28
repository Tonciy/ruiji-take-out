package cn.zero.reggie.service.impl;


import cn.zero.reggie.common.CustomException;
import cn.zero.reggie.dto.DishDto;
import cn.zero.reggie.entity.Dish;
import cn.zero.reggie.entity.DishFlavor;
import cn.zero.reggie.mapper.DishMapper;
import cn.zero.reggie.service.DishFlavorService;
import cn.zero.reggie.service.DishService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
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
    @Autowired
    private RedisTemplate redisTemplate;

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

    @Override
    @Transactional
    public void setStatus(int status, List<Long> ids) {
        // 缓存中的 key 前缀
        String keyPrefix = "dish_";
        for (Long id : ids) {
            LambdaUpdateWrapper<Dish> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(Dish::getId, id);
            updateWrapper.set(Dish::getStatus, status);
            this.update(updateWrapper);
            // 清除缓存
            Dish dish = this.getById(id);
            redisTemplate.delete(keyPrefix + dish.getCategoryId());
        }

    }

    @Override
    public void deleteWithFlavor(List<Long> ids) {
        // 查询要删除的菜品中是否包含在售状态
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Dish::getId, ids);
        queryWrapper.eq(Dish::getStatus, 1);
        int count = this.count(queryWrapper);
        if(count > 0){
            throw new CustomException("含菜品处于在售状态，删除失败");
        }
        // 删除菜品表的数据
        this.removeByIds(ids);
        // 删除口味表中对应数据
        LambdaQueryWrapper<DishFlavor> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.in(DishFlavor::getDishId, ids);
        dishFlavorService.remove(queryWrapper1);
    }
}
