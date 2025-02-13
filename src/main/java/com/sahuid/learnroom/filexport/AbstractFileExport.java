package com.sahuid.learnroom.filexport;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.json.JSONUtil;
import com.sahuid.learnroom.model.entity.Question;

import java.io.File;
import java.util.List;

/**
 * @Author: mcj
 * @Description: 文件导出抽象类
 * @DateTime: 2025/2/13 18:05
 **/
public abstract class AbstractFileExport implements FileExport{
    @Override
    public void fileExport2DB(String filePath) {
        // 检查文件
        File file = checkFile(filePath);
        // 读取文件内容
        List<Question> questionList = readFileContext(file);
        // 处理内容
        questionList = processData(questionList);
        // 保存数据库
        save2DB(questionList);
        System.out.println(questionList);
    }

    /**
     * 保存数据库
     * @param questionList
     */
    private void save2DB(List<Question> questionList) {
        System.out.println("save 2 db");
    }


    /**
     * 处理文件内容
     * @param list
     * @return
     */
    protected abstract List<Question> processData(List<Question> list);

    /**
     * 读取文件内容
     * @param file
     * @return
     */
    protected abstract List<Question> readFileContext(File file);

    /**
     * 检查文件
     * @param filePath
     * @return
     */
    private File checkFile(String filePath) {
        File file = new File(filePath);
        if(!file.exists() || file.isDirectory()) {
            throw new IllegalArgumentException("文件必须存在且不能是文件夹");
        }
        return file;
    }
}
