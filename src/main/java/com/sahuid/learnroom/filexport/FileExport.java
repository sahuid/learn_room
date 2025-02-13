package com.sahuid.learnroom.filexport;

/**
 * @Author: mcj
 * @Description: 文件导出接口
 * @DateTime: 2025/2/13 17:17
 **/
public interface FileExport {

    /**
     * 文件导出到数据库
     * @param filePath  文件路径
     */
    void fileExport2DB(String filePath);


    String supportFileType();
}
