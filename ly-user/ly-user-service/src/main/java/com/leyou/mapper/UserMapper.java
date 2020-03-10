package com.leyou.mapper;

import com.leyou.pojo.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

public interface UserMapper extends Mapper<User> {
    @Select("SELECT *,user_id as userId FROM tb_user WHERE phone = #{phone}")
    User queryUserByPhone(@Param("phone")String phone);
}
