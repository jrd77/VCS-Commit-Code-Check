package com.github.jrd77.codecheck.util;

import com.github.jrd77.codecheck.data.model.CodeMatchResult;
import com.github.jrd77.codecheck.data.model.FileMatchModel;
import com.github.jrd77.codecheck.data.model.MatchRule;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * @author zhen.wang
 * @description 转换工具
 * @date 2021/8/4 15:20
 */
public class ConvertUtil {

    public static Vector<Vector<String>> convertRule(List<String> ruleList) {

        List<MatchRule> matchRules = convertMatchRuleList(ruleList);
        Vector<Vector<String>> data = new Vector<Vector<String>>();
        int index=0;
        for (MatchRule matchRule : matchRules) {
            final Vector<String> strings = covertMatchRuleToVector(matchRule,++index);
            data.add(strings);
        }
        return data;
    }

    private static Vector<String> covertMatchRuleToVector(MatchRule matchRule, int index) {

        Vector<String> vector = new Vector<String>();
        vector.add(String.valueOf(index));
        vector.add(matchRule.getRule());
        vector.add(matchRule.getComment());
        vector.add(matchRule.getRuleType()==null?null:matchRule.getRuleType().name());
        return vector;
    }

    public static List<MatchRule> convertMatchRuleList(List<String> strs) {

        MatchRule matchRule;
        List<MatchRule> matchRuleList = new ArrayList<>();
        for (String str : strs) {
            matchRule = convertMatchRule(str);
            matchRuleList.add(matchRule);
        }
        return matchRuleList;
    }

    private static MatchRule convertMatchRule(String str) {
        return JsonUtil.fromJson(str, MatchRule.class);
    }

    public static Vector<Vector<String>> convertFileMatchList(List<String> fileMatchList) {

        Vector<Vector<String>> data = new Vector<Vector<String>>();
        int index = 0;
        for (String s : fileMatchList) {
            FileMatchModel fileMatchModel = JsonUtil.fromJson(s, FileMatchModel.class);
            Vector<String> vector = new Vector<>();
            vector.add(String.valueOf(++index));
            vector.add(fileMatchModel.getRule());
            vector.add(fileMatchModel.getComment());
            data.add(vector);
        }
        return data;
    }

    public static Vector<Vector<String>> convertGitDiffList(List<CodeMatchResult> cmdList) {

        Vector<Vector<String>> data = new Vector<Vector<String>>();
        int index = 0;
        for (CodeMatchResult gitDiffCmd : cmdList) {
            Vector<String> vector = new Vector<>();
            vector.add(String.valueOf(++index));
            vector.add(gitDiffCmd.getErrorLineStr());
            vector.add(String.valueOf(gitDiffCmd.getErrorLineNumber()));
            vector.add(gitDiffCmd.getErrorMatch());
            vector.add(gitDiffCmd.getFilePath());
            data.add(vector);
        }
//        TextAttributesKey.createTextAttributesKey("").


        return data;
    }
}
