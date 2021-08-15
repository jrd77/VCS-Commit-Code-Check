package com.github.jrd77.codecheck.data

import com.github.jrd77.codecheck.util.ConvertUtil
import com.github.jrd77.codecheck.window.rule.WindowSetting
import com.google.gson.Gson
import java.util.*
import java.util.logging.Logger

object CheckDataUtil {

    private val logger = Logger.getLogger(CheckDataUtil::class.java.name)
    val instance: VcsCheckSettingsState
        get() = VcsCheckSettingsState.getInstance()

    @JvmStatic
    fun addRule(rule: MatchRule?): Boolean {
        val gson = Gson()
        val jsonStr = gson.toJson(rule)
        return if (VcsCheckSettingsState.getInstance().ruleList.contains(jsonStr)) {
            false
        } else VcsCheckSettingsState.getInstance().ruleList.add(jsonStr)
    }

    @JvmStatic
    fun ruleClear(): Boolean {
        VcsCheckSettingsState.getInstance().ruleList=LinkedList()
        return true;
    }

    @JvmStatic
    fun addIgnore(ignoreRule: String?): Boolean {
        return if (VcsCheckSettingsState.getInstance().ignoreList.contains(ignoreRule)) {
            false
        } else VcsCheckSettingsState.getInstance().ignoreList.add(ignoreRule)
    }

    @JvmStatic
    fun ignoreClear(): Boolean {
        VcsCheckSettingsState.getInstance().ignoreList=LinkedList()
        return true;
    }
    @JvmStatic
    fun refreshData() {
        val instance = VcsCheckSettingsState.getInstance()
        if (instance == null) {
            logger.warning(InterUtil.getValue("logs.refresh.tabledata"))
            return
        }
        //刷新tableRule
        WindowSetting.reFreshTableIgnore(rebuildTableIgnoreData(instance))
        WindowSetting.reFreshTableRule(rebuildTableRuleData(instance))
    }

    @JvmStatic
    fun resultClear() {
        logger.warning(InterUtil.getValue("logs.refresh.result"))
        //刷新tableRule
        WindowSetting.reFreshTableResult(null)
    }

    @JvmStatic
    fun refreshResultData(cmdList: List<GitDiffCmd?>?) {
        if(cmdList==null||cmdList.size==0){
            return;
        }
        logger.warning(InterUtil.getValue("logs.refresh.result"))
        //刷新tableRule
        WindowSetting.reFreshTableResult(ConvertUtil.convertGitDiffList(cmdList))
    }

    /**
     * 重新构造表格数据
     * @return
     */
    private fun rebuildTableRuleData(instance: VcsCheckSettingsState): Vector<Vector<String>> {
        return ConvertUtil.convertRule(instance.ruleList)
    }

    /**
     * 重新构造表格数据
     * @return
     */
    private fun rebuildTableIgnoreData(instance: VcsCheckSettingsState): Vector<Vector<String>> {
        return ConvertUtil.convertIgnore(instance.ignoreList)
    }

    /**
     * 初始化过滤规则和匹配规则
     */
    @JvmStatic
    fun initCheckFileTypeList() {
        val instance = instance
        if (instance == null) {
            logger.severe("logs.save.failed")
            return
        }
        addIgnore(".java$")
        addIgnore(".properties$")
        addIgnore(".yml$")
        addIgnore(".xml$")
        addIgnore(".kt$")
        val matchRuleExample = MatchRule("localhost", RuleTypeEnum.REGEXP, "匹配示例")
        val matchRuleExample2 = MatchRule("127.0.0.1", RuleTypeEnum.REGEXP, "匹配示例2")
        addRule(matchRuleExample)
        addRule(matchRuleExample2)
    }
}