package com.leyou.api;

import com.leyou.vo.ResponseModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

public interface UserApi {
    @GetMapping("/userInfo")
    ResponseModel getUserInfo(@RequestParam(value = "userId")String userId,@RequestParam(value = "token")String token);
}
