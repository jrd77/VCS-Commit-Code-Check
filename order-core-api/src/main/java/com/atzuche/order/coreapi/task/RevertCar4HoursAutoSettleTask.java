package com.atzuche.order.coreapi.task;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.coreapi.entity.vo.ExceptionEmailServerVo;
import com.atzuche.order.coreapi.service.ExceptionEmailService;
import com.atzuche.order.coreapi.service.ExceptionGPSMail;
import com.atzuche.order.coreapi.service.OrderSearchRemoteService;
import com.atzuche.order.coreapi.service.OrderSettle;
import com.atzuche.order.settle.service.OrderSettleService;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;

/**
 * RevertCar4HoursAutoSettleTask
 *
 * @author shisong
 * @date 2020/1/15
 */
@Component
@JobHandler("revertCar4HoursTask")
public class RevertCar4HoursAutoSettleTask extends IJobHandler{

    private Logger logger = LoggerFactory.getLogger(RevertCar4HoursAutoSettleTask.class);

    @Resource
    private OrderSearchRemoteService orderSearchRemoteService;
    @Autowired
    private OrderSettle orderSettle;
    @Resource
    private OrderSettleService orderSettleService;
    @Resource
    private ExceptionEmailService exceptionEmailService;
    @Value("${bpo.emails}")
    private String emails;
    
    private String[] exceptionEmail() {
        return emails.split(",");
    }
    
    
    @Override
    public ReturnT<String> execute(String s) throws Exception {
        Transaction t = Cat.getProducer().newTransaction(CatConstants.XXL_JOB_CALL, "定时查询 还车4小时后，自动结算 定时任务");
        try {
            Cat.logEvent(CatConstants.XXL_JOB_METHOD,"revertCar4HoursTask.execute");
            Cat.logEvent(CatConstants.XXL_JOB_PARAM,null);
            logger.info("开始执行 还车4小时后，自动结算  定时器");
            XxlJobLogger.log("开始执行 还车4小时后，自动结算 定时器");

            logger.info("查询 远程调用 查询还车4小时后的订单列表,查询开始");
            XxlJobLogger.log("查询 远程调用 查询还车4小时后的订单列表,查询开始" );

            List<String> orderNos = orderSearchRemoteService.queryOrderNosWithRevertCar4Hours();

            logger.info("查询 远程调用 查询还车4小时后的订单列表,查询结果 models:[{}]", orderNos);
            XxlJobLogger.log("查询 远程调用 查询还车4小时后的订单列表,查询结果 models:" + orderNos);

            if(CollectionUtils.isNotEmpty(orderNos)){
                for (String orderNo : orderNos) {
                    try {
                        logger.info("执行 还车4小时后，自动结算 orderNo:[{}]",orderNo);
                        /**
                         * 结算失败通知邮件列表
                         */
                        List<String> listOrderNos = new ArrayList<String>();
                        
                        orderSettle.settleOrder(orderNo,listOrderNos);
                        
                        //发送邮件通知。
                        if(listOrderNos != null && listOrderNos.size() > 0) {
                        	ExceptionEmailServerVo email = exceptionEmailService.getEmailServer();
                            String[] emails = this.exceptionEmail();
                            String content = "订单号列表如下：";
                            for (String obj : orderNos) {
                            	content += " " + obj + ", ";
							}
                        	new ExceptionGPSMail(email.getHostName(),email.getFromAddr(),email.getFromName(),email.getFromPwd(),emails).send("请关注取还车油表或里程刻度异常结算被拦截",content);
                        }
                    } catch (Exception e) {
                        XxlJobLogger.log("执行 还车4小时后，自动结算 异常:" + e);
                        logger.error("执行 还车4小时后，自动结算 异常 orderNo:[{}] , e:[{}]",orderNo , e);
                        Cat.logError("执行 还车4小时后，自动结算 异常",e);
                        t.setStatus(e);
                    }
                }
            }
            logger.info("结束执行 还车4小时后，自动结算");
            XxlJobLogger.log("结束执行 还车4小时后，自动结算");
            t.setStatus(Transaction.SUCCESS);
            return SUCCESS;
        } catch (Exception e) {
            XxlJobLogger.log("执行 还车4小时后，自动结算 异常:" + e);
            logger.error("执行 还车4小时后，自动结算 异常",e);
            Cat.logError("执行 还车4小时后，自动结算 异常",e);
            t.setStatus(e);
            return new ReturnT(FAIL.getCode(),e.toString());
        } finally {
            if (t != null) {
                t.complete();
            }
        }
    }
}
