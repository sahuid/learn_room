package com.sahuid.learnroom.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @Author: mcj
 * @Description: 文件上传接口
 * @DateTime: 2025/2/11 14:40
 **/
public interface FileService {

    /**
     * 文件上传
     * @param multipartFile
     * @return  文件名称
     */
    String upload(MultipartFile multipartFile);

    /**
     * 文件保存到数据库中
     * @param file
     */
    void fileExport2DB(MultipartFile file);
}
