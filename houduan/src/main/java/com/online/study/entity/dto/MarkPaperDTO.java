package com.online.study.entity.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MarkPaperDTO {
    private Integer paperDetailId;
    private Double markScore;
    private String remark;
    private LocalDateTime markTime;
    private Integer markUser;
}
