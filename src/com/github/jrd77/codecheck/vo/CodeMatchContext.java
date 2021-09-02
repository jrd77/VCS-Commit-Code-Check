package com.github.jrd77.codecheck.vo;

import com.github.jrd77.codecheck.data.model.CheckSourceEnum;
import com.github.jrd77.codecheck.data.model.MatchRule;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.changes.LocalChangeList;

import java.util.List;

/**
 * @author zhen.wang
 * @description 匹配请求VO
 * @date 2021/8/23 18:16
 */
public class CodeMatchContext {

    private Project project;

    private List<MatchRule> codeMatchList;

    private List<String> fileMatchList;

    private CheckSourceEnum checkSource;

    private LocalChangeList changeList;

    public LocalChangeList getChangeList() {
        return changeList;
    }

    public void setChangeList(LocalChangeList changeList) {
        this.changeList = changeList;
    }

    public CheckSourceEnum getCheckSource() {

        return checkSource;
    }

    public void setCheckSource(CheckSourceEnum checkSource) {
        this.checkSource = checkSource;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public List<MatchRule> getCodeMatchList() {
        return codeMatchList;
    }

    public void setCodeMatchList(List<MatchRule> codeMatchList) {
        this.codeMatchList = codeMatchList;
    }

    public List<String> getFileMatchList() {
        return fileMatchList;
    }

    public void setFileMatchList(List<String> fileMatchList) {
        this.fileMatchList = fileMatchList;
    }
}
