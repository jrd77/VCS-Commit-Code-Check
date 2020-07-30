package com.atzuche.order.coreapi.task;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import org.springframework.stereotype.Component;

/**
 * 违章押金结算失败订单通知运营人员
 *
 *
 * @author pengcheng.fu
 * @date 2020/7/29 17:49
 */

@Component
@JobHandler("transWzSettleFailNoticeTask")
public class TransIllegalNoSettleNoticeTask extends IJobHandler {


    @Override
    public ReturnT<String> execute(String s) {



        return SUCCESS;
    }
}
