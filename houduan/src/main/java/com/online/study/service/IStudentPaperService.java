package com.online.study.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.online.study.common.Result;
import com.online.study.controller.dto.GradeDTO;
import com.online.study.entity.StudentPaper;
import com.online.study.entity.dto.MarkPaperDTO;
import com.online.study.entity.vo.ScoreStatistics;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author
 */
public interface IStudentPaperService extends IService<StudentPaper> {

    Result saveStudentPaper(StudentPaper studentPaper);

    StudentPaper getById(Integer id);

    Result getStudentPaperForAdmin(String name,String test, Integer pageNum, Integer pageSize);

    public ScoreStatistics getScore(String test);

    List<StudentPaper> getExcelData(String test);

    Boolean mark(List<MarkPaperDTO> markDTO);

    GradeDTO getGradeByExamId(Integer examId);

    List<StudentPaper> findScore(StudentPaper studentPaper);
}
