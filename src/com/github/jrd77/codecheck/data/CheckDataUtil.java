package com.github.jrd77.codecheck.data;

import com.github.jrd77.codecheck.window.rule.WindowSetting;
import com.google.gson.Gson;
import com.intellij.xml.util.HtmlUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

public class CheckDataUtil {


    public static AppSettingsState getInstance(){
        return AppSettingsState.getInstance();
    }

    public static boolean addRule(MatchRule rule){

        Gson gson = new Gson();
        final String jsonStr = gson.toJson(rule);
        if(AppSettingsState.getInstance().ruleList.contains(jsonStr)){
            return false;
        }
        return AppSettingsState.getInstance().ruleList.add(jsonStr);
    }

    public static void refreshData() {
        //刷新table
        WindowSetting.reFreshTableRule(getTableData());
    }

    private static Vector<Vector<String>> getTableData() {

        AppSettingsState instance = AppSettingsState.getInstance();
        if(instance==null){
            return null;
        }
        return convert(convertMatchRuleList(instance.ruleList));
    }

    private static Vector<Vector<String>> convert(List<MatchRule> ruleList) {

        final int size = ruleList.size();
        Vector<Vector<String>> data = new Vector<Vector<String>>();
        int index=0;
        for (MatchRule matchRule : ruleList) {
            final Vector<String> strings = covertData(matchRule,++index);
            data.add(strings);
        }
        return data;
    }

    private static Vector<String> covertData(MatchRule matchRule, int index) {

        Vector<String> vector = new Vector<String>();
        vector.add(String.valueOf(index));
        vector.add(matchRule.getRule());
        vector.add(matchRule.getComment());
        vector.add(matchRule.getRuleType().name());
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
        Gson gson = new Gson();
        return gson.fromJson(str, MatchRule.class);
    }
}
