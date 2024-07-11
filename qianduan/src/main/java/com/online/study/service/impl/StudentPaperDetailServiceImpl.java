package com.online.study.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.online.study.entity.StudentPaperDetail;
import com.online.study.entity.dto.AnswerDTO;
import com.online.study.mapper.StudentPaperDetailMapper;
import com.online.study.service.IStudentPaperDetailService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentPaperDetailServiceImpl extends ServiceImpl<StudentPaperDetailMapper, StudentPaperDetail> implements IStudentPaperDetailService {
    @Override
    public Integer sumScoreByStudentPaperId(Integer studentPaperId) {
        return this.baseMapper.sumScoreByStudentPaperId(studentPaperId);
    }


    @Override
    public List<AnswerDTO> getStudentAnswerByPaperId(Integer id) {
        return baseMapper.selectStudentAnswerByPaperId(id);
    }
}
