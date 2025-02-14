package com.sahuid.learnroom.filexport;

import javax.servlet.http.HttpServletResponse;

/**
 * @Author: mcj
 * @Description: 文件导出接口
 * @DateTime: 2025/2/13 17:17
 **/
public interface FileExport {

    /**
     * 文件导出到数据库
     * @param fileName  文件名称
     */
    void fileExport2DB(String fileName);


    String supportFileType();

    /**
     * 返回文件模板
     * @param response
     */
    void exportFileTemplate(HttpServletResponse response);
}
