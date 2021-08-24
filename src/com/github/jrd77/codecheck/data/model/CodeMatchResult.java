package com.github.jrd77.codecheck.data.model;

import com.intellij.openapi.vfs.VirtualFile;

/**
 * @author zhen.wang
 * @description TODO
 * @date 2021/8/19 14:43
 */
public class CodeMatchResult {

    private String filePath;
    private String fileName;
    private String ext;
    private String errorLineStr;
    private Integer errorLineNumber;
    private String readLine;
    private String errorMatch;
    private VirtualFile file;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getErrorLineStr() {
        return errorLineStr;
    }

    public void setErrorLineStr(String errorLineStr) {
        this.errorLineStr = errorLineStr;
    }

    public Integer getErrorLineNumber() {
        return errorLineNumber;
    }

    public void setErrorLineNumber(Integer errorLineNumber) {
        this.errorLineNumber = errorLineNumber;
    }

    public String getReadLine() {
        return readLine;
    }

    public void setReadLine(String readLine) {
        this.readLine = readLine;
    }

    public String getErrorMatch() {
        return errorMatch;
    }

    public void setErrorMatch(String errorMatch) {
        this.errorMatch = errorMatch;
    }

    public VirtualFile getFile() {
        return file;
    }

    public void setFile(VirtualFile file) {
        this.file = file;
    }

    @Override
    public String toString() {
        return "GitDiffCmd{" +
                "filePath='" + filePath + '\'' +
                ", fileName='" + fileName + '\'' +
                ", ext='" + ext + '\'' +
                ", errorLineStr='" + errorLineStr + '\'' +
                ", errorLineNumber=" + errorLineNumber +
                ", readLine='" + readLine + '\'' +
                ", errorMatch='" + errorMatch + '\'' +
                ", file=" + file +
                '}';
    }
}
