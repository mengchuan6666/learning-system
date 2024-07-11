package com.online.study.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @method: $
 * @description: 描述一下方法的作用
 * @date: $
 * @author: myth
 * @return $
 */
@Data
@TableName("study_resource_type")
public class StudyResourceType {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @Excel(name="类型名称")
    private String typeName;
    @Excel(name="老师姓名")
    private String teacherName;
    @Excel(name="创建时间")
    private Date createTime;
    @Excel(name="更新时间")
    private Date updateTime;
    @Excel(name="材料数量")
    private Integer number;
    @TableField(exist = false)
    private String createTimeStr;
    @TableField(exist = false)
    private String updateTimeStr;
}
