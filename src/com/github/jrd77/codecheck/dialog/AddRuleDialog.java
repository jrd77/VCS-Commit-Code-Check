package com.github.jrd77.codecheck.dialog;

import com.github.jrd77.codecheck.data.CheckDataUtils;
import com.github.jrd77.codecheck.data.InterUtil;
import com.github.jrd77.codecheck.data.model.MatchRule;
import com.github.jrd77.codecheck.data.model.RuleTypeEnum;
import com.github.jrd77.codecheck.util.StrUtil;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.ScreenUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.logging.Logger;

public class AddRuleDialog extends JDialog {

    private static final Logger logger = Logger.getLogger(AddRuleDialog.class.getName());

    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField ruleText;
    private JRadioButton regexpRadio;
    private JRadioButton strMatchRadio;
    private JLabel addDialogTitle;
    private JLabel contentLabel;
    private JLabel typeLabel;
    private JTextField remarkContent;


    public AddRuleDialog() {

        initDialogSize();
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        //国际化
        initComponentText();
        buttonOK.addActionListener(e -> onOK());


        buttonCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });
        regexpRadio.setSelected(true);
        regexpRadio.addChangeListener(e->selectedRegexpRadio());
        strMatchRadio.addChangeListener(e->selectedStrMatchRadio());

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
        logger.info(InterUtil.getValue("logs.param")+ruleText.getText()+",regexpRadio type:"+regexpRadio.isSelected());

        if(StrUtil.isBlank(ruleText.getText())){
            Messages.showErrorDialog(InterUtil.getValue("logs.validate.blank"),InterUtil.getValue("logs.validate.addfailed"));
            return;
        }
        if(!strMatchRadio.isSelected() && !regexpRadio.isSelected()){
            Messages.showErrorDialog(InterUtil.getValue("logs.validate.mustBeSelectedType"),InterUtil.getValue("logs.validate.addfailed"));
            return;
        }
        if(strMatchRadio.isSelected() && regexpRadio.isSelected()){
            Messages.showErrorDialog(InterUtil.getValue("logs.validate.mustBeNotSelectAll"),InterUtil.getValue("logs.validate.addfailed"));
            return;
        }
        MatchRule matchRule=new MatchRule();
        matchRule.setRule(ruleText.getText());
        matchRule.setRuleType(regexpRadio.isSelected()?RuleTypeEnum.REGEXP:RuleTypeEnum.STR_MATCH);
        final boolean b = CheckDataUtils.addCodeMatch(matchRule);
        if(!b){
            Messages.showErrorDialog(InterUtil.getValue("logs.validate.formatfailed"),InterUtil.getValue("logs.validate.addfailed"));
            return;
        }else{
            CheckDataUtils.refreshData();
        }
        dispose();
    }

    private void onCancel() {
        // 必要时在此处添加您的代码
//        dispose();
        this.setVisible(false);
    }

    public static void main(String[] args) {
        AddRuleDialog dialog = new AddRuleDialog();
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
        this.setLocation(Double.valueOf(x).intValue(), Double.valueOf(y).intValue());
        final int widthDialog = Double.valueOf(0.2 * width).intValue();
        final int heightDialog = Double.valueOf(0.2 * height).intValue();
        this.setSize(widthDialog, heightDialog);
        int heightRemark = this.remarkContent.getHeight();
        int widthRemark = this.remarkContent.getWidth();
        this.remarkContent.setSize(heightRemark, widthRemark / 2);
    }

    private void selectedRegexpRadio(){

        boolean selected = this.regexpRadio.isSelected();
        this.strMatchRadio.setSelected(!selected);
    }
    private void selectedStrMatchRadio(){

        boolean selected = this.strMatchRadio.isSelected();
        this.regexpRadio.setSelected(!selected);
    }

    private void initComponentText(){

//        ruleText.setToolTipText(InterUtil.getValue("show.content.dialog.btnAddRule.ruleText.text"));
        regexpRadio.setText(InterUtil.getValue("show.content.dialog.btnAddRule.regexpRadio.text"));
        strMatchRadio.setText(InterUtil.getValue("show.content.dialog.btnAddRule.strMatchRadio.text"));
        addDialogTitle.setText(InterUtil.getValue("show.content.dialog.btnAddRule.addDialogTitle.text"));
        contentLabel.setText(InterUtil.getValue("show.content.dialog.btnAddRule.contentLabel.text"));
        typeLabel.setText(InterUtil.getValue("show.content.dialog.btnAddRule.typeLabel.text"));
    }
}
