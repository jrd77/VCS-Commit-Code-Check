package com.github.jrd77.codecheck.window.rule;

import com.github.jrd77.codecheck.data.CheckDataUtil;
import com.github.jrd77.codecheck.dialog.AddDialog;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;

import javax.swing.*;
import java.util.logging.Logger;

public class VCSCheckWindow {

    private static final Logger logger = Logger.getLogger(VCSCheckWindow.class.getName());

    private JPanel checkWindow;
    private JButton btnNewRule;
    private JButton btnResetRule;
    private JButton btnNewIgnore;
    private JButton btnResetIgnore;
    private JTable tableRule;
    private JTable tableIgnore;
    private JPanel windowPanel;




    public VCSCheckWindow(Project project, ToolWindow toolWindow) {

        init();
        btnNewRule.addActionListener(e->{
            AddDialog addDialog=new AddDialog();
            addDialog.setVisible(true);
            addDialog.show(true);
        });
    }

    public JPanel getJcontent() {


        return windowPanel;
    }


    private void init(){
        this.tableIgnore.setModel(WindowSetting.TABLE_MODEL_IGNORE);
        this.tableIgnore.setEnabled(true);

        this.tableRule.setModel(WindowSetting.TABLE_MODEL_RULE);
        this.tableRule.setEnabled(true);

        CheckDataUtil.refreshData();
    }
}
