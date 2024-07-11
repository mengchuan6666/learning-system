package com.online.study.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.hutool.core.date.DatePattern;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author 
 
 */
@Getter
@Setter
@ApiModel(value = "Exam对象", description = "")
public class Exam implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer passScore;

    @ApiModelProperty("考试名称")
    @Excel(name="考试名称")
    private String name;

    @ApiModelProperty("考试信息")
    @Excel(name="考试信息")
    private String examDesc;

    @ApiModelProperty("考试开始时间")
    @Excel(name="考试开始时间")
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_MINUTE_PATTERN, timezone = "GMT+8")
    private LocalDateTime startTime;

    @ApiModelProperty("考试截至时间")
    @Excel(name="考试截至时间")
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_MINUTE_PATTERN, timezone = "GMT+8")
    private LocalDateTime endTime;

    @ApiModelProperty("老师")
    @Excel(name="老师")
    private String teacher;

    @TableField(exist = false)
    @Excel(name="及格分")
    private Boolean isAllowClose = Boolean.FALSE;

    @TableField(exist = false)
    @Excel(name="卷面分")
    private Integer totalScore = 0;

    @ApiModelProperty("考试状态 参考ExamState")
    @Excel(name="考试状态")
    private String state;

    private Integer createUser;

    private Date createTime;

    private Integer updateUser;

    private Date updateTime;


    @ApiModelProperty("考试状态描述")
    @Excel(name="考试状态描述")
    @TableField(exist = false)
    private String stateDesc;

    @ApiModelProperty("阅卷状态描述")
    @Excel(name="阅卷状态描述")
    @TableField(exist = false)
    private String markDesc="";

    @TableField(exist = false)
    private Boolean isAllowExam = Boolean.FALSE;

    @TableField(exist = false)
    private Boolean isAllowSign = Boolean.FALSE;

    @TableField(exist = false)
    private Boolean isAllowFinish = Boolean.FALSE;



    @TableField(exist = false)
    private Boolean isAllowSetPaper = Boolean.FALSE;
    @TableField(exist = false)
    private Boolean isAllowView = Boolean.FALSE;
}
