package com.leyou.api;

import com.leyou.vo.ResponseModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


public interface SpecificationApi {

    /**
     * 查询 规格分类
     * */
    @GetMapping("/spec/specParam")
    ResponseModel queryParamList(
            @RequestParam(value = "groupId",required = false) Long groupId,
            @RequestParam(value = "cid",required = false)Long cid,
            @RequestParam(value = "searching",required = false)Boolean searching
    );

    /**
     * 根据分类id 查询规格分组，同时查询分组下的规格参数
     * */
    @GetMapping("/spec/groupList")
    ResponseModel querySpecGroupsByCid(@RequestParam(value = "cid")Long cid);
}
