package com.leyou.mapper;

import com.leyou.pojo.Stock;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.additional.idlist.IdListMapper;
import tk.mybatis.mapper.additional.insert.InsertListMapper;
import tk.mybatis.mapper.common.Mapper;

public interface StockMapper extends Mapper<Stock>, IdListMapper<Stock,Long>, InsertListMapper<Stock> {
    @Update("update tb_stock set stock = stock-#{num} where sku_id = #{id} and stock >= #{num}")
    int decreaseStock(@Param("id")Long id,@Param("num")Integer num);
}
