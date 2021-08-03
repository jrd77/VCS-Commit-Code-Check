package com.github.jrd77.codecheck.util;


import com.github.jrd77.codecheck.data.GitDiffCmd;
import com.github.jrd77.codecheck.data.MatchRule;
import com.github.jrd77.codecheck.data.RuleTypeEnum;

import java.util.ArrayList;
import java.util.List;

public class HtmlUtil {

    public static final String tableStart = "<table>";
    public static final String tableEnd = "</table>";
    public static final String tableTh = "    <tr>\n" +
            "        <th>no</th>\n" +
            "        <th>match</th>\n" +
            "        <th>lineStr</th>\n" +
            "        <th>lineNumber</th>\n" +
            "        <th>filePath</th>\n" +
            "    </tr>";
    public static final String tableTdTemplate = "    <tr>\n" +
            "        <td>%s</td>\n" +
            "        <td>%s</td>\n" +
            "        <td>%s</td>\n" +
            "        <td>%s</td>\n" +
            "        <td>%s</td>\n" +
            "    </tr>";

    public static String buildHtmlTable(List<GitDiffCmd> diffCmdList) {

        StringBuilder result = new StringBuilder(tableStart);
        result.append(tableTh);
        int index = 0;
        for (GitDiffCmd gitDiffCmd : diffCmdList) {
            ++index;
            final String format = String.format(tableTdTemplate, index,
                    gitDiffCmd.getErrorMatch(),
                    gitDiffCmd.getErrorLineStr(),
                    gitDiffCmd.getErrorLineNumber(),
                    gitDiffCmd.getFilePath());
            result.append(format);
        }
        result.append(tableEnd);
        return result.toString();
    }

    public static String buildHtmlStr(List<MatchRule> matchRules) {

        StringBuilder stringBuilder = new StringBuilder();
        for (MatchRule matchRule : matchRules) {
            final String rule = matchRule.getRule();
            stringBuilder.append(rule);
            stringBuilder.append(System.lineSeparator());
        }
        return stringBuilder.toString();
    }

    public static String buildStrHtml(List<String> str) {

        StringBuilder stringBuilder = new StringBuilder();
        for (String matchRule : str) {
            stringBuilder.append(matchRule);
            stringBuilder.append(System.lineSeparator());
        }
        return stringBuilder.toString();
    }

    public static List<String> buildHtmlStrList(List<MatchRule> matchRules) {

        List<String> list = new ArrayList<>();
        for (MatchRule matchRule : matchRules) {
            final String rule = matchRule.getRule();
            list.add(rule);
        }
        return list;
    }

    public static MatchRule convertMatchRule(String str) {
        return new MatchRule(str, RuleTypeEnum.REGEXP, null);
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
}
