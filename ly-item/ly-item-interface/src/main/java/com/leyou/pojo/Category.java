package com.leyou.pojo;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;

@Table(name="tb_category") //通用mapper
@Data
public class Category {
  @Id
  @KeySql(useGeneratedKeys = true)
  private Long id;
  private String name;
  private Long parentId;
  private Boolean isParent;
  private Integer sort;


}
