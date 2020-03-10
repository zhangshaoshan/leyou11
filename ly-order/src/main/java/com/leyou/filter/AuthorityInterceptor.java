package com.leyou.filter;

import com.leyou.enums.ExceptionEnum;
import com.leyou.exception.LyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
public class AuthorityInterceptor implements HandlerInterceptor {
    @Autowired
    private StringRedisTemplate redisTemplate;
    //token过期时间
    private final Integer timeout = 120;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("被拦截的url为： "+request.getRequestURL());
        //获取前台请求参数
        Map<String, String[]> params = request.getParameterMap();
        if (params.containsKey("token") == false) {
            throw new LyException(ExceptionEnum.TOKEN_ERROR);
        }
        if (params.containsKey("userId") == false){
            throw new LyException(ExceptionEnum.USERID_ERROR);
        }
        String token = params.get("token")[0];
        String userId = params.get("userId")[0];
        //获取redis上的当前用户信息，如果为null就是没有登陆，要抛出异常
        String userToken = redisTemplate.opsForValue().get(userId);
        if (StringUtils.isEmpty(userToken)) {
            throw new LyException(ExceptionEnum.TOKEN_TIMEOUT);
        }
        if (token.equals(userToken) == false){
            throw new LyException(ExceptionEnum.TOKEN_TIMEOUT);
        }
        //把用户id存入
        //刷新token过期时间
        redisTemplate.opsForValue().set(userId,token,timeout, TimeUnit.MINUTES);
        return true;
    }
    //返回model前
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        //Controller执行完毕后，返回之前，可以对request和reponse进行处理
        //如果是前后端没有分离，在进入View层中前执行
    }

    //返回model后
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //在一个请求处理完毕，即将销毁的时候，执行，可以做一些资源释放之类的工作
    }
}
