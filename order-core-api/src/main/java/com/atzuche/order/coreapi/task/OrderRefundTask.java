package com.atzuche.order.coreapi.task;

import com.atzuche.order.cashieraccount.entity.CashierRefundApplyEntity;
import com.atzuche.order.cashieraccount.service.CashierPayService;
import com.atzuche.order.cashieraccount.service.CashierService;
import com.atzuche.order.cashieraccount.service.notservice.CashierRefundApplyNoTService;
import com.atzuche.order.commons.CatConstants;
import com.autoyol.autopay.gateway.vo.res.AutoPayResultVo;
import com.autoyol.commons.utils.GsonUtils;
import com.dianping.cat.Cat;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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
    @Autowired
    private CashierService cashierService;

    @Override
    public ReturnT<String> execute(String s) {
        logger.info("开始执行 退款订单任务");
        XxlJobLogger.log("开始执行 退款订单任务");
        List<CashierRefundApplyEntity> list = cashierRefundApplyNoTService.selectorderNoWaitingAll();
        logger.info("开始执行 退款订单任务 查询需要退换的 记录list={}", GsonUtils.toJson(list));
        XxlJobLogger.log("开始执行 退款订单任务 查询需要退换的 记录. list:" + GsonUtils.toJson(list));
        if (CollectionUtils.isNotEmpty(list)) {

            Map<String, List<CashierRefundApplyEntity>> dataMap =
                    list.stream().collect(Collectors.groupingBy(CashierRefundApplyEntity::getOrderNo));
            for (String key : dataMap.keySet()) {
                logger.info("Order [{}] begins to refund.", key);
                List<CashierRefundApplyEntity> records = dataMap.get(key);

                for (CashierRefundApplyEntity cashierRefundApplyEntity : records) {
                    AutoPayResultVo result = cashierPayService.refundOrderPay(cashierRefundApplyEntity);
                    if(Objects.nonNull(result)) {
                        cashierService.refundCallBackSuccess(result);
                    }
                }


            }


            for (CashierRefundApplyEntity cashierRefundApplyEntity : list) {
                Cat.logEvent(CatConstants.XXL_JOB_PARAM, GsonUtils.toJson(cashierRefundApplyEntity));
                try {
                    cashierPayService.refundOrderPay(cashierRefundApplyEntity);
                } catch (Exception e) {
                    logger.error("执行 退款操作异常 异常", e);
                    Cat.logError("执行 退款操作异常 异常", e);
                    XxlJobLogger.log("执行 退款操作异常 异常,params:" + GsonUtils.toJson(cashierRefundApplyEntity));
                }
            }
        } else {
            logger.info("开始执行 退款订单任务 未查询需要退换的 记录list=0");
        }
        logger.info("结束执行 退款 ");
        XxlJobLogger.log("结束执行 退款 ");

        return SUCCESS;

    }
}
