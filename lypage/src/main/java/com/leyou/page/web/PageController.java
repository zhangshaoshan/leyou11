package com.leyou.page.web;

import com.leyou.page.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Controller
public class PageController {

    @Autowired
    private PageService pageService;

//    @GetMapping("/hello")
//    public String toHello(Model model){
//        model.addAttribute("msg","hello,thymeleaf!");
//        return "hello";//返回普通字符串，会結合前缀后缀去寻找视图
//    }

    @GetMapping("/item/html/{id}.html")
    public String toItemPage(@PathVariable("id")Long spuId,Model model){
        Map<String,Object> map = pageService.loadModel(spuId);
        //准备模型数据
        model.addAllAttributes(map);
        //返回视图
        return "item";
    }
}
