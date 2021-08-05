package com.github.jrd77.codecheck.data

import com.github.jrd77.codecheck.data.AppSettingsState
import com.github.jrd77.codecheck.data.CheckDataUtil
import com.github.jrd77.codecheck.window.rule.WindowSetting
import com.github.jrd77.codecheck.data.GitDiffCmd
import com.github.jrd77.codecheck.util.ConvertUtil
import com.github.jrd77.codecheck.data.RuleTypeEnum
import com.google.gson.Gson
import java.util.*
import java.util.logging.Logger

object CheckDataUtil {
    private val logger = Logger.getLogger(CheckDataUtil::class.java.name)
    val instance: AppSettingsState
        get() = AppSettingsState.getInstance()

    @JvmStatic
    fun addRule(rule: MatchRule?): Boolean {
        val gson = Gson()
        val jsonStr = gson.toJson(rule)
        return if (AppSettingsState.getInstance().ruleList.contains(jsonStr)) {
            false
        } else AppSettingsState.getInstance().ruleList.add(jsonStr)
    }

    @JvmStatic
    fun addIgnore(ignoreRule: String?): Boolean {
        return if (AppSettingsState.getInstance().ignoreList.contains(ignoreRule)) {
            false
        } else AppSettingsState.getInstance().ignoreList.add(ignoreRule)
    }

    @JvmStatic
    fun refreshData() {
        val instance = AppSettingsState.getInstance()
        if (instance == null) {
            logger.warning("刷新表格数据")
            return
        }
        //刷新tableRule
        WindowSetting.reFreshTableIgnore(rebuildTableIgnoreData(instance))
        WindowSetting.reFreshTableRule(rebuildTableRuleData(instance))
    }

    @JvmStatic
    fun refreshResultData(cmdList: List<GitDiffCmd?>?) {
        logger.warning("刷新结果")
        //刷新tableRule
        WindowSetting.reFreshTableResult(ConvertUtil.convertGitDiffList(cmdList))
    }

    /**
     * 重新构造表格数据
     * @return
     */
    private fun rebuildTableRuleData(instance: AppSettingsState): Vector<Vector<String>> {
        return ConvertUtil.convertRule(instance.ruleList)
    }

    /**
     * 重新构造表格数据
     * @return
     */
    private fun rebuildTableIgnoreData(instance: AppSettingsState): Vector<Vector<String>> {
        return ConvertUtil.convertIgnore(instance.ignoreList)
    }

    /**
     * 初始化过滤规则和匹配规则
     */
    @JvmStatic
    fun initCheckFileTypeList() {
        val instance = instance
        if (instance == null) {
            logger.severe("PersistentState failed,持久化失败")
            return
        }
        addIgnore(".java$")
        addIgnore(".properties$")
        addIgnore(".yml$")
        addIgnore(".xml$")
        val matchRuleExample = MatchRule("localhost", RuleTypeEnum.REGEXP, "匹配示例")
        val matchRuleExample2 = MatchRule("127.0.0.1", RuleTypeEnum.REGEXP, "匹配示例2")
        addRule(matchRuleExample)
        addRule(matchRuleExample2)
    }
}