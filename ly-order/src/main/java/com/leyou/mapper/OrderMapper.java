package com.leyou.mapper;


import com.leyou.pojo.Order;
import tk.mybatis.mapper.additional.idlist.IdListMapper;
import tk.mybatis.mapper.common.Mapper;

public interface OrderMapper extends Mapper<Order>, IdListMapper<Order,Long> {
}
