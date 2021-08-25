package com.github.jrd77.codecheck.data.model;

/**
 * @author zhen.wang
 * @description TODO
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

    @Override
    public String toString() {
        return "CodeMatchModel{" +
                "rule='" + rule + '\'' +
                ", ruleType=" + ruleType +
                ", comment='" + comment + '\'' +
                '}';
    }


}
