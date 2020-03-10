package com.leyou.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;
import java.util.List;

@Table(name = "tb_spu")
@Data
public class Spu {

  @Id
  @KeySql(useGeneratedKeys = true)
  private Long id;
  private String title;
  private String subTitle;
  private Long cid1;
  private Long cid2;
  private Long cid3;
  private Long brandId;
  private Boolean saleable;
  @JsonIgnore
  private Boolean valid;
  @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
  private Date createTime;
  @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
  private Date lastUpdateTime;

  //分类名称 给前端用
  @Transient
  private String categoryName;
  //品牌名称 给前端用
  @Transient
  private String brandName;
  @Transient
  private List<Sku> skus;
  @Transient
  private SpuDetail spuDetail;
}
