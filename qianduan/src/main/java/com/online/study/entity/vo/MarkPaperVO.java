package com.online.study.entity.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MarkPaperVO {
    private Integer id;
    private Integer userId;
    private Integer studentPaperId;
    private Integer questionId;
    private Integer standardScore;
    private Integer markScore;
    private Integer markUser;
    private String studentAnswer;
    private String standardAnswer;
    private String remark;
    private LocalDateTime markTime;
}
