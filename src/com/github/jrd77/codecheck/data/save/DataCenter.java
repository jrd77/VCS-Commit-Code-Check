package com.github.jrd77.codecheck.data.save;

import com.github.jrd77.codecheck.data.InterUtil;
import com.github.jrd77.codecheck.data.MatchRule;
import com.github.jrd77.codecheck.data.RuleTypeEnum;
import com.github.jrd77.codecheck.util.JsonUtil;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhen.wang
 * @description TODO
 * @date 2021/8/19 14:36
 */
public class DataCenter {

    public static SaveInterface getInstance=new XmlFileSaveImpl();

    public static String[] getDefaultCodeMatchType(){
        List<MatchRule> entityList = getDefaultCodeMatchTypeEntityList();
        List<String> stringList = entityList.stream().map(JsonUtil::toJson).collect(Collectors.toList());
        return stringList.toArray(new String[]{});
    }

    public static List<MatchRule> getDefaultCodeMatchTypeEntityList(){
        MatchRule matchRuleExample =new MatchRule("TODO", RuleTypeEnum.REGEXP, InterUtil.getValue("show.content.tableData.matchRule.example1"));
        return Arrays.asList(matchRuleExample);
    }

    public static String[] getDefaultFileMatchTypeList(){
        return new String[]{".java$",".properties$",".yml$",".xml$",".kt$",".yaml$"};
    }
}
