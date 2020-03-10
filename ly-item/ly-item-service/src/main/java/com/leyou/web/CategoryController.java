package com.leyou.web;

import com.leyou.pojo.Category;
import com.leyou.service.CategoryService;
import com.leyou.vo.ResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 根据父节点  分页查询
     * */
    @RequestMapping(value = "/list",method = RequestMethod.GET)
    @ResponseBody  //返回请求体
    public ResponseEntity<ResponseModel> findCategoryListByPid(@RequestParam("pid")Long pid){
        ResponseModel responseModel = new ResponseModel();
        responseModel.setCode(HttpStatus.OK.value());
        responseModel.setData(categoryService.queryCategoryListByPid(pid));
        return ResponseEntity.ok(responseModel);
    }

    /**
     * 添加
     * */
    @RequestMapping(value = "/addCategory",method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ResponseModel> insertCategory(Category category){
        ResponseModel responseModel = new ResponseModel();
        responseModel.setCode(HttpStatus.OK.value());
        responseModel.setData(categoryService.insertCategory(category));
        return ResponseEntity.ok(responseModel);
    }

    @RequestMapping(value = "/updateCategory",method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ResponseModel> updateCategory(Category category){
        ResponseModel responseModel = new ResponseModel();
        responseModel.setCode(HttpStatus.OK.value());
        responseModel.setData(categoryService.updateCategory(category));
        return ResponseEntity.ok(responseModel);
    }

    /**
     * 根据id 查询分类
     * */
    @GetMapping("/list/cids")
    public ResponseEntity<ResponseModel> queryCategoryByIds(@RequestParam("ids")List<Long> ids){
        return ResponseEntity.ok(new ResponseModel(HttpStatus.OK.value(),"ok",categoryService.queryCategoryListByIds(ids)));
    }
}
