package com.github.jrd77.codecheck.service;

import com.github.jrd77.codecheck.data.InterUtil;
import com.github.jrd77.codecheck.data.model.CheckSourceEnum;
import com.github.jrd77.codecheck.data.model.GitDiffCmd;
import com.github.jrd77.codecheck.data.model.MatchRule;
import com.github.jrd77.codecheck.data.save.DataCenter;
import com.github.jrd77.codecheck.data.save.SaveInterface;
import com.github.jrd77.codecheck.data.save.XmlFileSaveImpl;
import com.github.jrd77.codecheck.util.Assert;
import com.github.jrd77.codecheck.util.ConvertUtil;
import com.github.jrd77.codecheck.util.ResultObject;
import com.github.jrd77.codecheck.util.StrUtil;
import com.github.jrd77.codecheck.vo.CodeMatchContext;
import com.github.jrd77.codecheck.vo.CodeMatchReq;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataKey;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.ChangeListManager;
import com.intellij.openapi.vcs.changes.ContentRevision;
import com.intellij.openapi.vcs.changes.LocalChangeList;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ui.SwingHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * @author zhen.wang
 * @description TODO
 * @date 2021/8/23 18:13
 */
public class CodeMatchService {

    private static Logger logger = Logger.getLogger(CodeMatchService.class.getName());

    private static SaveInterface saveInterface= XmlFileSaveImpl.getInstance();

    public ResultObject<?> startCodeMatch(CodeMatchContext context) throws VcsException {
        logger.info(InterUtil.getValue("logs.common.checkMainFlow"));
        check(context);

        int count = 0;
        List<GitDiffCmd> resultList = new ArrayList<>();

        LocalChangeList changeList = context.getChangeList();

        for (Change change : changeList.getChanges()) {
             VirtualFile changeFile = change.getVirtualFile();
            if (Objects.isNull(changeFile)) {
                logger.info(InterUtil.getValue("logs.validate.changetypeisnull"));
                continue;
            }
             Change.Type type = change.getType();
             String fileName = changeFile.getName();
            if (type.equals(Change.Type.DELETED) || type.equals(Change.Type.MOVED)) {
                logger.info(String.format(InterUtil.getValue("logs.validate.noNeedCheck"), fileName, type.name()));
                continue;
            }
            //change content is blank
            final ContentRevision afterRevision = change.getAfterRevision();
            if (afterRevision==null||StrUtil.isBlank(afterRevision.getContent())) {
                logger.info(String.format(InterUtil.getValue("logs.validate.changeContentIsNull"), fileName));
                continue;
            }
            //TODO
            Boolean contains=matchFileType(context.getIgnoreList(),fileName);
        }
        return ResultObject.ok(resultList);
    }

    private Boolean matchFileType(List<String> ignoreList,String fileNmae) {
        return ignoreList.stream().anyMatch(x->StrUtil.contains(x,fileNmae));
    }


    public CodeMatchContext convertCodeMatchContext(CodeMatchReq req){

        List<MatchRule> codeMatchList = ConvertUtil.convertMatchRuleList(saveInterface.codeMatchList());
        return convertCodeMatchContext(req,codeMatchList,saveInterface.fileMatchList());
    }
    public CodeMatchContext convertCodeMatchContext(CodeMatchReq req, List<MatchRule> codeMatchList,List<String> fileMatchList){

//        DataContext dataContext = DataManager.getInstance().getDataContext(SwingHelper.getComponentFromRecentMouseEvent());
//        Project project = dataContext.getData(DataKey.create("project"));
        //param check
        check(req);
        //req param copy
        CodeMatchContext context=copyProperties(req);
        //get changeFileList
        ChangeListManager changeListManager = ChangeListManager.getInstance(context.getProject());
        LocalChangeList defaultChangeList = changeListManager.getDefaultChangeList();
        context.setChangeList(defaultChangeList);
        context.setMatchRuleList(codeMatchList);
        context.setIgnoreList(fileMatchList);
        return context;
    }

    private CodeMatchContext copyProperties(CodeMatchReq req) {
        CodeMatchContext context=new CodeMatchContext();
        context.setProject(req.getProject());
        context.setCheckSource(req.getCheckSource());
        return context;
    }

    private void check(CodeMatchReq req) {

        Assert.notNull(req);
        Assert.notNull(req.getProject());
        Assert.notNull(req.getCheckSource());
    }

    private void check(CodeMatchContext context) {

        Assert.notNull(context);
        Assert.notNull(context.getProject());
        Assert.notNull(context.getCheckSource());
    }
}
