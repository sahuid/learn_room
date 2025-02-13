package com.sahuid.learnroom.filexport;

import cn.hutool.core.io.FileUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
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

    public void fileExport2DB(String path) {
        File file = new File(path);
        String type = FileUtil.getSuffix(file);
        FileExport fileExport = fileExportFactory.getFileExport(type);
        fileExport.fileExport2DB(path);
    }

    public void fileExport2DB(String path, String type) {
        FileExport fileExport = fileExportFactory.getFileExport(type);
        fileExport.fileExport2DB(path);
    }
}
