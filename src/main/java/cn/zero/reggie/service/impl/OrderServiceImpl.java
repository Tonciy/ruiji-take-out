package cn.zero.reggie.service.impl;

import cn.zero.reggie.common.BaseContext;
import cn.zero.reggie.common.CustomException;
import cn.zero.reggie.dto.OrdersDto;
import cn.zero.reggie.entity.*;
import cn.zero.reggie.mapper.OrderMapper;
import cn.zero.reggie.service.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author Zero
 * @Description 描述此类
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {
    @Autowired
    private UserService userService;
    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private AddressBookService addressBookService;
    @Autowired
    private OrderDetailService orderDetailService;

    @Override
    @Transactional
    public void submit(Orders orders) {
        // 查询当前用户 id
        Long userId = BaseContext.getCurrentId();
        // 查询当前用户的购物车数据
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, userId);
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(queryWrapper);
        if (shoppingCarts == null || shoppingCarts.size() == 0) {
            throw new CustomException("购物车为空，不能下单");
        }
        // 查询用户信息
        User user = userService.getById(userId);
        // 查询地址信息
        AddressBook addressBook = addressBookService.getById(orders.getAddressBookId());
        if (addressBook == null) {
            throw new CustomException("地址信息为空，不能下单");
        }
        // 向订单表插入数据，一条数据
        long orderId = IdWorker.getId();
        AtomicInteger amount = new AtomicInteger(0);
        List<OrderDetail> orderDetails = shoppingCarts.stream().map((item) -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());
        orders.setNumber(String.valueOf(orderId));
        orders.setId(orderId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get()));
        orders.setUserId(userId);
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + addressBook.getCityName() == null ? "" : addressBook.getCityName()
                + addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName()
                + addressBook.getDetail() == null ? "" : addressBook.getDetail());
        this.save(orders);
        // 向订单明细表插入数据，多条数据
        orderDetailService.saveBatch(orderDetails);

        // 清空购物车数据
        shoppingCartService.remove(queryWrapper);
    }

    @Override
    public Page page(int page, int pageSize, Long orderId, String beginTime, String endTime) {
        // 构建查询构造器
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<Orders>();
        queryWrapper.like(orderId != null,Orders::getId, orderId);
        queryWrapper.orderByDesc(Orders::getOrderTime);
        queryWrapper.apply(StringUtils.isNotBlank(beginTime), "date_format(order_time, '%Y-%m-%d %h:%m:%s') >= '" + beginTime + "'");
        queryWrapper.apply(StringUtils.isNotBlank(endTime), "date_format(order_time, '%Y-%m-%d %h:%m:%s') <= '" + endTime+ "'");
        Page<Orders> ordersPage = (Page<Orders>) new Page<Orders>(page, pageSize);
        this.page(ordersPage, queryWrapper);

        return ordersPage;
    }

    @Override
    public Page userPage(int page, int pageSize, Long userId) {
        // 构建查询构造器
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<Orders>();
        queryWrapper.like(userId != null,Orders::getUserId, userId);
        queryWrapper.orderByDesc(Orders::getOrderTime);
        Page<Orders> ordersPage = (Page<Orders>) new Page<Orders>(page, pageSize);
        this.page(ordersPage, queryWrapper);
        // 构造DTO 对象
        List<OrdersDto> ordersDtoList = ordersPage.getRecords().stream().map((item) -> {
            OrdersDto ordersDto = new OrdersDto();
            // 拷贝基本属性
            BeanUtils.copyProperties(item, ordersDto);
            // 查询对应订单明显表
            Long orderId = item.getId();
            LambdaQueryWrapper<OrderDetail> orderDetailLambdaQueryWrapper = new LambdaQueryWrapper<>();
            orderDetailLambdaQueryWrapper.eq(OrderDetail::getId, orderId);
            List<OrderDetail> list = orderDetailService.list(orderDetailLambdaQueryWrapper);
            // 复制给 DTO 对象
            ordersDto.setOrderDetails(list);
            return ordersDto;
        }).collect(Collectors.toList());
        // 构造状态DTO的分页对象
        Page<OrdersDto> ordersDtoPage = new Page<>();
        // 拷贝属性
        BeanUtils.copyProperties(ordersPage, ordersDtoPage);
        ordersDtoPage.setRecords(ordersDtoList);
        return ordersDtoPage;
    }
}
