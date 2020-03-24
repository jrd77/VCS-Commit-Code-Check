package com.atzuche.order.coreapi.task;

import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.commons.constant.OrderConstant;
import com.atzuche.order.commons.vo.req.CancelOrderDelayRefundReqVO;
import com.atzuche.order.coreapi.service.CancelOrderService;
import com.atzuche.order.parentorder.entity.OrderRefundRecordEntity;
import com.atzuche.order.parentorder.service.OrderRefundRecordService;
import com.autoyol.commons.utils.GsonUtils;
import com.dianping.cat.Cat;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

/**
 * 车主同意取消订单延时罚金处理
 *
 * @author pengcheng.fu
 * @date 2020/3/17 16:26
 */
@Component
@JobHandler("transRefundManageTask")
public class TransRefundManageTask extends IJobHandler {

    private static Logger logger = LoggerFactory.getLogger(TransRefundManageTask.class);

    @Autowired
    private CancelOrderService cancelOrderService;
    @Autowired
    private OrderRefundRecordService orderRefundRecordService;

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        logger.info("开始执行 车主同意取消订单任务");
        LocalDateTime date = LocalDateTime.now().minusHours(1);
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = date.atZone(zoneId);
        List<OrderRefundRecordEntity> recordList =
                orderRefundRecordService.findByCreateTime(Date.from(zdt.toInstant()));
        logger.info("查询需要执行的记录recordList:[{}]", GsonUtils.toJson(recordList));
        if (CollectionUtils.isNotEmpty(recordList)) {
            for (int i = 0; i < recordList.size(); i++) {
                OrderRefundRecordEntity refundRecordEntity = recordList.get(i);
                Cat.logEvent(CatConstants.XXL_JOB_PARAM, GsonUtils.toJson(refundRecordEntity));
                try {
                    CancelOrderDelayRefundReqVO reqVO = new CancelOrderDelayRefundReqVO();
                    reqVO.setOrderNo(refundRecordEntity.getOrderNo());
                    reqVO.setTakePenalty(String.valueOf(OrderConstant.YES));
                    cancelOrderService.ownerAgreeDelayRefund(reqVO, OrderConstant.TWO);
                } catch (Exception e) {
                    logger.error("执行 车主同意取消订单任务 异常 {},{}", GsonUtils.toJson(refundRecordEntity), e);
                    Cat.logError("执行 车主同意取消订单任务 异常 {}", e);
                    XxlJobLogger.log("执行 车主同意取消订单任务 异常 {}", GsonUtils.toJson(refundRecordEntity));
                }
            }

        }
        logger.info("结束执行 车主同意取消订单任务 ");
        XxlJobLogger.log("结束执行 车主同意取消订单任务 ");
        return SUCCESS;
    }
}
