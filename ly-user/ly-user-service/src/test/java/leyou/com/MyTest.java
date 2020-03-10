package leyou.com;

import com.leyou.LyUserServiceApplication;
import com.leyou.config.UserConfig;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


import java.security.NoSuchAlgorithmException;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LyUserServiceApplication.class)
public class MyTest {
    @Autowired
    private UserConfig userConfig;

    @Test
    public void testConfig(){
        System.out.println("属性的类：  "+userConfig.getTimeout());
    }

    @Test
    public void testMd5String() throws NoSuchAlgorithmException {
        String saltPassword = "MD5_SALT"+"123456";
        //String mdPassword = DigestUtils.md5DigestAsHex(saltPassword.getBytes());
        String mdPassword = DigestUtils.md5Hex(saltPassword);
        System.out.println("mdPassword  "+mdPassword);
    }

    @Test
    public void testData(){
        Date date = new Date(System.currentTimeMillis());
        System.out.println("date  "+date);
    }

}
