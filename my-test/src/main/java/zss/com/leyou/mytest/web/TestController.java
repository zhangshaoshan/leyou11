package zss.com.leyou.mytest.web;

import com.leyou.vo.ResponseModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import zss.com.leyou.mytest.domain.User;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("test")
public class TestController {

    @PostMapping("/userInfo")
    public ResponseEntity<ResponseModel> queryUserInfo(User user){
        return ResponseEntity.ok(new ResponseModel(HttpStatus.OK.value(),"成功",user));
    }

    @PostMapping("/valid/userInfo")
    public ResponseEntity<ResponseModel> testValidatorUserInfo(@RequestBody @Valid User user){//
        return ResponseEntity.ok(new ResponseModel(HttpStatus.OK.value(),"成功",user));
    }

    @PostMapping("/post/params")
    public ResponseEntity<ResponseModel> testPostNormalParams(@RequestBody Map<String,Object> params){
        return ResponseEntity.ok(new ResponseModel(HttpStatus.OK.value(),"成功",params));
    }

    @GetMapping("/get/params")
    public ResponseEntity<ResponseModel> testGetNormalParams(String name,Integer age,String sex){
        return ResponseEntity.ok(new ResponseModel(HttpStatus.OK.value(),"成功",name+"   "+age+"  "+sex));
    }

    @PostMapping("/postList/params")
    public ResponseEntity<ResponseModel> testDeleteNormalParams(@RequestBody Map<String,Object> params){
        return ResponseEntity.ok(new ResponseModel(HttpStatus.OK.value(),"成功",params));
    }
}
