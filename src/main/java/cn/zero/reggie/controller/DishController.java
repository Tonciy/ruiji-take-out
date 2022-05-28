package cn.zero.reggie.controller;

import cn.zero.reggie.common.R;
import cn.zero.reggie.dto.DishDto;
import cn.zero.reggie.entity.Category;
import cn.zero.reggie.entity.Dish;
import cn.zero.reggie.entity.DishFlavor;
import cn.zero.reggie.service.CategoryService;
import cn.zero.reggie.service.DishFlavorService;
import cn.zero.reggie.service.DishService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Zero
 * @Description 描述此类
 */
@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 新增菜品
     *
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        log.info(dishDto.toString());
        dishService.saveWithFlavor(dishDto);
        // 删除缓存中的数据
        String key = "dish_"+ dishDto.getCategoryId();
        redisTemplate.delete(key);
        return R.success("新增菜品成功");
    }

    /**
     * 分页查询菜品数据
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        Page<Dish> dishPage = new Page<>(page, pageSize);
        Page<DishDto> pageInfo = new Page<>();
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.like(StringUtils.isNotEmpty(name),Dish::getName, name);
        // 默认按照更新时间降序
        dishLambdaQueryWrapper.orderByDesc(Dish::getUpdateTime);
        dishService.page(dishPage, dishLambdaQueryWrapper);

        // 分页对象基础信息拷贝
        BeanUtils.copyProperties(dishPage, pageInfo, "records");
        // 获取每个菜品的名称
        List<Dish> records = dishPage.getRecords();
        List<DishDto> res = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            // 获取分类id
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            String categoryName = category.getName();
            dishDto.setCategoryName(categoryName);
            return dishDto;
        }).collect(Collectors.toList());
        pageInfo.setRecords(res);
        return R.success(pageInfo);
    }

    /**
     * 根据 id 查询菜品信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){
        System.out.println("--------------------");
        return R.success(dishService.getByIdWithFlavor(id));
    }

    /**
     * 修改菜品信息
     *
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        log.info(dishDto.toString());
        dishService.updateWithFlavor(dishDto);
        // 删除缓存中的数据
        String key = "dish_"+ dishDto.getCategoryId();
        redisTemplate.delete(key);
        return R.success("修改成功");
    }


    /**
     * 获取某个菜品分类下的所有具体菜品信息
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        String key = "dish_" + dish.getCategoryId();
        List<DishDto> res = null;
        // 先从缓存中获取数据
        res = (List<DishDto>)redisTemplate.opsForValue().get(key);
        if(res != null){
            // 缓存中有对应数据，则直接返回，无需查询数据库
            return  R.success(res);
        }
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        // 只查 在售状态的菜品
        queryWrapper.eq(Dish::getStatus, 1);
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> dishes = dishService.list(queryWrapper);
        res = dishes.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            // 查询菜品对应的口味信息
            Long id = item.getId();
            LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
            dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId, id);
            List<DishFlavor> dishFlavors = dishFlavorService.list(dishFlavorLambdaQueryWrapper);
            dishDto.setFlavors(dishFlavors);
            return dishDto;
        }).collect(Collectors.toList());
        // 将数据加入缓存
        redisTemplate.opsForValue().set(key, res, 60L, TimeUnit.SECONDS);
        return R.success(res);
    }

    /**
     * 修改菜品状态
     * @param ids
     * @return
     */
    @PostMapping("/status/{st}")
    public R<String> setStatus(@RequestParam List<Long> ids,@PathVariable int st){
        log.info("setStatus....");
        dishService.setStatus(st, ids);
        return R.success("修改状态成功");
    }

    /**
     * 根据 id 删除菜品
     * * 这里不用清除缓存是因为：
     *      * 对于在售状态的菜品，是禁止删除的，也就这个菜品的状态无变化
     *      * 对于停售状态的菜品，可以删除，但是由于我们用户端只会显示在售状态的菜品，也就缓存中缓存的是只有在售状态的菜品
     *        所以删除了停售状态的菜品，对缓存中的数据无影响
     *      * 综上：所以这里不用清除缓存
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        dishService.deleteWithFlavor(ids);
        return R.success("删除菜品成功");
    }
}
