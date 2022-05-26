package cn.zero.reggie.controller;

import cn.zero.reggie.common.BaseContext;
import cn.zero.reggie.common.R;
import cn.zero.reggie.entity.ShoppingCart;
import cn.zero.reggie.service.ShoppingCartService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Zero
 * @Description 描述此类
 */
@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;


    /**
     * 添加菜品/套餐到购物车中
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        // 设置用户id
        shoppingCart.setUserId(BaseContext.getCurrentId());
        // 查询当前菜品或者套餐是否在购物车中
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        if(shoppingCart.getDishId() != null){
            // 添加到购物车的是菜品
            queryWrapper.eq(ShoppingCart::getDishId, shoppingCart.getDishId());
        }else{
            // 添加到购物车的是套餐
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }
        ShoppingCart shoppingCartServiceOne = shoppingCartService.getOne(queryWrapper);
        if(shoppingCartServiceOne == null){
            // 当前之前并无添加过此菜品/套餐
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            shoppingCartServiceOne = shoppingCart;
        }else{
            // 之前已经有过添加过此菜品/套餐
            shoppingCartServiceOne.setNumber(shoppingCartServiceOne.getNumber() + 1);
            shoppingCartService.updateById(shoppingCartServiceOne);
        }

        return R.success(shoppingCartServiceOne);
    }

    /**
     * 减少菜品/套餐到购物车中
     * @return
     */
    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart){
        // 设置用户id
        shoppingCart.setUserId(BaseContext.getCurrentId());
        // 查询当前菜品或者套餐是否在购物车中
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        if(shoppingCart.getDishId() != null){
            // 减少到购物车的是菜品
            queryWrapper.eq(ShoppingCart::getDishId, shoppingCart.getDishId());
        }else{
            // 减少到购物车的是套餐
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }
        ShoppingCart shoppingCartServiceOne = shoppingCartService.getOne(queryWrapper);
        if(shoppingCartServiceOne.getNumber() == 1){
            shoppingCartService.removeById(shoppingCartServiceOne.getId());
            return R.success(null);
        }else {
            shoppingCartServiceOne.setNumber(shoppingCartServiceOne.getNumber() - 1);
            shoppingCartService.updateById(shoppingCartServiceOne);
            return R.success(shoppingCartServiceOne);
        }

    }


    /**
     * 查询购物车信息
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        queryWrapper.orderByDesc(ShoppingCart::getCreateTime);
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(queryWrapper);
        return R.success(shoppingCarts);
    }

    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> clean(){
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        shoppingCartService.remove(queryWrapper);
        return R.success("清空购物车成功");
    }
}
