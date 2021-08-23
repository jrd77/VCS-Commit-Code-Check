package com.github.jrd77.codecheck.window.rule;

import com.github.jrd77.codecheck.data.CheckDataUtils;
import com.github.jrd77.codecheck.data.InterUtil;
import com.github.jrd77.codecheck.data.VcsCheckSettingsState;
import com.github.jrd77.codecheck.data.model.GitDiffCmd;
import com.github.jrd77.codecheck.data.save.DataCenter;
import com.github.jrd77.codecheck.data.save.SaveInterface;
import com.github.jrd77.codecheck.dialog.AddIgnoreDialog;
import com.github.jrd77.codecheck.dialog.AddRuleDialog;
import com.github.jrd77.codecheck.handler.CheckCommitFilter;
import com.github.jrd77.codecheck.intellij.compoent.MyButton;
import com.github.jrd77.codecheck.intellij.compoent.MyTable;
import com.github.jrd77.codecheck.util.BooleanUtil;
import com.github.jrd77.codecheck.util.ResultObject;
import com.github.jrd77.codecheck.util.VcsUtil;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.Notifications;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.util.ui.UIUtil;

import javax.swing.*;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

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
    private JLabel tableRuleTitle;
    private JLabel tableFileTitle;
    private JLabel tableResultTitle;
    private NotificationGroup notificationGroup = NotificationGroup.toolWindowGroup("checkNotificationId","PreCommitCodeWindow");

    public VCSCheckWindow(Project project, ToolWindow toolWindow) {

        init();
        //国际化显示
        initComponentText();

        btnCheck.addActionListener(e->{
            int yesNoDialog=DIALOG_HIDDEN;
            final int columnCount = tableResult.getRowCount();
            if(columnCount!=0){
                yesNoDialog = Messages.showYesNoDialog(InterUtil.getValue("show.content.window.btnCheck.dialog.message"), InterUtil.getValue("show.content.window.btnCheck.dialog.title"), UIUtil.getWarningIcon());
            }
            if(yesNoDialog==0||columnCount==0){
                CheckDataUtils.resultClear();
                //其他检查
                ResultObject resultObject = CheckCommitFilter.checkCommitPre();
                if(resultObject.getOk()!=0){
                    Messages.showMessageDialog(resultObject.getMsg(),InterUtil.getValue("show.content.window.otherCheck.dialog.message"), UIUtil.getWarningIcon());
                    return;
                }
                //开始检查
                List<GitDiffCmd> gitDiffCmds = VcsUtil.checkMainFlow(project);
                if(gitDiffCmds==null||gitDiffCmds.size()==0){

                    Notification notification = notificationGroup.createNotification(InterUtil.getValue("show.component.notification.checkNotificationId.content"), MessageType.WARNING);
                    Notifications.Bus.notify(notification);
                }
            }
        });
        btnResetIgnore.addActionListener(e->{

            int yesNoDialog=DIALOG_HIDDEN;
            final int columnCount = tableIgnore.getRowCount();
            if(columnCount!=0){
                yesNoDialog = Messages.showYesNoDialog(InterUtil.getValue("show.content.window.btnResetIgnore.dialog.message"), InterUtil.getValue("show.content.window.btnResetIgnore.dialog.title"), UIUtil.getWarningIcon());
            }
            //yes
            if(yesNoDialog==0){
                CheckDataUtils.clearFileMatch();
                CheckDataUtils.refreshData();
            }
        });
        btnResetRule.addActionListener(e->{

            int showDialog=DIALOG_HIDDEN;
            final int columnCount = tableRule.getRowCount();
            if(columnCount!=0){
                showDialog = Messages.showYesNoDialog(InterUtil.getValue("show.content.window.btnResetRule.dialog.message"), InterUtil.getValue("show.content.window.btnResetRule.dialog.title"), UIUtil.getWarningIcon());
            }
            //yes
            if(showDialog==0){
                CheckDataUtils.clearCodeMatch();
                CheckDataUtils.refreshData();
            }
        });
        btnResetResult.addActionListener(e->{

            int showDialog=DIALOG_HIDDEN;
            final int columnCount = tableResult.getRowCount();
            if(columnCount!=0){
                showDialog = Messages.showYesNoDialog(InterUtil.getValue("show.content.window.btnResetResult.dialog.message"), InterUtil.getValue("show.content.window.btnResetResult.dialog.title"), UIUtil.getWarningIcon());
            }
            //yes
            if(showDialog==0){
                CheckDataUtils.resultClear();
            }
        });
        //列表选中事件
        tableResult.getSelectionModel().addListSelectionListener(e -> {
            final int selectedRow = tableResult.getSelectedRow();
            if(selectedRow<0){
                return;
            }
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
                        logger.warning(InterUtil.getValue("logs.validate.editorIsNull"));
                    }else{
                        //选中检查内容
                        editor.getSelectionModel().selectLineAtCaret();
                        final String selectedText = editor.getSelectionModel().getSelectedText();
                        logger.info(String.format(InterUtil.getValue("logs.common.printSelected"),selectedText));

                    }
                }
            }catch (Exception ex){
                logger.severe(String.format(InterUtil.getValue("logs.validate.openFileFaliedPrintFileName"),tableResult.getModel()));
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
            CheckDataUtils.initCheckFileTypeList();
            instance.openCheck=Boolean.TRUE;
        }
        try{
            initIgnoreTable();
            initRuleTable();
            initResultTable();
            CheckDataUtils.refreshData();
        }catch (Exception e){
            logger.severe("发生异常,开始初始化");
            e.printStackTrace();
            SaveInterface saveInterface= DataCenter.getInstance;
            saveInterface.clearCodeMatch();
            saveInterface.clearFileMatch();
            CheckDataUtils.refreshData();
        }
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

    private void initComponentText(){

        btnNewRule.setText(InterUtil.getValue("show.component.btnNewRule.text"));
        btnResetRule.setText(InterUtil.getValue("show.component.btnResetRule.text"));
        btnNewIgnore.setText(InterUtil.getValue("show.component.btnNewIgnore.text"));
        btnResetIgnore.setText(InterUtil.getValue("show.component.btnResetIgnore.text"));
        btnCheck.setText(InterUtil.getValue("show.component.btnCheck.text"));
        btnResetResult.setText(InterUtil.getValue("show.component.btnResetResult.text"));
        tableRuleTitle.setText(InterUtil.getValue("show.component.tableRuleTitle.text"));
        tableFileTitle.setText(InterUtil.getValue("show.component.tableFileTitle.text"));
        tableResultTitle.setText(InterUtil.getValue("show.component.tableResultTitle.text"));
    }
}
