package com.github.jrd77.codecheck.window.rule;

import com.github.jrd77.codecheck.data.model.CodeMatchResult;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;

import javax.swing.table.TableModel;

public class Windowhandler {

    protected static CodeMatchResult buildGitDiffCmd(TableModel resultModel, int selectedRow) {

        final Integer index = Integer.valueOf((String) resultModel.getValueAt(selectedRow, 0));
        final String errorLine = (String) resultModel.getValueAt(selectedRow, 1);
        final Integer errorLineNumber = Integer.valueOf((String) resultModel.getValueAt(selectedRow, 2));
        final String ruleMatch = (String) resultModel.getValueAt(selectedRow, 3);
        final String filePath = (String) resultModel.getValueAt(selectedRow, 4);
        final VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByPath(filePath);
        CodeMatchResult gitDiffCmd = new CodeMatchResult();
        gitDiffCmd.setErrorLineNumber(errorLineNumber);
        gitDiffCmd.setErrorMatch(ruleMatch);
        gitDiffCmd.setFilePath(filePath);
        gitDiffCmd.setErrorLineStr(errorLine);
        gitDiffCmd.setFile(virtualFile);
        return gitDiffCmd;
    }
}
