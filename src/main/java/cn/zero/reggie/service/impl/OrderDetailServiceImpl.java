package cn.zero.reggie.service.impl;

import cn.zero.reggie.entity.OrderDetail;
import cn.zero.reggie.mapper.OrderDetailMapper;
import cn.zero.reggie.service.OrderDetailService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author Zero
 * @Description 描述此类
 */
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
