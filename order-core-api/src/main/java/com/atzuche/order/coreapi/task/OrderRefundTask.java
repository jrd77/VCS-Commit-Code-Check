package com.atzuche.order.coreapi.task;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.cashieraccount.entity.CashierRefundApplyEntity;
import com.atzuche.order.cashieraccount.service.CashierPayService;
import com.atzuche.order.cashieraccount.service.CashierService;
import com.atzuche.order.cashieraccount.service.notservice.CashierRefundApplyNoTService;
import com.atzuche.order.cashieraccount.vo.res.pay.OrderPayCallBackSuccessVO;
import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.commons.constant.OrderConstant;
import com.atzuche.order.commons.enums.cashier.OrderRefundStatusEnum;
import com.atzuche.order.commons.enums.cashier.TransStatusEnum;
import com.atzuche.order.parentorder.dto.OrderStatusDTO;
import com.autoyol.autopay.gateway.constant.DataPayKindConstant;
import com.autoyol.autopay.gateway.vo.res.AutoPayResultVo;
import com.autoyol.commons.utils.GsonUtils;
import com.autoyol.event.rabbit.neworder.NewOrderMQActionEventEnum;
import com.dianping.cat.Cat;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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
                    list.stream().collect(Collectors.groupingBy(OrderRefundTask::fetchGroupKey));
            XxlJobLogger.log("分组后的数据 >> dataMap:" + JSON.toJSONString(dataMap));
            logger.info("分组后的数据 >> dataMap:[{}]", JSON.toJSONString(dataMap));

            for (String key : dataMap.keySet()) {
                logger.info("Order_paykind [{}] begins to refund.", key);
                XxlJobLogger.log("Order_paykind begins to refund. key=" + key);
                List<CashierRefundApplyEntity> records = dataMap.get(key);
                int sum = 0;
                String memNo = "";
                for (CashierRefundApplyEntity cashierRefundApplyEntity : records) {
                    AutoPayResultVo result = cashierPayService.orderRefundHandle(cashierRefundApplyEntity);
                    if (StringUtils.isBlank(memNo)) {
                        memNo = result.getMemNo();
                    }
                    if (Objects.nonNull(result) && StringUtils.equals(result.getTransStatus(), TransStatusEnum.PAY_SUCCESS.getCode())) {
                        sum = sum + OrderConstant.ONE;
                    }
                }

                logger.info("refund handle result >> sum:[{}], total:[{}]", sum, records.size());
                XxlJobLogger.log("refund handle result >> sum = " + sum + " total=" + records.size());
                //后续处理
                if (records.size() == sum && sum > OrderConstant.ZERO) {
                    cashierPayService.refundResultHandle(key, memNo);
                }
            }
        } else {
            logger.info("开始执行 退款订单任务 未查询需要退换的 记录list=0");
        }
        logger.info("结束执行 退款 ");
        XxlJobLogger.log("结束执行 退款 ");

        return SUCCESS;

    }

    /**
     * 組合鍵
     *
     * @param entity 申請記錄
     * @return String
     */
    private static String fetchGroupKey(CashierRefundApplyEntity entity) {
        return entity.getOrderNo() + ":" + entity.getPayKind();
    }



}
