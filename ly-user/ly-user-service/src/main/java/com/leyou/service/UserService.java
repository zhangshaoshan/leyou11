package com.leyou.service;

import com.leyou.enums.ExceptionEnum;
import com.leyou.exception.LyException;
import com.leyou.config.UserConfig;
import com.leyou.mapper.UserMapper;
import com.leyou.pojo.User;
import com.leyou.utils.NumberUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

@EnableConfigurationProperties(UserConfig.class)
@Slf4j
@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    AmqpTemplate amqpTemplate;
    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    UserConfig userConfig;

    private static final String KEY_PREFIX = "user:verify:phone:";
    private static final String MD5_SALT = "4$leoyuMD5salt!";

    public Boolean checkData(String data, Integer type) {
        //判断数据类型
        User record = new User();
        if (type != null){
            if (type == 1){
                record.setUsername(data);
            }else if (type == 2){
                record.setPhone(data);
            }else {
                throw new LyException(ExceptionEnum.INVALID_USER_DATA_TYPE);
            }
        }else {
            throw new LyException(ExceptionEnum.PARAM_FALL);
        }
        int count = userMapper.selectCount(record);//判断是否存在user
        return count >= 1;
    }

    public void sendCode(String phone) {
        //key
        String key = KEY_PREFIX + phone;
        //生成随机验证码
        String code = NumberUtils.generateCode(6);
        Map<String, String> msg = new HashMap<>();
        msg.put("phone",phone);
        msg.put("code",code);
        //发送验证码
        amqpTemplate.convertAndSend(userConfig.getExchange(),userConfig.getRoutingKey(),msg);
        //保存验证码
        redisTemplate.opsForValue().set(key,code,userConfig.getTimeout().longValue(), TimeUnit.MINUTES);
        log.info("保存了验证码为 ：  "+code);
    }

    public String register(String phone, String password, String code,String username,Integer type) {
        //判断用户名是否已经存在
        String data = null;
        if (type == 1){
            data = username;
        }else if (type == 2){
            data = phone;
        }else {
            throw new LyException(ExceptionEnum.INVALID_USER_DATA_TYPE);
        }
        if (checkData(data,type)){
            throw new LyException(ExceptionEnum.USER_EXISTED_ERROR);
        }
        //检测code是否正确
        String codeKey = KEY_PREFIX + phone;
        String verifyCode = redisTemplate.opsForValue().get(codeKey);
        if (StringUtils.isEmpty(verifyCode) || !code.equals(verifyCode)){
            throw new LyException(ExceptionEnum.VERIFY_CODE_ERROR);
        }
        //生成加密后的密码
        String saltPassword = MD5_SALT+password;
        String mdPassword = DigestUtils.md5DigestAsHex(saltPassword.getBytes());
        //生成唯一的盐 相当于userid
        String userId = UUID.randomUUID().toString().replace("-","");
        //保存到数据库
        User user = new User();
        user.setPhone(phone);
        user.setUsername(username);
        user.setPassword(mdPassword);
        user.setUserId(userId);
        user.setCreated(new Date());
        int row = userMapper.insert(user);
        if (row != 1){
            throw new LyException(ExceptionEnum.USER_REGISTER_ERROR);
        }
        //生成token
        String token = UUID.randomUUID().toString().replace("-","");
        //保存token 到redis 设置过期时间
        String tokenKey = user.getUserId();
        redisTemplate.opsForValue().set(tokenKey,token,120,TimeUnit.MINUTES);
        //移除注册校验码
        redisTemplate.delete(codeKey);
        return token;
    }

    public User login(String phone, String password) {
        //判断用户名是否已经存在
        if (!checkData(phone,2)){
            throw new LyException(ExceptionEnum.USER_NOEXISTED_ERROR);
        }
        //判断密码是否正确
        String saltPassword = MD5_SALT+password;
        String mdPassword = DigestUtils.md5DigestAsHex(saltPassword.getBytes());
        User user = userMapper.queryUserByPhone(phone);
        //查找密码失败
        if (StringUtils.isEmpty(user.getPassword())){
            throw new LyException(ExceptionEnum.USER_LOGIN_ERROR);
        }
        //密码错误
        if (!mdPassword.equals(user.getPassword())){
            throw new LyException(ExceptionEnum.USER_LOGIN_ERROR);
        }
        //生成token
        String token = UUID.randomUUID().toString().replace("-","");
        //保存token 到redis 设置过期时间
        String tokenKey = user.getUserId();
        if (StringUtils.isEmpty(tokenKey)){
            throw new LyException(ExceptionEnum.USER_NOEXISTED_ERROR);
        }
        redisTemplate.opsForValue().set(tokenKey,token,30,TimeUnit.MINUTES);
        user.setToken(token);
        log.info("【登录成功】");
        //返回user
        return user;
    }

    public User updatePassword(String phone, String oldPassword, String newPassword) {
        if (StringUtils.isEmpty(phone)||StringUtils.isEmpty(oldPassword)||StringUtils.isEmpty(newPassword)){
            throw new LyException(ExceptionEnum.PARAM_FALL);
        }
        //判断用户名是否已经存在
        if (!checkData(phone,2)){
            throw new LyException(ExceptionEnum.USER_NOEXISTED_ERROR);
        }
        //判断密码是否正确
        String saltPassword = MD5_SALT+oldPassword;
        String mdPassword = DigestUtils.md5DigestAsHex(saltPassword.getBytes());
        User user = new User();
        user.setPhone(phone);
        List<User> userList = userMapper.select(user);
        if (CollectionUtils.isEmpty(userList)){
            throw new LyException(ExceptionEnum.USER_NOEXISTED_ERROR);
        }
        user = userList.get(0);
        //查找密码失败
        if (StringUtils.isEmpty(user.getPassword())){
            throw new LyException(ExceptionEnum.PARAM_FALL);
        }
        //密码错误
        if (!mdPassword.equals(user.getPassword())){
            throw new LyException(ExceptionEnum.PARAM_FALL);
        }
        //修改密码
        if (StringUtils.isEmpty(newPassword)){
            throw new LyException(ExceptionEnum.PARAM_FALL);
        }
        //生成加密后的密码
        String newSaltPassword = MD5_SALT+newPassword;
        String newMdPassword = DigestUtils.md5DigestAsHex(newSaltPassword.getBytes());
        //保存密码
        user.setPassword(newMdPassword);
        int update = userMapper.updateByPrimaryKeySelective(user);
        if (update == 0){
            throw new LyException(ExceptionEnum.UPDATE_PASSWORD_FAIL);
        }
        //生成token
        String token = UUID.randomUUID().toString().replace("-","");
        //保存token 到redis 设置过期时间
        String tokenKey = user.getUserId();
        redisTemplate.opsForValue().set(tokenKey,token,30,TimeUnit.MINUTES);
        user.setToken(token);
        return user;
    }

    public User queryUserInfo(String userId) {
        if (StringUtils.isEmpty(userId)){
            throw new LyException(ExceptionEnum.USERID_ERROR);
        }
        User user = new User();
        user.setUserId(userId);
        List<User> userList = userMapper.select(user);
        if (CollectionUtils.isEmpty(userList)){
            throw new LyException(ExceptionEnum.USER_NOEXISTED_ERROR);
        }
        return userList.get(0);
    }
}
