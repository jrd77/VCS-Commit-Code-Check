package com.github.jrd77.codecheck.window.rule;

import com.github.jrd77.codecheck.data.CheckDataUtils;
import com.github.jrd77.codecheck.data.InterUtil;
import com.github.jrd77.codecheck.data.model.*;
import com.github.jrd77.codecheck.data.persistent.VcsCheckSettingsState;
import com.github.jrd77.codecheck.data.save.SaveInterface;
import com.github.jrd77.codecheck.data.save.XmlFileSaveImpl;
import com.github.jrd77.codecheck.dialog.AddIgnoreDialog;
import com.github.jrd77.codecheck.dialog.AddRuleDialog;
import com.github.jrd77.codecheck.intellij.compoent.MyButton;
import com.github.jrd77.codecheck.intellij.compoent.MyTable;
import com.github.jrd77.codecheck.service.CodeMatchService;
import com.github.jrd77.codecheck.util.BooleanUtil;
import com.github.jrd77.codecheck.util.ResultObject;
import com.github.jrd77.codecheck.vo.CodeMatchContext;
import com.github.jrd77.codecheck.vo.CodeMatchReq;
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
    static VcsCheckSettingsState instance = VcsCheckSettingsState.getInstance();
    private static SaveInterface saveInterface = XmlFileSaveImpl.getInstance();
    JPopupMenu popupMenuCodeMatch = new JPopupMenu();
    JPopupMenu popupMenuFileMatch = new JPopupMenu();
    JMenuItem item = new JMenuItem("??????");
    private NotificationGroup notificationGroup = NotificationGroup.toolWindowGroup("checkNotificationId", "PreCommitCodeWindow");

    public VCSCheckWindow(Project project, ToolWindow toolWindow) {

        init();
        //???????????????
        initComponentText();
        createCodeMatchPopupMenu();
        createFileMatchPopupMenu();
        tableIgnore.setName("tableIgnore");
        tableRule.setName("tableRule");
        btnCheck.addActionListener(e -> {


            instance.oldDataUpdated = true;
            System.out.println(instance.getState());

            //??????????????????
            CodeMatchReq codeMatchReq = new CodeMatchReq();
            codeMatchReq.setCheckSource(CheckSourceEnum.TOOL_WINDOW);
            codeMatchReq.setProject(project);
            //????????????
            CodeMatchContext context = CodeMatchService.convertCodeMatchContext(codeMatchReq);
            //??????
            ResultObject<List<CodeMatchResult>> resultObject = CodeMatchService.startCodeMatch(context);
            if (resultObject.getOk() != ResultObject.ok().getOk()) {
                Notification notification = notificationGroup.createNotification(resultObject.getMsg(), MessageType.WARNING);
                Notifications.Bus.notify(notification);
            } else {
                CheckDataUtils.refreshResultData(resultObject.getData());
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
        //??????????????????
        tableResult.getSelectionModel().addListSelectionListener(e -> {
            final int selectedRow = tableResult.getSelectedRow();
            if(selectedRow<0){
                return;
            }
            try {
                final TableModel resultModel = tableResult.getModel();
                //??????????????????
                final CodeMatchResult gitDiffCmd = Windowhandler.buildGitDiffCmd(resultModel, selectedRow);
                //??????
                if(Objects.nonNull(gitDiffCmd.getFile())){
                    OpenFileDescriptor descriptor = new OpenFileDescriptor(project, Objects.requireNonNull(gitDiffCmd.getFile()), gitDiffCmd.getErrorLineNumber()-1, 0);
                    descriptor.navigate(true);
                    Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
                    if(editor==null){
                        logger.warning(InterUtil.getValue("logs.validate.editorIsNull"));
                    }else{
                        //??????????????????
                        editor.getSelectionModel().selectLineAtCaret();
                    }
                }
            }catch (Exception ex){
                logger.severe(String.format(InterUtil.getValue("logs.validate.openFileFaliedPrintFileName"),tableResult.getModel()));
                ex.printStackTrace();
            }
        });
    }

    public JPanel getJcontent() {

        //?????????????????????
        btnNewRule.addActionListener(e -> {
            AddRuleDialog addDialog = new AddRuleDialog();
            addDialog.setVisible(true);
        });
        //?????????????????????
        btnNewIgnore.addActionListener(e -> {
            AddIgnoreDialog addDialog = new AddIgnoreDialog();
            addDialog.setVisible(true);
        });
        tableRule.getModel().addTableModelListener(tableRule);
        tableRule.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt, tableRule);
            }
        });
        tableIgnore.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt, tableIgnore);
            }
        });
        return windowPanel;
    }


    private void init(){

        VcsCheckSettingsState instance = VcsCheckSettingsState.getInstance();
        //??????init,??? ???????????????
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
            logger.severe("????????????,???????????????");
            e.printStackTrace();
        }
    }


    /**
     * ???????????????????????????
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
     * ???????????????????????????
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
     * ???????????????????????????
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

    /**
     * ????????????
     */
    private void createCodeMatchPopupMenu() {
        popupMenuCodeMatch = new JPopupMenu();

        JMenuItem delMenItem = new JMenuItem();
        delMenItem.setText("  ??????????????????  ");
        delMenItem.addActionListener(evt -> {
            int selectedRow = tableRule.getSelectedRow();
            String codeMatchContent = String.valueOf(tableRule.getModel().getValueAt(selectedRow, 1));
            String codeMatchType = String.valueOf(tableRule.getModel().getValueAt(selectedRow, 3));
            Boolean aBoolean = saveInterface.removeCodeMatch(new CodeMatchModel(codeMatchContent, RuleTypeEnum.fromName(codeMatchType)));
            if (aBoolean) {
                CheckDataUtils.refreshData();
            }
            String msg = aBoolean ? "????????????" : "????????????";
            MessageType messageType = aBoolean ? MessageType.INFO : MessageType.WARNING;
            Notification notification = notificationGroup.createNotification(msg, messageType);
            Notifications.Bus.notify(notification);
        });
        popupMenuCodeMatch.add(delMenItem);
    }

    /**
     * ????????????
     */
    private void createFileMatchPopupMenu() {
        popupMenuFileMatch = new JPopupMenu();

        JMenuItem delMenItem = new JMenuItem();
        delMenItem.setText("  ??????????????????  ");
        delMenItem.addActionListener(evt -> {
            int selectedRow = tableIgnore.getSelectedRow();
            String fileMatchContent = String.valueOf(tableIgnore.getModel().getValueAt(selectedRow, 1));
            String fileMatchType = String.valueOf(tableIgnore.getModel().getValueAt(selectedRow, 2));
            Boolean aBoolean = saveInterface.removeFileMatch(new FileMatchModel(fileMatchContent, RuleTypeEnum.fromName(fileMatchType)));
            if (aBoolean) {
                CheckDataUtils.refreshData();
            }
            String msg = aBoolean ? "????????????" : "????????????";
            MessageType messageType = aBoolean ? MessageType.INFO : MessageType.WARNING;
            Notification notification = notificationGroup.createNotification(msg, messageType);
            Notifications.Bus.notify(notification);

        });
        popupMenuFileMatch.add(delMenItem);
    }


    private void jTable1MouseClicked(java.awt.event.MouseEvent evt, JTable table) {

        mouseRightButtonClick(evt, table);
    }

    //????????????????????????
    private void mouseRightButtonClick(java.awt.event.MouseEvent evt, JTable table) {
        //????????????????????????BUTTON3?????????BUTTON3???????????????
        if (evt.getButton() == java.awt.event.MouseEvent.BUTTON3) {
            //????????????????????????????????????????????????
            int focusedRowIndex = table.rowAtPoint(evt.getPoint());
            if (focusedRowIndex == -1) {
                return;
            }
            //????????????????????????????????????????????????
            table.setRowSelectionInterval(focusedRowIndex, focusedRowIndex);
            evt.setSource(table);
            //????????????
            if (table.getName().equals(tableRule.getName())) {
                popupMenuCodeMatch.show(table, evt.getX(), evt.getY());
            } else {
                popupMenuFileMatch.show(table, evt.getX(), evt.getY());
            }
        }

    }
}
