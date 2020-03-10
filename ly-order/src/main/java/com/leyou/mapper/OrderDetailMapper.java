package com.leyou.mapper;

import com.leyou.pojo.OrderDetail;
import tk.mybatis.mapper.additional.idlist.IdListMapper;
import tk.mybatis.mapper.additional.insert.InsertListMapper;
import tk.mybatis.mapper.common.Mapper;

public interface OrderDetailMapper extends Mapper<OrderDetail>, IdListMapper<OrderDetail,Long>, InsertListMapper<OrderDetail> {
}
