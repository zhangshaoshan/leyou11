package zss.com.leyou.mytest.log;

import com.alibaba.fastjson.JSON;
import com.leyou.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import tk.mybatis.mapper.util.StringUtil;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Configuration
@Slf4j
public class LogAspect {

    @Pointcut("execution(* zss.com.leyou.mytest.web.*Controller.*(..))")
    public void excudeService() {
    }
    @Around("excudeService()")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes) ra;
        HttpServletRequest request = sra.getRequest();
        Enumeration<String> headerNames = request.getHeaderNames();
        Map<String, Object> headerMap = new HashMap<>(10);
        do {
            String header = headerNames.nextElement();
            headerMap.put(header, request.getHeader(header));
        } while (headerNames.hasMoreElements());

        long start=System.currentTimeMillis ();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        log.info("***************************start "+sdf.format(start)+" *************************************************");
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String paraString = JSON.toJSONString(request.getParameterMap());
        String current = sdf.format(System.currentTimeMillis());
        log.info("\n" +
                        "请求地址  >>>  {}\n" +
                        "请求方法  >>>  {}\n" +
                        "请求参数  >>>  {}\n" +
                        "请求来源  >>>  {}\n" +
                        "内容类型  >>>  {}\n" +
                        "用户令牌  >>>  {}\n" +
                        "用户实例  >>>  {}\n" +
                        "请求头部  >>>  {}\n",
                request.getRequestURI(),
                request.getMethod(),
                JSON.toJSONString(request.getParameterMap()),
                request.getRemoteAddr(),
                request.getContentType(),
                JSON.toJSONString(headerMap));
        log.info("请求开始, URI: {}, method: {}, params: {} 时间:{}", uri, method, paraString,current);
        Object result = pjp.proceed();
        long end=System.currentTimeMillis ();
        log.info("\n"+
                "请求结束"+uri+" "+sdf.format(end)+"耗时 "+(end-start)+"ms"+"\n"+
                 JSON.toJSONString(result,true));
        return result;
    }

}