package com.online.study.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author 
 
 */@Data
@ApiModel(value = "Question对象", description = "")
public class Question implements Serializable {

    private static final long serialVersionUID = 6640674535067783381L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("名称")
    @Excel(name="名称")
    private String name;

    @ApiModelProperty("类型：1选择，2判断，3问答")
    @Excel(name="类型")
    private Integer type;

    //课程类型
    @Excel(name="课程ID")
    private Integer ctypeId;

    @Excel(name="课程名称")
    @TableField(exist = false)
    private String ctypeName;

    @ApiModelProperty("a选项")
    @Excel(name="a选项")
    private String a;

    @ApiModelProperty("b选项")
    @Excel(name="b选项")
    private String b;

    @ApiModelProperty("c选项")
    @Excel(name="c选项")
    private String c;

    @ApiModelProperty("d选项")
    @Excel(name="d选项")
    private String d;

    @ApiModelProperty("分数")
    @Excel(name="分数")
    private Double score;

    @ApiModelProperty("答案")
    @Excel(name="答案")
    private String answer;

    @ApiModelProperty("解析")
    @Excel(name="解析")
    private String detail;

    @ApiModelProperty("难度系数 0.3-1之间")
    @Excel(name="难度系数")
    private Double difficulty;

    @ApiModelProperty("出题人id")
    @Excel(name="出题人id")
    private Integer userId;

    @ApiModelProperty("出题时间")
    @Excel(name="出题时间")
    private String time;

    @TableField(exist = false)
    private Integer questionId;

    /**知识点ID*/
    private long pointId;
}
