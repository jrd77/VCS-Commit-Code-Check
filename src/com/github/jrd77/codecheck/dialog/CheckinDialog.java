package com.github.jrd77.codecheck.dialog;

import com.github.jrd77.codecheck.data.InterUtil;
import com.intellij.ui.ScreenUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class CheckinDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JScrollPane resultJane;

    private int yesOrNo;

    public CheckinDialog() {

        initDialogSize();
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        //国际化
        initComponentText();
        buttonOK.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());

        // 点击 X 时调用 onCancel()
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // 遇到 ESCAPE 时调用 onCancel()
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        // 在此处添加您的代码
        this.yesOrNo = 1;
        dispose();
    }

    private void onCancel() {
        // 必要时在此处添加您的代码
        this.yesOrNo = 0;
        dispose();
    }

    private void initComponentText() {

//        ruleText.setToolTipText(InterUtil.getValue("show.content.dialog.btnAddRule.ruleText.text"));
        buttonOK.setText(InterUtil.getValue("show.component.btn.ok"));
        buttonCancel.setText(InterUtil.getValue("show.component.btn.cancel"));
    }

    public static void main(String[] args) {
        CheckinDialog dialog = new CheckinDialog();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    public int getYesOrNo() {
        return yesOrNo;
    }

    private void initDialogSize() {
        final Rectangle rectangle = ScreenUtil.getAllScreensRectangle();
        final double width = rectangle.getWidth();
        final double height = rectangle.getHeight();
        final double x = width * 0.5 - 0.1 * width;
        final double y = height * 0.5 - 0.1 * height;
        this.setLocation(Double.valueOf(x).intValue(), Double.valueOf(y).intValue());
        final int widthDialog = Double.valueOf(0.2 * width).intValue();
        final int heightDialog = Double.valueOf(0.2 * height).intValue();
        this.setSize(widthDialog, heightDialog);
        int heightRemark = this.contentPane.getHeight();
        int widthRemark = this.contentPane.getWidth();
        this.contentPane.setSize(widthRemark / 2, heightRemark);
    }

    public void setResultJane(JScrollPane pane) {
        contentPane.add(pane);
    }
}
