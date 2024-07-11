package com.online.study.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("comment")
public class Comment {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @Excel(name="用户姓名")
    private String username;

    @Excel(name="内容")
    private String content;

    private Integer userId;
    private Integer foreignId;
    private Integer pid;
    private String target;

    @TableField(exist = false)
    private Integer replyNum = 0;

    @Excel(name="时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createtime;

    @TableField(exist = false)
    private List<Comment> replies;

    @TableField(exist = false)
    private String img;
}
