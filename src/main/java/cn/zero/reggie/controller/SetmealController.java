package cn.zero.reggie.controller;

import cn.zero.reggie.common.R;
import cn.zero.reggie.dto.SetmealDto;
import cn.zero.reggie.entity.Dish;
import cn.zero.reggie.entity.Setmeal;
import cn.zero.reggie.entity.SetmealDish;
import cn.zero.reggie.service.SetMealService;
import cn.zero.reggie.service.SetmealDishService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Zero
 * @Description 套餐管理接口
 */
@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetMealService setMealService;


    @Autowired
    private SetmealDishService setmealDishService;
    /**
     * 新增套餐
     * @return
     */
    @PostMapping
    @CacheEvict(value = "setmealCache", key = "#setmealDto.categoryId + '_' + #setmealDto.status")
    public R<String> save(@RequestBody SetmealDto setmealDto){
        log.info("新增套餐...");
        setMealService.addSetMealWithDish(setmealDto);
        return R.success("新增套餐成功");
    }

    /**
     * 分页查询套餐信息
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        return setMealService.pageWithCategory(page, pageSize, name);
    }

    /**
     * 批量删除套餐/单个删除套餐
     * @return
     */
    @DeleteMapping
    @CacheEvict(value = "setmealCache", allEntries = true)
    public R<String> delete(@RequestParam List<Long> ids){
        log.info("删除套餐：{}", ids.toString());
        setMealService.deleteWithDish(ids);
        return R.success("删除成功");
    }

    /**
     * 获取某个套餐分类下的所有具体套餐
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    @Cacheable(value = "setmealCache",key = "#setmeal.categoryId + '_' + #setmeal.status")
    public R<List<Setmeal>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId());
        // 只查 在售状态的菜品
        queryWrapper.eq(Setmeal::getStatus, 1);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> res = setMealService.list(queryWrapper);

        return R.success(res);
    }

    /**
     * 修改套餐状态信息
     * @param status
     * @param ids
     * @return
     */
    @CacheEvict(value = "setmealCache", allEntries = true)
    @PostMapping("/status/{status}")
    public R<String> setStatus(@PathVariable int status, @RequestParam List<Long> ids){
        setMealService.setStatus(ids, status);
        return R.success("修改成功");
    }

    /**
     * 根据 id 获取对应的套餐具体信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> getSetmeal(@PathVariable Long id){
        // 查询套餐基本信息
        Setmeal setmeal = setMealService.getById(id);
        // 构建 Dto 对象，进行额外属性封装
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal, setmealDto);
        // 查询此套餐对应的菜品信息
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(id != null,SetmealDish::getSetmealId, id);
        queryWrapper.orderByDesc(SetmealDish::getUpdateTime);
        List<SetmealDish> setmealDishList = setmealDishService.list(queryWrapper);
        setmealDto.setSetmealDishes(setmealDishList);
        return R.success(setmealDto);
    }
}
