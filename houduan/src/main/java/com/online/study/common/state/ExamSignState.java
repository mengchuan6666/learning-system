package com.online.study.common.state;

import cn.hutool.core.util.EnumUtil;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public enum ExamSignState {
    /**
     * 待审核
     * 学生不可参加考试
     * 可流转 待审核 -> 审核通过 待审核 -> 删除
     */
    WAIT_AUDIT("00", "待审核", "报名待审核"),
    /**
     * 审核通过（业务终态）
     * 学生允许进行考试
     * 可流转 AUDITED_PASSED -> DELETED
     */
    AUDITED_PASSED("01", "审核通过", "报名成功"),
    /**
     * 审核拒绝（业务终态）
     * 学生不允许进行考试，可以查看拒绝理由
     * 可流转 AUDITED_REFUSED -> DELETED
     */
    AUDITED_REFUSED("02", "审核拒绝", "报名拒绝"),
    /**
     * 已删除（终态）
     * 该申请不再显示在前端和后端
     */
    DELETED("99", "已删除", "报名拒绝"),
    ;
    private String state;
    private String desc;

    private String signDesc;

    ExamSignState(String state, String desc, String signDesc) {
        this.state = state;
        this.desc = desc;
        this.signDesc = signDesc;
    }

    public String getState() {
        return state;
    }

    public String getDesc() {
        return desc;
    }

    public static String getDescByState(String state) {
        String ret = "";
        List<ExamSignState> result = EnumUtil
                .getEnumMap(ExamSignState.class)
                .values()
                .stream()
                .filter(examSignState -> examSignState.getState().equals(state))
                .collect(Collectors.toList());
        if (result.size() > 0) {
            return result.get(0).getDesc();
        }
        return ret;
    }


    public static String getSignDescByState(String state) {
        String ret = "";
        List<ExamSignState> result = EnumUtil
                .getEnumMap(ExamSignState.class)
                .values()
                .stream()
                .filter(examSignState -> examSignState.getState().equals(state))
                .collect(Collectors.toList());
        if (result.size() > 0) {
            return result.get(0).getSignDesc();
        }
        return ret;
    }

    public String getSignDesc() {
        return signDesc;
    }
}
