package com.leyou;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.domain.ThumbImageConfig;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FdfsTest {
    @Autowired
    private FastFileStorageClient storageClient;
    @Autowired
    private ThumbImageConfig thumbImageConfig;

    @Test
    public void testUpload() throws FileNotFoundException {
        File file = new File("C:/Users/Administrator/Desktop/33.jpg");
        StorePath storePath = this.storageClient.uploadFile(new FileInputStream(file),file.length(),"jpg",null);
        System.out.println("带分组的路径："+storePath.getFullPath());
        System.out.println("不带分组的路径："+storePath.getPath());
    }

    @Test
    public void testUploadImageAndCreatThum() throws FileNotFoundException {
        File file = new File("C:/Users/Administrator/Desktop/33.jpg");
        StorePath storePath = this.storageClient.uploadImageAndCrtThumbImage(new FileInputStream(file),file.length(),"jpg",null);
        System.out.println("带分组的路径："+storePath.getFullPath());
        System.out.println("不带分组的路径："+storePath.getPath());
        //获得缩略图路径
        String path = thumbImageConfig.getThumbImagePath(storePath.getPath());
        System.out.println("缩略图路径："+path);
    }

}
