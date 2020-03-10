package com.leyou.web;

import com.leyou.pojo.Brand;
import com.leyou.service.BrandService;
import com.leyou.vo.PageResult;
import com.leyou.vo.ResponseModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/brand")
public class BrandController {
    @Autowired
    private BrandService brandService;

    /**
     * 分页查询品牌
     * */
    @RequestMapping(value = "/page",method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ResponseModel> queryBrandByPage(
            @RequestParam(value = "page",defaultValue = "1" ) Integer page,
            @RequestParam(value = "rows",defaultValue = "5" ) Integer rows,
            @RequestParam(value = "sortBy",required = false) String sortBy,
            @RequestParam(value = "desc",defaultValue = "false" ) Boolean desc,
            @RequestParam(value = "key",required = false) String key
    ){
        PageResult<Brand> pageResult = brandService.queryBrandByPage(page,rows,sortBy,desc,key);
        ResponseModel responseModel = new ResponseModel(HttpStatus.OK.value(),"ok",pageResult);
        return ResponseEntity.ok(responseModel);
    }

    /**
     * 新增品牌
     * */
    @RequestMapping(value = "/addBrand",method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ResponseModel> addBrand(Brand brand, @RequestParam(value = "cids")List<Long> cids){
        brandService.createBrand(brand,cids);
        ResponseModel responseModel = new ResponseModel(HttpStatus.CREATED.value(),"ok",null);
        return ResponseEntity.ok(responseModel);
    }

    /**
     * 按照分类id查询品牌
     * */
    @GetMapping("/getBrandByCid")
    public ResponseEntity<ResponseModel> queryBrandByCid(@RequestParam(value = "cid")Long cid){
        List<Brand> brands = brandService.queryBrandByCid(cid);
        ResponseModel responseModel = new ResponseModel(HttpStatus.CREATED.value(),"ok",brands);
        return ResponseEntity.ok(responseModel);
    }

    /**
     * request body  test
     * 参数放在请求体中的测试
     * */
//    @GetMapping("/getBrandByCid")
//    public ResponseEntity<ResponseModel> queryBrandByCid(@RequestBody Map<String,String> requst){
//        List<Brand> brands = brandService.queryBrandByCid(1L);
//        ResponseModel responseModel = new ResponseModel(HttpStatus.CREATED.value(),"ok",brands);
//        return ResponseEntity.ok(responseModel);
//    }


    /**
     * 根据品牌id查询品牌
     * */
    @GetMapping("/id")
    public ResponseEntity<ResponseModel> queryBrandByBrandId(@RequestParam("id")Long id){
        return ResponseEntity.ok(new ResponseModel(HttpStatus.OK.value(),"ok",brandService.queryBrandByBrandId(id)));
    }
}
