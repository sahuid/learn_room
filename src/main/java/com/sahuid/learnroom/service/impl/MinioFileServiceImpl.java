package com.sahuid.learnroom.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.sahuid.learnroom.config.MinioConfig;
import com.sahuid.learnroom.exception.RequestParamException;
import com.sahuid.learnroom.service.UploadService;
import com.sahuid.learnroom.utils.ThrowUtil;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.rmi.ServerException;
import java.util.UUID;

/**
 * @Author: mcj
 * @Description: minio 文件上传实现类
 * @DateTime: 2025/2/11 14:41
 **/
@Service
@Slf4j
public class MinioFileServiceImpl implements UploadService {

    @Resource
    private MinioConfig minioConfig;

    @Resource
    private MinioClient minioClient;

    @Override
    public String upload(MultipartFile multipartFile) {
        ThrowUtil.throwIf(multipartFile == null,() -> new RequestParamException("文件为空"));
        // 获取文件真实名称
        String originalFilename = multipartFile.getOriginalFilename();
        // 获取文件后缀
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        // 生成随机的名称
        String newFileNamePrefix = RandomUtil.randomString(32);
        String newFileName = newFileNamePrefix + suffix;
        try {
            // 构建文件上传相关内容
            PutObjectArgs args = PutObjectArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .object(newFileName)
                    .stream(multipartFile.getInputStream(), multipartFile.getSize(), -1)
                    .contentType(multipartFile.getContentType())
                    .build();
            // 文件上传
            minioClient.putObject(args);
            log.info("文件上传成功，文件名称：{}", newFileName);
        } catch (Exception e) {
            log.error("文件下载失败：{}", e.getMessage());
            throw new RuntimeException("文件下载异常:" + e.getMessage());
        }
        return "http://127.0.0.1:9005" + "/" + minioConfig.getBucketName() + "/" + newFileName;
    }
}
