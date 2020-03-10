package com.leyou.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

@Table(name="tb_user")
@Data
public class User {

  @Id
  @KeySql(useGeneratedKeys = true)
  private long id;
  private String username;
  @JsonIgnore  //前端在查询user的时候 不会把password返回到前端
  private String password;
  private String phone;
  @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss") //指定时区和格式
  private Date created;
  //用户id  用户唯一标示
  private String userId;

  @Transient
  private String token;//用户验证凭证

}
