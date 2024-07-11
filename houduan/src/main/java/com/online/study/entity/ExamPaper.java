package com.online.study.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author 
 
 */
@Getter
@Setter
@TableName("exam_paper")
@ApiModel(value = "ExamPaper对象", description = "")
public class ExamPaper implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("考试id")
    private Integer examId;

    @ApiModelProperty("试卷id")
    private Integer paperId;


}
