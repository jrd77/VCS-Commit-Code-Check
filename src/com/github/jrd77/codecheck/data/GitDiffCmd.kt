package com.github.jrd77.codecheck.data

import com.intellij.openapi.vfs.VirtualFile

class GitDiffCmd {
    var filePath: String? = null
    var fileName: String? = null
    var ext: String? = null
    var errorLineStr: String? = null
    var errorLineNumber: Int? = null
    var readLine: String? = null
    var errorMatch: String? = null
    var file: VirtualFile? = null
    override fun toString(): String {
        return "GitDiffCmd{" +
                "filePath='" + filePath + '\'' +
                ", fileName='" + fileName + '\'' +
                ", ext='" + ext + '\'' +
                ", errorLineStr='" + errorLineStr + '\'' +
                ", errorLineNumber=" + errorLineNumber +
                ", readLine='" + readLine + '\'' +
                ", errorMatch='" + errorMatch + '\'' +
                ", file=" + file +
                '}'
    }
}