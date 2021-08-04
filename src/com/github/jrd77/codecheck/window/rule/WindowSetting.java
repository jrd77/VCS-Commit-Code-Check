package com.github.jrd77.codecheck.window.rule;

import javax.swing.table.DefaultTableModel;
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
        logger.info("初始化设置table标题");
        COLUMN_NAME_RULE.add("编号");
        COLUMN_NAME_RULE.add("内容");
        COLUMN_NAME_RULE.add("备注");
        COLUMN_NAME_RULE.add("类型");
        COLUMN_NAME_IGNORE.add("编号");
        COLUMN_NAME_IGNORE.add("内容");
        COLUMN_NAME_IGNORE.add("类型");
        COLUMN_NAME_RESULT.add("编号");
        COLUMN_NAME_RESULT.add("所在行");
        COLUMN_NAME_RESULT.add("行号");
        COLUMN_NAME_RESULT.add("命中规则");
        COLUMN_NAME_RESULT.add("文件地址");
        TABLE_MODEL_RULE.setDataVector(null, WindowSetting.COLUMN_NAME_RULE);
        TABLE_MODEL_IGNORE.setDataVector(null, WindowSetting.COLUMN_NAME_IGNORE);
        TABLE_MODEL_RESULT.setDataVector(null,COLUMN_NAME_RESULT);
        logger.info("初始化设置table标题完成");
    }

    public static void reFreshTableRule(Vector<Vector<String>> dataVector){

        logger.info("刷新检查规则列表");
        TABLE_MODEL_RULE.setDataVector(dataVector,COLUMN_NAME_RULE);
    }

    public static void reFreshTableIgnore(Vector<Vector<String>> dataVector){

        logger.info("刷新文件过滤规则列表");
        TABLE_MODEL_IGNORE.setDataVector(dataVector,COLUMN_NAME_IGNORE);
    }
    public static void reFreshTableResult(Vector<Vector<String>> dataVector){

        logger.info("刷新文件过滤规则列表");
        TABLE_MODEL_RESULT.setDataVector(dataVector,COLUMN_NAME_RESULT);
    }
}
