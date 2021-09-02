package com.github.jrd77.codecheck.data.model;

import java.util.Arrays;

/**
 * @Classname RuleTypeEnum
 * @Description 匹配类型
 * @Date 2021/8/19 14:46
 * @Author W.Z
 */
public enum RuleTypeEnum {

    REGEXP(1), STR_MATCH(2);

    private final long type;

    RuleTypeEnum(long type) {
        this.type = type;
    }

    public long getType() {
        return type;
    }

    public static RuleTypeEnum fromName(String name) {
        RuleTypeEnum ruleTypeEnum = Arrays.stream(RuleTypeEnum.values()).filter(x -> x.name().equals(name)).findFirst().get();
        return ruleTypeEnum;
    }
}
