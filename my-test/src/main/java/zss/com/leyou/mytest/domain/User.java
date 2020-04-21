package zss.com.leyou.mytest.domain;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;

@Data
public class User {
    @NotNull(message = "用户名不能为空")
    private String name;
    private Integer age;
    private Double pay;
    private ArrayList<String> list;
    @NotNull(message = "密码不能为空")
    @Size(min = 6,max = 16,message =  "密码长度必须是6-16个字符")
    private String password;
    @NotNull(message = "用户邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;
}
