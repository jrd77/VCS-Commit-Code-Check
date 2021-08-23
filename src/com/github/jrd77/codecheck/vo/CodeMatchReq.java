package com.github.jrd77.codecheck.vo;

import com.github.jrd77.codecheck.data.model.CheckSourceEnum;
import com.intellij.openapi.project.Project;


/**
 * @author zhen.wang
 * @description 匹配请求VO
 * @date 2021/8/23 18:16
 */
public class CodeMatchReq {

    private Project project;

    private CheckSourceEnum checkSource;

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

}
