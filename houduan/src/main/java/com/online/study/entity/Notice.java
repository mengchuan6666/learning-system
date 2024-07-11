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
@ApiModel(value = "Notice对象", description = "")
public class Notice implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("标题")
    @Excel(name="标题")
    private String name;

    @ApiModelProperty("内容")
    @Excel(name="内容")
    private String content;

    @ApiModelProperty("发布时间")
    @Excel(name="发布时间")
    private String time;

    @ApiModelProperty("发布人")
    @Excel(name="发布人")
    private String user;

    @ApiModelProperty("封面")
    @Excel(name="封面")
    private String img;


}
