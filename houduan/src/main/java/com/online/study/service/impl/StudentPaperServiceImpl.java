package com.online.study.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.online.study.common.Constants;
import com.online.study.common.Result;
import com.online.study.common.state.StudentPaperState;
import com.online.study.common.type.QuestionType;
import com.online.study.controller.dto.GradeDTO;
import com.online.study.entity.ExamPaper;
import com.online.study.entity.Question;
import com.online.study.entity.StudentPaper;
import com.online.study.entity.StudentPaperDetail;
import com.online.study.entity.User;
import com.online.study.entity.dto.AnswerDTO;
import com.online.study.entity.dto.MarkPaperDTO;
import com.online.study.entity.vo.ScoreStatistics;
import com.online.study.exception.ServiceException;
import com.online.study.mapper.StudentPaperMapper;
import com.online.study.service.IExamPaperService;
import com.online.study.service.IQuestionService;
import com.online.study.service.IStudentPaperDetailService;
import com.online.study.service.IStudentPaperService;
import com.online.study.utils.TokenUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author
 */
@Service
public class StudentPaperServiceImpl extends ServiceImpl<StudentPaperMapper, StudentPaper> implements IStudentPaperService {
    @Autowired
    private IStudentPaperDetailService paperDetailService;
    @Autowired
    private IQuestionService questionService;
    @Autowired
    private IExamPaperService examPaperService;

    @Autowired
    private StudentPaperMapper studentPaperMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result saveStudentPaper(StudentPaper studentPaper) {
        if (studentPaper.getId() == null) {
            List<StudentPaper> list = list(new QueryWrapper<StudentPaper>().eq("exam_id", studentPaper.getExamId())
                    .eq("user_id", Objects.requireNonNull(TokenUtils.getCurrentUser()).getId()));
            if (CollUtil.isNotEmpty(list)) {
                throw new ServiceException("-1", "您已提交考卷！");
            }

            ExamPaper examPaper = examPaperService
                    .lambdaQuery()
                    .eq(ExamPaper::getExamId, studentPaper.getExamId())
                    .orderByDesc(ExamPaper::getId).one();
            if(examPaper == null) {
                throw new ServiceException("-1", "考卷信息有误！");
            }

            studentPaper.setTime(LocalDateTime.now());
            studentPaper.setUserId(TokenUtils.getCurrentUser().getId());
            studentPaper.setUser(TokenUtils.getCurrentUser().getUsername());
        }

        if(CollectionUtil.isEmpty(studentPaper.getAnswerList())) {
            throw new ServiceException("-1", "答案不可为空！");
        }
        saveOrUpdate(studentPaper);
        // 自动匹配答案打分
        this.autoMark(studentPaper.getAnswerList());
        // 批量插入作答明细
        return Result.getResult(
                this.paperDetailService
                        .saveBatch(studentPaper
                        .getAnswerList()
                        .stream()
                        .map((e) -> assemblePaperDetail(e,studentPaper))
                        .collect(Collectors.toList())));
    }

    private void autoMark(List<AnswerDTO> answerList) {
        answerList.forEach(e -> {
            if (e.getId() != null) {
                Question question = questionService.getById(e.getId());
                if (QuestionType.isAllowAuto(question.getType())
                        && question.getAnswer().equals(e.getStudentAnswer())) {
                    e.setScore(question.getScore());
                    e.setAutoMarkScore(question.getScore());
                }
            }
        });
    }

    private StudentPaperDetail assemblePaperDetail(AnswerDTO answerDTO, StudentPaper studentPaper) {
        if (StringUtils.isEmpty(answerDTO.getStudentAnswer()) || answerDTO.getId() == null) {
            throw new ServiceException("-1", "作答未完成！");
        } else {
            return StudentPaperDetail
                    .builder()
                    .userId(studentPaper.getUserId())
                    .studentPaperId(studentPaper.getId())
                    .questionId(answerDTO.getId())
                    .score(answerDTO.getScore())
                    .markScore(answerDTO.getAutoMarkScore())
                    .markUser(Constants.ADMIN_ID)
                    .answer(answerDTO.getStudentAnswer())
                    .build();
        }
    }

    @Override
    public Result getStudentPaperForAdmin(String name,String test, Integer pageNum, Integer pageSize) {
        User currentUser = TokenUtils.getCurrentUser();
        StudentPaper studentPaper=new StudentPaper();
        QueryWrapper<StudentPaper> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        if (!"".equals(name)) {
            queryWrapper.like("name", name);
            studentPaper.setName(name);
        }
        if (currentUser.getRole().equals("ROLE_STUDENT")) {
            queryWrapper.eq("user", currentUser.getUsername());
            studentPaper.setUserId(currentUser.getId());
        }
        if(null!=test && !test.equals("")) {
            studentPaper.setTest(test);
        }
        IPage<StudentPaper> page=baseMapper.selectStudentPaper(new Page<>(pageNum, pageSize), queryWrapper,studentPaper);
        //Page<StudentPaper> page = page(new Page<>(pageNum, pageSize), queryWrapper);
        page.getRecords().forEach(e->{
            Integer sumScore = paperDetailService.sumScoreByStudentPaperId(e.getId());
            e.setScore(sumScore);
        });

        return Result.success(page);
    }

    public List<StudentPaper> getExcelData(String test){
        List<StudentPaper> list=studentPaperMapper.getExcelData(test);

        list.forEach(e->{
            Integer sumScore = paperDetailService.sumScoreByStudentPaperId(e.getId());
            e.setScore(sumScore);

        });

        return list;
    }

   public ScoreStatistics getScore(String test){
       ScoreStatistics scoreStatistics = new ScoreStatistics();
       List<StudentPaper> list=studentPaperMapper.getScore(test);
       list.forEach(e->{
           Integer sumScore = paperDetailService.sumScoreByStudentPaperId(e.getId());
           e.setScore(sumScore);

       });
       //最大值
       scoreStatistics.setMaxScore(list.stream().mapToDouble(StudentPaper::getScore).max().getAsDouble());
       //最小值
       scoreStatistics.setMinScore(list.stream().mapToDouble(StudentPaper::getScore).min().getAsDouble());
       //平均数
       scoreStatistics.setAvScoreg(list.stream().mapToDouble(StudentPaper::getScore).average().getAsDouble());
       //中位数
       List<Integer> collect = list.stream().map(StudentPaper::getScore).collect(Collectors.toList());
       if(list.size()%2==0){
           Integer i=(collect.get(collect.size()/2-1)+collect.get(collect.size()/2))/2;
           scoreStatistics.setMidScoreg(i);
       }else{
           Integer i=collect.get(collect.size()/2);
           scoreStatistics.setMidScoreg(i);
       }

       return scoreStatistics;
   }

    @Override
    public StudentPaper getById(Integer id) {
        StudentPaper studentPaper = super.getById(id);
        studentPaper.setAnswerList(paperDetailService.getStudentAnswerByPaperId(id));
        return studentPaper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean mark(List<MarkPaperDTO> markDTO) {
        StudentPaperDetail paperDetail = paperDetailService.getById(markDTO.get(0).getPaperDetailId());
        return this.lambdaUpdate()
                .set(StudentPaper::getMarkUser, TokenUtils.getCurrentUser().getId())
                .set(StudentPaper::getMarkTime, LocalDateTime.now())
                .set(StudentPaper::getState, StudentPaperState.MARKED.getState())
                .eq(StudentPaper::getId, paperDetail.getStudentPaperId())
                .update()
                && paperDetailService.updateBatchById(
                        markDTO
                                .stream()
                                .map(this::assembleEntityForMark)
                                .collect(Collectors.toList()));
    }

    private StudentPaperDetail assembleEntityForMark(MarkPaperDTO dto) {
        return StudentPaperDetail.builder()
                .id(dto.getPaperDetailId())
                .markUser(TokenUtils.getCurrentUser().getId())
                .markTime(LocalDateTime.now())
                .remark(dto.getRemark())
                .markScore(dto.getMarkScore())
                .build();
    }

    @Override
    public GradeDTO getGradeByExamId(Integer examId) {
        Integer userId = TokenUtils.getCurrentUser().getId();
        StudentPaper studentPaper = this
                .lambdaQuery()
                .orderByDesc(StudentPaper::getId)
                .eq(StudentPaper::getState, StudentPaperState.MARKED.getState())
                .eq(StudentPaper::getUserId, userId)
                .eq(StudentPaper::getExamId, examId)
                .one();
        List<AnswerDTO> answerDTOLis = paperDetailService.getStudentAnswerByPaperId(studentPaper.getId());
        return GradeDTO
                .builder()
                .answerList(answerDTOLis)
                .markScore(answerDTOLis
                        .stream()
                        .mapToInt(AnswerDTO::getMarkScore)
                        .sum())
                .build();
    }

    @Override
    public List<StudentPaper> findScore(StudentPaper studentPaper) {
        List<StudentPaper>list=null;
        if(null!=studentPaper.getUserId()){
            list=studentPaperMapper.findscore(studentPaper);
        }else {
            list = studentPaperMapper.findscore2(studentPaper);
        }
        return list;
    }
}
