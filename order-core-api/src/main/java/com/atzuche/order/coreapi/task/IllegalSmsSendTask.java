package com.atzuche.order.coreapi.task;

import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.coreapi.service.IllegalSmsSendService;
import com.atzuche.order.renterwz.vo.IllegalToDO;
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
 * IllegalSmsSendTask
 *
 * @author shisong
 * @date 2020/1/3
 */
@Component
@JobHandler("illegalSmsSendTask")
public class IllegalSmsSendTask extends IJobHandler {

    private Logger logger = LoggerFactory.getLogger(IllegalSmsSendTask.class);

    @Resource
    private IllegalSmsSendService illegalSmsSendService;

    /**
     * 1.查询待发短信
     * 2.组装待发MAP列表
     * 3.JPUSH通知APP推送信息
     */
    @Override
    public ReturnT<String> execute(String s) throws Exception {
        Transaction t = Cat.getProducer().newTransaction(CatConstants.XXL_JOB_CALL, "查询违章待发短信，jPush通知和App推送信息");
        try {
            Cat.logEvent(CatConstants.XXL_JOB_METHOD,"IllegalSmsSendTask.execute");
            Cat.logEvent(CatConstants.XXL_JOB_PARAM,null);
            logger.info("开始执行 查询违章待发短信，jPush通知和App推送信息  定时器");
            XxlJobLogger.log("开始执行 查询违章待发短信，jPush通知和App推送信息 定时器");

            illegalSmsSendService.smsNotice();

            logger.info("结束执行 查询违章待发短信，jPush通知和App推送信息 ");
            XxlJobLogger.log("结束执行 查询违章待发短信，jPush通知和App推送信息 ");
            t.setStatus(Transaction.SUCCESS);
            return SUCCESS;
        } catch (Exception e) {
            XxlJobLogger.log("执行 查询违章待发短信，jPush通知和App推送信息 异常:"+ e);
            logger.error("执行 查询违章待发短信，jPush通知和App推送信息 异常",e);
            Cat.logError("执行 查询违章待发短信，jPush通知和App推送信息 异常",e);
            t.setStatus(e);
            return new ReturnT(FAIL.getCode(),e.toString());
        } finally {
            if (t != null) {
                t.complete();
            }
        }
    }
}
