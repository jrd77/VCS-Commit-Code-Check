package com.github.jrd77.codecheck.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindowManager;

import java.util.Objects;

public class ShowCheckWindow extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        // TODO: insert action logic here
        ToolWindowManager.getInstance(Objects.requireNonNull(e.getProject())).getToolWindow("预提交代码检查配置").show(()->{
            System.out.println("打开vcsCheck");
        });

    }
}
