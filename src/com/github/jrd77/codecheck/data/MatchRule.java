package com.github.jrd77.codecheck.data;

/**
 * @author zhen.wang
 * @description TODO
 * @date 2021/8/19 14:44
 */
public class MatchRule {

    private String rule;
    private RuleTypeEnum ruleType;
    private String comment;


    public MatchRule(String rule, RuleTypeEnum ruleType, String comment) {
        this.rule = rule;
        this.ruleType = ruleType;
        this.comment = comment;
    }

    public MatchRule() {
    }

    @Override
    public String toString() {
        return "MatchRule{" +
                "rule='" + rule + '\'' +
                ", ruleType='" + ruleType + '\'' +
                ", comment='" + comment + '\'' +
                '}';
    }

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
}
