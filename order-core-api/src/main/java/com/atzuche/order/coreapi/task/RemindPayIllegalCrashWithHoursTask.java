package com.atzuche.order.coreapi.task;

import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.commons.DateUtils;
import com.atzuche.order.commons.entity.orderDetailDto.OrderDTO;
import com.atzuche.order.commons.enums.OrderStatusEnum;
import com.atzuche.order.commons.vo.req.CancelOrderReqVO;
import com.atzuche.order.coreapi.listener.sms.SMSOrderBaseEventService;
import com.atzuche.order.coreapi.service.CancelOrderService;
import com.atzuche.order.coreapi.service.OrderSearchRemoteService;
import com.atzuche.order.coreapi.service.RemindPayIllegalCrashService;
import com.atzuche.order.coreapi.utils.SMSTaskDateTimeUtils;
import com.atzuche.order.mq.enums.ShortMessageTypeEnum;
import com.atzuche.order.mq.util.SmsParamsMapUtil;
import com.atzuche.order.parentorder.entity.OrderStatusEntity;
import com.atzuche.order.parentorder.service.OrderStatusService;
import com.autoyol.commons.utils.DateUtil;
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
import java.util.Map;

/**
 * @author 胡春林
 */
@Component
@JobHandler("remindPayIllegalCrashWithHoursTask")
public class RemindPayIllegalCrashWithHoursTask extends IJobHandler {

    private Logger logger = LoggerFactory.getLogger(RemindPayIllegalCrashWithHoursTask.class);
    @Autowired
    OrderStatusService orderStatusService;
    @Resource
    private RemindPayIllegalCrashService remindPayIllegalCrashService;

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        Transaction t = Cat.getProducer().newTransaction(CatConstants.XXL_JOB_CALL, "取车时间前24小时、12小时、6小时、2小时、1小时、30分钟提醒支付违章押金 定时任务");
        try {
            Cat.logEvent(CatConstants.XXL_JOB_METHOD,"RemindPayIllegalCrashWithHoursTask.execute");
            Cat.logEvent(CatConstants.XXL_JOB_PARAM,null);
            logger.info("开始执行 取车时间前24小时、12小时、6小时、2小时、1小时、30分钟提醒支付违章押金  定时器");
            XxlJobLogger.log("开始执行 取车时间前24小时、12小时、6小时、2小时、1小时、30分钟提醒支付违章押金 定时器");
            List<OrderDTO> orderNos = remindPayIllegalCrashService.findProcessOrderInfo();
            if (CollectionUtils.isEmpty(orderNos)) {
                return SUCCESS;
            }
            for (OrderDTO violateBO : orderNos) {
                OrderStatusEntity orderStatusEntity = orderStatusService.getByOrderNo(violateBO.getOrderNo());
                if (orderStatusEntity.getWzPayStatus().intValue() == 0 && orderStatusEntity.getStatus().intValue() < 8) {
                    //没有支付违章押金
                    boolean is24HoursAgo = SMSTaskDateTimeUtils.isArriveRentTime(DateUtil.asDateTime(violateBO.getExpRentTime()), 24);
                    remindPayIllegalCrashService.sendShortMessageData(is24HoursAgo, violateBO.getOrderNo());
                    boolean is12HoursAgo = SMSTaskDateTimeUtils.isArriveRentTime(DateUtil.asDateTime(violateBO.getExpRentTime()), 12);
                    remindPayIllegalCrashService.sendShortMessageData(is12HoursAgo, violateBO.getOrderNo());
                    boolean is6HoursAgo = SMSTaskDateTimeUtils.isArriveRentTime(DateUtil.asDateTime(violateBO.getExpRentTime()), 6);
                    remindPayIllegalCrashService.sendShortMessageData(is6HoursAgo, violateBO.getOrderNo());
                    boolean is2HoursAgo = SMSTaskDateTimeUtils.isArriveRentTime(DateUtil.asDateTime(violateBO.getExpRentTime()), 2);
                    remindPayIllegalCrashService.sendShortMessageData(is2HoursAgo, violateBO.getOrderNo());
                    boolean is1HoursAgo = SMSTaskDateTimeUtils.isArriveRentTime(DateUtil.asDateTime(violateBO.getExpRentTime()), 1);
                    remindPayIllegalCrashService.sendShortMessageData(is1HoursAgo, violateBO.getOrderNo());
                    boolean isHoursAgo = SMSTaskDateTimeUtils.isArriveRentTime(DateUtil.asDateTime(violateBO.getExpRentTime()), 0.5);
                    remindPayIllegalCrashService.sendShortMessageData(isHoursAgo, violateBO.getOrderNo());
                }
            }
            logger.info("结束执行 取车时间前24小时、12小时、6小时、2小时、1小时、30分钟提醒支付违章押金 ");
            XxlJobLogger.log("结束执行 取车时间前24小时、12小时、6小时、2小时、1小时、30分钟提醒支付违章押金 ");
            return SUCCESS;
        } catch (Exception e) {
            XxlJobLogger.log("执行 取车时间前24小时、12小时、6小时、2小时、1小时、30分钟提醒支付违章押金 异常:" + e);
            logger.error("执行 取车时间前24小时、12小时、6小时、2小时、1小时、30分钟提醒支付违章押金 异常",e);
            Cat.logError("执行 取车时间前24小时、12小时、6小时、2小时、1小时、30分钟提醒支付违章押金 异常",e);
            return new ReturnT(FAIL.getCode(),e.toString());
        } finally {
            if (t != null) {
                t.complete();
            }
        }
    }
}
