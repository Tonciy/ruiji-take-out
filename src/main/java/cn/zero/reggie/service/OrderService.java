package cn.zero.reggie.service;

import cn.zero.reggie.dto.OrdersDto;
import cn.zero.reggie.entity.Orders;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author Zero
 * @Description 描述此类
 */
public interface OrderService extends IService<Orders> {

    /**
     * 下单
     * @param orders
     */
    void submit(Orders orders);

    /**
     * 后台管理总分页查询
     * @param page
     * @param pageSize
     * @param orderId
     * @param beginTime
     * @param endTime
     * @return
     */
    Page page(int page, int pageSize, Long orderId, String beginTime, String endTime);

    /**
     * 用户订单分页查询
     * @param page
     * @param pageSize
     * @param userId
     * @return
     */
    Page userPage(int page, int pageSize, Long userId);
}
