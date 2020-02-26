package com.atzuche.order.coreapi.task;

import com.atzuche.order.cashieraccount.entity.CashierRefundApplyEntity;
import com.atzuche.order.cashieraccount.service.CashierPayService;
import com.atzuche.order.cashieraccount.service.notservice.CashierRefundApplyNoTService;
import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.coreapi.service.OrderSearchRemoteService;
import com.atzuche.order.renterwz.service.TransIllegalSendAliYunMq;
import com.atzuche.order.renterwz.vo.OrderInfoForIllegal;
import com.autoyol.commons.utils.GsonUtils;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * OrderRefundTask
 *
 * @date 2020/1/3
 */
@Component
@JobHandler("orderRefundTask")
public class OrderRefundTask extends IJobHandler {

    private Logger logger = LoggerFactory.getLogger(OrderRefundTask.class);

    @Autowired
    CashierRefundApplyNoTService cashierRefundApplyNoTService;
    @Autowired
    CashierPayService cashierPayService;

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        logger.info("开始执行 退款订单任务");
        List<CashierRefundApplyEntity> list = cashierRefundApplyNoTService.selectorderNoWaitingAll();
        logger.info("查询需要退换的 记录", GsonUtils.toJson(list));
        if (CollectionUtils.isNotEmpty(list)) {
            for (int i = 0; i < list.size(); i++) {
                Cat.logEvent(CatConstants.XXL_JOB_PARAM, GsonUtils.toJson(list));
                try {
                    cashierPayService.refundOrderPay(list.get(i));
                } catch (Exception e) {
                    logger.error("执行 退款操作异常 异常 {},{}", GsonUtils.toJson(list.get(i)), e);
                    Cat.logError("执行 退款操作异常 异常 {}", e);
                }
            }
        }
        logger.info("结束执行 退款 ");
        XxlJobLogger.log("结束执行 退款 ");
        return SUCCESS;

    }
}
