package com.online.study.common.state;

import cn.hutool.core.util.EnumUtil;

import java.util.List;
import java.util.stream.Collectors;

public enum StudentPaperState {
    /**
     * 未启用
     * 不在前端显示
     * 可变更绑定的试卷
     * 可变更信息内容
     * 可流转 未启用 -> 启用 未启用 -> 删除
     */
    NEW("00", "待阅卷"),
    /**
     * 启用
     * 在时间条件通过则 考生可开始考试
     * 未到开始时间 允许学生报名
     * 不可变更绑定的试卷
     * 不可变更考试的信息
     * 可流转 启用 -> 关闭 启用 -> 结束
     */
    MARKED("01", "已阅卷"),

    /**
     * 已删除（终态）
     * 删除前提：1.角色为管理员
     * 不会在学生前端显示
     * 不会在管理后台显示
     */
    DELETED("99", "已删除"),
    ;
    private final String state;
    private final String desc;
    StudentPaperState(String state, String desc) {
        this.state = state;
        this.desc = desc;
    }

    public String getState() {
        return state;
    }

    public String getDesc() {
        return desc;
    }

    public static String getDescByState(String state) {
        String ret = "";
        List<StudentPaperState> result = EnumUtil
                .getEnumMap(StudentPaperState.class)
                .values()
                .stream()
                .filter(examSignState -> examSignState.getState().equals(state))
                .collect(Collectors.toList());
        if (result.size() > 0) {
            return result.get(0).getDesc();
        }
        return ret;
    }


}
