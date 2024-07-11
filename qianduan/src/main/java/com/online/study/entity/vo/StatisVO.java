package com.online.study.entity.vo;

import com.online.study.entity.dto.ExamStatisDTO;
import com.online.study.entity.dto.ResourceStatisDTO;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class StatisVO {
    private Long userCount;
    private Long teacherCount;
    private Long examCountMonthly;
    private Long newsCountMonthly;
    private List<ExamStatisDTO> examStatisMonthly;
    private List<ResourceStatisDTO> resStatisMonthly;
}
