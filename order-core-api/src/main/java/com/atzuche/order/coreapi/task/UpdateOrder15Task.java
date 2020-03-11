package com.atzuche.order.coreapi.task;

import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.commons.vo.req.ModifyApplyHandleReq;
import com.atzuche.order.coreapi.service.ModifyOrderOwnerConfirmService;
import com.atzuche.order.coreapi.service.OrderSearchRemoteService;
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
 * updateOrder15Task
 *
 * @author shisong
 * @date 2020/3/5
 */
@Component
@JobHandler("updateOrder15Task")
public class UpdateOrder15Task extends IJobHandler {

    private Logger logger = LoggerFactory.getLogger(UpdateOrder15Task.class);

    @Resource
    private OrderSearchRemoteService orderSearchRemoteService;

    @Resource
    private ModifyOrderOwnerConfirmService modifyOrderOwnerConfirmService;

    @Override
    public ReturnT<String> execute(String s){
        Transaction t = Cat.getProducer().newTransaction(CatConstants.XXL_JOB_CALL, "定时查询 车主收到修改申请15分钟后没有操作,自动取消 定时任务");
        try {
            Cat.logEvent(CatConstants.XXL_JOB_METHOD,"updateOrder15Task.execute");
            Cat.logEvent(CatConstants.XXL_JOB_PARAM,null);
            logger.info("开始执行 车主收到修改申请15分钟后没有操作,自动取消  定时器");
            XxlJobLogger.log("开始执行 车主收到修改申请15分钟后没有操作,自动取消 定时器");

            logger.info("查询 远程调用 车主收到修改申请15分钟后没有操作的列表,查询开始");
            XxlJobLogger.log("查询 远程调用 车主收到修改申请15分钟后没有操作的列表,查询开始" );

            List<String> orderNos = orderSearchRemoteService.queryRenterOrderChangeApply();

            logger.info("查询 远程调用 车主收到修改申请15分钟后没有操作的列表,查询结果 models:[{}]", orderNos);
            XxlJobLogger.log("查询 远程调用 车主收到修改申请15分钟后没有操作的列表,查询结果 models:" + orderNos);

            if(CollectionUtils.isNotEmpty(orderNos)){
                for (String orderNo : orderNos) {
                    try {
                        logger.info("执行 车主收到修改申请15分钟后没有操作,自动取消 orderNo:[{}]",orderNo);
                        ModifyApplyHandleReq dto = new ModifyApplyHandleReq();
                        dto.setModifyApplicationId(orderNo);
                        dto.setFlag("0");
                        modifyOrderOwnerConfirmService.modifyConfirm(dto);
                    } catch (Exception e) {
                        XxlJobLogger.log("执行 车主收到修改申请15分钟后没有操作,自动取消 异常:"+ e);
                        logger.error("执行 车主收到修改申请15分钟后没有操作,自动取消 异常 orderNo:[{}] ,e:[{}]",orderNo,e);
                        Cat.logError("执行 车主收到修改申请15分钟后没有操作,自动取消 异常",e);
                        t.setStatus(e);
                    }
                }
            }
            logger.info("结束执行 车主收到修改申请15分钟后没有操作,自动取消 ");
            XxlJobLogger.log("结束执行 车主收到修改申请15分钟后没有操作,自动取消 ");
            t.setStatus(Transaction.SUCCESS);
            return SUCCESS;
        } catch (Exception e) {
            XxlJobLogger.log("执行 车主收到修改申请15分钟后没有操作,自动取消 异常:"+ e);
            logger.error("执行 车主收到修改申请15分钟后没有操作,自动取消 异常",e);
            Cat.logError("执行 车主收到修改申请15分钟后没有操作,自动取消 异常",e);
            t.setStatus(e);
            return new ReturnT(FAIL.getCode(),e.toString());
        } finally {
            if (t != null) {
                t.complete();
            }
        }
    }

}
