package com.github.jrd77.codecheck.data;

import com.github.jrd77.codecheck.util.ConvertUtil;
import com.github.jrd77.codecheck.window.rule.WindowSetting;
import com.google.gson.Gson;

import java.util.Vector;
import java.util.logging.Logger;

public class CheckDataUtil {

    private static final Logger logger = Logger.getLogger(CheckDataUtil.class.getName());

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

    public static boolean addIgnore(String ignoreRule){

        if(AppSettingsState.getInstance().ignoreList.contains(ignoreRule)){
            return false;
        }
        return AppSettingsState.getInstance().ignoreList.add(ignoreRule);
    }
    public static void refreshData() {
        AppSettingsState instance = AppSettingsState.getInstance();
        if(instance==null){
            logger.warning("");
            return;
        }
        //刷新tableRule
        WindowSetting.reFreshTableIgnore(rebuildTableRuleData(instance));
    }

    /**
     * 重新构造表格数据
     * @return
     */
    private static Vector<Vector<String>> rebuildTableRuleData(AppSettingsState instance) {

        return ConvertUtil.convertRule(instance.ruleList);
    }



    /**
     * 初始化过滤规则和匹配规则
     */
    public static void initCheckFileTypeList() {
        AppSettingsState instance = getInstance();
        if(instance==null){
            logger.severe("PersistentState failed,持久化失败");
            return;
        }
        instance.ignoreList.add(".java$");
        instance.ignoreList.add(".properties$");
        instance.ignoreList.add(".yml$");
        instance.ignoreList.add(".xml$");
        MatchRule matchRuleExample=new MatchRule("localhost",RuleTypeEnum.REGEXP,"匹配示例");
        MatchRule matchRuleExample2=new MatchRule("127.0.0.1",RuleTypeEnum.REGEXP,"匹配示例2");
        addRule(matchRuleExample);
        addRule(matchRuleExample2);
    }
}
