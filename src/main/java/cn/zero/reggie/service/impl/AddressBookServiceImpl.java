package cn.zero.reggie.service.impl;

import cn.zero.reggie.entity.AddressBook;
import cn.zero.reggie.mapper.AddressBookMapper;
import cn.zero.reggie.service.AddressBookService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author Zero
 * @Description 描述此类
 */
@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}
