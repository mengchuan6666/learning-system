package com.online.study.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.online.study.entity.Question;
import com.online.study.entity.StudentPaperDetail;
import com.online.study.entity.dto.AnswerDTO;

import java.util.List;

public interface IStudentPaperDetailService extends IService<StudentPaperDetail> {
    Integer sumScoreByStudentPaperId(Integer id);

    List<AnswerDTO> getStudentAnswerByPaperId(Integer id);
}
