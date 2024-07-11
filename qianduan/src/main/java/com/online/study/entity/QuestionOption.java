package com.online.study.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Data
@TableName("question_option")
@ApiModel(value = "问题选项", description = "")
public class QuestionOption implements Serializable {
    private static final long serialVersionUID = -1566672501716909095L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("选项序 如 a b c d")
    private String optionOrder;

    @ApiModelProperty("内容")
    private String content;

    @ApiModelProperty("问题id")
    private Integer questionId;
}
