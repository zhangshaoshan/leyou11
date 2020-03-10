package com.leyou.pojo;


import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

@Table(name = "tb_spec_param")
@Data
public class SpecParam {
  @Id
  @KeySql(useGeneratedKeys = true)
  private Long id;
  private Long cid;
  private Long groupId;
  private String name;
    @Column(name = "`numeric`")  //加单引号  防止被数据库认为是关键字
  private Boolean numeric;
  private String unit;
  private Boolean generic;
  private Boolean searching;
  private String segments;
  @Transient
  private List<String> segmentList;
}
