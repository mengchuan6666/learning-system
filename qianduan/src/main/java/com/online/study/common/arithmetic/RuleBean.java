package com.online.study.common.arithmetic;

import lombok.Data;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * 组卷规则Bean
 */
@Data
public class RuleBean {
    /**
     * 规则id
     */
    private long id;
    /**
     * 规则对应的考试id
     */
    private long examId;
    /**
     * 试卷总分
     */
    private double totalMark;
    /**
     * 试卷期望难度系数
     */
    private double difficulty;
    /**
     * 单选题数量
     */
    private int singleNum;
    /**
     * 单选题单个分值
     */
    private double singleScore;
    /**
     * 填空题数量
     */
    private int completeNum;
    /**
     * 填空题单个分值
     */
    private double completeScore;
    /**
     * 主观题数量
     */
    private int subjectiveNum;
    /**
     * 主观题单个分值
     */
    private double subjectiveScore;
    /**
     * 试卷包含的知识点id
     */
    private List<String> pointIds;

    private Integer ctypeId;
    /**
     * 规则创建时间
     */
    private Date createTime;



    public void setPointIds(String pointIds) {
        // 是否是表单传过来的数据
        if (pointIds.endsWith("#")) {
            pointIds = pointIds.substring(0, pointIds.lastIndexOf("#"));
        }
        // 使用HashSet去重
        this.pointIds = new ArrayList<>(new HashSet<>(Arrays.asList(pointIds.split("#"))));
    }


}
