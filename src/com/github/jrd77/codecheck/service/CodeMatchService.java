package com.github.jrd77.codecheck.service;

import com.github.jrd77.codecheck.data.InterUtil;
import com.github.jrd77.codecheck.data.model.CodeMatchResult;
import com.github.jrd77.codecheck.data.model.FileMatchModel;
import com.github.jrd77.codecheck.data.model.MatchRule;
import com.github.jrd77.codecheck.data.save.SaveInterface;
import com.github.jrd77.codecheck.data.save.XmlFileSaveImpl;
import com.github.jrd77.codecheck.util.*;
import com.github.jrd77.codecheck.vo.CodeMatchContext;
import com.github.jrd77.codecheck.vo.CodeMatchReq;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.ChangeListManager;
import com.intellij.openapi.vcs.changes.LocalChangeList;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author zhen.wang
 * @description TODO
 * @date 2021/8/23 18:13
 */
public class CodeMatchService {

    private static Logger logger = Logger.getLogger(CodeMatchService.class.getName());

    private static SaveInterface saveInterface= XmlFileSaveImpl.getInstance();

    public static ResultObject<List<CodeMatchResult>> startCodeMatch(CodeMatchContext context) {
        logger.info(InterUtil.getValue("logs.common.checkMainFlow"));
        //check param
        ResultObject<List<Change>> resultCheck = checkContext(context);
        //if has err ,should show notification in right below window
        if (resultCheck.getOk() == ResultObject.ResultConstant.SHOULD_NOTIFICATION) {
            return ResultObject.err(resultCheck.getOk(), resultCheck.getMsg());
        }
        List<Change> changeList = resultCheck.getData();
        List<CodeMatchResult> resultList = new ArrayList<>();
        for (Change changeFile : changeList) {
            VirtualFile virtualFile = changeFile.getVirtualFile();
            Assert.notNull(virtualFile);
            PsiFile file = PsiManager.getInstance(context.getProject()).findFile(virtualFile);
            if (file == null) {
                logger.warning(InterUtil.getValue("logs.validate.PsiFileIsNull"));
                continue;
            }
            //spit code by line
            List<String> readLines = Arrays.stream(file.getText().split("\\r?\\n")).collect(Collectors.toList());
            if (CollUtil.isEmpty(readLines)) {
                continue;
            }
            for (int i = 0; i < readLines.size(); i++) {
                final String lineStr = readLines.get(i);
                //check code match line by line
                final List<MatchRule> matchRuleErrorList = ContainUtil.matchError(context.getCodeMatchList(), lineStr);
                if (CollUtil.isNotEmpty(matchRuleErrorList)) {
                    //add all err match every line 添加各行检查出的所有错误
                    List<CodeMatchResult> codeMatchResults = codeMatchResultBuild(matchRuleErrorList, virtualFile, lineStr, i);
                    resultList.addAll(codeMatchResults);
                }
            }
        }
        return ResultObject.ok(resultList);
    }

    public static CodeMatchContext convertCodeMatchContext(CodeMatchReq req, List<MatchRule> codeMatchList, List<String> fileMatchList) {

        //param check
        checkContext(req);
        //req param copy
        CodeMatchContext context = copyProperties(req);
        //get changeFileList
        ChangeListManager changeListManager = ChangeListManager.getInstance(context.getProject());
        LocalChangeList defaultChangeList = changeListManager.getDefaultChangeList();
        context.setChangeList(defaultChangeList);
        context.setCodeMatchList(codeMatchList);
        context.setFileMatchList(fileMatchList);
        return context;
    }

    public static CodeMatchContext convertCodeMatchContext(CodeMatchReq req) {

        //param check
        checkContext(req);
        //req param copy
        CodeMatchContext context = copyProperties(req);
        //get changeFileList
        ChangeListManager changeListManager = ChangeListManager.getInstance(context.getProject());
        LocalChangeList defaultChangeList = changeListManager.getDefaultChangeList();
        context.setChangeList(defaultChangeList);
        if (CollUtil.isNotEmpty(saveInterface.codeMatchList())) {
            List<MatchRule> matchRuleList = ConvertUtil.convertMatchRuleList(saveInterface.codeMatchList());
            context.setCodeMatchList(matchRuleList);
        }
        context.setFileMatchList(saveInterface.fileMatchList());
        return context;
    }


    private static List<CodeMatchResult> codeMatchResultBuild(List<MatchRule> matchRuleErrorList, VirtualFile virtualFile, String lineStr, int lineNum) {

        List<CodeMatchResult> resultList = new ArrayList<>();
        for (MatchRule matchRule : matchRuleErrorList) {
            CodeMatchResult diff = new CodeMatchResult();
            diff.setExt(virtualFile.getExtension());
            diff.setFilePath(virtualFile.getPath());
            diff.setFileName(virtualFile.getNameWithoutExtension());
            diff.setReadLine(lineStr);
            diff.setErrorLineStr(lineStr);
            diff.setErrorMatch(matchRule.getRule());
            diff.setErrorLineNumber(lineNum + 1);
            diff.setFile(virtualFile);
            resultList.add(diff);
        }
        return resultList;
    }

    private static CodeMatchContext copyProperties(CodeMatchReq req) {
        CodeMatchContext context = new CodeMatchContext();
        context.setProject(req.getProject());
        context.setCheckSource(req.getCheckSource());
        return context;
    }

    private static void checkContext(CodeMatchReq req) {

        Assert.notNull(req);
        Assert.notNull(req.getProject());
        Assert.notNull(req.getCheckSource());
    }

    private static ResultObject<List<Change>> checkContext(CodeMatchContext context) {

        Assert.notNull(context);
        Assert.notNull(context.getProject());
        Assert.notNull(context.getCheckSource());
        //check file name rule
        if (CollUtil.isEmpty(context.getFileMatchList())) {
            return ResultObject.err(ResultObject.ResultConstant.SHOULD_NOTIFICATION, InterUtil.getValue("show.component.notification.checkContextId.nofilematch.content"));
        }
        //check code match rule
        if (CollUtil.isEmpty(context.getCodeMatchList())) {
            return ResultObject.err(ResultObject.ResultConstant.SHOULD_NOTIFICATION, InterUtil.getValue("show.component.notification.checkContextId.nocodematch.content"));
        }
        LocalChangeList changeList = context.getChangeList();
        if (changeList == null || changeList.getChanges() == null) {
            return ResultObject.err(ResultObject.ResultConstant.SHOULD_NOTIFICATION, InterUtil.getValue("show.component.notification.checkContextId.nochangefile.content"));
        }
        List<Change.Type> changeTypeList = changeList.getChanges().stream().map(Change::getType).collect(Collectors.toList());
        //check type change.type add and change.type.modify
        boolean matchChangeType = changeTypeList.stream().anyMatch(x -> x.equals(Change.Type.NEW) || x.equals(Change.Type.MODIFICATION));
        if (!matchChangeType) {
            return ResultObject.err(ResultObject.ResultConstant.SHOULD_NOTIFICATION, InterUtil.getValue("show.component.notification.checkContextId.noaddormodifychangefile.content"));
        }
        List<String> fileNameList = changeList.getChanges().stream().map(Change::getVirtualFile).filter(Objects::nonNull).map(VirtualFile::getName).collect(Collectors.toList());
        List<FileMatchModel> fileMatchModelList = context.getFileMatchList().stream().map(x -> JsonUtil.fromJson(x, FileMatchModel.class)).collect(Collectors.toList());
        List<Change> containsFile = ContainUtil.containsFile(fileMatchModelList, changeList);
        //check file name
        if (CollUtil.isEmpty(containsFile)) {
            return ResultObject.err(ResultObject.ResultConstant.SHOULD_NOTIFICATION, InterUtil.getValue("show.component.notification.checkContextId.nofilematchresult.content"));
        }
        List<Change> changes;
        try {
            changes = ContainUtil.codeMatch(context.getCodeMatchList(), containsFile);
        } catch (VcsException e) {
            e.printStackTrace();
            return ResultObject.err(ResultObject.ResultConstant.SHOULD_NOTIFICATION, InterUtil.getValue("show.component.notification.vcs.exception.content"));
        }
        //check has code-match first
        if (CollUtil.isEmpty(changes)) {
            return ResultObject.err(ResultObject.ResultConstant.SHOULD_NOTIFICATION, InterUtil.getValue("show.component.notification.checkContextId.noresult.content"));
        }
        return ResultObject.ok(changes);
    }


}
