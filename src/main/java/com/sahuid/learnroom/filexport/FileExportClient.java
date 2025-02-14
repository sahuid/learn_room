package com.sahuid.learnroom.filexport;

import cn.hutool.core.io.FileUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;

/**
 * @Author: mcj
 * @Description: 文件导出数据库门面
 * @DateTime: 2025/2/13 23:03
 **/
@Component
public class FileExportClient {

    @Resource
    private FileExportFactory fileExportFactory;

    public void fileExport2DB(String fileName) {
        String type = FileUtil.getSuffix(fileName);
        this.fileExport2DB(fileName, type);
    }

    public void fileExport2DB(String fileName, String type) {
        FileExport fileExport = fileExportFactory.getFileExport(type);
        fileExport.fileExport2DB(fileName);
    }

    public void exportFileTemplate(String type, HttpServletResponse response) {
        FileExport fileExport = fileExportFactory.getFileExport(type);
        fileExport.exportFileTemplate(response);
    }
}
