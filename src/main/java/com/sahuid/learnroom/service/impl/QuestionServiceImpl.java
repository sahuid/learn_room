package com.sahuid.learnroom.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sahuid.learnroom.common.PageResult;
import com.sahuid.learnroom.common.UserThreadLocalData;
import com.sahuid.learnroom.constants.AiConstant;
import com.sahuid.learnroom.es.model.QuestionEsDTO;
import com.sahuid.learnroom.exception.DataBaseAbsentException;
import com.sahuid.learnroom.exception.DataOperationException;
import com.sahuid.learnroom.exception.RequestParamException;
import com.sahuid.learnroom.manager.AiService;
import com.sahuid.learnroom.mapper.QuestionMapper;
import com.sahuid.learnroom.model.entity.Question;
import com.sahuid.learnroom.model.entity.QuestionView;
import com.sahuid.learnroom.model.req.question.*;
import com.sahuid.learnroom.model.vo.QuestionViewHistoryVo;
import com.sahuid.learnroom.model.vo.QuestionVo;
import com.sahuid.learnroom.model.vo.UserVo;
import com.sahuid.learnroom.service.QuestionService;
import com.sahuid.learnroom.service.QuestionViewService;
import com.sahuid.learnroom.service.UserService;
import com.sahuid.learnroom.utils.ThrowUtil;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
* @author Lenovo
* @description 针对表【question】的数据库操作Service实现
* @createDate 2024-12-12 12:08:39
*/
@Service
@Slf4j
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question>
    implements QuestionService{

    @Resource
    private UserService userService;
    
    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Resource
    private AiService aiService;

    @Resource
    private QuestionViewService questionViewService;

    @Override
    public void addQuestion(AddQuestionRequest addQuestionRequest) {
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

        UserVo currentUser = userService.getCurrentUser();
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
        ThrowUtil.throwIf(!updateById, () -> new DataOperationException("修改失败"));
    }

    @Override
    public QuestionVo queryQuestionById(Long id) {
        if (id == null || id <= 0) {
            throw new RequestParamException("请求参数错误");
        }
        Question question = this.getById(id);
        if (question == null) {
            throw new DataBaseAbsentException("当前题目不存在");
        }
        QuestionVo questionVo = new QuestionVo();
        BeanUtil.copyProperties(question, questionVo, false);
        return questionVo;
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
        return this.page(page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void increaseQuestionViewCount(IncreaseQuestionViewCountRequest increaseQuestionViewCountRequest) {
        ThrowUtil.throwIf(increaseQuestionViewCountRequest == null, () -> new RequestParamException("请求参数错误"));
        Long questionId = increaseQuestionViewCountRequest.getQuestionId();
        Long userId = increaseQuestionViewCountRequest.getUserId();
        ThrowUtil.throwIf(questionId == null || userId == null || questionId <= 0 || userId <= 0,
                () -> new RequestParamException("请求参数错误"));
        // 修改浏览量
        LambdaUpdateWrapper<Question> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Question::getId, questionId);
        wrapper.setSql("viewCount = viewCount + 1");
        boolean update = this.update(wrapper);
        ThrowUtil.throwIf(!update, () -> new DataOperationException("计数失败"));
        // 插入或修改浏览记录
        LambdaQueryWrapper<QuestionView> viewLambdaQueryWrapper = new LambdaQueryWrapper<>();
        viewLambdaQueryWrapper.eq(QuestionView::getUserId, userId);
        viewLambdaQueryWrapper.eq(QuestionView::getQuestionId, questionId);
        QuestionView questionView = questionViewService.getOne(viewLambdaQueryWrapper);
        if (questionView == null) {
            // 不存在创建一条数据
            questionView = new QuestionView();
            questionView.setQuestionId(questionId);
            questionView.setUserId(userId);
            boolean save = questionViewService.save(questionView);
            ThrowUtil.throwIf(!save, () -> new DataOperationException("计数失败"));
            return;
        }
        // 存在修改查询次数和查看时间
        questionView.setViewCount(questionView.getViewCount() + 1);
        questionView.setViewTime(new Date(System.currentTimeMillis()));
        boolean updateById = questionViewService.updateById(questionView);
        ThrowUtil.throwIf(!updateById, () -> new DataOperationException("计数失败"));
    }

    @Override
    public PageResult<QuestionViewHistoryVo> getQuestionViewHistory(QueryQuestionViewHistoryRequest queryQuestionViewHistoryRequest) {
        ThrowUtil.throwIf(queryQuestionViewHistoryRequest == null, () -> new RequestParamException("请求参数错误"));
        Long userId = queryQuestionViewHistoryRequest.getUserId();
        ThrowUtil.throwIf(userId == null || userId <= 0, () -> new RequestParamException("请求参数错误"));
        int currentPage = queryQuestionViewHistoryRequest.getPage();
        int pageSize = queryQuestionViewHistoryRequest.getPageSize();
        Page<QuestionView> page = new Page<>(currentPage, pageSize);
        // 查询记录表
        LambdaQueryWrapper<QuestionView> viewLambdaQueryWrapper = new LambdaQueryWrapper<>();
        viewLambdaQueryWrapper.eq(QuestionView::getUserId, userId);
        viewLambdaQueryWrapper.orderBy(true, false, QuestionView::getViewTime);
        questionViewService.page(page, viewLambdaQueryWrapper);
        // 查询题目
        List<QuestionView> records = page.getRecords();
        // 非空判断
        PageResult<QuestionViewHistoryVo> pageResult = new PageResult<>();
        if (records.isEmpty()) {
            pageResult.setData(Collections.emptyList());
            pageResult.setTotal(0L);
            return pageResult;
        }
        List<Long> questionIds = records.stream().map(QuestionView::getQuestionId).collect(Collectors.toList());
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        // 确保顺序
        wrapper.in(Question::getId, questionIds);
        wrapper.last("ORDER BY FIELD(id," + StrUtil.join(",", questionIds) + ")");
        List<Question> list = this.list(wrapper);
        // 组装 VO
        Map<Long, QuestionView> idAndViewLogMap = records.stream().collect(Collectors.toMap(QuestionView::getQuestionId, questionView -> questionView));
        List<QuestionViewHistoryVo> resultList = list.stream().map(question -> {
            QuestionViewHistoryVo questionViewHistoryVo = new QuestionViewHistoryVo();
            BeanUtil.copyProperties(question, questionViewHistoryVo);
            QuestionView questionView = idAndViewLogMap.get(question.getId());
            questionViewHistoryVo.setViewTime(questionView.getViewTime());
            questionViewHistoryVo.setViewCount(questionView.getViewCount());
            return questionViewHistoryVo;
        }).collect(Collectors.toList());
        pageResult.setData(resultList);
        pageResult.setTotal(page.getTotal());
        return pageResult;
    }

    @Override
    public PageResult<Question> queryFromEs(QueryQuestionByPageRequest queryQuestionByPageRequest) {
        ThrowUtil.throwIf(queryQuestionByPageRequest == null,
                () -> new RequestParamException("查询请求参数错误"));
        // 获取查询参数
        String searchText = queryQuestionByPageRequest.getSearchText();
        Long questionBankId = queryQuestionByPageRequest.getQuestionBankId();
        // 获取分页参数 注意，ES 的起始页为 0
        int pageSize = queryQuestionByPageRequest.getPageSize();
        int page = queryQuestionByPageRequest.getPage() - 1;

        // 构造查询条件
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        // 精准查询
        boolQueryBuilder.filter(QueryBuilders.termQuery("isDelete", 0));
        if (questionBankId != null) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("questionBankId", questionBankId));
        }
        // 按关键词检索
        if (StrUtil.isNotBlank(searchText)) {
            boolQueryBuilder.should(QueryBuilders.matchQuery("title", searchText));
            boolQueryBuilder.should(QueryBuilders.matchQuery("context", searchText));
            boolQueryBuilder.should(QueryBuilders.matchQuery("answer", searchText));
            boolQueryBuilder.minimumShouldMatch(1);
        }
        // 分页
        PageRequest pageRequest = PageRequest.of(page, pageSize);
        // 构造查询
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(boolQueryBuilder)
                .withPageable(pageRequest)
                .build();
        SearchHits<QuestionEsDTO> searchHits =
                elasticsearchRestTemplate.search(searchQuery, QuestionEsDTO.class);
        // 封装返回对象
        PageResult<Question> pageResult = new PageResult<>();
        pageResult.setTotal(searchHits.getTotalHits());
        List<Question> dataList = new ArrayList<>();
        if (searchHits.hasSearchHits()) {
            List<SearchHit<QuestionEsDTO>> searchHitList = searchHits.getSearchHits();
            for (SearchHit<QuestionEsDTO> questionEsDTOSearchHit : searchHitList) {
                dataList.add(QuestionEsDTO.esToObj(questionEsDTOSearchHit.getContent()));
            }
        }
        pageResult.setData(dataList);
        return pageResult;
    }

    @Override
    public boolean aiGenerateQuestions(AIGenerateQuestionRequest aiGenerateQuestionRequest) {
        ThrowUtil.throwIf(aiGenerateQuestionRequest == null,
                () -> new RequestParamException("请求参数异常"));
        String questionType = aiGenerateQuestionRequest.getQuestionType();
        Integer number = aiGenerateQuestionRequest.getNumber();
        if (number == null || number < 0 || number > 100) {
            log.error("ai 生成题目数量异常");
            throw new RequestParamException("题目数量异常，需要大于0且不能超过 100");
        }
        ThrowUtil.throwIf(StrUtil.isBlank(questionType),
                () -> new RequestParamException("ai 生成题目类型错误"));

        // 定义系统 prompt
        String systemPrompt = AiConstant.DEEPSEEK_GENERATE_QUESTION_SYSTEM_PROMPT;
        // 生成用户 prompt
        String userPrompt = String.format("题目数量：%s, 题目方向：%s", number, questionType);
        // 调用 ai 生成
        String result = aiService.onceChat(systemPrompt, userPrompt);
        // 结果预处理
        // 拆分题目
        List<String> questionList = Arrays.asList(result.split("\n"));
        // 去除序号和特殊符号
        List<String> titleList = questionList.stream()
                // 删除序号
                .map(line -> StrUtil.removePrefix(line, StrUtil.subBefore(line, " ", false)))
                // 删除 ` 符号
                .map(line -> line.replace("`", ""))
                .collect(Collectors.toList());

        UserVo currentUser = userService.getCurrentUser();
        // 保存到数据库中
        List<Question> questions = titleList.stream()
                .map(title -> {
                    Question question = new Question();
                    question.setTitle(title);
                    question.setUserId(currentUser.getId());
                    question.setTags("[\"AI待审核\"]");
                    question.setAnswer(aiGenerateQuestionAnswer(title));
                    return question;
                })
                .collect(Collectors.toList());
        boolean ans = this.saveBatch(questions);
        if (!ans) {
            log.error("ai 生成题目插入题库失败");
            throw new DataOperationException("ai 生成题目插入题库失败");
        }
        return true;
    }


    /**
     * ai 生成题目答案
     * @param questionTitle
     * @return
     */
    private String aiGenerateQuestionAnswer(String questionTitle) {
        // 定义系统提示词
        String systemPrompt = AiConstant.DEEPSEEK_GENERATE_ANSWER_SYSTEM_PROMPT;
        // 拼接用户 prompt
        String userPrompt = String.format("面试题：%s", questionTitle);
        return aiService.onceChat(systemPrompt, userPrompt);
    }

}




