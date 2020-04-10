package com.atzuche.order.coreapi.task;

import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.commons.entity.orderDetailDto.OrderDTO;
import com.atzuche.order.coreapi.service.RemindPayIllegalCrashService;
import com.atzuche.order.coreapi.utils.SMSTaskDateTimeUtils;
import com.atzuche.order.parentorder.entity.OrderStatusEntity;
import com.atzuche.order.parentorder.service.OrderStatusService;
import com.autoyol.commons.utils.DateUtil;
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
 * @author 胡春林
 */
@Component
@JobHandler("RemindPushPayDepositTask")
public class RemindPushPayDepositTask extends IJobHandler {

    private Logger logger = LoggerFactory.getLogger(RemindPushPayDepositTask.class);
    @Autowired
    OrderStatusService orderStatusService;
    @Resource
    private RemindPayIllegalCrashService remindPayIllegalCrashService;
    @Override
    public ReturnT<String> execute(String s) throws Exception {
        Transaction t = Cat.getProducer().newTransaction(CatConstants.XXL_JOB_CALL, "您还未支付预定车辆的押金，请在X小时内完成支付。否则该订单将被取消 定时任务");
        try {
            Cat.logEvent(CatConstants.XXL_JOB_METHOD,"RemindPushPayDepositTask.execute");
            Cat.logEvent(CatConstants.XXL_JOB_PARAM,null);
            logger.info("开始执行 您还未支付预定车辆的押金，请在X小时内完成支付。否则该订单将被取消  定时器");
            XxlJobLogger.log("开始执行 您还未支付预定车辆的押金，请在X小时内完成支付。否则该订单将被取消 定时器");
            List<OrderDTO> orderNos = remindPayIllegalCrashService.findProcessOrderInfo();
            if (CollectionUtils.isEmpty(orderNos)) {
                return SUCCESS;
            }
            for (OrderDTO violateBO : orderNos) {
                OrderStatusEntity orderStatusEntity = orderStatusService.getByOrderNo(violateBO.getOrderNo());
                if (orderStatusEntity.getStatus().intValue() != 0 && orderStatusEntity.getWzPayStatus().intValue() == 0 && orderStatusEntity.getStatus().intValue() < 8) {
                    //没有支付违章押金
                }
            }
            logger.info("结束执行 您还未支付预定车辆的押金，请在X小时内完成支付。否则该订单将被取消 ");
            XxlJobLogger.log("结束执行 您还未支付预定车辆的押金，请在X小时内完成支付。否则该订单将被取消 ");
            return SUCCESS;
        } catch (Exception e) {
            XxlJobLogger.log("执行 您还未支付预定车辆的押金，请在X小时内完成支付。否则该订单将被取消 异常:" + e);
            logger.error("执行 您还未支付预定车辆的押金，请在X小时内完成支付。否则该订单将被取消 异常",e);
            Cat.logError("执行 您还未支付预定车辆的押金，请在X小时内完成支付。否则该订单将被取消 异常",e);
            return new ReturnT(FAIL.getCode(),e.toString());
        } finally {
            if (t != null) {
                t.complete();
            }
        }
    }
}
