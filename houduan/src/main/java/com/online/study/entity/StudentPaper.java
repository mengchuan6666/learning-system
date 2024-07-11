package com.online.study.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.online.study.entity.dto.AnswerDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 
 * </p>
 *
 * @author 

 */
@Getter
@Setter
@TableName("student_paper")
@ApiModel(value = "StudentPaper对象", description = "")
public class StudentPaper implements Serializable {

    private static final long serialVersionUID = -4229881236976122420L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("考试id")
    private Integer examId;

    @ApiModelProperty("学生id")
    private Integer userId;

    @ApiModelProperty("学生姓名")
    @Excel(name="学生姓名")
    private String user;

    @TableField(exist = false)
    @Excel(name="试卷名称")
    private String ename;

    @TableField(exist = false)
    private String name;

    @ApiModelProperty("分数")
    @TableField(exist = false)
    @Excel(name="分数")
    private Integer score;

    @TableField(exist = false)
    private Integer value;

    @ApiModelProperty("提交时间")
    @Excel(name="提交时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime time;

    @ApiModelProperty("状态 00-未打分 01-已打分 ")
    @Excel(name="状态")
    private String state;

    @ApiModelProperty("打分用户")
    @Excel(name="打分用户")
    private Integer markUser;

    @ApiModelProperty("打分时间")
    @Excel(name="打分时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime markTime;



    @ApiModelProperty("答卷内容")
    @Excel(name="答卷内容")
    @TableField(exist = false)
    private List<AnswerDTO> answerList;



    @TableField(exist = false)
    private String escore;

    @TableField(exist = false)
    private String test;
}
