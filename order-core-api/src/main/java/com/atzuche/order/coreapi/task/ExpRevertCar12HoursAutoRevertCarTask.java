package com.atzuche.order.coreapi.task;

import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.commons.vo.req.ReturnCarReqVO;
import com.atzuche.order.coreapi.service.OrderSearchRemoteService;
import com.atzuche.order.coreapi.service.OwnerReturnCarService;
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
 * ExpRevertCar12HoursAutoRevertCarTask
 *
 * @author shisong
 * @date 2020/1/15
 */
@Component
@JobHandler("expRevertCar12HoursAutoRevertCarTask")
public class ExpRevertCar12HoursAutoRevertCarTask extends IJobHandler {

    private Logger logger = LoggerFactory.getLogger(RevertCar4HoursAutoSettleTask.class);

    @Resource
    private OrderSearchRemoteService orderSearchRemoteService;

    @Resource
    private OwnerReturnCarService ownerReturnCarService;

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        Transaction t = Cat.getProducer().newTransaction(CatConstants.XXL_JOB_CALL, "定时查询 预计还车12小时后，自动还车 定时任务");
        try {
            Cat.logEvent(CatConstants.XXL_JOB_METHOD,"expRevertCar12HoursAutoRevertCarTask.execute");
            Cat.logEvent(CatConstants.XXL_JOB_PARAM,null);
            logger.info("开始执行 预计还车12小时后，自动还车  定时器");
            XxlJobLogger.log("开始执行 预计还车12小时后，自动还车 定时器");

            logger.info("查询 远程调用 查询预计还车12小时后的订单列表,查询开始");
            XxlJobLogger.log("查询 远程调用 查询预计还车12小时后的订单列表,查询开始" );

            List<String> orderNos = orderSearchRemoteService.queryOrderNosWithExpRevertCar12Hours();

            logger.info("查询 远程调用 查询预计还车12小时后的订单列表,查询结果 models:[{}]", orderNos);
            XxlJobLogger.log("查询 远程调用 查询预计还车12小时后的订单列表,查询结果 models:" + orderNos);

            if(CollectionUtils.isNotEmpty(orderNos)){
                for (String orderNo : orderNos) {
                    ReturnCarReqVO reqVO = new ReturnCarReqVO();
                    reqVO.setOrderNo(orderNo);
                    reqVO.setOperatorName("System");
                    ownerReturnCarService.returnCar(reqVO);
                }
            }
            logger.info("结束执行 预计还车12小时后，自动还车");
            XxlJobLogger.log("结束执行 预计还车12小时后，自动还车");
            return SUCCESS;
        } catch (Exception e) {
            XxlJobLogger.log("执行 预计还车12小时后，自动还车 异常:",e);
            logger.error("执行 预计还车12小时后，自动还车 异常",e);
            Cat.logError("执行 预计还车12小时后，自动还车 异常",e);
            return new ReturnT(FAIL.getCode(),e.toString());
        } finally {
            if (t != null) {
                t.complete();
            }
        }
    }

}
