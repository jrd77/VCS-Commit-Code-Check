package com.github.jrd77.codecheck.util;

import com.github.jrd77.codecheck.data.model.MatchRule;
import com.github.jrd77.codecheck.data.model.RuleTypeEnum;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.LocalChangeList;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author zhen.wang
 * @description TODO
 * @date 2021/8/24 13:35
 */
public class ContainUtil {


    public static boolean contains(String regexp, String str) {
        return Pattern.compile(regexp).matcher(str).find();
    }

    public static boolean contains(List<String> regexps, String str) {
        return regexps.stream().anyMatch(x -> contains(x, str));
    }

    /**
     * find all code match changes
     *
     * @param matchRuleList
     * @param changeList
     * @return
     * @throws VcsException
     */
    public static List<Change> codeMatch(List<MatchRule> matchRuleList, LocalChangeList changeList) throws VcsException {

        List<Change> matchChanges = new ArrayList<>();
        if (CollUtil.isEmpty(matchRuleList) || changeList == null || CollUtil.isEmpty(changeList.getChanges())) {
            return matchChanges;
        }
        for (Change change : changeList.getChanges()) {
            if (change == null || change.getVirtualFile() == null || change.getAfterRevision() == null || StrUtil.isBlank(change.getAfterRevision().getContent())) {
                continue;
            }
            String str = change.getAfterRevision().getContent();
            for (MatchRule matchRule : matchRuleList) {
                //regexp match
                if (matchRule.getRuleType() == RuleTypeEnum.REGEXP) {
                    boolean contains = contains(matchRule.getRule(), str);
                    if (contains) {
                        matchChanges.add(change);
                    }
                } else if (matchRule.getRuleType() == RuleTypeEnum.STR_MATCH) {
                    //str match
                    boolean contains = str.contains(matchRule.getRule());
                    if (contains) {
                        matchChanges.add(change);
                    }
                }
            }
        }
        return matchChanges;
    }

    /**
     * 是否命中规则
     *
     * @param ruleList
     * @param changeContent
     * @return
     */
    public static List<MatchRule> matchError(List<MatchRule> ruleList, String changeContent) {

        List<MatchRule> typeList = new ArrayList<>();
        for (MatchRule matchRule : ruleList) {
            RuleTypeEnum ruleTypeEnum = null;
            String rule = null;
            if (matchRule.getRuleType().equals(RuleTypeEnum.REGEXP) &&
                    contains(matchRule.getRule(), changeContent)) {
                ruleTypeEnum = matchRule.getRuleType();
                rule = matchRule.getRule();
            } else if (matchRule.getRuleType().equals(RuleTypeEnum.STR_MATCH) &&
                    changeContent.contains(matchRule.getRule())) {
                ruleTypeEnum = matchRule.getRuleType();
                rule = matchRule.getRule();
            }
            if (ruleTypeEnum != null) {
                MatchRule matchRuleResult = new MatchRule();
                matchRuleResult.setRule(rule);
                matchRuleResult.setRuleType(ruleTypeEnum);
                typeList.add(matchRuleResult);
            }
        }
        return typeList;
    }
}
