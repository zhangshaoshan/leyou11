package com.leyou.web;

import com.leyou.pojo.SpecGroup;
import com.leyou.pojo.SpecParam;
import com.leyou.service.SpecificationService;
import com.leyou.vo.ResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/spec")
public class SpecificationController {
    @Autowired
    private SpecificationService specificationService;
    @GetMapping("/group")///{cid} //http://api.leyou.com/item/spec/group/76
    //public ResponseEntity<ResponseModel> queryGroupByCid(@PathVariable("cid") Long cid){
    public ResponseEntity<ResponseModel> queryGroupByCid(@RequestParam(value = "cid") Long cid){
        ResponseModel responseModel = new ResponseModel();
        responseModel.setCode(HttpStatus.OK.value());
        responseModel.setData(specificationService.queryGroupByCid(cid));
        return ResponseEntity.ok(responseModel);
    }

    /**
     * 增    删    改
     * **/
    @PostMapping("/addGroup")
    public ResponseEntity<ResponseModel> addGroup(SpecGroup specGroup){
        Long groupId = specificationService.addGroup(specGroup);
        return ResponseEntity.ok(new ResponseModel(HttpStatus.OK.value(),"ok",groupId));
    }

    /**
     * 查询 规格分类
     * */
    @GetMapping("/specParam")
    public ResponseEntity<ResponseModel> queryParamList(
            @RequestParam(value = "groupId",required = false) Long groupId,
            @RequestParam(value = "cid",required = false)Long cid,
            @RequestParam(value = "searching",required = false)Boolean searching
    ){
        List<SpecParam> list = specificationService.queryParamList(groupId,cid,searching);
        return ResponseEntity.ok(new ResponseModel(HttpStatus.OK.value(),"ok",list));
    }

    /**
     * 新增规格参数
     * */
    @PostMapping("/addSpecParam")
    public ResponseEntity<ResponseModel> addParam(SpecParam specParam){
        Long id = specificationService.addSpecParam(specParam);
        return ResponseEntity.ok(new ResponseModel(HttpStatus.OK.value(),"ok",id));
    }

    /**
     * 编辑规格参数
     * */
    @PutMapping("/editSpecParam")
    public ResponseEntity<ResponseModel> editParam(SpecParam specParam){
        Long id = specificationService.editParam(specParam);
        return ResponseEntity.ok(new ResponseModel(HttpStatus.OK.value(),"ok",id));
    }


    /**
     * 根据分类id 查询规格分组，同时查询分组下的规格参数
     * */
    @GetMapping("/groupList")
    public ResponseEntity<ResponseModel> querySpecGroupListByCid(@RequestParam(value = "cid")Long cid){
        List<SpecGroup> specGroupList = specificationService.querySpecGroupByCid(cid);
        return ResponseEntity.ok(new ResponseModel(HttpStatus.OK.value(),"ok",specGroupList));
    }

}
