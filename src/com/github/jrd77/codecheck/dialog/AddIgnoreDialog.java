package com.github.jrd77.codecheck.dialog;

import com.github.jrd77.codecheck.data.CheckDataUtil;
import com.github.jrd77.codecheck.data.MatchRule;
import com.github.jrd77.codecheck.data.RuleTypeEnum;
import com.github.jrd77.codecheck.util.StringUtils;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.ScreenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AddIgnoreDialog extends JDialog {

    private static final Logger logger = LoggerFactory.getLogger(AddIgnoreDialog.class.getName());
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField textFieldContent;
    private JTextPane textPaneTip;
//    private JTextField textFieldComment;

    public AddIgnoreDialog() {

        initDialogSize();
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK());

        buttonCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // 单击 X 时调用 onCancel()
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // 遇到 ESCAPE 时调用 onCancel()
//        contentPane.registerKeyboardAction(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                onCancel();
//            }
//        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        // 在此处添加您的代码
        // 在此处添加代码
        logger.info("参数:ruleText"+textFieldContent.getText());

        if(StringUtils.isBlank(textFieldContent.getText())){
            Messages.showErrorDialog("添加失败,不能为空","添加失败");
            return;
        }
        String contentText = textFieldContent.getText();
        final boolean b = CheckDataUtil.addIgnore(contentText);
        if(!b){
            Messages.showErrorDialog("添加失败,请检查是否重复或者有特殊格式","添加失败");
            return;
        }else{
            CheckDataUtil.refreshData();
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
}
