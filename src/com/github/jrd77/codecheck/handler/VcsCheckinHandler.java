package com.github.jrd77.codecheck.handler;

import com.github.jrd77.codecheck.data.*;
import com.github.jrd77.codecheck.util.*;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vcs.CheckinProjectPanel;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.ChangeListManager;
import com.intellij.openapi.vcs.changes.ContentRevision;
import com.intellij.openapi.vcs.changes.LocalChangeList;
import com.intellij.openapi.vcs.checkin.CheckinHandler;
import com.intellij.openapi.vcs.ui.RefreshableOnComponent;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ui.UIUtil;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class VcsCheckinHandler extends CheckinHandler {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(VcsCheckinHandler.class.getName());
    private static final String CHECKER_STATE_KEY = "COMMIT_CHECKER_STATE_KEY";
    private final CheckinProjectPanel panel;


    public VcsCheckinHandler(CheckinProjectPanel panel) {
        this.panel = panel;
    }

    public static boolean isCheckMessageEnabled() {
        return PropertiesComponent.getInstance().getBoolean(CHECKER_STATE_KEY, true);
    }

    @Override
    public RefreshableOnComponent getBeforeCheckinConfigurationPanel() {
        final JCheckBox checkBox = new JCheckBox(InterUtil.getValue("show.content.vcs.checkinhandler.name"));

        return new RefreshableOnComponent() {

            @Override
            public JComponent getComponent() {
                JPanel root = new JPanel(new BorderLayout());
                root.add(checkBox, "West");
                return root;
            }

            @Override
            public void refresh() {
            }

            @Override
            public void saveState() {
                PropertiesComponent.getInstance().setValue(CHECKER_STATE_KEY, checkBox.isSelected());
            }

            @Override
            public void restoreState() {
                checkBox.setSelected(isCheckMessageEnabled());
            }
        };
    }



    /**
     * 在提交之前检查
     *
     * @return
     */
    @Override
    public ReturnResult beforeCheckin() {

        logger.info(InterUtil.getValue("logs.common.startcheck"));
        if (!isCheckMessageEnabled()) {
            logger.warning(InterUtil.getValue("logs.validate.notopencheck"));
            return super.beforeCheckin();
        }
        //其他检查
        ResultObject resultObject = CheckCommitFilter.checkCommitPre();
        if(resultObject.getOk()!=0){
            return super.beforeCheckin();
        }
        //检查项目中是否有符合规则的提交内容
        final List<GitDiffCmd> cmdList = VcsUtil.checkMainFlow(panel.getProject());
        if (cmdList != null && cmdList.size() > 0) {
            final String htmlTable = HtmlUtil.buildHtmlTable(cmdList);
            String html = "<html><head>" + UIUtil.getCssFontDeclaration(UIUtil.getLabelFont()) + "</head><body>" +
                    "<br><h3>"+InterUtil.getValue("show.content.vcs.checkinhandler.message.whether")+"</h3>" +
                    "<br>" +
                    htmlTable +
                    "</body></html>";
            int yesOrNo = Messages.showYesNoDialog(html,
                    InterUtil.getValue("show.content.vcs.checkinhandler.message.title"),
                    UIUtil.getErrorIcon());
            return yesOrNo == 0 ? ReturnResult.COMMIT : ReturnResult.CANCEL;
        }
        return ReturnResult.COMMIT;
    }
}
