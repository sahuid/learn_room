package com.sahuid.learnroom.controller;

import com.sahuid.learnroom.common.R;
import com.sahuid.learnroom.service.FileService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * @Author: mcj
 * @Description: 文件上传下载控制器
 * @DateTime: 2025/2/11 14:32
 **/
@RestController
@RequestMapping("/file")
public class FileController {

    @Resource
    private FileService fileService;

    @PostMapping("/upload")
    public R<String> upload(@RequestParam("file") MultipartFile file) {
        String fileName = fileService.upload(file);
        return R.ok(fileName, "上传成功");
    }

    @PostMapping("/export")
    public R<Void> fileExport2DB(@RequestParam("file") MultipartFile file) {
        fileService.fileExport2DB(file);
        return R.ok("保存成功");
    }
}
