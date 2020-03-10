package com.leyou.search.web;

import com.leyou.search.pojo.SearchRequest;
import com.leyou.search.service.SearchService;
import com.leyou.vo.ResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
public class SearchController {
    @Autowired
    private SearchService searchService;

    @PostMapping("/page")
    public ResponseEntity<ResponseModel> search(@RequestBody SearchRequest searchRequest){
        return ResponseEntity.ok(new ResponseModel(HttpStatus.OK.value(),"ok",searchService.search(searchRequest)));
    }


}
