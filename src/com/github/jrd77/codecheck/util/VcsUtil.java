package com.github.jrd77.codecheck.util;

import com.github.jrd77.codecheck.data.CheckDataUtils;
import com.github.jrd77.codecheck.data.InterUtil;
import com.github.jrd77.codecheck.data.model.CodeMatchResult;
import com.github.jrd77.codecheck.data.model.MatchRule;
import com.github.jrd77.codecheck.data.model.RuleTypeEnum;
import com.github.jrd77.codecheck.data.persistent.VcsCheckSettingsState;
import com.github.jrd77.codecheck.data.save.SaveInterface;
import com.github.jrd77.codecheck.data.save.XmlFileSaveImpl;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.ChangeListManager;
import com.intellij.openapi.vcs.changes.ContentRevision;
import com.intellij.openapi.vcs.changes.LocalChangeList;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class VcsUtil {
    private static final Logger logger = Logger.getLogger(VcsUtil.class.getName());

    private static SaveInterface saveInterface = XmlFileSaveImpl.getInstance();

    public static LocalChangeList getChangeFileListFromProject(Project project) {
        ChangeListManager changeListManager = ChangeListManager.getInstance(project);
        return changeListManager.getDefaultChangeList();
    }


    /**
     * 检查主体流程
     *
     * @param project
     */
    @Deprecated
    public static List<CodeMatchResult> checkMainFlow(Project project) {

        logger.info(InterUtil.getValue("logs.common.checkMainFlow"));
        VcsCheckSettingsState instance = VcsCheckSettingsState.getInstance();
        final List<MatchRule> matchRuleList = ConvertUtil.convertMatchRuleList(saveInterface.codeMatchList());
        final List<String> ignoreList = saveInterface.fileMatchList();
        //获取变更文件
        final LocalChangeList defaultChangeList = VcsUtil.getChangeFileListFromProject(project);
        List<CodeMatchResult> cmdList = null;
        try {
            cmdList = VcsUtil.getCheckErrorListFromChange(defaultChangeList, matchRuleList, ignoreList, project);
        } catch (VcsException | IOException e) {
            e.printStackTrace();
        }
        CheckDataUtils.refreshResultData(cmdList);
        return cmdList;
    }

    public static List<CodeMatchResult> getCheckErrorListFromChange(LocalChangeList defaultChangeList, List<MatchRule> matchRuleList, List<String> ignoreList, Project project) throws VcsException, IOException {

        int count = 0;
        List<CodeMatchResult> resultList = new ArrayList<>();
        for (Change change : defaultChangeList.getChanges()) {
            final VirtualFile changeFile = change.getVirtualFile();
            if (Objects.isNull(changeFile)) {
                logger.info(InterUtil.getValue("logs.validate.changetypeisnull"));
                continue;
            }
            final Change.Type type = change.getType();
            final FileType fileType = changeFile.getFileType();
            final String fileTypeName = fileType.getName();
            final String fileName = changeFile.getName();
            if (type.equals(Change.Type.DELETED) || type.equals(Change.Type.MOVED)) {
                logger.info(String.format(InterUtil.getValue("logs.validate.noNeedCheck"), fileName, type.name()));
                continue;
            }
            final boolean contains = ignoreList.stream().anyMatch(x -> matchs(x, fileName));

            final ContentRevision afterRevision = change.getAfterRevision();
            if (afterRevision == null) {
                logger.info(String.format(InterUtil.getValue("logs.validate.changeContentIsNull"), fileName));
                continue;
            }

            if (!contains) {
                logger.info(String.format(InterUtil.getValue("logs.validate.matchTypeFailed"), fileName));
                continue;
            }
            final String changeContent = afterRevision.getContent();

            //修改/新增内容是否违规内容
            final List<MatchRule> matchRules = matchError(matchRuleList, changeContent);
            if (matchRules.size() > 0) {
                PsiFile file = PsiManager.getInstance(project).findFile(changeFile);
                if(file==null){
                    logger.warning(InterUtil.getValue("logs.validate.PsiFileIsNull"));
                    continue;
                }
                List<String> readLines = Arrays.stream(file.getText().split("\\r?\\n")).collect(Collectors.toList());
                if(readLines==null){
                    continue;
                }
                for (int i = 0; i < readLines.size(); i++) {

                    final String lineStr = readLines.get(i);
                    final List<MatchRule> matchRuleErrorList = matchError(matchRuleList, lineStr);
                    if (matchRuleErrorList.size() > 0) {
                        for (MatchRule matchRule : matchRuleErrorList) {
                            CodeMatchResult diff = new CodeMatchResult();
                            diff.setExt(fileTypeName);
                            diff.setFilePath(changeFile.getPath());
                            diff.setFileName(fileName);
                            diff.setReadLine(lineStr);
                            diff.setErrorLineStr(lineStr);
                            diff.setErrorMatch(matchRule.getRule());
                            diff.setErrorLineNumber(i + 1);
                            diff.setFile(changeFile);
                            logger.info(++count + InterUtil.getValue("logs.common.countCompare"));
                            resultList.add(diff);
                        }
                    }
                }
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
