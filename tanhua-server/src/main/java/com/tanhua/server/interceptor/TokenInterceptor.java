package com.tanhua.server.interceptor;

import com.tanhua.domain.db.User;
import com.tanhua.server.UserHolder;
import com.tanhua.server.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.NamedThreadLocal;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.lang.Nullable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
@Component
@Slf4j
public class TokenInterceptor implements HandlerInterceptor {
    @Autowired
    private UserService userService;


    private NamedThreadLocal<Long> startTimeThreadLocal = new NamedThreadLocal<Long>("StopWatch-startTimed");


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            long timed = System.currentTimeMillis();
            startTimeThreadLocal.set(timed);
            String requestURL = request.getRequestURI();
            log.info("111****preHandle****当前请求的URL：{}****", requestURL);
            log.info("111****preHandle****执行目标方法: {}****", handler);
            //获取请求参数
            String queryString = request.getQueryString();
            log.info("111****preHandle****请求参数:{}****", queryString);
        } catch (Exception e) {
            log.error("111****preHandle****异常：****", e);
        }

        log.info("进入前置拦截器。。。");
        String token = request.getHeader("Authorization");
        if (StringUtils.isNotEmpty(token)){
            User user = userService.getUserByToken(token);
            if (user==null){
                response.setStatus(401);
                return false;
            }
            UserHolder.setUser(user);
            return true;
        }
        response.setStatus(401);
        return false;
    }


    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
        try {
            long timeend = System.currentTimeMillis();
            log.info("333****postHandle****执行业务逻辑代码耗时：【{}】", timeend - startTimeThreadLocal.get());
        } catch (Exception e) {
            log.error("333****postHandle***异常：", e);
        }
    }
}
