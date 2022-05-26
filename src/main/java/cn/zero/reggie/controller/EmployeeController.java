package cn.zero.reggie.controller;

import cn.zero.reggie.common.R;
import cn.zero.reggie.entity.Employee;
import cn.zero.reggie.service.EmployeeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Enumeration;

/**
 * @author Zero
 * @Description 员工的控制层接口
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登入
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    private R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        // 1. 将页面提交的密码 password 进行 MD5 加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        // 2. 根据页面提交的用户名 username 查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        // 3. 如果没有查询到则返回登录失败结果--用户名不存在
        if (emp == null){
            return R.error("登录失败");
        }

        // 4. 密码比对，如果不一致则返回登录失败结果--密码不正确
        if( ! emp.getPassword().equals(password)){
            return R.error("登录失败");
        }

        // 5. 查看员工状态，如果为已禁用状态，则返回员工已禁用结果
        if(emp.getStatus() == 0){
            return R.error("此账号已禁用");
        }

        // 6. 登录成功，将员工 id 存入 Session 并返回登录成功结果
        request.getSession().setAttribute("employee", emp.getId());
        Enumeration<String> attributeNames = request.getSession().getAttributeNames();
        while (attributeNames.hasMoreElements()){
            String s = attributeNames.nextElement();
            System.out.println(s);
            System.out.println(request.getSession().getAttribute(s));
            System.out.println("------------------------");
        }
        return R.success(emp);
    }


    /**
     * 员工退出
     * @param request
     * @return
     */
    @PostMapping("/logout")
    private R<String> logout(HttpServletRequest request){
        // 清理 Session 中保存的当前登录员工的 id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * 新增员工
     * @param request
     * @param employee
     * @return
     */
    @PostMapping
    private R<String> save(HttpServletRequest request, @RequestBody Employee employee){
        log.info("新增员工，员工信息为：{}",employee.toString());

        // 设置初始值
        // 设置初始密码为 123456，需要进行 MD5 加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
        // 获取当前登录用户的 id
//        Long empId = (long)request.getSession().getAttribute("employee");
//        employee.setCreateUser(empId);
//        employee.setUpdateUser(empId);
        // 保存用户
        employeeService.save(employee);
        return R.success("新增员工成功");

    }


    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        log.info("page = {}, pageSize = {}, name = {}",page, pageSize, name);
        // 构造分页构造器
        Page<Employee> pageInfo = new Page<>(page, pageSize);
        // 构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        // 添加过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name);
        // 添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        // 执行查询
        employeeService.page(pageInfo, queryWrapper);
        return R.success(pageInfo);
    }


    /**
     * 更新员工信息
     * @param request
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee){
        log.info(employee.toString());
//        Long empId = (long)request.getSession().getAttribute("employee");
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser(empId);
        employeeService.updateById(employee);
        return R.success("更新成功");
    }

    /**
     * 根据 id 查询员工信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        log.info("根据 id 查询员工信息...");
        Employee employee = employeeService.getById(id);
        if(employee != null){
            return R.success(employee);
        }
        return R.error("查无用户");
    }

}
