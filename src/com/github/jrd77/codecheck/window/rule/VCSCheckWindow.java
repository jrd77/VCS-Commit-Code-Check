package com.github.jrd77.codecheck.window.rule;

import com.github.jrd77.codecheck.handler.CheckCommitFilter;
import com.github.jrd77.codecheck.intellij.compoent.MyButton;
import com.github.jrd77.codecheck.intellij.compoent.MyTable;
import com.github.jrd77.codecheck.data.VcsCheckSettingsState;
import com.github.jrd77.codecheck.data.CheckDataUtil;
import com.github.jrd77.codecheck.data.GitDiffCmd;
import com.github.jrd77.codecheck.dialog.AddIgnoreDialog;
import com.github.jrd77.codecheck.dialog.AddRuleDialog;
import com.github.jrd77.codecheck.util.BooleanUtil;
import com.github.jrd77.codecheck.util.ResultObject;
import com.github.jrd77.codecheck.util.VcsUtil;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.util.ui.UIUtil;

import javax.swing.*;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import static com.github.jrd77.codecheck.data.CheckDataUtil.resultClear;

public class VCSCheckWindow {

    private static final Logger logger = Logger.getLogger(VCSCheckWindow.class.getName());

    private static VcsCheckSettingsState instance = VcsCheckSettingsState.getInstance();

    private static final int DIALOG_HIDDEN=1;
    private static final int DIALOG_SHOW=0;

    private JPanel checkWindow;
    private MyButton btnNewRule;
    private MyButton btnResetRule;
    private MyButton btnNewIgnore;
    private MyButton btnResetIgnore;
    private MyTable tableRule;
    private MyTable tableIgnore;
    private JPanel windowPanel;
    private MyTable tableResult;
    private MyButton btnCheck;
    private MyButton btnResetResult;


    public VCSCheckWindow(Project project, ToolWindow toolWindow) {

        init();

        btnCheck.addActionListener(e->{
            int yesNoDialog=DIALOG_HIDDEN;
            final int columnCount = tableResult.getRowCount();
            if(columnCount!=0){
                yesNoDialog = Messages.showYesNoDialog("是否清空检查结果,重新检查", "重新检查", UIUtil.getWarningIcon());
            }
            if(yesNoDialog==0||columnCount==0){
                CheckDataUtil.resultClear();
                //其他检查
                ResultObject resultObject = CheckCommitFilter.checkCommitPre();
                if(resultObject.getOk()!=0){
                    Messages.showMessageDialog(resultObject.getMsg(),"重新检查失败", UIUtil.getWarningIcon());
                    return;
                }
                //开始检查
                VcsUtil.checkMainFlow(project);
            }
        });
        btnResetIgnore.addActionListener(e->{

            int yesNoDialog=DIALOG_HIDDEN;
            final int columnCount = tableIgnore.getRowCount();
            if(columnCount!=0){
                yesNoDialog = Messages.showYesNoDialog("是否清空忽略规则(需要另外添加,如不添加将导致不能检查)", "清空检查文件规则", UIUtil.getWarningIcon());
            }
            //yes
            if(yesNoDialog==0){
                CheckDataUtil.ignoreClear();
                CheckDataUtil.refreshData();
            }
        });
        btnResetRule.addActionListener(e->{

            int showDialog=DIALOG_HIDDEN;
            final int columnCount = tableRule.getRowCount();
            if(columnCount!=0){
                showDialog = Messages.showYesNoDialog("是否清空检查规则(需要另外添加,如不添加将导致不能检查)", "清空检查规则", UIUtil.getWarningIcon());
            }
            //yes
            if(showDialog==0){
                CheckDataUtil.ruleClear();
                CheckDataUtil.refreshData();
            }
        });
        btnResetResult.addActionListener(e->{

            int showDialog=DIALOG_HIDDEN;
            final int columnCount = tableResult.getRowCount();
            if(columnCount!=0){
                showDialog = Messages.showYesNoDialog("是否清空检查结果", "清空检查结果", UIUtil.getWarningIcon());
            }
            //yes
            if(showDialog==0){
                CheckDataUtil.resultClear();
            }
        });
        //列表选中事件
        tableResult.getSelectionModel().addListSelectionListener(e -> {
            final int selectedRow = tableResult.getSelectedRow();
            try {
                final TableModel resultModel = tableResult.getModel();
                //组装数据实体
                final GitDiffCmd gitDiffCmd = Windowhandler.buildGitDiffCmd(resultModel, selectedRow);
                //跳转
                if(Objects.nonNull(gitDiffCmd.getFile())){
                    OpenFileDescriptor descriptor = new OpenFileDescriptor(project, Objects.requireNonNull(gitDiffCmd.getFile()), gitDiffCmd.getErrorLineNumber()-1, 0);
                    descriptor.navigate(true);
                    Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
                    if(editor==null){
                        logger.warning("editor为空");
                    }else{
                        logger.info("打印选中");
                        //选中检查内容
                        editor.getSelectionModel().selectLineAtCaret();
                        final String selectedText = editor.getSelectionModel().getSelectedText();
                        logger.info("打印选中:{"+selectedText+"}");
                    }
                }
            }catch (Exception ex){
                logger.severe("打开文件失败"+tableResult.getModel());
                ex.printStackTrace();
            }
        });
    }

    public JPanel getJcontent() {

        //添加新扫描类型
        btnNewRule.addActionListener(e->{
            AddRuleDialog addDialog=new AddRuleDialog();
            addDialog.setVisible(true);
        });
        //添加新扫描类型
        btnNewIgnore.addActionListener(e->{
            AddIgnoreDialog addDialog=new AddIgnoreDialog();
            addDialog.setVisible(true);
        });

        return windowPanel;
    }


    private void init(){

        VcsCheckSettingsState instance = VcsCheckSettingsState.getInstance();
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
