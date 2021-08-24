package com.github.jrd77.codecheck.data;

import com.github.jrd77.codecheck.data.model.CodeMatchResult;
import com.github.jrd77.codecheck.data.model.MatchRule;
import com.github.jrd77.codecheck.data.save.DataCenter;
import com.github.jrd77.codecheck.data.save.SaveInterface;
import com.github.jrd77.codecheck.data.save.XmlFileSaveImpl;
import com.github.jrd77.codecheck.util.ConvertUtil;
import com.github.jrd77.codecheck.util.JsonUtil;
import com.github.jrd77.codecheck.window.rule.WindowSetting;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

public class CheckDataUtils {


    private static Logger logger = Logger.getLogger(CheckDataUtils.class.getName());

    private static SaveInterface saveInterface= XmlFileSaveImpl.getInstance();


    public static Boolean addCodeMatch(MatchRule rule){
        String jsonStr = JsonUtil.toJson(rule);
        if(saveInterface.codeMatchContain(jsonStr)){
            return false;
        }else{
            return saveInterface.addCodeMatch(jsonStr);
        }
    }

    public static Boolean clearCodeMatch()  {
        return saveInterface.clearCodeMatch();
    }

    public static Boolean addFileMatch(String fileMatch){
        if(saveInterface.fileMatchContain(fileMatch)){
            return false;
        }else{
            return saveInterface.addFileMatch(fileMatch);
        }
    }

    public static Boolean clearFileMatch()  {
        return saveInterface.clearFileMatch();
    }

    public static void refreshData() {
        if (saveInterface.codeMatchList() == null||saveInterface.fileMatchList()==null) {
            logger.warning(InterUtil.getValue("logs.refresh.tabledata"));
            return;
        }
        //刷新tableRule
        WindowSetting.reFreshTableIgnore(rebuildTableIgnoreData(saveInterface.fileMatchList()));
        WindowSetting.reFreshTableRule(rebuildTableRuleData(saveInterface.codeMatchList()));
    }

    /**
     * 重新构造表格数据
     * @return
     */
    private static Vector<Vector<String>> rebuildTableRuleData(List<String> list){
        return ConvertUtil.convertRule(list);
    }
    /**
     * 重新构造表格数据
     * @return
     */
    private static Vector<Vector<String>> rebuildTableIgnoreData(List<String> list){
        return ConvertUtil.convertIgnore(list);
    }

    public static void resultClear(){
        logger.warning(InterUtil.getValue("logs.refresh.result"));
        //刷新tableRule
        WindowSetting.reFreshTableResult(null);
    }


    public static void refreshResultData(List<CodeMatchResult> cmdList) {
        if (cmdList == null) {
            return;
        }
        logger.warning(InterUtil.getValue("logs.refresh.result"));
        //刷新tableRule
        WindowSetting.reFreshTableResult(ConvertUtil.convertGitDiffList(cmdList));
    }

    /**
     * 初始化过滤规则和匹配规则
     */
    public static void initCheckFileTypeList() {

        if (saveInterface == null) {
            logger.severe(InterUtil.getValue("logs.save.failed"));
            return;
        }
        String[] fileMatchList = DataCenter.getDefaultFileMatchTypeList();
        Arrays.asList(fileMatchList).forEach(CheckDataUtils::addFileMatch);

        List<MatchRule> defaultCodeMatchTypeEntityList = DataCenter.getDefaultCodeMatchTypeEntityList();
        defaultCodeMatchTypeEntityList.forEach(CheckDataUtils::addCodeMatch);
        saveInterface.setOpenCheck(true);
    }
}
