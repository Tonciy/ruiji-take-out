package cn.zero.reggie.service.impl;

import cn.zero.reggie.entity.ShoppingCart;
import cn.zero.reggie.mapper.ShoppingCartMapper;
import cn.zero.reggie.service.ShoppingCartService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author Zero
 * @Description 描述此类
 */
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}
