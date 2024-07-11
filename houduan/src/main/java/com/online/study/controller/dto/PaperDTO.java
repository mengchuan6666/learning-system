package com.online.study.controller.dto;

import lombok.Data;

@Data
public class PaperDTO {
    private Integer type1;  //选择题数量
    private double type1Score;
    private Integer type2;  // 填空
    private double type2Score;
    private Integer type3;  // 问答
    private double type3Score;
    private Integer paperId;   // 试卷id
    private Integer ctypeId; //课程
    private double difficulty;//试卷期望难度系数
}
