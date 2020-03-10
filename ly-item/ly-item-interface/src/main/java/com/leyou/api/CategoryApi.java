package com.leyou.api;

import com.leyou.vo.ResponseModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface CategoryApi {
    @GetMapping("/category/list/cids")
    ResponseModel queryCategoryByCids(@RequestParam("ids")List<Long> ids);
}
