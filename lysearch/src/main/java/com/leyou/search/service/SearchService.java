package com.leyou.search.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.leyou.enums.ExceptionEnum;
import com.leyou.exception.LyException;
import com.leyou.pojo.*;
import com.leyou.search.client.BrandClient;
import com.leyou.search.client.CategoryClient;
import com.leyou.search.client.GoodsClient;
import com.leyou.search.client.SpecificationClient;
import com.leyou.search.pojo.Goods;
import com.leyou.search.pojo.SearchRequest;
import com.leyou.search.repository.GoodsRespository;
import com.leyou.vo.PageResult;
import com.leyou.vo.ResponseModel;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchService {
    @Autowired
    private CategoryClient categoryClient;
    @Autowired
    private BrandClient brandClient;
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private SpecificationClient specificationClient;
    @Autowired
    private GoodsRespository goodsRespository;

    /**
     * 将spu 分装成goods
     * */
    public Goods buildGoods(Spu spu){
        //分类
        ResponseModel categoryResponseModel = categoryClient.queryCategoryByCids(Arrays.asList(spu.getCid1(),spu.getCid2(),spu.getCid3()));
        List<Category> categoryList = JSONObject.parseArray(JSON.toJSONString(categoryResponseModel.getData()),Category.class);
        if (CollectionUtils.isEmpty(categoryList)){
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FOUND);
        }
        List<String> names = categoryList.stream().map(Category::getName).collect(Collectors.toList());
        //品牌
        ResponseModel brandResponseModel = brandClient.queryBrandByBrandId(spu.getBrandId());
        Brand brand = JSONObject.parseObject(JSONObject.toJSONString(brandResponseModel.getData()),Brand.class);
        //all 搜索字段
        String all = spu.getTitle() + StringUtils.join(names," ") + brand.getName();

        //查询 sku
        ResponseModel skuResponseModel = goodsClient.querySkuBySpuId(spu.getId());
        List<Sku> skuList = JSONObject.parseArray(JSONObject.toJSONString(skuResponseModel.getData()),Sku.class);
        if (CollectionUtils.isEmpty(skuList)){
            throw new LyException(ExceptionEnum.SKU_NOT_FOUND);
        }
        //sku的价格集合
        List<Long> skuPriceList = new ArrayList<>();
        //处理sku  取出需要的字段
        List<Map<String,Object>> skus = new ArrayList<>();
        for (Sku sku:
             skuList) {
            Map<String,Object> my_sku = new HashMap<>();
            my_sku.put("id",sku.getId());
            my_sku.put("title",sku.getTitle());
            my_sku.put("price",sku.getPrice());
            my_sku.put("image",StringUtils.substringBefore(sku.getImages(),","));
            skus.add(my_sku);
            skuPriceList.add(sku.getPrice());
        }
        //所有sku的json串
        String skuJsonString = JSONObject.toJSONString(skus);

        //查询规格参数  查找出cid3下的所有能被搜索用的规格参数
        ResponseModel specResponseModel = specificationClient.queryParamList(null, spu.getCid3(), true);
        List<SpecParam> specParamList = JSONObject.parseArray(JSONObject.toJSONString(specResponseModel.getData()),SpecParam.class);
        if (CollectionUtils.isEmpty(specParamList)){
            throw new LyException(ExceptionEnum.SPEC_PARAM_NOT_FOUND);
        }
        //查询商品详情
        ResponseModel spuDetailResponseModel = goodsClient.querySpuDetailBySpuId(spu.getId());
        SpuDetail spuDetail = JSONObject.parseObject(JSONObject.toJSONString(spuDetailResponseModel.getData()),SpuDetail.class);
        //通用规格参数
        Map<Long,String> genericSpec = JSONObject.parseObject(spuDetail.getGenericSpec(),new TypeReference<Map<Long,String>>(){});
        //特有规格参数
        Map<Long,List<String>> specialSpec = JSONObject.parseObject(spuDetail.getSpecialSpec(),new TypeReference<Map<Long,List<String>>>(){});
        //规格参数   将规格参数的name作为key  在tb_spu_detail中获取通用规格参数或私有规格参数，装换成map,通过id取出里面的值，将name 跟 值写入specs这个map中
        Map<String,Object> specs = new HashMap<>();
        Object value = "";
        for (SpecParam specParam:specParamList){
            String key = specParam.getName();
            if (specParam.getGeneric() == true){
                value = genericSpec.get(specParam.getId());
                if (specParam.getNumeric() == true){
                    value = chooseSegment(value.toString(),specParam);
                }

            }else {
                value = specialSpec.get(specParam.getId());
            }
            specs.put(key,value);
        }

        Goods goods = new Goods();
        goods.setBrandId(spu.getBrandId());
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        goods.setCreateTime(spu.getCreateTime());
        goods.setId(spu.getId());
        goods.setAll(all);// 用来搜索的字段  包括字段，标题，分类，品牌，规格等
        goods.setPrice(skuPriceList);// 商品价格
        goods.setSkus(skuJsonString);// 所有sku的集合json格式
        goods.setSpecs(specs);// 所有可搜索的规格参数
        goods.setSubTitle(spu.getSubTitle());
        return goods;
    }

    private String chooseSegment(String value, SpecParam p) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if(segs.length == 2){
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if(val >= begin && val < end){
                if(segs.length == 1){
                    result = segs[0] + p.getUnit() + "以上";
                }else if(begin == 0){
                    result = segs[1] + p.getUnit() + "以下";
                }else{
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }

    /**
     * 分页查询数据
     * */
    public PageResult<Goods> search(SearchRequest searchRequest){
        String key = searchRequest.getKey();
        if (StringUtils.isEmpty(key)){
            return null;
        }
        Integer page = searchRequest.getPage() - 1;// page 从0开始
        Integer size = searchRequest.getSize();

        //1 创建查询构建器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //对查询结果进行筛选  只取出需要的数据
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id","skus","subTitle"}, null));
        //基本查询
        queryBuilder.withQuery(QueryBuilders.matchQuery("all",key));
        //分页
        queryBuilder.withPageable(PageRequest.of(page,size));
        //结果
        Page<Goods> result = goodsRespository.search(queryBuilder.build());

        // 4、解析结果
        Long total = result.getTotalElements();
        //Long totalPage = (total + size - 1) / size;
        Integer totalPage = result.getTotalPages();
        return new PageResult<Goods>(total, totalPage, result.getContent());
    }

    /**
     * 对索引库进行新增或修改
     * */
    public void createOrUpdateIndex(Long spuId) {
        ResponseModel responseModel = goodsClient.querySpuBySpuId(spuId);
        Spu spu = JSONObject.parseObject(JSON.toJSONString(responseModel.getData()),Spu.class);
        Goods goods = buildGoods(spu);
        goodsRespository.save(goods);
    }

    public void deleteIndex(Long spuId) {
        goodsRespository.deleteById(spuId);
    }
}
