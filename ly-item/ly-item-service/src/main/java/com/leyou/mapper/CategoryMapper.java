package com.leyou.mapper;

import com.leyou.pojo.Category;
import tk.mybatis.mapper.additional.idlist.IdListMapper;
import tk.mybatis.mapper.additional.insert.InsertListMapper;
import tk.mybatis.mapper.common.Mapper;


/**
 * IdListMapper<Category,Long>
 *     Category 查找的对象模型（Catetory）
 *     Long  查找参数(id)的类型是Long
 * */

public interface CategoryMapper extends Mapper<Category>,IdListMapper<Category,Long>, InsertListMapper<Category>{
}
