package com.atzuche.order.coreapi.task;

import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.commons.enums.DispatcherReasonEnum;
import com.atzuche.order.commons.vo.req.RefuseOrderReqVO;
import com.atzuche.order.coreapi.service.OrderSearchRemoteService;
import com.atzuche.order.coreapi.service.OwnerRefuseOrderService;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * OwnerRejectTask
 *
 * @author shisong
 * @date 2020/1/15
 */
@Component
@JobHandler("ownerRejectTask")
public class OwnerRejectTask extends IJobHandler {

    private Logger logger = LoggerFactory.getLogger(OwnerRejectTask.class);

    @Resource
    private OrderSearchRemoteService orderSearchRemoteService;

    @Resource
    OwnerRefuseOrderService ownerRefuseOrderService;

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        Transaction t = Cat.getProducer().newTransaction(CatConstants.XXL_JOB_CALL, "定时查询下单后15分钟，车主没有接单,自动拒单 定时任务");
        try {
            Cat.logEvent(CatConstants.XXL_JOB_METHOD,"OwnerRejectTask.execute");
            Cat.logEvent(CatConstants.XXL_JOB_PARAM,null);
            logger.info("开始执行 下单后15分钟，车主没有接单,自动拒单  定时器");
            XxlJobLogger.log("开始执行 下单后15分钟，车主没有接单,自动拒单 定时器");

            logger.info("查询 远程调用 查询下单后15分钟，车主没有接单的订单列表,查询开始");
            XxlJobLogger.log("查询 远程调用 查询下单后15分钟，车主没有接单的订单列表,查询开始" );

            List<String> orderNos = orderSearchRemoteService.queryOrderNosWithOwnerHasNotAgree();

            logger.info("查询 远程调用 查询下单后15分钟，车主没有接单的订单列表,查询结果 models:[{}]", orderNos);
            XxlJobLogger.log("查询 远程调用 查询下单后15分钟，车主没有接单的订单列表,查询结果 models:" + orderNos);

            if(CollectionUtils.isNotEmpty(orderNos)){
                for (String orderNo : orderNos) {
                    RefuseOrderReqVO req = new RefuseOrderReqVO();
                    req.setOrderNo(orderNo);
                    ownerRefuseOrderService.refuse(req, DispatcherReasonEnum.timeout);
                }
            }
            logger.info("结束执行 下单后15分钟，车主没有接单,自动拒单 ");
            XxlJobLogger.log("结束执行 下单后15分钟，车主没有接单,自动拒单 ");
            return SUCCESS;
        } catch (Exception e) {
            XxlJobLogger.log("执行 下单后15分钟，车主没有接单,自动拒单 异常:",e);
            logger.error("执行 下单后15分钟，车主没有接单,自动拒单 异常",e);
            Cat.logError("执行 下单后15分钟，车主没有接单,自动拒单 异常",e);
            return new ReturnT(FAIL.getCode(),e.toString());
        } finally {
            if (t != null) {
                t.complete();
            }
        }
    }
}
