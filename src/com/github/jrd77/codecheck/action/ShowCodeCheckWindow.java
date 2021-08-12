package com.github.jrd77.codecheck.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.wm.ToolWindowManager;

import java.util.Objects;

public class ShowCodeCheckWindow extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        // TODO: insert action logic here
        ToolWindowManager.getInstance(Objects.requireNonNull(e.getProject())).getToolWindow("预提交代码检查配置").show(()->{
            System.out.println("打开vcsCheck");
        });
    }
}
