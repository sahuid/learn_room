package com.sahuid.learnroom.es.dao;

import com.sahuid.learnroom.es.model.QuestionEsDTO;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @Author: mcj
 * @Description: ES 题目操作
 * @DateTime: 2025/2/25 15:12
 **/
public interface QuestionEsDao extends ElasticsearchRepository<QuestionEsDTO, Long> {
}
