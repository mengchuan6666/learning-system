package com.online.study.service.impl;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.online.study.common.Result;
import com.online.study.common.state.ExamState;
import com.online.study.common.state.StudentPaperState;
import com.online.study.entity.Exam;
import com.online.study.entity.ExamPaper;
import com.online.study.entity.StudentPaper;
import com.online.study.entity.User;
import com.online.study.entity.dto.ExamStatisDTO;
import com.online.study.mapper.ExamMapper;
import com.online.study.service.IExamPaperService;
import com.online.study.service.IExamService;
import com.online.study.service.IPaperService;
import com.online.study.service.ISignService;
import com.online.study.service.IStudentPaperService;
import com.online.study.utils.TokenUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.online.study.common.Constants.CODE_600;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author
 */
@Service
public class ExamServiceImpl extends ServiceImpl<ExamMapper, Exam> implements IExamService {
    @Autowired
    private IExamPaperService examPaperService;

    @Autowired
    private ISignService signService;

    @Autowired
    private IPaperService paperService;

    @Autowired
    private IStudentPaperService studentPaperService;

    @Override
    public Result saveExam(Exam exam) {
        if (exam.getId() != null && !checkForUpdateExam(exam)) {
            return Result.error(CODE_600, "更新规则错误");
        }

        return Result.getResult(super.saveOrUpdate(exam));
    }

    @Override
    public Exam getById(Integer id) {
        Exam ret = super.getById(id);
        if(ret !=null) {
            ret.setTotalScore(sumScoreByExamId(id));
        }

        return ret;
    }

    @Override
    public Result finishExamById(Integer id) {
        Exam exam = this.getById(id);
        if (!ExamState.ENABLE.getState().equals(exam.getState())) {
            return Result.error(CODE_600, "状态不正确");
        }

        if (!LocalDateTimeUtil.now().isAfter(exam.getEndTime())) {
            return Result.error(CODE_600, "考试未到结束日");
        }

        LambdaUpdateChainWrapper<Exam> updateWrapper = lambdaUpdate().eq(Exam::getId, id).set(Exam::getState, ExamState.FINISH.getState());
        return Result.getResult(updateWrapper.update());
    }

    @Override
    public Result enableSwitchById(Integer id) {
        Exam exam = this.getById(id);
        String examStateUpdate = exam.getState();
        if (!ExamState.ENABLE.getState().equals(exam.getState())
                && !ExamState.DISABLE.getState().equals(exam.getState())
                && !ExamState.NEW.getState().equals(exam.getState())) {
            return Result.error(CODE_600, "状态不正确");
        }

        if (ExamState.NEW.getState().equals(exam.getState())
                || ExamState.DISABLE.getState().equals(exam.getState())) {
            examStateUpdate = ExamState.ENABLE.getState();
        } else {
            examStateUpdate = ExamState.DISABLE.getState();
        }

        LambdaUpdateChainWrapper<Exam> updateWrapper = lambdaUpdate()
                .eq(Exam::getId, id)
                .set(Exam::getState, examStateUpdate);

        return Result.getResult(updateWrapper.update());
    }

    @Override
    public Result deleteExamByIds(List<Integer> ids) {
        LambdaUpdateChainWrapper<Exam> updateWrapper = lambdaUpdate()
                .in(Exam::getId, ids)
                .set(Exam::getState, ExamState.DELETED.getState());

        examPaperService.remove(new UpdateWrapper<ExamPaper>().in("exam_id",ids));
        return Result.getResult(updateWrapper.remove());
    }

    private boolean checkForUpdateExam(Exam updateExam) {
        Exam examInDB = this.getById(updateExam.getId());
        if (ExamState.NEW.getState().equals(examInDB.getState())) {
            return true;
        } else if (ExamState.ENABLE.getState().equals(examInDB.getState())
                || ExamState.FINISH.getState().equals(examInDB.getState())
                || ExamState.DELETED.getState().equals(examInDB.getState())) {
            return false;
        } else if (ExamState.DISABLE.getState().equals(examInDB.getState())) {
            return updateExam.getStartTime() == null && updateExam.getEndTime() == null;
        } else {
            return false;
        }
    }

    @Override
    public Object getAdminPage(String name, Integer pageNum, Integer pageSize) {
        QueryWrapper<Exam> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        queryWrapper.ne("state", ExamState.DELETED.getState());
        if (!"".equals(name)) {
            queryWrapper.like("name", name);
        }

        Page<Exam> page = this.page(new Page<>(pageNum, pageSize), queryWrapper);
        for (Exam exam : page.getRecords()) {
            this.setStateForAdmin(exam);
            exam.setTotalScore(this.sumScoreByExamId(exam.getId()));
        }

        return page;
    }

    @Override
    public IPage<Exam> getExamFrontPage(String name, Integer pageNum, Integer pageSize) {
        QueryWrapper<Exam> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        queryWrapper.in("state", ExamState.ENABLE.getState(), ExamState.FINISH.getState());
        if (!StringUtils.isEmpty(name)) {
            queryWrapper.like("em.name", name);
        }

        IPage<Exam> page=baseMapper.getExamFront(new Page<>(pageNum, pageSize),queryWrapper);
        //Page<Exam> page = page(new Page<>(pageNum, pageSize), queryWrapper);
        User currentUser = TokenUtils.getCurrentUser();
        if (currentUser.getRole().equals("ROLE_STUDENT")) {
            queryWrapper.eq("user", currentUser.getUsername());
        }

        for (Exam exam : page.getRecords()) {
            exam.setIsAllowSign(Boolean.TRUE);
            exam.setStateDesc(ExamState.getDescByState(exam.getState()));
            // 到期则不允许报名
            if (LocalDateTime.now().isAfter(exam.getStartTime())) {
                exam.setIsAllowSign(Boolean.FALSE);
            }

            this.setStateForFront(exam);
            exam.setTotalScore(this.sumScoreByExamId(exam.getId()));
        }

        return page;
    }

    @Override
    public IPage<Exam> getExamFrontPageTest(Integer id, Integer pageNum, Integer pageSize) {
        QueryWrapper<Exam> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        queryWrapper.in("state", ExamState.ENABLE.getState(), ExamState.FINISH.getState());
        if (null!=id) {
            queryWrapper.eq("id", id);
        }
        Page<Exam> page = page(new Page<>(pageNum, pageSize), queryWrapper);
       // IPage<Exam> page = baseMapper.getHomeWorkTest(new Page<>(pageNum, pageSize), queryWrapper,id);
        User currentUser = TokenUtils.getCurrentUser();
        if (currentUser.getRole().equals("ROLE_STUDENT")) {
            queryWrapper.eq("user", currentUser.getUsername());
        }

        for (Exam exam : page.getRecords()) {
            exam.setIsAllowSign(Boolean.TRUE);
            exam.setStateDesc(ExamState.getDescByState(exam.getState()));
            // 到期则不允许报名
            if (LocalDateTime.now().isAfter(exam.getStartTime())) {
                exam.setIsAllowSign(Boolean.FALSE);
            }

            this.setStateForFront(exam);
            exam.setTotalScore(this.sumScoreByExamId(exam.getId()));
        }

        return page;
    }

    private void setStateForFront(Exam exam) {
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(exam.getStartTime())) {
            exam.setStateDesc("未开始");
        } else if (now.isAfter(exam.getEndTime())) {
            exam.setStateDesc("已结束");
        } else {
            // 考试的开始时间在当前时间之后，并且开始的结束时间在当前的时间之前
            exam.setStateDesc("进行中");
            User user = TokenUtils.getCurrentUser();
            if (user != null) {
                StudentPaper studentPaper = studentPaperService
                        .lambdaQuery()
                        .eq(StudentPaper::getExamId, exam.getId())
                        .eq(StudentPaper::getUserId, user.getId())
                        .ne(StudentPaper::getState, StudentPaperState.DELETED)
                        .orderByDesc(StudentPaper::getId)
                        .one();
                if (studentPaper!=null) {
                    exam.setIsAllowExam(Boolean.FALSE);
                    exam.setIsAllowView(StudentPaperState.MARKED.getState().equals(studentPaper.getState()));
                    exam.setMarkDesc(StudentPaperState.getDescByState(studentPaper.getState()));
                } else {
                    exam.setIsAllowExam(Boolean.TRUE);
                }
            } else {
                exam.setIsAllowExam(Boolean.TRUE);
            }
        }
    }

    private void setStateForAdmin(Exam exam) {
        if (ExamState.NEW.getState().equals(exam.getState())) {
            exam.setStateDesc("未启用");
            exam.setIsAllowSetPaper(Boolean.TRUE);
        } else if (ExamState.DISABLE.getState().equals(exam.getState())) {
            exam.setStateDesc("关闭");
        } else if (ExamState.FINISH.getState().equals(exam.getState())) {
            exam.setStateDesc("已结束");
        } else if (ExamState.ENABLE.getState().equals(exam.getState())) {
            exam.setStateDesc("已启用");
            // todo 评分完毕才能结束
            if (LocalDateTimeUtil.now().isAfter(exam.getEndTime())) {
                exam.setIsAllowFinish(Boolean.TRUE);
            }

            if (LocalDateTimeUtil.now().isBefore(exam.getStartTime())) {
                exam.setIsAllowClose(Boolean.TRUE);
            }
        } else {
            exam.setStateDesc("");
        }
    }

    @Override
    public Integer sumScoreByExamId(Integer id) {
        return ObjectUtil.defaultIfNull(this.baseMapper.sumScoreByExamId(id), 0);
    }

    @Override
    public List<ExamStatisDTO> getStatis(LocalDateTime stTime, LocalDateTime edTime) {
        return baseMapper.selectStatisDTO(stTime,edTime);
    }

    @Override
    public List<Exam> getTestExamPaper(Integer ctypeId) {

        return baseMapper.getTestExamPaper(ctypeId);
    }
}
