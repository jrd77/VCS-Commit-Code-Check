package com.github.jrd77.codecheck.data.model;

/**
 * @Classname RuleTypeEnum
 * @Description TODO
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
}
