package com.leyou.pojo;


import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "tb_order_status")
public class OrderStatus {

  @Id
  private Long orderId;
  private Integer status;
  private Date createTime;
  private Date paymentTime;
  private Date consignTime;
  private Date endTime;
  private Date closeTime;
  private Date commentTime;

}
