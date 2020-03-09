package com.atzuche.order.coreapi.task;

import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.coreapi.service.OrderSearchRemoteService;
import com.atzuche.order.settle.service.OrderWzSettleService;
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
 * TransIllegalSettleTask
 *
 * @author shisong
 * @date 2020/1/15
 */
@Component
@JobHandler("transIllegalSettleTask")
public class TransIllegalSettleTask extends IJobHandler {

    private Logger logger = LoggerFactory.getLogger(TransIllegalSettleTask.class);

    @Resource
    private OrderSearchRemoteService orderSearchRemoteService;

    @Resource
    private OrderWzSettleService orderWzSettleService;


    @Override
    public ReturnT<String> execute(String s) throws Exception {
        Transaction t = Cat.getProducer().newTransaction(CatConstants.XXL_JOB_CALL, "查询按规则配置日期内完成的订单，获取待结算的对象列表 ，查询能否结算");
        try {
            Cat.logEvent(CatConstants.XXL_JOB_METHOD,"transIllegalSettleTask.execute");
            Cat.logEvent(CatConstants.XXL_JOB_PARAM,null);
            logger.info("开始执行 查询按规则配置日期内完成的订单，获取待结算的对象列表 ，查询能否结算  定时器");
            XxlJobLogger.log("开始执行 查询按规则配置日期内完成的订单，获取待结算的对象列表 ，查询能否结算 定时器");

            logger.info("查询 远程调用 查询按规则配置日期内完成的订单，获取待结算的对象列表 ,查询开始");
            XxlJobLogger.log("查询 远程调用 查询按规则配置日期内完成的订单，获取待结算的对象列表 ,查询开始" );

            List<String> list = orderSearchRemoteService.queryOrderNosWithTransIllegalSettle();

            logger.info("查询 远程调用 查询按规则配置日期内完成的订单，获取待结算的对象列表 ,查询结果 models:[{}]", list);
            XxlJobLogger.log("查询 远程调用 查询按规则配置日期内完成的订单，获取待结算的对象列表 ,查询结果 models:" + list);

            if(CollectionUtils.isNotEmpty(list)){
                for (String orderNo : list) {
                    try {
                        logger.info("结束执行 查询按规则配置日期内完成的订单，获取待结算的对象列表 ，查询能否结算 ");
                        orderWzSettleService.settleWzOrder(orderNo);
                    } catch (Exception e) {
                        XxlJobLogger.log("执行 查询按规则配置日期内完成的订单，获取待结算的对象列表 ，查询能否结算 异常:"+ e);
                        logger.error("执行 查询按规则配置日期内完成的订单，获取待结算的对象列表 ，查询能否结算 异常 orderNo:[{}] e:[{}]",orderNo ,e);
                        Cat.logError("执行 查询按规则配置日期内完成的订单，获取待结算的对象列表 ，查询能否结算 异常",e);
                        t.setStatus(e);
                    }
                }
            }
            logger.info("结束执行 查询按规则配置日期内完成的订单，获取待结算的对象列表 ，查询能否结算 ");
            XxlJobLogger.log("结束执行 查询按规则配置日期内完成的订单，获取待结算的对象列表 ，查询能否结算 ");
            t.setStatus(Transaction.SUCCESS);
            return SUCCESS;
        } catch (Exception e) {
            XxlJobLogger.log("执行 查询按规则配置日期内完成的订单，获取待结算的对象列表 ，查询能否结算 异常:"+ e);
            logger.error("执行 查询按规则配置日期内完成的订单，获取待结算的对象列表 ，查询能否结算 异常",e);
            Cat.logError("执行 查询按规则配置日期内完成的订单，获取待结算的对象列表 ，查询能否结算 异常",e);
            t.setStatus(e);
            return new ReturnT(FAIL.getCode(),e.toString());
        } finally {
            if (t != null) {
                t.complete();
            }
        }
    }

}
