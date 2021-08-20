package com.github.jrd77.codecheck.util;


import com.github.jrd77.codecheck.data.model.GitDiffCmd;

import java.util.List;

public class HtmlUtil {

    public static final String tableStart = "<table>";
    public static final String tableEnd = "</table>";
    public static final String tableTh = "    <tr>\n" +
            "        <th>no</th>\n" +
            "        <th>match</th>\n" +
            "        <th>lineStr</th>\n" +
            "        <th>lineNumber</th>\n" +
            "        <th>filePath</th>\n" +
            "    </tr>";
    public static final String tableTdTemplate = "    <tr>\n" +
            "        <td>%s</td>\n" +
            "        <td>%s</td>\n" +
            "        <td>%s</td>\n" +
            "        <td>%s</td>\n" +
            "        <td>%s</td>\n" +
            "    </tr>";

    public static String buildHtmlTable(List<GitDiffCmd> diffCmdList) {

        StringBuilder result = new StringBuilder(tableStart);
        result.append(tableTh);
        int index = 0;
        for (GitDiffCmd gitDiffCmd : diffCmdList) {
            ++index;
            final String format = String.format(tableTdTemplate, index,
                    gitDiffCmd.getErrorMatch(),
                    gitDiffCmd.getErrorLineStr(),
                    gitDiffCmd.getErrorLineNumber(),
                    gitDiffCmd.getFilePath());
            result.append(format);
        }
        result.append(tableEnd);
        return result.toString();
    }

}
