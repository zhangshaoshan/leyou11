package com.leyou.search.client;

import com.alibaba.fastjson.JSONObject;
import com.leyou.pojo.Category;
import com.leyou.vo.ResponseModel;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest
public class CategoryClientTest {

    @Autowired
    private CategoryClient categoryClient;
    @Test
    public void testQueryCategoryByids(){
        ResponseModel responseModel = categoryClient.queryCategoryByCids(Arrays.asList(1L, 2L, 3L));
        List<Category> categoryList  =  JSONObject.parseArray(JSONObject.toJSONString(responseModel.getData()),Category.class);
        Assert.assertEquals(3,categoryList.size());
        for (Category category:
             categoryList) {
            System.out.println("category = "+category);
        }
    }
}