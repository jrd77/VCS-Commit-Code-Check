package com.atzuche.order.coreapi.task;

import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.commons.entity.orderDetailDto.OrderDTO;
import com.atzuche.order.commons.entity.orderDetailDto.OrderStatusDTO;
import com.atzuche.order.commons.vo.req.CancelOrderReqVO;
import com.atzuche.order.coreapi.service.CancelOrderService;
import com.atzuche.order.coreapi.service.RemindPayIllegalCrashService;
import com.atzuche.order.open.service.FeignOrderDetailService;
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
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author 胡春林
 */
@Component
@JobHandler("noPayDepositBeforeRentTimeTask")
public class NoPayDepositBeforeRentTimeTask extends IJobHandler {

    private Logger logger = LoggerFactory.getLogger(NoPayDepositBeforeRentTimeTask.class);

    @Autowired
    OrderStatusService orderStatusService;
    @Resource
    private RemindPayIllegalCrashService remindPayIllegalCrashService;
    @Resource
    private CancelOrderService cancelOrderService;

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        Transaction t = Cat.getProducer().newTransaction(CatConstants.XXL_JOB_CALL, "租客在取车时间前未支付租车押金或违章押金 定时任务");
        try {
            Cat.logEvent(CatConstants.XXL_JOB_METHOD, "NoPayDepositBeforeRentTimeTask.execute");
            Cat.logEvent(CatConstants.XXL_JOB_PARAM, null);
            logger.info("开始执行 租客在取车时间前未支付租车押金或违章押金  定时器");
            XxlJobLogger.log("开始执行 租客在取车时间前未支付租车押金或违章押金定时器");
            List<OrderDTO> orderNos = remindPayIllegalCrashService.findProcessOrderInfo();
            if (CollectionUtils.isEmpty(orderNos)) {
                return SUCCESS;
            }
            for (OrderDTO violateBO : orderNos) {
                OrderStatusEntity orderStatusEntity = orderStatusService.getByOrderNo(violateBO.getOrderNo());

                logger.info("当前订单状态数据：orderStatusEntity:[{}],预计取车时间[{}]",orderStatusEntity.getStatus().toString(),violateBO.getExpRentTime());
                if (orderStatusEntity.getStatus().intValue() < 8 && (orderStatusEntity.getDepositPayStatus().intValue() == 0 || orderStatusEntity.getWzPayStatus().intValue() == 0)) {
                    if (LocalDateTime.now().isBefore(violateBO.getExpRentTime()) && LocalDateTime.now().plusMinutes(5).isAfter(violateBO.getExpRentTime()) ) {
                        //String typeName = orderStatusEntity.getDepositPayStatus().intValue() == 0 ? "租车押金" : "违章押金";
                        //remindPayIllegalCrashService.sendNoPayShortMessageData(violateBO.getOrderNo(), typeName);
                        remindPayIllegalCrashService.sendNoPayIllegalDepositShortMessageData(violateBO.getOrderNo());
                        //取消订单
//                        CancelOrderReqVO req = new CancelOrderReqVO();
//                        req.setOrderNo(violateBO.getOrderNo());
//                        req.setCancelReason("租客在取车时间前未支付租车押金或违章押金,自动取消");
//                        req.setMemRole("2");
//                        req.setOperatorName("system");
//                        logger.info("执行 租客在取车时间前未支付租车押金或违章押金 orderNo:[{}]", violateBO.getOrderNo());
//                        cancelOrderService.cancel(req);
                    }
                }
            }
            logger.info("结束执行 租客在取车时间前未支付租车押金或违章押金 ");
            XxlJobLogger.log("结束执行 租客在取车时间前未支付租车押金或违章押金 ");
            t.setStatus(Transaction.SUCCESS);
            return SUCCESS;
        } catch (Exception e) {
            XxlJobLogger.log("执行 租客在取车时间前未支付租车押金或违章押金 异常:" + e);
            logger.error("执行 租客在取车时间前未支付租车押金或违章押金 异常", e);
            Cat.logError("执行 租客在取车时间前未支付租车押金或违章押金 异常", e);
            t.setStatus(e);
            return new ReturnT(FAIL.getCode(), e.toString());
        } finally {
            if (t != null) {
                t.complete();
            }
        }
    }
}
