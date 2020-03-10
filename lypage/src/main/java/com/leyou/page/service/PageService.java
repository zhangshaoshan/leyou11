package com.leyou.page.service;

import com.alibaba.fastjson.JSONObject;
import com.leyou.page.client.BrandClient;
import com.leyou.page.client.CategoryClient;
import com.leyou.page.client.GoodsClient;
import com.leyou.page.client.SpecificationClient;
import com.leyou.pojo.Brand;
import com.leyou.pojo.Category;
import com.leyou.pojo.Sku;
import com.leyou.pojo.Spu;
import com.leyou.vo.ResponseModel;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Log4j2
public class PageService {
    @Autowired
    private BrandClient brandClient;
    @Autowired
    private CategoryClient categoryClient;
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private SpecificationClient specificationClient;

    @Autowired
    private TemplateEngine templateEngine;

    public Map<String, Object> loadModel(Long spuId) {
        Map<String,Object> model = new HashMap<>();
        ResponseModel spuResponseModel = goodsClient.querySpuBySpuId(spuId);
        Spu spu = JSONObject.parseObject(JSONObject.toJSONString(spuResponseModel.getData()),Spu.class);
        model.put("spu",spu);
        List<Sku> skus = spu.getSkus();
        model.put("skus",skus);
        ResponseModel brandResponseModel = brandClient.queryBrandByBrandId(spu.getBrandId());
        Brand brand = JSONObject.parseObject(JSONObject.toJSONString(brandResponseModel.getData()),Brand.class);
        model.put("brand",brand);
//        ResponseModel categoryBesponseModel = categoryClient.queryCategoryByCids(Arrays.asList(spu.getCid1(),spu.getCid2(),spu.getCid3()));
//        List<Category> categoryList = JSONObject.parseArray(categoryBesponseModel.getData().toString(),Category.class);
        model.put("category",spu.getCategoryName());
        return model;
    }

    /**
     * 新建html文件
     * */
    public void createHtml(Long spuId){
        //生成HTML
        Context context = new Context();
        //获取数据
        context.setVariables(loadModel(spuId));
        //输出流
        File dest = new File("E:/idea/leyou/upload",spuId+".html");

        if (dest.exists()){
            dest.delete();
        }

        try (PrintWriter writer = new PrintWriter(dest,"UTF-8")){
            templateEngine.process("item",context,writer);
        }catch (Exception e){
            log.error("【静态页服务】 生成静态页异常",e);
        }

    }

    /**
     * 删除html文件
     * */
    public void deleteHtml(Long spuId) {
        //输出流
        File dest = new File("E:/idea/leyou/upload",spuId+".html");

        if (dest.exists()){
            dest.delete();
        }
    }
}
