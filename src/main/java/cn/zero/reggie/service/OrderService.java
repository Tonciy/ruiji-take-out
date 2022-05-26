package cn.zero.reggie.service;

import cn.zero.reggie.entity.Orders;
import com.baomidou.mybatisplus.extension.service.IService;

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
}
