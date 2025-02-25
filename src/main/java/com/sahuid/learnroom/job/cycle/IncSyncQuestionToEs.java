package com.sahuid.learnroom.job.cycle;

import cn.hutool.core.collection.CollUtil;
import com.sahuid.learnroom.es.dao.QuestionEsDao;
import com.sahuid.learnroom.es.model.QuestionEsDTO;
import com.sahuid.learnroom.model.dto.QuestionDto;
import com.sahuid.learnroom.service.QuestionBankQuestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: mcj
 * @Description: 增量题目同步到 ES
 * @DateTime: 2025/2/25 18:43
 **/
@Component
@Slf4j
public class IncSyncQuestionToEs {

    @Resource
    private QuestionEsDao questionEsDao;

    @Resource
    private QuestionBankQuestionService questionBankQuestionService;

    /**
     * 每分钟查询一次，查询近 5 分钟的修改数据
     */
    @Scheduled(fixedDelay = 60 * 1000)
    public void run() {
        long FIVE_MINUTES = 5 * 60 * 1000;
        Date fiveMinutesAgoDate = new Date(new Date().getTime() - FIVE_MINUTES);
        List<QuestionDto> questionDtoList =
                questionBankQuestionService.queryFiveMinutesAgoQuestionAssembleBankId(fiveMinutesAgoDate);
        if (CollUtil.isEmpty(questionDtoList)) {
            log.info("没有需要增量的数据");
            return;
        }
        List<QuestionEsDTO> questionEsDTOList = questionDtoList.stream()
                .map(QuestionEsDTO::dtoToEs)
                .collect(Collectors.toList());
        final int pageSize = 500;
        int total = questionEsDTOList.size();
        log.info("增量同步开始，增量总数据：{}", total);
        int end = 0;
        for(int start = 0; start < total; start += pageSize) {
            end = Math.min(total, start + pageSize);
            log.info("题目增量同步 开始：{}， 结束：{}", start, end);
            questionEsDao.saveAll(questionEsDTOList.subList(start, end));
        }
        log.info("题目增量同步结束， 完成总量为:{}", end);
    }
}
