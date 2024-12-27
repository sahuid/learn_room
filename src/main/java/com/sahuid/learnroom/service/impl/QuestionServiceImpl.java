package com.sahuid.learnroom.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sahuid.learnroom.common.R;
import com.sahuid.learnroom.exception.DataBaseAbsentException;
import com.sahuid.learnroom.exception.DataOperationException;
import com.sahuid.learnroom.exception.RequestParamException;
import com.sahuid.learnroom.model.dto.question.AddQuestionRequest;
import com.sahuid.learnroom.model.dto.question.QueryQuestionByPageRequest;
import com.sahuid.learnroom.model.dto.question.UpdateQuestionRequest;
import com.sahuid.learnroom.model.entity.Question;
import com.sahuid.learnroom.model.vo.UserVo;
import com.sahuid.learnroom.service.QuestionService;
import com.sahuid.learnroom.mapper.QuestionMapper;
import com.sahuid.learnroom.service.UserService;
import com.sahuid.learnroom.utils.ThrowUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author Lenovo
* @description 针对表【question】的数据库操作Service实现
* @createDate 2024-12-12 12:08:39
*/
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question>
    implements QuestionService{

    @Resource
    private UserService userService;

    @Override
    public void addQuestion(AddQuestionRequest addQuestionRequest, HttpServletRequest request) {
        if (addQuestionRequest == null) {
            throw new RequestParamException("请求参数错误");
        }

        String title = addQuestionRequest.getTitle();
        String answer = addQuestionRequest.getAnswer();
        if (StrUtil.isBlank(title) || StrUtil.isBlank(answer)) {
            throw new RequestParamException("题目或者答案不能为空");
        }

        Question question = new Question();
        BeanUtil.copyProperties(addQuestionRequest, question, false);

        List<String> tags = addQuestionRequest.getTags();
        String tagsStr = JSONUtil.parseArray(JSONUtil.toJsonStr(tags)).toString();
        question.setTags(tagsStr);

        UserVo currentUser = userService.getCurrentUser(request);
        question.setUserId(currentUser.getId());
        boolean save = this.save(question);
        ThrowUtil.throwIf(!save, () -> new DataOperationException("添加失败"));

    }

    @Override
    public void updateQuestion(UpdateQuestionRequest updateQuestionRequest) {
        if (updateQuestionRequest == null) {
            throw new RequestParamException("请求参数错误");
        }

        Long id = updateQuestionRequest.getId();
        Question question = this.getById(id);
        if (question == null) {
            throw new DataBaseAbsentException("当前题目不存在");
        }

        String answer = updateQuestionRequest.getAnswer();
        String context = updateQuestionRequest.getContext();
        String title = updateQuestionRequest.getTitle();
        List<String> tags = updateQuestionRequest.getTags();
        if (StrUtil.isNotBlank(answer)) {
            question.setAnswer(answer);
        }
        if (StrUtil.isNotBlank(context)) {
            question.setAnswer(context);
        }
        if (StrUtil.isNotBlank(title)) {
            question.setTitle(title);
        }
        if (tags != null) {
            String tagsStr = JSONUtil.parseArray(JSONUtil.toJsonStr(tags)).toString();
            question.setTags(tagsStr);
        }
        boolean updateById = this.updateById(question);
        // todo
    }

    @Override
    public Question queryQuestionById(Long id) {
        if (id == null || id <= 0) {
            throw new RequestParamException("请求参数错误");
        }
        Question question = this.getById(id);
        if (question == null) {
            throw new DataBaseAbsentException("当前题目不存在");
        }
        return question;
    }

    /**
     * 分页查询题目列表
     * @param queryQuestionByPageRequest
     * @return
     */
    @Override
    public Page<Question> queryQuestionByPage(QueryQuestionByPageRequest queryQuestionByPageRequest) {
        ThrowUtil.throwIf(queryQuestionByPageRequest == null, () -> new RequestParamException("请求参数错误"));
        int currPage = queryQuestionByPageRequest.getPage();
        int pageSize = queryQuestionByPageRequest.getPageSize();
        Page<Question> page = new Page<>(currPage, pageSize);
        Page<Question> questionPage = this.page(page);
        return questionPage;
    }
}




