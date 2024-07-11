package com.online.study.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@TableName("student_paper_detail")
@ApiModel(value = "StudentPaper对象", description = "")
@Builder
public class StudentPaperDetail implements Serializable {
    private static final long serialVersionUID = -764773186023248477L;
    @TableId(value = "id", type = IdType.AUTO)
    public Integer id;
    public Integer userId;
    public Integer studentPaperId;
    public Integer questionId;
    public Double score;
    public Double markScore;
    public Integer markUser;
    public String answer;
    public String remark;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public LocalDateTime markTime;
}
