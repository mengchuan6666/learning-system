package com.online.study.common.type;

import cn.hutool.core.util.EnumUtil;

import java.util.List;
import java.util.stream.Collectors;

public enum QuestionType {
    /**
     * 1选择，2判断，3问答
     */
    SELECT(1, "选择题", Boolean.TRUE),
    JUDGE(2, "判断题", Boolean.TRUE),
    ANSWER(3, "问答题", Boolean.FALSE),
    ;

    public Integer getType() {
        return type;
    }

    public Boolean getAutoMark() {
        return isAutoMark;
    }

    private Integer type;
    private String desc;
    private Boolean isAutoMark;

    QuestionType(Integer type, String desc, Boolean isAutoMark) {
        this.type = type;
        this.desc = desc;
        this.isAutoMark = isAutoMark;
    }

    public static Boolean isAllowAuto(Integer type) {
        return EnumUtil
                .getEnumMap(QuestionType.class)
                .values()
                .stream()
                .anyMatch(e-> e.getType().equals(type) && e.getAutoMark());
    }

    public Integer getState() {
        return type;
    }

    public String getDesc() {
        return desc;
    }

    public static String getDescByState(String state) {
        String ret = "";
        List<QuestionType> result = EnumUtil
                .getEnumMap(QuestionType.class)
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
