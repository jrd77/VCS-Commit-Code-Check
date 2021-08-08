package com.github.jrd77.codecheck.util;

import com.github.jrd77.codecheck.data.*;
import com.github.jrd77.codecheck.handler.CheckCommitFilter;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.ChangeListManager;
import com.intellij.openapi.vcs.changes.ContentRevision;
import com.intellij.openapi.vcs.changes.LocalChangeList;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ui.UIUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class VcsUtil {
    private static final Logger logger = Logger.getLogger(VcsUtil.class.getName());

    public static LocalChangeList getChangeFileListFromProject(Project project){
        ChangeListManager changeListManager = ChangeListManager.getInstance(project);
        return changeListManager.getDefaultChangeList();
    }


    /**
     * 检查主体流程
     * @param project
     */
    public static List<GitDiffCmd> checkMainFlow(Project project){

        logger.info("checkMainFlow 进入检查流程");
        VcsCheckSettingsState instance = VcsCheckSettingsState.getInstance();
        final List<MatchRule> matchRuleList = ConvertUtil.convertMatchRuleList(instance.ruleList);
        final List<String> ignoreList = instance.ignoreList;
        //获取变更文件
        final LocalChangeList defaultChangeList = VcsUtil.getChangeFileListFromProject(project);
        List<GitDiffCmd> cmdList = null;
        try {
            cmdList = VcsUtil.getCheckErrorListFromChange(defaultChangeList, matchRuleList, ignoreList);
        } catch (VcsException | IOException e) {
            e.printStackTrace();
        }
        CheckDataUtil.refreshResultData(cmdList);
        return cmdList;
    }
    public static List<GitDiffCmd> getCheckErrorListFromChange(LocalChangeList defaultChangeList, List<MatchRule> matchRuleList, List<String> ignoreList) throws VcsException, IOException {

        int count = 0;
        List<GitDiffCmd> resultList = new ArrayList<>();
        for (Change change : defaultChangeList.getChanges()) {
            final VirtualFile changeFile = change.getVirtualFile();
            if (Objects.isNull(changeFile)) {
                logger.info("change file type is null");
                continue;
            }
            final Change.Type type = change.getType();
            final FileType fileType = changeFile.getFileType();
            final String fileTypeName = fileType.getName();
            final String fileName = changeFile.getName();
            if (type.equals(Change.Type.DELETED) || type.equals(Change.Type.MOVED)) {
                logger.info(String.format("change file [%s]  is%s ,no need check", fileName, type.name()));
                continue;
            }
            final ContentRevision afterRevision = change.getAfterRevision();
            if (afterRevision == null) {
                logger.info(String.format("change file [%s] afterRevision is null  ,no need check", fileName));
                continue;
            }

            final boolean contains = ignoreList.stream().anyMatch(x -> matchs(x, fileName));
            if (!contains) {
                logger.info(String.format("this change file [%s] match fileType fail ,no need check", fileName));
                continue;
            }
            final String changeContent = afterRevision.getContent();
            //修改/新增内容是否违规内容
            final List<MatchRule> matchRules = matchError(matchRuleList, changeContent);
            if (matchRules.size() > 0) {
                final InputStream inputStream = changeFile.getInputStream();
                final List<String> readLines = IoUtil.readLines(inputStream, StandardCharsets.UTF_8, new ArrayList<>());
                if(readLines==null){
                    continue;
                }
                for (int i = 0; i < readLines.size(); i++) {

                    final String lineStr = readLines.get(i);
                    final List<MatchRule> matchRuleErrorList = matchError(matchRuleList, lineStr);
                    if (matchRuleErrorList.size() > 0) {
                        for (MatchRule matchRule : matchRuleErrorList) {
                            GitDiffCmd diff = new GitDiffCmd();
                            diff.setExt(fileTypeName);
                            diff.setFilePath(changeFile.getPath());
                            diff.setFileName(fileName);
                            diff.setReadLine(lineStr);
                            diff.setErrorLineStr(lineStr);
                            diff.setErrorMatch(matchRule.getRule());
                            diff.setErrorLineNumber(i + 1);
                            diff.setFile(changeFile);
                            System.out.println(++count + "-----------------------------");
                            resultList.add(diff);
                        }
                    }
                }
                IoUtil.close(inputStream);
            }
        }
        return resultList;
    }

    /**
     * 是否命中规则
     *
     * @param ruleList
     * @param changeContent
     * @return
     */
    private static List<MatchRule> matchError(List<MatchRule> ruleList, String changeContent) {

        List<MatchRule> typeList = new ArrayList<>();
        for (MatchRule matchRule : ruleList) {
            RuleTypeEnum ruleTypeEnum = null;
            String rule = null;
            if (matchRule.getRuleType().equals(RuleTypeEnum.REGEXP) &&
                    matchs(matchRule.getRule(), changeContent)) {
                ruleTypeEnum = matchRule.getRuleType();
                rule = matchRule.getRule();
            } else if (matchRule.getRuleType().equals(RuleTypeEnum.STR_MATCH) &&
                    matchs(matchRule.getRule(), changeContent)) {
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

    private static boolean matchs(String regexp, String str) {
        return Pattern.compile(regexp).matcher(str).find();
    }
}