package com.leyou.api;

import com.leyou.dto.CartDTO;
import com.leyou.vo.ResponseModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface GoodsApi {
    @GetMapping("spu/page")
    ResponseModel querySpuPage(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows,
            @RequestParam(value = "saleable", required = false) Boolean saleable,
            @RequestParam(value = "key", required = false) String key
    );

    @GetMapping("/spu/detail")
    ResponseModel querySpuDetailBySpuId(@RequestParam(value = "spuId") Long spuId);

    @GetMapping("/spu/sku")
    ResponseModel querySkuBySpuId(@RequestParam(value = "spuId") Long spuId);

    @GetMapping("/spu/spuId")
    ResponseModel querySpuBySpuId(@RequestParam(value = "spuId") Long spuId);

    @GetMapping("/skuList")
    ResponseModel querySkuListBySkuIds(@RequestParam(value = "ids") List<Long> ids);

    @PostMapping("/stock/decrease")
    ResponseModel decreaseStockByCarts(@RequestBody List<CartDTO> cartDTOs);
}
