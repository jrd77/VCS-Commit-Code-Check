package com.github.jrd77.codecheck.data.model;

import java.util.Map;

/**
 * @author zhen.wang
 * @description TODO
 * @date 2021/8/19 14:44
 */
public class MatchRule implements ModelJsonParser {

    private String rule;
    private RuleTypeEnum ruleType;
    private String ruleTypeStr;
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
        return "MatchRule:ToString{" +
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

    public String getRuleTypeStr() {
        return ruleTypeStr;
    }

    public void setRuleTypeStr(String ruleTypeStr) {
        this.ruleTypeStr = ruleTypeStr;
    }

    @Override
    public MatchRule mapToBean(Map<String, Object> map) {

        return null;
    }
}
