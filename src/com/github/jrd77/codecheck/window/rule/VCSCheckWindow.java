package com.github.jrd77.codecheck.window.rule;

import com.github.jrd77.codecheck.data.CheckDataUtils;
import com.github.jrd77.codecheck.data.InterUtil;
import com.github.jrd77.codecheck.data.model.*;
import com.github.jrd77.codecheck.data.persistent.VcsCheckSettingsState;
import com.github.jrd77.codecheck.data.save.SaveInterface;
import com.github.jrd77.codecheck.data.save.XmlFileSaveImpl;
import com.github.jrd77.codecheck.dialog.AddIgnoreDialog;
import com.github.jrd77.codecheck.dialog.AddRuleDialog;
import com.github.jrd77.codecheck.dialog.CheckinDialog;
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

    private static final int DIALOG_HIDDEN = 1;
    private static final int DIALOG_SHOW = 0;

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
    private JScrollPane resultPane;
    private static VcsCheckSettingsState instance = VcsCheckSettingsState.getInstance();
    private JPopupMenu popupMenuCodeMatch;
    private JPopupMenu popupMenuFileMatch;
    private NotificationGroup notificationGroup = NotificationGroup.toolWindowGroup("checkNotificationId", "PreCommitCodeWindow");
    private static SaveInterface saveInterface = XmlFileSaveImpl.getInstance();

    public VCSCheckWindow(Project project, ToolWindow toolWindow) {
        getInstance(project);
    }

    public VCSCheckWindow(Project project) {

        getInstance(project);
    }

    public VCSCheckWindow getInstance(Project project) {
        init();
        //国际化显示
        initComponentText();
        createCodeMatchPopupMenu();
        createFileMatchPopupMenu();
        tableIgnore.setName("tableIgnore");
        tableRule.setName("tableRule");
        btnCheck.addActionListener(e -> {
            //重新检查
            this.btnCheckPress(project);
        });
        btnResetIgnore.addActionListener(e -> {

            btnResetIgnorePress();
        });
        btnResetRule.addActionListener(e -> {

            btnResetRule();
        });
        btnResetResult.addActionListener(e -> {

            btnResetResultPress();
//            btnNewRuleTemp();
        });
        //列表选中事件
        tableResult.getSelectionModel().addListSelectionListener(e -> {
            btnTableResultRightClick(project);
        });
        return this;
    }
    public JPanel getJcontent() {

        //添加新扫描类型
        btnNewRule.addActionListener(e -> {
            AddRuleDialog addDialog = new AddRuleDialog();
            addDialog.setVisible(true);
        });
        //添加新扫描类型
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


    private void init() {

        VcsCheckSettingsState instance = VcsCheckSettingsState.getInstance();
        //是否init,否 进行初始化
        if (BooleanUtil.isNotTrue(instance.openCheck)) {
            CheckDataUtils.initCheckFileTypeList();
            instance.openCheck = Boolean.TRUE;
        }
        try {
            initIgnoreTable();
            initRuleTable();
            initResultTable(tableResult);
            CheckDataUtils.refreshData();
        } catch (Exception e) {
            logger.severe("发生异常,开始初始化");
            e.printStackTrace();
        }
    }


    /**
     * 初始化忽略规则表格
     */
    private void initIgnoreTable() {
        this.tableIgnore.setModel(WindowSetting.TABLE_MODEL_IGNORE);
        this.tableIgnore.setEnabled(true);
        final TableColumnModel columnModel = tableIgnore.getColumnModel();
        int index = 0;
        final int width = columnModel.getColumn(index).getPreferredWidth();
        final int globalWidth = columnModel.getColumnCount() * width;
        columnModel.getColumn(index++).setPreferredWidth(globalWidth / 10);
        double contentWidth = globalWidth - (globalWidth * 0.1);
        columnModel.getColumn(index++).setPreferredWidth((int) contentWidth / 2);
        columnModel.getColumn(index).setPreferredWidth((int) contentWidth / 2);
    }

    /**
     * 初始化匹配规则表格
     */
    private void initRuleTable() {
        this.tableRule.setModel(WindowSetting.TABLE_MODEL_RULE);
        this.tableRule.setEnabled(true);
        final TableColumnModel columnModel = tableRule.getColumnModel();
        int index = 0;
        final int width = columnModel.getColumn(index).getPreferredWidth();
        final int globalWidth = columnModel.getColumnCount() * width;
        columnModel.getColumn(index++).setPreferredWidth(globalWidth / 10);
        columnModel.getColumn(index++).setPreferredWidth(globalWidth / 10);
        double contentWidth = globalWidth - (globalWidth * 0.3);
        columnModel.getColumn(index++).setPreferredWidth((int) contentWidth);
        columnModel.getColumn(index).setPreferredWidth(globalWidth / 10);
    }

    /**
     * 初始化忽略规则表格
     */
    private void initResultTable(JTable jTable) {
        jTable.setModel(WindowSetting.TABLE_MODEL_RESULT);
        jTable.setEnabled(true);
        final TableColumnModel columnModel = jTable.getColumnModel();
        int index = 0;
        final int width = columnModel.getColumn(index).getPreferredWidth();
        final int globalWidth = columnModel.getColumnCount() * width;
        columnModel.getColumn(index++).setPreferredWidth(globalWidth / 10);
        double contentWidth = globalWidth - (globalWidth * 0.1);
        columnModel.getColumn(index++).setPreferredWidth((int) contentWidth / 2);
        columnModel.getColumn(index).setPreferredWidth((int) contentWidth / 2);
    }

    private void initComponentText() {

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
     * 右键菜单
     */
    private void createCodeMatchPopupMenu() {
        popupMenuCodeMatch = new JPopupMenu();

        JMenuItem delMenItem = new JMenuItem();
        delMenItem.setText(InterUtil.getValue("show.component.popupMenuCodeMatch.name"));
        delMenItem.addActionListener(evt -> {
            int selectedRow = tableRule.getSelectedRow();
            String codeMatchContent = String.valueOf(tableRule.getModel().getValueAt(selectedRow, 1));
            String codeMatchType = String.valueOf(tableRule.getModel().getValueAt(selectedRow, 3));
            Boolean aBoolean = saveInterface.removeCodeMatch(new CodeMatchModel(codeMatchContent, RuleTypeEnum.fromName(codeMatchType)));
            if (aBoolean) {
                CheckDataUtils.refreshData();
            }
            String msg = aBoolean ? InterUtil.getValue("show.component.notification.del.ok") : InterUtil.getValue("show.component.notification.del.failed");
            MessageType messageType = aBoolean ? MessageType.INFO : MessageType.WARNING;
            Notification notification = notificationGroup.createNotification(msg, messageType);
            Notifications.Bus.notify(notification);
        });
        popupMenuCodeMatch.add(delMenItem);
    }

    /**
     * 右键菜单
     */
    private void createFileMatchPopupMenu() {
        popupMenuFileMatch = new JPopupMenu();

        JMenuItem delMenItem = new JMenuItem();
        delMenItem.setText(InterUtil.getValue("show.component.popupMenuFileMatch.name"));
        delMenItem.addActionListener(evt -> {
            int selectedRow = tableIgnore.getSelectedRow();
            String fileMatchContent = String.valueOf(tableIgnore.getModel().getValueAt(selectedRow, 1));
            String fileMatchType = String.valueOf(tableIgnore.getModel().getValueAt(selectedRow, 2));
            Boolean aBoolean = saveInterface.removeFileMatch(new FileMatchModel(fileMatchContent, RuleTypeEnum.fromName(fileMatchType)));
            if (aBoolean) {
                CheckDataUtils.refreshData();
            }
            String msg = aBoolean ? InterUtil.getValue("show.component.notification.del.ok") : InterUtil.getValue("show.component.notification.del.failed");
            MessageType messageType = aBoolean ? MessageType.INFO : MessageType.WARNING;
            Notification notification = notificationGroup.createNotification(msg, messageType);
            Notifications.Bus.notify(notification);

        });
        popupMenuFileMatch.add(delMenItem);
    }


    private void jTable1MouseClicked(java.awt.event.MouseEvent evt, JTable table) {

        mouseRightButtonClick(evt, table);
    }

    //鼠标右键点击事件
    private void mouseRightButtonClick(java.awt.event.MouseEvent evt, JTable table) {
        //判断是否为鼠标的BUTTON3按钮，BUTTON3为鼠标右键
        if (evt.getButton() == java.awt.event.MouseEvent.BUTTON3) {
            //通过点击位置找到点击为表格中的行
            int focusedRowIndex = table.rowAtPoint(evt.getPoint());
            if (focusedRowIndex == -1) {
                return;
            }
            //将表格所选项设为当前右键点击的行
            table.setRowSelectionInterval(focusedRowIndex, focusedRowIndex);
            evt.setSource(table);
            //弹出菜单
            if (table.getName().equals(tableRule.getName())) {
                popupMenuCodeMatch.show(table, evt.getX(), evt.getY());
            } else {
                popupMenuFileMatch.show(table, evt.getX(), evt.getY());
            }
        }
    }

    public JScrollPane getResultPane() {
        this.resultPane.setViewportView(tableResult);
        this.resultPane.setVisible(true);
        return this.resultPane;
    }

    public void btnNewRuleTemp() {

        CheckinDialog dialog = new CheckinDialog();
        dialog.setContentPane(this.getResultPane());
        dialog.pack();
        dialog.setVisible(true);
    }

    /**
     * 重新检查
     *
     * @param project
     */
    public void btnCheckPress(Project project) {
        instance.oldDataUpdated = true;
        logger.info("" + instance.getState());
        System.out.println(instance.getState());

        //检查流程发起
        CodeMatchReq codeMatchReq = new CodeMatchReq();
        codeMatchReq.setCheckSource(CheckSourceEnum.TOOL_WINDOW);
        codeMatchReq.setProject(project);
        //配置参数
        CodeMatchContext context = CodeMatchService.convertCodeMatchContext(codeMatchReq);
        //检查
        ResultObject<List<CodeMatchResult>> resultObject = CodeMatchService.startCodeMatch(context);
        if (resultObject.getOk() != ResultObject.ok().getOk()) {
            Notification notification = notificationGroup.createNotification(resultObject.getMsg(), MessageType.WARNING);
            Notifications.Bus.notify(notification);
        } else {
            CheckDataUtils.refreshResultData(resultObject.getData());
        }
    }

    public void btnResetIgnorePress() {
        int yesNoDialog = DIALOG_HIDDEN;
        final int columnCount = tableIgnore.getRowCount();
        if (columnCount != 0) {
            yesNoDialog = Messages.showYesNoDialog(InterUtil.getValue("show.content.window.btnResetIgnore.dialog.message"), InterUtil.getValue("show.content.window.btnResetIgnore.dialog.title"), UIUtil.getWarningIcon());
        }
        //yes
        if (yesNoDialog == 0) {
            CheckDataUtils.clearFileMatch();
            CheckDataUtils.refreshData();
        }
    }

    private void btnResetRule() {
        int showDialog = DIALOG_HIDDEN;
        final int columnCount = tableRule.getRowCount();
        if (columnCount != 0) {
            showDialog = Messages.showYesNoDialog(InterUtil.getValue("show.content.window.btnResetRule.dialog.message"), InterUtil.getValue("show.content.window.btnResetRule.dialog.title"), UIUtil.getWarningIcon());
        }
        //yes
        if (showDialog == 0) {
            CheckDataUtils.clearCodeMatch();
            CheckDataUtils.refreshData();
        }
    }

    private void btnResetResultPress() {
        int showDialog = DIALOG_HIDDEN;
        final int columnCount = tableResult.getRowCount();
        if (columnCount != 0) {
            showDialog = Messages.showYesNoDialog(InterUtil.getValue("show.content.window.btnResetResult.dialog.message"), InterUtil.getValue("show.content.window.btnResetResult.dialog.title"), UIUtil.getWarningIcon());
        }
        //yes
        if (showDialog == 0) {
            CheckDataUtils.resultClear();
        }
    }

    private void btnTableResultRightClick(Project project) {
        final int selectedRow = tableResult.getSelectedRow();
        if (selectedRow < 0) {
            return;
        }
        try {
            final TableModel resultModel = tableResult.getModel();
            //组装数据实体
            final CodeMatchResult gitDiffCmd = Windowhandler.buildGitDiffCmd(resultModel, selectedRow);
            //跳转
            if (Objects.nonNull(gitDiffCmd.getFile())) {
                OpenFileDescriptor descriptor = new OpenFileDescriptor(project, Objects.requireNonNull(gitDiffCmd.getFile()), gitDiffCmd.getErrorLineNumber() - 1, 0);
                descriptor.navigate(true);
                Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
                if (editor == null) {
                    logger.warning(InterUtil.getValue("logs.validate.editorIsNull"));
                } else {
                    //选中检查内容
                    editor.getSelectionModel().selectLineAtCaret();
                }
            }
        } catch (Exception ex) {
            logger.severe(String.format(InterUtil.getValue("logs.validate.openFileFaliedPrintFileName"), tableResult.getModel()));
            ex.printStackTrace();
        }
    }
}
