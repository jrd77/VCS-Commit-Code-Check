package com.atzuche.order.coreapi.task;

import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.commons.EmailUtils;
import com.atzuche.order.commons.vo.req.CancelOrderReqVO;
import com.atzuche.order.coreapi.service.OrderSearchRemoteService;
import com.atzuche.order.coreapi.service.SendEmailService;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * UnPayViolationDepositTask
 *
 * @author shisong
 * @date 2020/1/15
 */
@Component
@JobHandler("unPayViolationDepositTask")
public class UnPayViolationDepositTask extends IJobHandler {

    private Logger logger = LoggerFactory.getLogger(UnPayViolationDepositTask.class);

    @Resource
    private OrderSearchRemoteService orderSearchRemoteService;

    @Resource
    private SendEmailService sendEmailService;

    @Value("${un.pay.violation.deposit.email}")
    private String emailsStr;

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        Transaction t = Cat.getProducer().newTransaction(CatConstants.XXL_JOB_CALL, "定时 查询订单开始仍未支付违章押金，发邮件 定时任务");
        try {
            Cat.logEvent(CatConstants.XXL_JOB_METHOD,"unPayViolationDepositTask.execute");
            Cat.logEvent(CatConstants.XXL_JOB_PARAM,null);
            logger.info("开始执行 查询订单开始仍未支付违章押金，发邮件  定时器");
            XxlJobLogger.log("开始执行 查询订单开始仍未支付违章押金，发邮件 定时器");

            logger.info("查询 远程调用 查询待调度后4小时，仍未调度的订单列表,查询开始");
            XxlJobLogger.log("查询 远程调用 查询待调度后4小时，仍未调度的订单列表,查询开始" );

            List<String> orderNos = orderSearchRemoteService.queryOrderNosWithUnPayViolationDeposit();

            logger.info("查询 远程调用 查询待调度后4小时，仍未调度的订单列表,查询结果 models:[{}]", orderNos);
            XxlJobLogger.log("查询 远程调用 查询待调度后4小时，仍未调度的订单列表,查询结果 models:" + orderNos);

            if(CollectionUtils.isNotEmpty(orderNos)){
                EmailUtils.sendMail(emailsStr.split(";"), null, "订单开始，租客未支付违章押金", sendEmailService.getContent(orderNos));
            }
            logger.info("结束执行 查询订单开始仍未支付违章押金，发邮件 ");
            XxlJobLogger.log("结束执行 查询订单开始仍未支付违章押金，发邮件");
            return SUCCESS;
        } catch (Exception e) {
            XxlJobLogger.log("执行 查询订单开始仍未支付违章押金，发邮件 异常:"+ e);
            logger.error("执行 查询订单开始仍未支付违章押金，发邮件 异常",e);
            Cat.logError("执行 查询订单开始仍未支付违章押金，发邮件 异常",e);
            return new ReturnT(FAIL.getCode(),e.toString());
        } finally {
            if (t != null) {
                t.complete();
            }
        }
    }

}
