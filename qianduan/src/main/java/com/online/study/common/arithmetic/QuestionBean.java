package com.online.study.common.arithmetic;

import lombok.Data;

import java.sql.Date;

/**
 * 问题Bean
 */
@Data
public class QuestionBean {
    /**
     * 题目id
     */
    private Integer id;
    /**
     * 题目类型 1-单选  2-填空 3-主观
     */
    private int type;
    /**
     * 难度系数 0.3-1之间
     */
    private double difficulty;
    /**
     * 对应知识点id
     */
    private long pointId;
    /**
     * 题干
     */
    private String stem;
    /**
     * 选项A
     */
    private String a;
    /**
     * 选项B
     */
    private String b;
    /**
     * 选项C
     */
    private String c;
    /**
     * 选项D
     */
    private String d;
    /**
     * 答案
     */
    private String answer;
    /**
     * 出题人id
     */
    private long userId;
    /**
     * 试题创建时间，默认为当前时间戳
     */
    private Date createTime;

    // 以下为补充字段，为了方便界面展示
    /**
     * 出题人姓名
     */
    private String userName;
    /**
     * 知识点名称
     *
     * @return
     */
    private String knowledgeName;

    // 补充字段，便于使用遗传算法组卷
    /**
     * 问题分数，由HR出卷时指定
     */
    private double score;

}
