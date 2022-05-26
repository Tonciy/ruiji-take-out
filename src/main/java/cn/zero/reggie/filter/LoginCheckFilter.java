package cn.zero.reggie.filter;

import cn.zero.reggie.common.BaseContext;
import cn.zero.reggie.common.R;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * @author Zero
 * @Description 检查用户是否已经登录
 */
@Slf4j
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
public class LoginCheckFilter implements Filter {

    /**
     * 路径匹配器，支持通配符
     */
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    /**
     * 定义不需要处理的 URI 请求
     */
    private String[] path = {
            "/employee/login",
            "/employee/logout",
            "/backend/**",
            "/front/**",
            "/common/**",
            "/user/login",  // 移动端发送短信
            "/user/sendMsg" // 移动端登录
     };

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        // 1. 转化
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        // 2. 获取本次请求的 URI
        String requestURI = request.getRequestURI();
        log.info("拦截到请求了：{}",requestURI);
        // 3. 如果不需要处理，则直接放行
        if(check(requestURI)){
//            log.info("不需要处理请求：{}",requestURI);
            filterChain.doFilter(request, response);
            return;
        }
        // 4 - 1  判断登录状态，如果已经登录，则直接放行
        if(request.getSession().getAttribute("employee") != null){
            BaseContext.setCurrentId((long)request.getSession().getAttribute("employee"));
            filterChain.doFilter(request, response);
            return;
        }
        // 4 - 2 移动端判断登录状态，如果已经登录，则直接放行
        if(request.getSession().getAttribute("user") != null){
            BaseContext.setCurrentId((long)request.getSession().getAttribute("user"));
            filterChain.doFilter(request, response);
            return;
        }
        log.info("未登录：{}",requestURI);
        // 5. 如果未登录则返回未登录结果，通过输出流方式想客户端页面响应数据--注意要配合客户端未登录时接收到的数据值要求
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));

    }

    /**
     * 路径匹配，检查本次请求是否需要放行
     * @param requestUri
     * @return
     */
    public boolean check(String requestUri){
        for (String s : path) {
            boolean match = PATH_MATCHER.match(s, requestUri);
            if(match){
                return true;
            }
        }
        return false;
    }
}
