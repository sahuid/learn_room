package com.sahuid.learnroom.job.once;

import cn.hutool.core.collection.CollUtil;
import com.sahuid.learnroom.es.dao.QuestionEsDao;
import com.sahuid.learnroom.es.model.QuestionEsDTO;
import com.sahuid.learnroom.model.dto.QuestionDto;
import com.sahuid.learnroom.service.QuestionBankQuestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: mcj
 * @Description: 全量将题目同步到 ES
 * @DateTime: 2025/2/25 15:16
 **/
//@Component
@Slf4j
public class FullSyncQuestionToEs implements CommandLineRunner {

    @Resource
    private QuestionEsDao questionEsDao;

    @Resource
    private QuestionBankQuestionService questionBankQuestionService;

    /**
     * 全量获取题目（数据量不大的情况）
     * @param args
     * @throws Exception
     */
    @Override
    public void run(String... args) throws Exception {
        List<QuestionDto> questionDtoList = questionBankQuestionService.queryQuestionAssembleBankId();
        if (CollUtil.isEmpty(questionDtoList)) {
            log.info("没有需要全量同步的数据");
            return;
        }
        // 转换为 ES 实体类
        List<QuestionEsDTO> questionEsDTOList = questionDtoList.stream()
                .map(QuestionEsDTO::dtoToEs)
                .collect(Collectors.toList());
        // 分批插入到 ES
        final int pageSize = 500;
        int total = questionEsDTOList.size();
        log.info("题目全量同步到 ES 开始，总量为：{}", total);
        int end = 0;
        for(int start = 0; start < total; start += pageSize) {
            end = Math.min(total, start + pageSize);
            log.info("题目全量同步 开始：{}， 结束：{}", start, end);
            questionEsDao.saveAll(questionEsDTOList.subList(start, end));
        }
        log.info("题目全量同步结束， 完成总量为:{}", end);
    }
}
