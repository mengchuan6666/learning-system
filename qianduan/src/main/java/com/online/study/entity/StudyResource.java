package com.online.study.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("study_resource")
public class StudyResource {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @Excel(name="资料名称")
    private String resourceName;
    @Excel(name="资料简介")
    private String introduction;
    @Excel(name="文件id")
    private Integer sysFileId;
    @Excel(name="视频id")
    private Integer sysMp4Id;
    @Excel(name="资料状态")
    private Integer status;
    private Integer downloadCount;
    private Integer createUser;
    @Excel(name="创建时间")
    private Date createTime;
    private Integer updateUser;
    @Excel(name="更新时间")
    private Date updateTime;
    private Integer type;
    private Integer testType;
    @TableField(exist = false)
    private String picUrl;
    @TableField(exist = false)
    private String fileUrl;
    @TableField(exist = false)
    private String mp4Url;
    @TableField(exist = false)
    private String createTimeStr;
    @TableField(exist = false)
    private String userName;
}
