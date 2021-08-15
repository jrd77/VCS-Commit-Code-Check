package com.github.jrd77.codecheck.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.wm.ToolWindowManager;

import java.util.Objects;

public class ShowCodeCheckWindow extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {

        ToolWindowManager.getInstance(Objects.requireNonNull(e.getProject())).getToolWindow("PreCommitCodeWindow").show(()->{
            System.out.println("open VcsCheck");
        });
    }
}
