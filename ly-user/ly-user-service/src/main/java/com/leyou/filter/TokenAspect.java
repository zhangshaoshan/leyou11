//package com.leyou.user.filter;
//
//import com.leyou.enums.ExceptionEnum;
//import com.leyou.exception.LyException;
//import lombok.extern.slf4j.Slf4j;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Before;
//import org.aspectj.lang.annotation.Pointcut;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.stereotype.Component;
//import org.springframework.util.StringUtils;
//
//import javax.servlet.http.HttpServletRequest;
//import java.util.Map;
//import java.util.concurrent.TimeUnit;
//
///*******
// * 切面的方式校验
// * ****/
//
//@Slf4j
//@Component
//@Aspect
//public class TokenAspect {
//    @Autowired
//    private StringRedisTemplate redisTemplate;
//    //用来获取前台请求头中的所有信息
//    @Autowired
//    private HttpServletRequest request;
//
//    @Pointcut("!execution(* *..web..UserController.checkData(..)) &&" +
//                "execution(* *..web..UserController.sendCode(..)) &&" +
//                "execution(* *..web..UserController.register(..)) &&" +
//                "execution(* *..web..UserController.login(..))"
//            )
//    public void controllerAspect(){}
//
//    //前置通知：在连接点之前执行的通知
//    @Before("controllerAspect()")
//    public void doBefore() {
//        //获取前台请求参数
//        Map<String, String[]> params = request.getParameterMap();
//        if (params.containsKey("token") == false) {
//            throw new LyException(ExceptionEnum.TOKEN_ERROR);
//        }
//        if (params.containsKey("userid") == false){
//            throw new LyException(ExceptionEnum.USERID_ERROR);
//        }
//        String token = params.get("token")[0];
//        String userid = params.get("userid")[0];
//        //获取redis上的当前用户信息，如果为null就是没有登陆，如要抛出异常
//        String userToken = redisTemplate.opsForValue().get(userid);
//        if (StringUtils.isEmpty(userToken)) {
//            throw new LyException(ExceptionEnum.TOKEN_TIMEOUT);
//        }
//        //刷新token过期时间
//        redisTemplate.opsForValue().set(userid,token,30, TimeUnit.MINUTES);
//    }
//}
