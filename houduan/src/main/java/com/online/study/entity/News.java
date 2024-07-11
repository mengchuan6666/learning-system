package com.online.study.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
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
@ApiModel(value = "News对象", description = "")
public class News implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @Excel(name="标题")
    private String name;

    @Excel(name="内容")
    private String content;

    @Excel(name="发布时间")
    private String time;

    @Excel(name="发布人")
    private String user;

    @Excel(name="图片")
    private String img;
}
