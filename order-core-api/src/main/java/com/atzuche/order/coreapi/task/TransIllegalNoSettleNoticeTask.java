package com.atzuche.order.coreapi.task;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.commons.ListUtil;
import com.atzuche.order.commons.constant.OrderConstant;
import com.atzuche.order.coreapi.common.EmailCommon;
import com.atzuche.order.coreapi.config.IllegalNoSettleNoticeEmailConfig;
import com.atzuche.order.coreapi.utils.EmailUtil;
import com.atzuche.order.parentorder.entity.OrderStatusEntity;
import com.atzuche.order.parentorder.service.OrderStatusService;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.EmailException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 违章押金结算失败订单通知运营人员
 *
 * @author pengcheng.fu
 * @date 2020/7/29 17:49
 */

@Component
@JobHandler("transWzSettleFailNoticeTask")
public class TransIllegalNoSettleNoticeTask extends IJobHandler {

    private static Logger logger = LoggerFactory.getLogger(TransIllegalNoSettleNoticeTask.class);

    @Autowired
    private OrderStatusService orderStatusService;

    @Autowired
    private IllegalNoSettleNoticeEmailConfig illegalNoSettleNoticeEmailConfig;

    @Override
    public ReturnT<String> execute(String s) {
        logger.info("开始执行 违章押金结算失败订单通知运营人员 定时任务");
        XxlJobLogger.log("开始执行 违章押金结算失败订单通知运营人员 定时任务");

        Transaction t = Cat.getProducer().newTransaction(CatConstants.XXL_JOB_CALL, "违章押金结算失败订单通知运营人员 定时任务");
        try {
            Cat.logEvent(CatConstants.XXL_JOB_METHOD, "TransIllegalNoSettleNoticeTask.execute");


            List<OrderStatusEntity> orderList = orderStatusService.queryByWzSettleStatus(OrderConstant.TWO);
            logger.info("查询 违章押金结算失败订单 [{}]", JSON.toJSONString(orderList));
            XxlJobLogger.log("查询 违章押金结算失败订单:" + JSON.toJSONString(orderList));
            Cat.logEvent(CatConstants.XXL_JOB_RESULT, JSON.toJSONString(orderList));

            if (CollectionUtils.isNotEmpty(orderList)) {
                // 发送邮件
                sendEmail(orderList);
            }
            t.setStatus(Transaction.SUCCESS);
        } catch (Exception e) {
            logger.error("执行 违章押金结算失败订单通知运营人员 定时任务 异常", e);
            XxlJobLogger.log("执行 违章押金结算失败订单通知运营人员 定时任务 异常:" + e);
            Cat.logError("执行 违章押金结算失败订单通知运营人员 定时任务 异常", e);
            t.setStatus(e);
        } finally {
            if (t != null) {
                t.complete();
            }
        }
        logger.info("结束执行 违章押金结算失败订单通知运营人员 定时任务");
        XxlJobLogger.log("结束执行 违章押金结算失败订单通知运营人员 定时任务");
        return SUCCESS;
    }


    private void sendEmail(List<OrderStatusEntity> orderList) throws EmailException {
        if (Objects.isNull(illegalNoSettleNoticeEmailConfig)) {
            return;
        }
        String toAddrs = illegalNoSettleNoticeEmailConfig.toAddrs;
        if (StringUtils.isNotBlank(toAddrs)) {
            EmailCommon common = new EmailCommon();
            common.setHostName(illegalNoSettleNoticeEmailConfig.hostName);
            common.setFromAddr(illegalNoSettleNoticeEmailConfig.fromAddr);
            common.setFromName(illegalNoSettleNoticeEmailConfig.fromName);
            common.setFromPwd(illegalNoSettleNoticeEmailConfig.fromPwd);

            String subject = "违章结算异常邮件";
            List<Integer> orderNos =
                    orderList.stream().map(order -> Integer.valueOf(order.getOrderNo())).collect(Collectors.toList());
            String context = "违章结算异常\r" +
                    "\t订单号：" + ListUtil.reduce(orderNos, ",") + "，违章未结算，请及时处理！";
            String mesgId = EmailUtil.sendSimpleEmail(common, toAddrs.split(","), subject, context);
            logger.info("Send illegal settle fial notice >> result is, mesgId:[{}]", mesgId);
        }
    }
}
