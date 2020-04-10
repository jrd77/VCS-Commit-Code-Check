package com.atzuche.order.coreapi.task;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import org.springframework.stereotype.Component;

/**
 * @author 胡春林
 */
@Component
//@JobHandler("pushPayDepositBefore4HoursTask")
public class PushPayDepositBefore4HoursTask extends IJobHandler {
    @Override
    public ReturnT<String> execute(String s) throws Exception {
        return null;
    }
}
