package com.github.jrd77.codecheck.window.rule;

import com.github.jrd77.codecheck.data.InterUtil;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.util.Vector;
import java.util.logging.Logger;

public class WindowSetting {

    private static final Logger logger = Logger.getLogger(WindowSetting.class.getName());
    private static final Vector<String> COLUMN_NAME_RULE = new Vector<>();
    private static final Vector<String> COLUMN_NAME_IGNORE = new Vector<>();
    private static final Vector<String> COLUMN_NAME_RESULT = new Vector<>();

    public static DefaultTableModel TABLE_MODEL_RULE = new DefaultTableModel();
    public static DefaultTableModel TABLE_MODEL_IGNORE = new DefaultTableModel();
    public static DefaultTableModel TABLE_MODEL_RESULT = new DefaultTableModel();

    static {
        logger.info(InterUtil.getValue("logs.common.initTableHeaderStart"));
        COLUMN_NAME_RULE.add(InterUtil.getValue("show.content.table.header.no"));
        COLUMN_NAME_RULE.add(InterUtil.getValue("show.content.table.header.content"));
        COLUMN_NAME_RULE.add(InterUtil.getValue("show.content.table.header.remark"));
        COLUMN_NAME_RULE.add(InterUtil.getValue("show.content.table.header.type"));
        COLUMN_NAME_IGNORE.add(InterUtil.getValue("show.content.table.header.no"));
        COLUMN_NAME_IGNORE.add(InterUtil.getValue("show.content.table.header.content"));
        COLUMN_NAME_IGNORE.add(InterUtil.getValue("show.content.table.header.type"));
        COLUMN_NAME_RESULT.add(InterUtil.getValue("show.content.table.header.no"));
        COLUMN_NAME_RESULT.add(InterUtil.getValue("show.content.table.header.lineStr"));
        COLUMN_NAME_RESULT.add(InterUtil.getValue("show.content.table.header.line"));
        COLUMN_NAME_RESULT.add(InterUtil.getValue("show.content.table.header.matchRule"));
        COLUMN_NAME_RESULT.add(InterUtil.getValue("show.content.table.header.filePath"));
        TABLE_MODEL_RULE.setDataVector(null, WindowSetting.COLUMN_NAME_RULE);
        TABLE_MODEL_IGNORE.setDataVector(null, WindowSetting.COLUMN_NAME_IGNORE);
        TABLE_MODEL_RESULT.setDataVector(null,COLUMN_NAME_RESULT);
        logger.info(InterUtil.getValue("logs.common.initTableHeaderFinish"));
    }

    public static void reFreshTableRule(Vector<Vector<String>> dataVector){

        logger.info(InterUtil.getValue("logs.refresh.refreshTableMathRule"));
        TABLE_MODEL_RULE.setDataVector(dataVector,COLUMN_NAME_RULE);
    }

    public static void reFreshTableIgnore(Vector<Vector<String>> dataVector) {

        logger.info(InterUtil.getValue("logs.refresh.refreshTableFileMatch"));
        TABLE_MODEL_IGNORE.setDataVector(dataVector, COLUMN_NAME_IGNORE);
    }

    public static TableModel reFreshTableResult(Vector<Vector<String>> dataVector) {

        logger.info(InterUtil.getValue("logs.refresh.refreshTableFileMatch"));
        TABLE_MODEL_RESULT.setDataVector(dataVector, COLUMN_NAME_RESULT);
        return TABLE_MODEL_RESULT;
    }
}
