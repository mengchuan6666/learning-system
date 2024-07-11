package com.online.study.entity.dto;

import com.online.study.entity.Question;
import lombok.Data;

@Data
public class AnswerDTO extends Question {
    //questionId
    private Integer id;
    private Integer paperDetailId;
    private String studentAnswer;
    private String standardAnswer;
    private String questionDetail;
    private Integer questionType;
    private String questionName;
    private String remark;
    private Double score = 0.0;
    private Integer markScore = 0;
    private Double autoMarkScore = 0.0;
}
