package com.sahuid.learnroom.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sahuid.learnroom.common.R;
import com.sahuid.learnroom.exception.DataBaseAbsentException;
import com.sahuid.learnroom.exception.RequestParamException;
import com.sahuid.learnroom.model.dto.questionbank.AddQuestionBankRequest;
import com.sahuid.learnroom.model.dto.questionbank.QueryQuestionBankRequest;
import com.sahuid.learnroom.model.dto.questionbank.UpdateQuestionBankRequest;
import com.sahuid.learnroom.model.entity.QuestionBank;
import com.sahuid.learnroom.model.vo.UserVo;
import com.sahuid.learnroom.service.QuestionBankService;
import com.sahuid.learnroom.mapper.QuestionBandMapper;
import com.sahuid.learnroom.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author Lenovo
 * @description 针对表【question_band】的数据库操作Service实现
 * @createDate 2024-12-12 12:08:39
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class QuestionBankServiceImpl extends ServiceImpl<QuestionBandMapper, QuestionBank>
        implements QuestionBankService {

    private final UserService userService;

    @Override
    public void addQuestionBank(AddQuestionBankRequest addQuestionBankRequest, HttpServletRequest request) {
        if (addQuestionBankRequest == null) {
            throw new RequestParamException("请求参数不能为空");
        }
        String title = addQuestionBankRequest.getTitle();
        if (StrUtil.isBlank(title)) {
            throw new RequestParamException("标题不能为空");
        }
        QuestionBank questionBand = new QuestionBank();
        BeanUtil.copyProperties(addQuestionBankRequest, questionBand, false);

        R<UserVo> currentUser = userService.getCurrentUser(request);
        Long userId = currentUser.getValue().getId();

        questionBand.setUserId(userId);

        boolean save = this.save(questionBand);
        // todo
        if (!save) {

        }
    }

    @Override
    public void updateQuestionBank(UpdateQuestionBankRequest updateQuestionBankRequest, HttpServletRequest request) {
        if (updateQuestionBankRequest == null) {
            throw new RequestParamException("请求参数不能为空");
        }

        Long id = updateQuestionBankRequest.getId();
        if (id == null || id <= 0) {
            throw new RequestParamException("请求参数错误");
        }

        QuestionBank questionBand = this.getById(id);
        if (questionBand == null) {
            throw new DataBaseAbsentException("当前题库不存在");
        }
        String title = updateQuestionBankRequest.getTitle();
        String description = updateQuestionBankRequest.getDescription();
        if (StrUtil.isNotBlank(title)) {
            questionBand.setTitle(title);
        }
        if (StrUtil.isNotBlank(description)) {
            questionBand.setDescription(description);
        }

        boolean updateById = this.updateById(questionBand);
        // todo
        if (!updateById) {

        }
    }

    @Override
    public QuestionBank queryBankById(Long id) {
        if(id == null || id <= 0) {
            throw new RequestParamException("请求参数错误");
        }
        QuestionBank questionBank = this.getById(id);
        if (questionBank == null) {
            throw new DataBaseAbsentException("题库不存在");
        }
        return questionBank;
    }

    @Override
    public Page<QuestionBank> queryQuestionBankByPage(QueryQuestionBankRequest queryQuestionBankRequest) {
        if (queryQuestionBankRequest == null) {
            throw new RequestParamException("请求参数错误");
        }

        int currentPage = queryQuestionBankRequest.getPage();
        int pageSize = queryQuestionBankRequest.getPageSize();
        Page<QuestionBank> page = new Page<>(currentPage, pageSize);

        String title = queryQuestionBankRequest.getTitle();
        String description = queryQuestionBankRequest.getDescription();
        LambdaQueryWrapper<QuestionBank> queryWrapper = new LambdaQueryWrapper<>();

        if (StrUtil.isNotBlank(title)){
            queryWrapper.like(QuestionBank::getTitle, title);
        }
        if (StrUtil.isNotBlank(description)) {
            queryWrapper.like(QuestionBank::getDescription, description);
        }
        this.page(page, queryWrapper);
        return page;
    }

    @Override
    public void deleteQuestionBanks(List<Long> ids) {
        this.removeBatchByIds(ids);
    }
}




