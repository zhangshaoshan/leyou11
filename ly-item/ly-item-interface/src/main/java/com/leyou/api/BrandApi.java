package com.leyou.api;

import com.leyou.vo.ResponseModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

public interface BrandApi {
    @GetMapping("/brand/id")
    ResponseModel queryBrandByBrandId(@RequestParam("id")Long id);
}
