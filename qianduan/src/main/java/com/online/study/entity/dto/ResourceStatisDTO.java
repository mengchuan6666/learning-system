package com.online.study.entity.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ResourceStatisDTO {
    String uploaderName;
    Integer uploadCount;
}
