package com.sahuid.learnroom.model.vo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sahuid.learnroom.model.entity.Question;
import com.sahuid.learnroom.model.entity.QuestionBank;
import lombok.Data;

@Data
public class QuestionBankVo {

    private QuestionBank questionBank;

    private Page<Question> questionPage;
}
