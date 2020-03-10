package com.leyou.vo;

import com.leyou.enums.ExceptionEnum;
import jdk.nashorn.internal.objects.annotations.Constructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class ResponseModel {

    private Integer code;
    private String message;
    private Object data;

    public ResponseModel(){

    }

    public ResponseModel(ExceptionEnum em){
        this.code = em.getCode();
        this.message = em.getMsg();
        this.data = null;
    }

    public ResponseModel(Integer code,String message,Object data){
        this.code = code;
        this.message = message;
        this.data = data;
    }
}
