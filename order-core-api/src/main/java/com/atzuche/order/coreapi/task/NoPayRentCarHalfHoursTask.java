package com.atzuche.order.coreapi.task;

import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.commons.entity.orderDetailDto.OrderDTO;
import com.atzuche.order.coreapi.service.RemindPayIllegalCrashService;
import com.atzuche.order.coreapi.utils.SMSTaskDateTimeUtils;
import com.atzuche.order.parentorder.entity.OrderStatusEntity;
import com.atzuche.order.parentorder.service.OrderStatusService;
import com.autoyol.search.entity.ViolateBO;
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

@Component
@JobHandler("noPayRentCarHalfHoursTask")
public class NoPayRentCarHalfHoursTask extends IJobHandler {

    private Logger logger = LoggerFactory.getLogger(NoPayRentCarHalfHoursTask.class);

    @Autowired
    OrderStatusService orderStatusService;
    @Resource
    private RemindPayIllegalCrashService remindPayIllegalCrashService;

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        Transaction t = Cat.getProducer().newTransaction(CatConstants.XXL_JOB_CALL, "距离支付结束时间只有30分钟时，如还未支付租车押金 定时任务");
        try {
            Cat.logEvent(CatConstants.XXL_JOB_METHOD, "NoPayDepositBeforeRentTimeTask.execute");
            Cat.logEvent(CatConstants.XXL_JOB_PARAM, null);
            logger.info("开始执行 距离支付结束时间只有30分钟时，如还未支付租车押金  定时器");
            XxlJobLogger.log("开始执行 距离支付结束时间只有30分钟时，如还未支付租车押金定时器");
            List<OrderDTO> orderNos = remindPayIllegalCrashService.findProcessOrderInfo();
            if (CollectionUtils.isEmpty(orderNos)) {
                return SUCCESS;
            }
            for (OrderDTO violateBO : orderNos) {
                OrderStatusEntity orderStatusEntity = orderStatusService.getByOrderNo(violateBO.getOrderNo());
                if (orderStatusEntity.getRentCarPayStatus().intValue() == 0) {
                    if (SMSTaskDateTimeUtils.getDateLatterCompareNowScoend(violateBO.getReqTime(), 60) == 30) {
                        remindPayIllegalCrashService.sendNoPayCarShortMessageData(violateBO.getOrderNo());
                    }
                }
            }
            logger.info("结束执行 距离支付结束时间只有30分钟时，如还未支付租车押金 ");
            XxlJobLogger.log("结束执行 距离支付结束时间只有30分钟时，如还未支付租车押金 ");
            t.setStatus(Transaction.SUCCESS);
            return SUCCESS;
        } catch (Exception e) {
            XxlJobLogger.log("执行 距离支付结束时间只有30分钟时，如还未支付租车押金 异常:" + e);
            logger.error("执行 距离支付结束时间只有30分钟时，如还未支付租车押金 异常", e);
            Cat.logError("执行 距离支付结束时间只有30分钟时，如还未支付租车押金 异常", e);
            t.setStatus(e);
            return new ReturnT(FAIL.getCode(), e.toString());
        } finally {
            if (t != null) {
                t.complete();
            }
        }
    }
}
