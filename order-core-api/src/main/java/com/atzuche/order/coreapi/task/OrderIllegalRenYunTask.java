package com.atzuche.order.coreapi.task;

import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.renterwz.vo.OrderInfoForIllegal;
import com.atzuche.order.coreapi.service.OrderSearchRemoteService;
import com.atzuche.order.renterwz.service.TransIllegalSendAliYunMq;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * OrderIllegalRenYunTask
 *
 * @author shisong
 * @date 2020/1/3
 */
@Component("orderIllegalRenYunTask")
public class OrderIllegalRenYunTask extends IJobHandler {

    private Logger logger = LoggerFactory.getLogger(OrderIllegalRenYunTask.class);


    @Resource
    private OrderSearchRemoteService orderSearchRemoteService;

    @Resource
    private TransIllegalSendAliYunMq transIllegalSendAliYunMq;

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        Transaction t = Cat.getProducer().newTransaction(CatConstants.XXL_JOB_CALL, "每天定时处理 实际还车15天后系统自动生成，调用流程系统，查询是否有违章记录");
        try {
            Cat.logEvent(CatConstants.XXL_JOB_METHOD,"OrderIllegalRenYunTask.execute");
            Cat.logEvent(CatConstants.XXL_JOB_PARAM,null);
            logger.info("开始执行 每天定时处理 实际还车15天后系统自动生成，调用流程系统，查询是否有违章记录  定时器");
            XxlJobLogger.log("开始执行 每天定时处理 实际还车15天后系统自动生成，调用流程系统，查询是否有违章记录 定时器");

            logger.info("查询 远程调用 结算前15分钟订单,查询开始");
            XxlJobLogger.log("查询 远程调用 结算前15分钟订单,查询开始" );

            List<OrderInfoForIllegal> list = orderSearchRemoteService.renterWzOrders();

            logger.info("查询 远程调用 结算前15分钟订单,查询结果 models:[{}]", list);
            XxlJobLogger.log("查询 远程调用 结算前15分钟订单,查询结果 models:" + list);

            if(CollectionUtils.isNotEmpty(list)){
                transIllegalSendAliYunMq.sendTrafficViolationMq(list);
                logger.info("发送阿里云mq完成,list.size={},list={}", list.size(), list);
            }
            logger.info("结束执行 每天定时处理结算前15分钟订单，查询是否有违章记录 ");
            XxlJobLogger.log("结束执行 每天定时处理结算前15分钟订单，查询是否有违章记录 ");
            return SUCCESS;
        } catch (Exception e) {
            XxlJobLogger.log("执行 每天定时处理结算前15分钟订单，查询是否有违章记录 异常:",e);
            logger.error("执行 每天定时处理结算前15分钟订单，查询是否有违章记录 异常",e);
            Cat.logError("执行 每天定时处理结算前15分钟订单，查询是否有违章记录 异常",e);
            return new ReturnT(FAIL.getCode(),e.toString());
        } finally {
            if (t != null) {
                t.complete();
            }
        }
    }
}
