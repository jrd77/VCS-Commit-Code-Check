package com.github.jrd77.codecheck.window.rule;

import com.github.jrd77.codecheck.MyTable;
import com.github.jrd77.codecheck.data.AppSettingsState;
import com.github.jrd77.codecheck.data.CheckDataUtil;
import com.github.jrd77.codecheck.dialog.AddIgnoreDialog;
import com.github.jrd77.codecheck.dialog.AddRuleDialog;
import com.github.jrd77.codecheck.util.BooleanUtil;
import com.intellij.openapi.editor.ex.util.EditorUIUtil;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;

import javax.swing.*;
import javax.swing.table.TableColumnModel;
import java.util.Objects;
import java.util.logging.Logger;

public class VCSCheckWindow {

    private static final Logger logger = Logger.getLogger(VCSCheckWindow.class.getName());

    private JPanel checkWindow;
    private JButton btnNewRule;
    private JButton btnResetRule;
    private JButton btnNewIgnore;
    private JButton btnResetIgnore;
    private MyTable tableRule;
    private MyTable tableIgnore;
    private JPanel windowPanel;
    private JButton btnResetResult;
    private MyTable tableResult;


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
        tableResult.getSelectionModel().addListSelectionListener(e -> {
            final int selectedColumn = tableResult.getSelectedColumn();
            final int selectedRow = tableResult.getSelectedRow();
            System.out.println(selectedColumn);
            System.out.println(selectedRow);

            try {
                final Integer index = Integer.valueOf((String) tableResult.getModel().getValueAt(selectedRow, 0));
                final String errorLine = (String) tableResult.getModel().getValueAt(selectedRow, 1);
                final Integer errorLineNumber = Integer.valueOf((String) tableResult.getModel().getValueAt(selectedRow, 2));
                final String ruleMatch = (String) tableResult.getModel().getValueAt(selectedRow, 3);
                final String filePath = (String) tableResult.getModel().getValueAt(selectedRow, 4);
                System.out.println(filePath);
                //跳转
                if(Objects.nonNull(filePath)){
                    final VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByPath(filePath.toString());
                    if(Objects.nonNull(virtualFile)){
                        final int indexColumn = errorLine.indexOf(ruleMatch);
                        final OpenFileDescriptor openFileDescriptor = new OpenFileDescriptor(project, virtualFile, errorLineNumber, indexColumn);
                        openFileDescriptor.navigate(true);
                    }
                }
            }catch (Exception ex){
                logger.severe("打开文件失败"+tableResult.getModel().getValueAt(selectedRow, 4));
                ex.printStackTrace();
            }
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
        initResultTable();
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

    /**
     * 初始化忽略规则表格
     */
    private void initResultTable(){
        this.tableResult.setModel(WindowSetting.TABLE_MODEL_RESULT);
        this.tableResult.setEnabled(true);
//        this.tableResult.seted(false);
        final TableColumnModel columnModel = tableIgnore.getColumnModel();
        int index=0;
        final int width = columnModel.getColumn(index).getPreferredWidth();
        final int globalWidth = columnModel.getColumnCount() * width;
        columnModel.getColumn(index++).setPreferredWidth(globalWidth/10);
        double contentWidth=globalWidth-(globalWidth*0.1);
        columnModel.getColumn(index++).setPreferredWidth((int) contentWidth /2);
        columnModel.getColumn(index).setPreferredWidth((int) contentWidth /2);
    }
}
