package com.github.jrd77.codecheck.window.rule;

import com.github.jrd77.codecheck.data.AppSettingsState;
import com.github.jrd77.codecheck.data.CheckDataUtil;
import com.github.jrd77.codecheck.dialog.AddIgnoreDialog;
import com.github.jrd77.codecheck.dialog.AddRuleDialog;
import com.github.jrd77.codecheck.util.BooleanUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;

import javax.swing.*;
import javax.swing.table.TableColumnModel;
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
            AddRuleDialog addDialog=new AddRuleDialog();
            addDialog.setVisible(true);
            addDialog.show(true);
        });
        btnNewIgnore.addActionListener(e->{
            AddIgnoreDialog addDialog=new AddIgnoreDialog();
            addDialog.setVisible(true);
            addDialog.show(true);
        });
    }

    public JPanel getJcontent() {


        return windowPanel;
    }


    private void init(){

        AppSettingsState instance = AppSettingsState.getInstance();
        //是否init,否 进行初始化
        if(BooleanUtil.isNotTrue(instance.openCheck)){
            CheckDataUtil.initCheckFileTypeList();
            instance.openCheck=Boolean.TRUE;
        }
        initIgnoreTable();
        initRuleTable();
        CheckDataUtil.refreshData();
    }


    /**
     * 初始化忽略规则表格
     */
    private void initIgnoreTable(){
        this.tableIgnore.setModel(WindowSetting.TABLE_MODEL_IGNORE);
        this.tableIgnore.setEnabled(true);
        final TableColumnModel columnModel = tableIgnore.getColumnModel();
        int index=0;
        final int width = columnModel.getColumn(index).getPreferredWidth();
        final int globalWidth = columnModel.getColumnCount() * width;
        columnModel.getColumn(index++).setPreferredWidth(globalWidth/10);
        double contentWidth=globalWidth-(globalWidth*0.1);
        columnModel.getColumn(index++).setPreferredWidth((int) contentWidth /2);
        columnModel.getColumn(index).setPreferredWidth((int) contentWidth /2);
    }

    /**
     * 初始化匹配规则表格
     */
    private void initRuleTable(){
        this.tableRule.setModel(WindowSetting.TABLE_MODEL_RULE);
        this.tableRule.setEnabled(true);
        final TableColumnModel columnModel = tableRule.getColumnModel();
        int index=0;
        final int width = columnModel.getColumn(index).getPreferredWidth();
        final int globalWidth = columnModel.getColumnCount() * width;
        columnModel.getColumn(index++).setPreferredWidth(globalWidth/10);
        columnModel.getColumn(index++).setPreferredWidth(globalWidth/10);
        double contentWidth=globalWidth-(globalWidth*0.3);
        columnModel.getColumn(index++).setPreferredWidth((int) contentWidth);
        columnModel.getColumn(index).setPreferredWidth(globalWidth/10);
    }
}
