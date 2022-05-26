package cn.zero.reggie.service.impl;

import cn.zero.reggie.entity.Employee;
import cn.zero.reggie.mapper.EmployeeMapper;
import cn.zero.reggie.service.EmployeeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author Zero
 * @Description 描述此类
 */

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
}
