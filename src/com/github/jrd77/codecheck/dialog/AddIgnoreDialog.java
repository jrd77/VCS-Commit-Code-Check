package com.github.jrd77.codecheck.dialog;

import com.github.jrd77.codecheck.data.CheckDataUtils;
import com.github.jrd77.codecheck.data.InterUtil;
import com.github.jrd77.codecheck.data.model.FileMatchModel;
import com.github.jrd77.codecheck.data.model.RuleTypeEnum;
import com.github.jrd77.codecheck.util.StrUtil;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.ScreenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class AddIgnoreDialog extends JDialog {

    private static final Logger logger = LoggerFactory.getLogger(AddIgnoreDialog.class.getName());
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField textFieldContent;
    private JTextPane textPaneTip;
    private JLabel addDialogTitle;
    private JLabel descLabel;
//    private JTextField textFieldComment;

    public AddIgnoreDialog() {

        initDialogSize();
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        initComponentText();

        buttonOK.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());

        // 单击 X 时调用 onCancel()
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

    }

    private void onOK() {
        // 在此处添加您的代码
        // 在此处添加代码
        logger.info(InterUtil.getValue("logs.param") + textFieldContent.getText());

        if (StrUtil.isBlank(textFieldContent.getText())) {
            Messages.showErrorDialog(InterUtil.getValue("logs.validate.blank"), InterUtil.getValue("logs.validate.addfailed"));
            return;
        }
        String contentText = textFieldContent.getText();
        FileMatchModel fileMatchModel = new FileMatchModel();
        fileMatchModel.setRule(contentText);
        fileMatchModel.setRuleType(RuleTypeEnum.REGEXP);
        final boolean b = CheckDataUtils.addFileMatch(fileMatchModel);
        if (!b) {
            Messages.showErrorDialog(InterUtil.getValue("logs.validate.formatfailed"), InterUtil.getValue("logs.validate.addfailed"));
            return;
        } else {
            CheckDataUtils.refreshData();
        }
        dispose();
    }

    private void onCancel() {
        // 必要时在此处添加您的代码
        dispose();
    }

    public static void main(String[] args) {
        AddIgnoreDialog dialog = new AddIgnoreDialog();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    private void initDialogSize(){
        final Rectangle rectangle = ScreenUtil.getAllScreensRectangle();
        final double width = rectangle.getWidth();
        final double height = rectangle.getHeight();
        final double x = width * 0.5 - 0.1 * width;
        final double y = height * 0.5 - 0.1 * height;
        this.setLocation(Double.valueOf(x).intValue(),Double.valueOf(y).intValue());
        final int widthDialog = Double.valueOf(0.2 * width).intValue();
        final int heightDialog = Double.valueOf(0.2 * height).intValue();
        this.setSize(widthDialog,heightDialog);
    }

    private void initComponentText(){

//        textFieldContent.setText(InterUtil.getValue("show.content.dialog.btnNewIgnore.textFieldContent.text"));
        textPaneTip.setText(InterUtil.getValue("show.content.dialog.btnNewIgnore.textPaneTip.text"));
        addDialogTitle.setText(InterUtil.getValue("show.content.dialog.btnNewIgnore.addContentLabel.text"));
        descLabel.setText(InterUtil.getValue("show.content.dialog.btnNewIgnore.descLabel.text"));
    }
}
