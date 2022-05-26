package cn.zero.reggie.service.impl;

import cn.zero.reggie.entity.User;
import cn.zero.reggie.mapper.UserMapper;
import cn.zero.reggie.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author Zero
 * @Description 描述此类
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
