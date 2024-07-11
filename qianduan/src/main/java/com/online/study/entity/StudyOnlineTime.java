package com.online.study.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

/**
 * @method: $
 * @description: 学生在线学习时间
 * @date: $
 * @author: myth
 * @return $
 */
@Data
@TableName("study_online_time")
@ApiModel(value = "在线学习时间对象", description = "")
public class StudyOnlineTime implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer uid;

    private String onlineStudayTime;

    private String studayDate;

    @TableField(exist = false)
    private String time;

    @TableField(exist = false)
    private String date;

    @TableField(exist = false)
    private String uname;




}
