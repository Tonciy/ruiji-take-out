package cn.zero.reggie.controller;

import cn.zero.reggie.common.R;
import cn.zero.reggie.entity.User;
import cn.zero.reggie.service.UserService;
import cn.zero.reggie.utils.SMSUtils;
import cn.zero.reggie.utils.ValidateCodeUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @author Zero
 * @Description 描述此类
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 发送验证码
     * @param user
     * @param httpSession
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession httpSession){
        String phone = user.getPhone();
        if(StringUtils.isNotEmpty(phone)){
            // 生成验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code={}", code);
            // 发送短息
//            SMSUtils.sendMessage("阿里云短信测试","SMS_154950909",phone,code);
            // 存入 session
            httpSession.setAttribute(phone, code);
            return R.success("手机验证码发送成功");
        }
        return R.error("请输入正确的手机号");
    }

    /**
     * 登录
     * @param map
     * @param httpSession
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map<String, String> map,HttpSession httpSession){
        String phone = map.get("phone").toString();
        String code = map.get("code");
        String codeInSession = (String)httpSession.getAttribute(phone);
        if(codeInSession != null && codeInSession.equals(code)){
            // 对比成功，登录成功
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone, phone);
            User user = userService.getOne(queryWrapper);
            if(user == null){
                // 当前手机号为新用户
                user = new User();
                user.setName("用户" + phone.substring(phone.length() - 4));
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            httpSession.setAttribute("user", user.getId());
            return R.success(user);
        }
        return R.error("登录失败");
    }

    /**
     * 用户退出账号
     * @return
     */
    @PostMapping("/loginout")
    public R<String> loginOut(HttpSession httpSession){
        httpSession.removeAttribute("user");
        return R.success("退出成功");
    }
}
