package com.leyou.search.repository;


import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.leyou.pojo.Spu;
import com.leyou.search.client.GoodsClient;
import com.leyou.search.pojo.Goods;
import com.leyou.search.service.SearchService;
import com.leyou.vo.PageResult;
import com.leyou.vo.ResponseModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GoodsRespositoryTest {
    @Autowired
    private GoodsRespository goodsRespository;
    @Autowired
    private ElasticsearchTemplate template;
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private SearchService searchService;

    @Test
    public void testCreateIndex(){
        template.createIndex(Goods.class);
        template.putMapping(Goods.class);
    }

    @Test
    public void mapTest(){
        Map<Long,String> map0 = new HashMap<>();
        map0.put(11L,"sf");
        map0.put(22L,"sdfsfd");
        String map0String = JSONObject.toJSONString(map0);
        Map<Long,String> genericSpec = (Map<Long, String>) JSONObject.parseObject(map0String,new TypeReference<Map<Long,String>>(){});
        System.out.println("-------------"+genericSpec);
        String string11 = genericSpec.get(11L);
        System.out.println("-------------"+string11);
        //特有规格参数
        List<String> list1 = Arrays.asList("11","aad","sdfsdf");
        List<String> list2 = Arrays.asList("sdf11","aad效果","sdfs所发生的df");
        List<String> list3 = Arrays.asList("1水电费1","asfdad","sdfDGFS21sdf");
        Map<Long,List<String>> map = new HashMap<>();
        map.put(11L,list1);
        map.put(22L,list2);
        map.put(33L,list3);
        String mapString = JSONObject.toJSONString(map);
        Map<Long, List<String>> specialSpec = (Map<Long, List<String>>) JSONObject.parseObject(mapString,new TypeReference<Map<Long,List<String>>>(){});
        List<String> list4 = specialSpec.get(11L);
        System.out.println("==============="+list4.size());
        System.out.println("==============="+list4.get(0));
        System.out.println("==============="+specialSpec);
    }

    /**
     * 将数据插入elasticsearch中
     * */
    @Test
    public void loadData(){
        Integer page = 1;
        Integer row = 100;
        int size = 0;
        do {
            //分页查询spu
            ResponseModel spuResponseModel = goodsClient.querySpuPage(page, row, true, null); //elasticsearch里面只存放能被查询的商品
            PageResult<Spu> pageResult = JSONObject.parseObject(JSONObject.toJSONString(spuResponseModel.getData()),new TypeReference<PageResult<Spu>>(){});
            if (pageResult == null){
                break;
            }
            List<Spu> spuList = pageResult.getItems();
            //遍历spuList  构建goods
            List<Goods> goodsList = spuList.stream().map(searchService::buildGoods).collect(Collectors.toList());
            //存入索引库
            goodsRespository.saveAll(goodsList);
            //翻页
            page ++;
            size = spuList.size();
        }while (size == row);

    }


}