package com.sahuid.learnroom.filexport;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.sahuid.learnroom.config.MinioConfig;
import com.sahuid.learnroom.exception.DataOperationException;
import com.sahuid.learnroom.exception.MinioConnectionException;
import com.sahuid.learnroom.exception.MinioOperationException;
import com.sahuid.learnroom.model.entity.Message;
import com.sahuid.learnroom.model.entity.Question;
import com.sahuid.learnroom.mq.RabbitMqService;
import com.sahuid.learnroom.service.MessageService;
import com.sahuid.learnroom.service.QuestionService;
import com.sahuid.learnroom.utils.MinioUtil;
import io.minio.MinioClient;
import io.minio.StatObjectArgs;
import io.minio.errors.ErrorResponseException;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.pqc.legacy.math.linearalgebra.IntUtils;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @Author: mcj
 * @Description: 文件导出抽象类
 * @DateTime: 2025/2/13 18:05
 **/
@Slf4j
public abstract class AbstractFileExport implements FileExport {

    @Resource
    protected MinioClient minioClient;

    @Resource
    protected MinioConfig minioConfig;

    @Resource
    protected RabbitMqService rabbitMqService;

    @Resource
    protected TransactionTemplate transactionTemplate;

    @Resource
    protected MessageService messageService;


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
    }

    protected abstract boolean needProcessData();

    /**
     * 保存数据库
     *
     * @param questionList
     */
    private void save2DB(List<Question> questionList) {
        String jsonStr = JSONUtil.toJsonStr(questionList);
        Message message = Message.builder()
                .content(jsonStr)
                .messageId(RandomUtil.randomLong())
                .messageType("question")
                .status("init")
                .build();
        boolean save = messageService.save(message);
        if (save) {
            // 保存成功，向 rabbitmq 发送消息
            rabbitMqService.sendQuestion2DBMessage(message);
        }else {
            log.error("保存本地消息库失败，消息内容：{}", message);
            throw new DataOperationException("保存本地消息库失败");
        }

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
