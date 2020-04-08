package com.atzuche.order.coreapi.task;

import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.commons.DateUtils;
import com.atzuche.order.commons.entity.orderDetailDto.OrderDTO;
import com.atzuche.order.coreapi.service.RemindPayIllegalCrashService;
import com.atzuche.order.coreapi.utils.SMSTaskDateTimeUtils;
import com.atzuche.order.parentorder.entity.OrderStatusEntity;
import com.atzuche.order.parentorder.service.OrderStatusService;
import com.autoyol.commons.utils.DateUtil;
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
import java.util.List;

/**
 * @author 胡春林
 */
@Component
@JobHandler("remindVoicePayIllegalCrashWithHoursTask")
public class RemindVoicePayIllegalCrashWithHoursTask extends IJobHandler {

    private Logger logger = LoggerFactory.getLogger(RemindVoicePayIllegalCrashWithHoursTask.class);
    @Autowired
    OrderStatusService orderStatusService;
    @Resource
    private RemindPayIllegalCrashService remindPayIllegalCrashService;

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        Transaction t = Cat.getProducer().newTransaction(CatConstants.XXL_JOB_CALL, "取车时间前2小时、1小时、30分钟語音提醒支付违章押金 定时任务");
        try {
            Cat.logEvent(CatConstants.XXL_JOB_METHOD,"remindVoicePayIllegalCrashWithHoursTask.execute");
            Cat.logEvent(CatConstants.XXL_JOB_PARAM,null);
            logger.info("开始执行 取车时间前2小时、1小时、30分钟語音提醒支付违章押金  定时器");
            XxlJobLogger.log("开始执行 取车时间前2小时、1小时、30分钟語音提醒支付违章押金 定时器");
            List<OrderDTO> orderNos = remindPayIllegalCrashService.findProcessOrderInfo();
            if (CollectionUtils.isEmpty(orderNos)) {
                return SUCCESS;
            }
            for (OrderDTO violateBO : orderNos) {
                    OrderStatusEntity orderStatusEntity = orderStatusService.getByOrderNo(violateBO.getOrderNo());
                    if (orderStatusEntity.getWzPayStatus().intValue() == 0 && orderStatusEntity.getStatus().intValue() < 8) {
                        //没有支付违章押金
                        boolean is2HoursAgo = SMSTaskDateTimeUtils.isArriveRentTime(DateUtil.asDateTime(violateBO.getExpRentTime()), 2);
                        remindPayIllegalCrashService.sendVoiceRemindVoicePayIllegalCrashWithHoursData(is2HoursAgo,  violateBO.getOrderNo(),"13628645717", DateUtils.formate(violateBO.getExpRentTime(), DateUtils.DATE_DEFAUTE));
                        boolean is1HoursAgo = SMSTaskDateTimeUtils.isArriveRentTime(DateUtil.asDateTime(violateBO.getExpRentTime()), 1);
                        remindPayIllegalCrashService.sendVoiceRemindVoicePayIllegalCrashWithHoursData(is1HoursAgo, violateBO.getOrderNo(),"13628645717", DateUtils.formate(violateBO.getExpRentTime(), DateUtils.DATE_DEFAUTE));
                        boolean isHoursAgo = SMSTaskDateTimeUtils.isArriveRentTime(DateUtil.asDateTime(violateBO.getExpRentTime()), 0.5);
                        remindPayIllegalCrashService.sendVoiceRemindVoicePayIllegalCrashWithHoursData(isHoursAgo, violateBO.getOrderNo(),"13628645717", DateUtils.formate(violateBO.getExpRentTime(), DateUtils.DATE_DEFAUTE));
                    }
            }
            logger.info("结束执行 取车时间前2小时、1小时、30分钟語音提醒支付违章押金 ");
            XxlJobLogger.log("结束执行 取车时间前2小时、1小时、30分钟語音提醒支付违章押金 ");
            return SUCCESS;
        } catch (Exception e) {
            XxlJobLogger.log("执行 取车时间前2小时、1小时、30分钟語音提醒支付违章押金 异常:" + e);
            logger.error("执行 取车时间前2小时、1小时、30分钟語音提醒支付违章押金 异常",e);
            Cat.logError("执行 取车时间前2小时、1小时、30分钟語音提醒支付违章押金 异常",e);
            return new ReturnT(FAIL.getCode(),e.toString());
        } finally {
            if (t != null) {
                t.complete();
            }
        }
    }
}
