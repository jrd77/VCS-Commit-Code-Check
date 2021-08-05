package com.github.jrd77.codecheck.data

import com.github.jrd77.codecheck.data.RuleTypeEnum
import java.util.*

/**
 * @author WZ
 */
class MatchRule {
    var rule: String? = null
    var ruleType: RuleTypeEnum? = null
    var comment: String? = null

    constructor() {}
    constructor(rule: String?, ruleType: RuleTypeEnum?, comment: String?) {
        this.rule = rule
        this.ruleType = ruleType
        this.comment = comment
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val matchRule = o as MatchRule
        return rule == matchRule.rule &&
                ruleType === matchRule.ruleType
    }

    override fun hashCode(): Int {
        return Objects.hash(rule, ruleType)
    }

    override fun toString(): String {
        return "MatchRule{" +
                "rule='" + rule + '\'' +
                ", ruleType=" + ruleType +
                ", comment='" + comment + '\'' +
                '}'
    }
}