package com.github.jrd77.codecheck.handler;

import com.github.jrd77.codecheck.data.*;
import com.github.jrd77.codecheck.util.BooleanUtil;
import com.github.jrd77.codecheck.util.ConvertUtil;
import com.github.jrd77.codecheck.util.HtmlUtil;
import com.github.jrd77.codecheck.util.IoUtil;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vcs.CheckinProjectPanel;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.ChangeListManager;
import com.intellij.openapi.vcs.changes.ContentRevision;
import com.intellij.openapi.vcs.changes.LocalChangeList;
import com.intellij.openapi.vcs.checkin.CheckinHandler;
import com.intellij.openapi.vcs.ui.RefreshableOnComponent;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.util.ui.UIUtil;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

public class VcsCheckinHandler extends CheckinHandler {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(VcsCheckinHandler.class.getName());
    private static final String CHECKER_STATE_KEY = "COMMIT_CHECKER_STATE_KEY";
    private final CheckinProjectPanel panel;


    public VcsCheckinHandler(CheckinProjectPanel panel) {
        this.panel = panel;
    }

    public static boolean isCheckMessageEnabled() {
        return PropertiesComponent.getInstance().getBoolean(CHECKER_STATE_KEY, true);
    }

    @Override
    public RefreshableOnComponent getBeforeCheckinConfigurationPanel() {
        final JCheckBox checkBox = new JCheckBox("特征检查");

        return new RefreshableOnComponent() {

            @Override
            public JComponent getComponent() {
                JPanel root = new JPanel(new BorderLayout());
                root.add(checkBox, "West");
                return root;
            }

            @Override
            public void refresh() {
            }

            @Override
            public void saveState() {
                PropertiesComponent.getInstance().setValue(CHECKER_STATE_KEY, checkBox.isSelected());
            }

            @Override
            public void restoreState() {
                checkBox.setSelected(isCheckMessageEnabled());
            }
        };
    }

    private static List<GitDiffCmd> getCheckErrorListFromChange(LocalChangeList defaultChangeList, List<MatchRule> matchRuleList, List<String> ignoreList, Project project) throws VcsException, IOException {

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
     * 在提交之前检查
     *
     * @return
     */
    @Override
    public ReturnResult beforeCheckin() {


        if (!isCheckMessageEnabled()) {
            logger.warning("未开启违规检查");
            return super.beforeCheckin();
        }
        logger.info("commit file content check start");
        AppSettingsState instance = AppSettingsState.getInstance();
        if (instance.ruleList.size() == 0) {
            logger.warning("没有配置匹配规则");
            return super.beforeCheckin();
        }
        if (instance.ignoreList.size() == 0) {
            logger.warning("没有配置文件匹配规则");
            return super.beforeCheckin();
        }
        final List<MatchRule> matchRuleList = ConvertUtil.convertMatchRuleList(instance.ruleList);
        final List<String> ignoreList = instance.ignoreList;

        Project project = panel.getProject();
        ChangeListManager changeListManager = ChangeListManager.getInstance(project);

        final LocalChangeList defaultChangeList = changeListManager.getDefaultChangeList();
        List<GitDiffCmd> cmdList = null;
        try {
            cmdList = getCheckErrorListFromChange(defaultChangeList, matchRuleList, ignoreList,project);
        } catch (VcsException | IOException e) {
            e.printStackTrace();
        }
        if (cmdList != null && cmdList.size() > 0) {
            CheckDataUtil.refreshResultData(cmdList);
            final String htmlTable = HtmlUtil.buildHtmlTable(cmdList);
            String html = "<html><head>" + UIUtil.getCssFontDeclaration(UIUtil.getLabelFont()) + "</head><body>" +
                    "<br><h3>检测到命中特征的代码，是否继续提交</h3>" +
                    "<br>" +
                    htmlTable +
                    "</body></html>";
            int yesOrNo = Messages.showYesNoDialog(html,
                    "特征检查",
                    UIUtil.getErrorIcon());
            String errorLineStr = cmdList.get(0).getErrorLineStr();
            String trim = errorLineStr.trim();
            int column = errorLineStr.length() - trim.length();
            OpenFileDescriptor descriptor = new OpenFileDescriptor(project, cmdList.get(0).getFile(), cmdList.get(0).getErrorLineNumber(), column);
//            .navigate(...)
//            new OpenFileDescriptor(project, changeFile).navigate(true);
//            descriptor.navigate(true);
            return yesOrNo == 0 ? ReturnResult.COMMIT : ReturnResult.CANCEL;
        }
        return ReturnResult.COMMIT;
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
