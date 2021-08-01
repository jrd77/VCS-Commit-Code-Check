package com.github.jrd77.codecheck.data;


import java.util.Objects;


/**
 * @author WZ
 */
public class MatchRule {


    private String rule;

    private RuleTypeEnum ruleType;

    private String comment;

    public MatchRule() {
    }

    public MatchRule(String rule, RuleTypeEnum ruleType, String comment) {
        this.rule = rule;
        this.ruleType = ruleType;
        this.comment = comment;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MatchRule matchRule = (MatchRule) o;
        return Objects.equals(rule, matchRule.rule) &&
                ruleType == matchRule.ruleType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(rule, ruleType);
    }
}
