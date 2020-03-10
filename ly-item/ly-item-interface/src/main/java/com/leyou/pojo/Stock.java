package com.leyou.pojo;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "tb_stock")
@Data
public class Stock {

  @Id
  @KeySql(useGeneratedKeys = true)
  private Long skuId;
  private Long seckillStock;
  private Long seckillTotal;
  private Long stock;
}
