package com.online.study.controller.dto;

import com.online.study.entity.Exam;
import com.online.study.entity.dto.AnswerDTO;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GradeDTO {
    private Integer markScore;
    private Exam exam;
    private List<AnswerDTO> answerList;
}
