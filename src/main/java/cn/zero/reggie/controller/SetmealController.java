package cn.zero.reggie.controller;

import cn.zero.reggie.common.R;
import cn.zero.reggie.dto.SetmealDto;
import cn.zero.reggie.entity.Dish;
import cn.zero.reggie.entity.Setmeal;
import cn.zero.reggie.service.SetMealService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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


    /**
     * 新增套餐
     * @return
     */
    @PostMapping
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
    public R<List<Setmeal>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId());
        // 只查 在售状态的菜品
        queryWrapper.eq(Setmeal::getStatus, 1);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> res = setMealService.list(queryWrapper);

        return R.success(res);
    }
}
