package com.sahuid.learnroom.filexport;

import com.sahuid.learnroom.config.MinioConfig;
import com.sahuid.learnroom.exception.MinioConnectionException;
import com.sahuid.learnroom.exception.MinioOperationException;
import com.sahuid.learnroom.model.entity.Question;
import com.sahuid.learnroom.service.QuestionService;
import com.sahuid.learnroom.utils.MinioUtil;
import io.minio.MinioClient;
import io.minio.StatObjectArgs;
import io.minio.errors.ErrorResponseException;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: mcj
 * @Description: 文件导出抽象类
 * @DateTime: 2025/2/13 18:05
 **/
public abstract class AbstractFileExport implements FileExport {

    @Resource
    protected MinioClient minioClient;

    @Resource
    protected MinioConfig minioConfig;

    @Resource
    protected QuestionService questionService;


    @Override
    public void fileExport2DB(String fileName) {
        // 检查文件
        checkMinioExist(fileName);
        // 读取文件内容
        List<Question> questionList = readFileContext(fileName);
        // 处理内容
        if (needProcessData()) {
            questionList = processData(questionList);
        }
        // 保存数据库
        save2DB(questionList);
        System.out.println(questionList);
    }

    protected abstract boolean needProcessData();

    /**
     * 保存数据库
     *
     * @param questionList
     */
    private void save2DB(List<Question> questionList) {
        questionService.saveBatch(questionList);
    }


    /**
     * 处理文件内容
     *
     * @param list
     * @return
     */
    protected List<Question> processData(List<Question> list) {
        return list;
    }

    /**
     * 读取文件内容
     *
     * @param fileName
     * @return
     */
    protected abstract List<Question> readFileContext(String fileName);

    /**
     * 检查文件
     *
     * @param fileName
     */
    private void checkMinioExist(String fileName) {
        String bucketName = minioConfig.getBucketName();
        MinioUtil.checkMinioExist(minioClient, bucketName, fileName);
    }
}
