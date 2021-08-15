package com.github.jrd77.codecheck.handler;

import com.github.jrd77.codecheck.data.InterUtil;
import com.github.jrd77.codecheck.data.VcsCheckSettingsState;
import com.github.jrd77.codecheck.util.ResultObject;

public class CheckCommitFilter {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(CheckCommitFilter.class.getName());

    public static ResultObject checkCommitPre(){

        VcsCheckSettingsState instance = VcsCheckSettingsState.getInstance();
        if (instance.ruleList.size() == 0) {
            String msg= InterUtil.getValue("logs.validate.norules");
            logger.warning(msg);
            return ResultObject.err(msg);
        }
        if (instance.ignoreList.size() == 0) {
            String msg=InterUtil.getValue("logs.validate.nofilematchrules");
            logger.warning(msg);
            return ResultObject.err(msg);
        }
        return ResultObject.ok();
    }
}
