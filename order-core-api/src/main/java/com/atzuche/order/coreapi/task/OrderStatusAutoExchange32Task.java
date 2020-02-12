package com.atzuche.order.coreapi.task;

import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.commons.vo.req.GetCarReqVO;
import com.atzuche.order.commons.vo.req.ReturnCarReqVO;
import com.atzuche.order.coreapi.service.OrderSearchRemoteService;
import com.atzuche.order.coreapi.service.OwnerReturnCarService;
import com.atzuche.order.coreapi.service.RenterGetCarService;
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
 * OrderStatusAutoExchange32Task 待取车变为待还车
 *
 * @author shisong
 * @date 2020/1/15
 */
@Component
@JobHandler("orderStatusAutoExchange32Task")
public class OrderStatusAutoExchange32Task extends IJobHandler {

    private Logger logger = LoggerFactory.getLogger(OrderStatusAutoExchange32Task.class);

    @Resource
    private OrderSearchRemoteService orderSearchRemoteService;

    @Resource
    private RenterGetCarService renterGetCarService;

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        Transaction t = Cat.getProducer().newTransaction(CatConstants.XXL_JOB_CALL, "定时查询 到达订单开始时，将待取车变为待还车 定时任务");
        try {
            Cat.logEvent(CatConstants.XXL_JOB_METHOD,"expRevertCar12HoursAutoRevertCarTask.execute");
            Cat.logEvent(CatConstants.XXL_JOB_PARAM,null);
            logger.info("开始执行 到达订单开始时，将待取车变为待还车  定时器");
            XxlJobLogger.log("开始执行 到达订单开始时，将待取车变为待还车 定时器");

            logger.info("查询 远程调用 到达订单开始时,但仍是待取车状态的订单,查询开始");
            XxlJobLogger.log("查询 远程调用 到达订单开始时,但仍是待取车状态的订单,查询开始" );

            List<String> orderNos = orderSearchRemoteService.queryWaitingForCar();

            logger.info("查询 远程调用 到达订单开始时,但仍是待取车状态的订单,查询结果 models:[{}]", orderNos);
            XxlJobLogger.log("查询 远程调用 到达订单开始时,但仍是待取车状态的订单,查询结果 models:" + orderNos);

            if(CollectionUtils.isNotEmpty(orderNos)){
                for (String orderNo : orderNos) {
                    GetCarReqVO reqVO = new GetCarReqVO();
                    reqVO.setOrderNo(orderNo);
                    reqVO.setOperatorName("System");
                    try {
                        logger.info("执行 到达订单开始时，将待取车变为待还车 orderNo:[{}]",orderNo);
                        renterGetCarService.pickUpCar(reqVO);
                    } catch (Exception e) {
                        XxlJobLogger.log("执行 到达订单开始时，将待取车变为待还车 异常:",e);
                        logger.error("执行 到达订单开始时，将待取车变为待还车 异常orderNo:[{}] , e:[{}]",orderNo ,e);
                        Cat.logError("执行 到达订单开始时，将待取车变为待还车 异常",e);
                    }
                }
            }
            logger.info("结束执行 到达订单开始时，将待取车变为待还车");
            XxlJobLogger.log("结束执行 到达订单开始时，将待取车变为待还车");
            return SUCCESS;
        } catch (Exception e) {
            XxlJobLogger.log("执行 到达订单开始时，将待取车变为待还车 异常:",e);
            logger.error("执行 到达订单开始时，将待取车变为待还车 异常",e);
            Cat.logError("执行 到达订单开始时，将待取车变为待还车 异常",e);
            return new ReturnT(FAIL.getCode(),e.toString());
        } finally {
            if (t != null) {
                t.complete();
            }
        }
    }

}
