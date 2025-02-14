package com.sahuid.learnroom.filexport;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.json.JSONUtil;
import com.sahuid.learnroom.common.UserThreadLocalData;
import com.sahuid.learnroom.config.MinioConfig;
import com.sahuid.learnroom.constants.FileConstant;
import com.sahuid.learnroom.exception.MinioOperationException;
import com.sahuid.learnroom.model.entity.Question;
import com.sahuid.learnroom.utils.MinioUtil;
import io.minio.GetObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: mcj
 * @Description: json文件导出
 * @DateTime: 2025/2/13 17:19
 **/
@Component
@Slf4j
public class JsonExport extends AbstractFileExport {

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
        String jsonStr = MinioUtil.readMinioContext(minioClient, bucketName, fileName);
        List<Question> list;
        try {
            list = JSONUtil.toBean(jsonStr, new TypeReference<List<Question>>() {
            }, false);
        } catch (Exception e) {
            log.error("json 文件格式不正确，请按照模板文件进行书写,{}", e.getMessage());
            throw new IllegalArgumentException("json 文件格式不正确");
        }
        return list;
    }

    @Override
    public String supportFileType() {
        return FileTypeEnum.JSON.getType();
    }

    @Override
    public void exportFileTemplate(HttpServletResponse response) {
        String bucketName = minioConfig.getBucketName();
        try (InputStream stream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(FileConstant.JSON_FILE_TEMPLATE)
                        .build())) {
            // 设置响应头
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=" + FileConstant.JSON_FILE_TEMPLATE);

            // 将文件流写入响应输出流
            OutputStream outputStream = response.getOutputStream();
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = stream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try {
                log.error("文件下载失败！！");
                response.getWriter().write("文件下载失败: " + e.getMessage());
            } catch (Exception ex) {
                log.error("文件下载失败！！");
                throw new MinioOperationException("minio 文件下载失败");
            }
        }
    }
}
