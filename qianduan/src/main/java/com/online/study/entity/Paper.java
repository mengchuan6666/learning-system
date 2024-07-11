package com.online.study.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author 
 
 */
@Getter
@Setter
@ApiModel(value = "Paper对象", description = "")
public class Paper implements Serializable {

    private static final long serialVersionUID = -3391295767966906939L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("名称")
    @Excel(name="名称")
    private String name;

    @ApiModelProperty("总分")
    @Excel(name="总分")
    private Integer score;

    @ApiModelProperty("时长")
    @Excel(name="时长")
    private Integer duration;

    //课程类型
    @Excel(name="课程")
    private Integer ctypeId;
    //试卷类型0-考试，1-单元测试
    @Excel(name="类型")
    private Integer test;
}
