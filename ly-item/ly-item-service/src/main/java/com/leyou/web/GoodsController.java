package com.leyou.web;

import com.leyou.dto.CartDTO;
import com.leyou.pojo.Spu;
import com.leyou.service.GoodsService;
import com.leyou.vo.ResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class GoodsController {
    @Autowired
    private GoodsService goodsService;

    @GetMapping("spu/page")
    private ResponseEntity<ResponseModel> querySpuPage(
            @RequestParam(value = "page",defaultValue = "1" ) Integer page,
            @RequestParam(value = "rows",defaultValue = "5" ) Integer rows,
            @RequestParam(value = "saleable",required = false) Boolean saleable,
            @RequestParam(value = "key",required = false) String key
    ){
        ResponseModel responseModel = new ResponseModel();
        responseModel.setCode(HttpStatus.OK.value());
        responseModel.setData(goodsService.querySpuPage(page,rows,saleable,key));
        return ResponseEntity.ok(responseModel);
    }

    /**
     * 新增商品
     * */
    @PostMapping("/addGoods")
    public ResponseEntity<ResponseModel> addGoods(@RequestBody Spu spu){
        goodsService.addGoods(spu);
        return ResponseEntity.ok(new ResponseModel(HttpStatus.CREATED.value(),"ok",spu));
    }

    /**
     * 查找商品spuDetail
     * */
    @GetMapping("/spu/detail")
    public ResponseEntity<ResponseModel> querySpuDetailBySpuId(@RequestParam(value = "spuId")Long spuId){
        return ResponseEntity.ok(new ResponseModel(HttpStatus.CREATED.value(),"ok",goodsService.querySpuDetailBySpuId(spuId)));
    }
    /**
     * 查找商品sku
     * */
    @GetMapping("/spu/sku")
    public ResponseEntity<ResponseModel> querySkuBySpuId(@RequestParam(value = "spuId")Long spuId){
        return ResponseEntity.ok(new ResponseModel(HttpStatus.CREATED.value(),"ok",goodsService.querySkuBySpuId(spuId)));
    }

    /**
     * 修改商品
     * */
    @PostMapping("/edit/spu")
    public ResponseEntity<ResponseModel> updateGoods(@RequestBody Spu spu){
        return ResponseEntity.ok(new ResponseModel(HttpStatus.CREATED.value(),"ok",goodsService.updateGoods(spu)));
    }

    /**
     * 根据spuid 查询spu
     * */
    @GetMapping("/spu/spuId")
    public ResponseEntity<ResponseModel> querySpuBySpuId(@RequestParam(value = "spuId") Long spuId){
        return ResponseEntity.ok(new ResponseModel(HttpStatus.CREATED.value(),"ok",goodsService.querySpuBySpuId(spuId)));
    }

    /**
     * 根据sku id集合去查询sku集合
     * */
    @GetMapping("/skuList")
    public ResponseEntity<ResponseModel> querySkuListBySkuIds(@RequestParam(value = "ids")List<Long> ids){
        return ResponseEntity.ok(new ResponseModel(HttpStatus.OK.value(),"ok",goodsService.querySkuListBySkuIds(ids)));
    }

    @PostMapping("/stock/decrease")
    public ResponseEntity<ResponseModel> decreaseStockByCarts(@RequestBody List<CartDTO> cartDTOs){
        goodsService.decreaseStockByCarts(cartDTOs);
        return ResponseEntity.ok(new ResponseModel(HttpStatus.OK.value(),"ok",null));
    }

}
