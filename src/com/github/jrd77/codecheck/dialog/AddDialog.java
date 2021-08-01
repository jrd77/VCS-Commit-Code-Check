package com.github.jrd77.codecheck.dialog;

import com.github.jrd77.codecheck.data.CheckDataUtil;
import com.github.jrd77.codecheck.data.MatchRule;
import com.github.jrd77.codecheck.data.RuleTypeEnum;
import com.github.jrd77.codecheck.window.rule.VCSCheckWindow;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.impl.CheckUtil;

import javax.swing.*;
import java.awt.event.*;
import java.util.logging.Logger;

public class AddDialog extends JDialog {

    private static final Logger logger = Logger.getLogger(AddDialog.class.getName());

    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField ruleText;
    private JRadioButton regexpRadio;
    private JRadioButton strMatchRadio;

    public AddDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });


        buttonCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // 单击十字时调用 onCancel()
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // 遇到 ESCAPE 时调用 onCancel()
        contentPane.registerKeyboardAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        // 在此处添加代码
        logger.info("参数:ruleText"+ruleText.getText()+",regexpRadio type:"+regexpRadio.isSelected());

        MatchRule matchRule=new MatchRule();
        matchRule.setRule(ruleText.getText());
        matchRule.setRuleType(RuleTypeEnum.REGEXP);
        final boolean b = CheckDataUtil.addRule(matchRule);
        if(!b){
            Messages.showErrorDialog("添加失败,请检查是否重复或者有特殊格式","添加失败");
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
        AddDialog dialog = new AddDialog();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
