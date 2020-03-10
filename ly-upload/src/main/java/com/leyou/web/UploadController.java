package com.leyou.web;

import com.leyou.service.UploadService;
import com.leyou.vo.ResponseModel;
import com.netflix.discovery.converters.Auto;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Controller   //@RestController /* @Controller + @ResponseBody*/
@RequestMapping("/upload")
public class UploadController {
    @Autowired
    private UploadService uploadService;
    /**
     * 上传文件
     * */
    @RequestMapping(value = "/file",method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ResponseModel> uploadFile(@RequestParam(value = "eventId",required = false)Long eventId,@RequestParam("file")MultipartFile file){ //springmvc 会自动把文件封装到file中，我们可以操控file来处理文件
        String uploadFile = uploadService.uploadFile(file);
        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("uploadFile",uploadFile);
        resultMap.put("eventId",eventId);
        ResponseModel responseModel = new ResponseModel(HttpStatus.OK.value(),"ok",resultMap);
        return ResponseEntity.ok(responseModel);
    }
}
