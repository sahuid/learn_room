package com.sahuid.learnroom.es.model;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.sahuid.learnroom.model.dto.QuestionDto;
import com.sahuid.learnroom.model.entity.Question;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Author: mcj
 * @Description: ES 对应题目实体类
 * @DateTime: 2025/2/25 14:57
 **/
@Document(indexName = "question")
@Data
public class QuestionEsDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    /**
     * id
     */
    @Id
    private Long id;

    /**
     * 题目标题
     */
    private String title;

    /**
     * 题目内容
     */
    private String context;

    /**
     * 创建人id
     */
    private Long userId;

    /**
     * 推荐答案
     */
    private String answer;

    /**
     * 标签(json 数组)
     */
    private List<String> tags;

    /**
     * 题库 id
     */
    private Long questionBankId;

    /**
     * 浏览量
     */
    private Integer viewCount;

    /**
     * 创建时间
     */
    @Field(type = FieldType.Date, format = {}, pattern = DATE_TIME_PATTERN)
    private Date createTime;

    /**
     * 修改时间
     */
    @Field(type = FieldType.Date, format = {}, pattern = DATE_TIME_PATTERN)
    private Date updateTime;

    /**
     * 逻辑删除
     */
    private Integer isDelete;


    /**
     * dto转包装类
     * @param questionDto
     * @return
     */
    public static QuestionEsDTO dtoToEs(QuestionDto questionDto) {
        if (questionDto == null) {
            return null;
        }
        QuestionEsDTO questionEsDTO = new QuestionEsDTO();
        BeanUtil.copyProperties(questionDto, questionEsDTO, false);
        String tagsJsonStr = questionDto.getTags();
        if (StrUtil.isNotBlank(tagsJsonStr)) {
            questionEsDTO.setTags(JSONUtil.toList(tagsJsonStr, String.class));
        }
        return questionEsDTO;
    }


    /**
     * 包装类转对象
     * @param questionEsDTO
     * @return
     */
    public static Question esToObj(QuestionEsDTO questionEsDTO) {
        if (questionEsDTO == null) {
            return null;
        }
        Question question = new Question();
        BeanUtil.copyProperties(questionEsDTO, question, false);
        List<String> tags = questionEsDTO.getTags();
        if (CollUtil.isNotEmpty(tags)) {
            question.setTags(JSONUtil.toJsonStr(tags));
        }
        return question;
    }

}
