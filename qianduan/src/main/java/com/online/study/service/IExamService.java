package com.online.study.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.online.study.common.Result;
import com.online.study.entity.Exam;
import com.online.study.entity.dto.ExamStatisDTO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 
 
 */
public interface IExamService extends IService<Exam> {

    Result saveExam(Exam exam);

    Result deleteExamByIds(List<Integer> ids);

    Result enableSwitchById(Integer id);

    Exam getById(Integer id);

    Result finishExamById(Integer id);

    Object getAdminPage(String name, Integer pageNum, Integer pageSize);

    IPage<Exam> getExamFrontPage(String name, Integer pageNum, Integer pageSize);

    Integer sumScoreByExamId(Integer id);

    List<ExamStatisDTO> getStatis(LocalDateTime stTime, LocalDateTime edTime);

    List<Exam> getTestExamPaper(Integer ctypeId);

    IPage<Exam> getExamFrontPageTest(Integer testType, Integer pageNum, Integer pageSize);
}
