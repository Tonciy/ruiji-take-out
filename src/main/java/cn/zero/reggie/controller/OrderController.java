package cn.zero.reggie.controller;

import cn.zero.reggie.common.BaseContext;
import cn.zero.reggie.common.R;
import cn.zero.reggie.dto.OrdersDto;
import cn.zero.reggie.entity.Orders;
import cn.zero.reggie.service.OrderService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;
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
        log.info("当前用户的id是："+BaseContext.getCurrentId());
        orderService.submit(orders);
        return R.success("下单成功");
    }

    /**
     * 后台管理系统分页查询
     * @param page
     * @param pageSize
     * @param number
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, @RequestParam(required = false)Long number, @RequestParam(required = false) String beginTime, @RequestParam(required = false) String endTime){
        log.info(beginTime);
        log.info(endTime);
        return R.success(orderService.page(page, pageSize, number,beginTime, endTime));
    }
    /**
     * 员工订单分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public R<Page> userPage(int page, int pageSize){
        log.info("当前用户的id是："+BaseContext.getCurrentId());
        return R.success(orderService.userPage(page, pageSize, BaseContext.getCurrentId()));
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
