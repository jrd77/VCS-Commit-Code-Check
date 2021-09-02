package com.github.jrd77.codecheck.data.model;

import java.util.Objects;

/**
 * @author zhen.wang
 * @description 代码匹配
 * @date 2021/8/25 10:33
 */
public class CodeMatchModel {

    private String rule;
    private RuleTypeEnum ruleType;
    private String comment;

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public RuleTypeEnum getRuleType() {
        return ruleType;
    }

    public void setRuleType(RuleTypeEnum ruleType) {
        this.ruleType = ruleType;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public CodeMatchModel(String rule, RuleTypeEnum ruleType) {
        this.rule = rule;
        this.ruleType = ruleType;
    }

    public CodeMatchModel() {
    }

    @Override
    public String toString() {
        return "CodeMatchModel{" +
                "rule='" + rule + '\'' +
                ", ruleType=" + ruleType +
                ", comment='" + comment + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CodeMatchModel that = (CodeMatchModel) o;
        return rule.equals(that.rule) && ruleType == that.ruleType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(rule, ruleType);
    }
}
