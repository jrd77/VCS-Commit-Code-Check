package com.github.jrd77.codecheck.data;

public class GitDiffCmd {

    String filePath;

    String fileName;

    String ext;

    private String errorLineStr;

    private Integer errorLineNumber;

    String readLine;

    private String errorMatch;

    public String getErrorMatch() {
        return errorMatch;
    }

    public void setErrorMatch(String errorMatch) {
        this.errorMatch = errorMatch;
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

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

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

    @Override
    public String toString() {
        return "GitDiffCmd{" +
                "filePath='" + filePath + '\'' +
                ", fileName='" + fileName + '\'' +
                ", ext='" + ext + '\'' +
                ", errorLineStr='" + errorLineStr + '\'' +
                ", errorLineNumber=" + errorLineNumber +
                ", readLine=" + readLine +
                '}';
    }
}