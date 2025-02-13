package com.sahuid.learnroom.filexport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author: mcj
 * @Description: 文件导出工厂
 * @DateTime: 2025/2/13 18:24
 **/
@Component
public class FileExportFactory {

    private final Map<String, FileExport> CACHE;

    public FileExportFactory(List<FileExport> fileExports) {
        CACHE = fileExports.stream()
               .collect(Collectors.toMap(
                       FileExport::supportFileType,
                       fileExport -> fileExport,
                       (v1, v2) -> v1
               ));
    }


    public FileExport getFileExport(String type) {
        if (!CACHE.containsKey(type)) {
            throw new IllegalArgumentException("不能处理该类型的文件");
        }
        return CACHE.get(type);
    }
}
