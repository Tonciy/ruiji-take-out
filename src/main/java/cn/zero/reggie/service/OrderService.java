package cn.zero.reggie.service;

import cn.zero.reggie.dto.OrdersDto;
import cn.zero.reggie.entity.Orders;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

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
     * 分页查询
     * @param page
     * @param pageSize
     * @param orderId
     * @return
     */
    Page list(int page, int pageSize, Long orderId);
}
