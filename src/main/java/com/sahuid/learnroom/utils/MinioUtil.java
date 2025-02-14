package com.sahuid.learnroom.utils;

import cn.hutool.core.io.IoUtil;
import com.sahuid.learnroom.exception.MinioConnectionException;
import com.sahuid.learnroom.exception.MinioOperationException;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.StatObjectArgs;
import io.minio.errors.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @Author: mcj
 * @Description: Minio 工具类
 * @DateTime: 2025/2/14 14:01
 **/
@Slf4j
public class MinioUtil {

    /**
     * 检查 minio 中是否存在该对象
     *
     * @param minioClient
     * @param bucketName
     * @param fileName
     */
    public static void checkMinioExist(MinioClient minioClient, String bucketName, String fileName) {
        try {
            minioClient.statObject(StatObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .build());
        } catch (ErrorResponseException e) {
            if (e.response().code() == 404) {
                return;
            }
            throw new MinioOperationException("检查对象存在性失败");
        } catch (Exception e) {
            throw new MinioConnectionException("MinIO 连接异常");
        }
    }

    /**
     * 读取minio中的内容
     *
     * @param minioClient
     * @param bucketName
     * @param fileName
     */
    public static String readMinioContext(MinioClient minioClient, String bucketName, String fileName) {
        String context;
        try (InputStream stream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .build())) {
            context = IoUtil.readUtf8(stream);
        } catch (Exception e) {
            log.error("Minio 读取文件异常，{}", e.getMessage());
            throw new RuntimeException(e);
        }
        return context;
    }
}
