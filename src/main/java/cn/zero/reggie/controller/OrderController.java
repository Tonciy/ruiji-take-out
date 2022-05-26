package cn.zero.reggie.controller;

import cn.zero.reggie.common.R;
import cn.zero.reggie.dto.OrdersDto;
import cn.zero.reggie.entity.Orders;
import cn.zero.reggie.service.OrderService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Zero
 * @Description 描述此类
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 下订单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        orderService.submit(orders);
        return R.success("下单成功");
    }

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @param number
     * @return
     */
    @GetMapping("/page")
    public R<Page> list(int page, int pageSize, Long number){
        return R.success(orderService.list(page, pageSize, number));
    }

    /**
     * 修改订单状态
     * @param orders
     * @return
     */
    @PutMapping
    public R<String> setStatus(@RequestBody Orders orders){
        orderService.updateById(orders);
        return R.success("修改状态成功");
    }
}
