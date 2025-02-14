package com.sahuid.learnroom.filexport;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.json.JSONUtil;
import com.sahuid.learnroom.common.UserThreadLocalData;
import com.sahuid.learnroom.model.entity.Question;
import io.minio.GetObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: mcj
 * @Description: json文件导出
 * @DateTime: 2025/2/13 17:19
 **/
@Component
@Slf4j
public class JsonExport extends AbstractFileExport{

    @Override
    protected List<Question> processData(List<Question> list) {
        Long userId = UserThreadLocalData.getUserData();
        return list.stream()
                .map(question -> handlerQuestion(question, userId))
                .collect(Collectors.toList());
    }

    @NotNull
    private static Question handlerQuestion(Question question, Long userId) {
        String trimTitle = question.getTitle().trim();
        String trimContext = question.getContext().trim();
        String trimAnswer = question.getAnswer().trim();
        String trimTags = question.getTags().trim();
        Question newQuestion = new Question();
        newQuestion.setAnswer(trimAnswer);
        newQuestion.setUserId(userId);
        newQuestion.setTags(trimTags);
        newQuestion.setContext(trimContext);
        newQuestion.setTitle(trimTitle);
        return newQuestion;
    }

    @Override
    protected boolean needProcessData() {
        return true;
    }

    @Override
    protected List<Question> readFileContext(String fileName) {
        String bucketName = minioConfig.getBucketName();
        String jsonStr;
        try (InputStream stream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .build())) {
            jsonStr = IoUtil.readUtf8(stream);
        } catch (Exception e) {
            log.error("Minio 读取文件异常，{}", e.getMessage());
            throw new RuntimeException(e);
        }
        return JSONUtil.toBean(jsonStr, new TypeReference<List<Question>>() {
        }, false);
    }

    @Override
    public String supportFileType() {
        return FileTypeEnum.JSON.getType();
    }
}
