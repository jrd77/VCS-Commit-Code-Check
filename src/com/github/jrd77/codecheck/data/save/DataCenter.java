package com.github.jrd77.codecheck.data.save;

import com.github.jrd77.codecheck.data.InterUtil;
import com.github.jrd77.codecheck.data.model.FileMatchModel;
import com.github.jrd77.codecheck.data.model.MatchRule;
import com.github.jrd77.codecheck.data.model.RuleTypeEnum;
import com.github.jrd77.codecheck.util.JsonUtil;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhen.wang
 * @description 默认配置
 * @date 2021/8/19 14:36
 */
public class DataCenter {
    public static String[] getDefaultCodeMatchType(){
        List<MatchRule> entityList = getDefaultCodeMatchTypeEntityList();
        List<String> stringList = entityList.stream().map(JsonUtil::toJson).collect(Collectors.toList());
        return stringList.toArray(new String[]{});
    }

    public static List<MatchRule> getDefaultCodeMatchTypeEntityList() {
        MatchRule matchRuleExample = new MatchRule("TODO", RuleTypeEnum.REGEXP, InterUtil.getValue("show.content.tableData.matchRule.example1"));
        return Arrays.asList(matchRuleExample);
    }

    public static List<FileMatchModel> getDefaultFileMatchTypeList() {

        FileMatchModel matchRuleExample = new FileMatchModel("java$", RuleTypeEnum.REGEXP, null);
        FileMatchModel matchRuleExample2 = new FileMatchModel("kt$", RuleTypeEnum.REGEXP, null);
        FileMatchModel matchRuleExample3 = new FileMatchModel("py$", RuleTypeEnum.REGEXP, null);
        FileMatchModel matchRuleExample4 = new FileMatchModel("js$", RuleTypeEnum.REGEXP, null);
        FileMatchModel matchRuleExample5 = new FileMatchModel("go$", RuleTypeEnum.REGEXP, null);
        FileMatchModel matchRuleExample6 = new FileMatchModel("sql$", RuleTypeEnum.REGEXP, null);
        return Arrays.asList(matchRuleExample, matchRuleExample2, matchRuleExample3, matchRuleExample4, matchRuleExample5, matchRuleExample6);
    }
}
