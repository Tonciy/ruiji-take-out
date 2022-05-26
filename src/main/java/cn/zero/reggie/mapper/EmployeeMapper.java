package cn.zero.reggie.mapper;

import cn.zero.reggie.entity.Employee;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Zero
 * @Description 员工类 Mapper 接口
 */
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
