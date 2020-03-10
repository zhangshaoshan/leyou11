package com.leyou.web;

import com.leyou.service.UserService;
import com.leyou.vo.ResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
//@RequestMapping("/user")   网关中已经添加有/user ,,所以这里就不要再写了
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 用户校验，校验用户数据 是否 已经存在  用于注册前校验
     * */
    @GetMapping(value = "/check")
    public ResponseEntity<ResponseModel> checkData(@RequestParam("data")String data,@RequestParam("type")Integer type){
        return ResponseEntity.ok(new ResponseModel(HttpStatus.OK.value(),"ok",userService.checkData(data,type)));
    }

    /**
     * 发送短信验证码
     * */
    @PostMapping("/code")
    public void sendCode(@RequestParam("phone")String phone){
        userService.sendCode(phone);
    }

    /**
     * 用户注册
     * */
    @PostMapping("/register")
    public ResponseEntity<ResponseModel> register(@RequestParam("phone")String phone,@RequestParam("password")String password,@RequestParam("code")String code,@RequestParam("username")String username,@RequestParam("type")Integer type){
        return ResponseEntity.ok(new ResponseModel(HttpStatus.OK.value(),"ok",userService.register(phone,password,code,username,type)));
    }

    /**
     * 用户登录
     * */
    @PostMapping("/login")
    public ResponseEntity<ResponseModel> login(@RequestParam("phone")String phone,@RequestParam("password")String password){
        return ResponseEntity.ok(new ResponseModel(HttpStatus.OK.value(),"ok",userService.login(phone,password)));
    }

    /**
     * 修改密码
     * */
    @PostMapping("/updatePassword")
    public ResponseEntity<ResponseModel> updatePassword(@RequestParam("phone")String phone,@RequestParam("oldPassword")String oldPassword,@RequestParam("newPassword")String newPassword){
        return ResponseEntity.ok(new ResponseModel(HttpStatus.OK.value(),"ok",userService.updatePassword(phone,oldPassword,newPassword)));
    }

    /**
     * 获取用户信息
     * */
    @GetMapping("/userInfo")
    public ResponseEntity<ResponseModel> getUserInfo(@RequestParam(value = "userId")String userId,@RequestParam(value = "token")String token){
        return ResponseEntity.ok(new ResponseModel(HttpStatus.OK.value(),"ok",userService.queryUserInfo(userId)));
    }

}
