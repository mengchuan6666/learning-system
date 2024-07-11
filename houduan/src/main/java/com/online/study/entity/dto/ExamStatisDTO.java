package com.online.study.entity.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class ExamStatisDTO {
    String examName;
    BigDecimal maxScore;
    BigDecimal avgScore;
    BigDecimal varianceScore;
}
