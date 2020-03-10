package com.leyou.pojo;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

@Table(name = "tb_sku")
@Data
public class Sku {

  @Id
  @KeySql(useGeneratedKeys = true)
  private Long id;
  private Long spuId;
  private String title;
  private String images;
  private Long price;
  private String indexes;
  private String ownSpec;
  private Boolean enable;
  @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
  private Date createTime;
  @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
  private Date lastUpdateTime;

  @Transient
  private Long stock;// 库存

}
