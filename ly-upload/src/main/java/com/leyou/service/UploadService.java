package com.leyou.service;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.leyou.config.UploadProperties;
import com.leyou.enums.ExceptionEnum;
import com.leyou.exception.LyException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
@EnableConfigurationProperties(UploadProperties.class)
public class UploadService {

    @Autowired
    private FastFileStorageClient storageClient;

    @Autowired
    private UploadProperties uploadProperties;

    //private static final List<String> ALLOW_TYPE = Arrays.asList("image/jpeg","image/png","image/bmp");
    public String uploadFile(MultipartFile file) {
        try {
            //筛选文件类型
            if (!uploadProperties.getAllowType().contains(file.getContentType())){
                throw new LyException(ExceptionEnum.INVALID_FILE_TYPE);
            }
            //筛选文件内容
            BufferedImage image = ImageIO.read(file.getInputStream()); //读取文件内容，如果是图片，那么就能被读出来
            if (image == null){
                throw new LyException(ExceptionEnum.INVALID_FILE_TYPE);
            }

            String extension = StringUtils.substringAfterLast(file.getOriginalFilename(),".");//从最后获取
            //String extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
            StorePath storePath = storageClient.uploadFile(file.getInputStream(),file.getSize(),extension,null);

            //目标路径  在保存文件的服务器上新建一个保存文件的文件夹
            //File dest = new File("E:/idea/leyou/upload",file.getOriginalFilename());
            //保存文件到本地
            //file.transferTo(dest);
            //返回路径
            return uploadProperties.getBaseUrl() + storePath.getFullPath();
        } catch (IOException e) {
            log.error("文件上传失败！",e);
            throw new LyException(ExceptionEnum.BRAND_SAVE_ERROR);
        }
    }
}
