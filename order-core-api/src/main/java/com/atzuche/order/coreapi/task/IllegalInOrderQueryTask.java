package com.atzuche.order.coreapi.task;

import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.coreapi.service.IllegalToDoService;
import com.atzuche.order.renterwz.vo.IllegalToDO;
import com.atzuche.order.coreapi.service.OrderSearchRemoteService;
import com.atzuche.order.renterwz.service.RenterOrderWzEverydayTodoService;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import com.xxl.job.core.handler.IJobHandler;

import java.util.List;

/**
 * IllegalInOrderQueryTask
 *
 * @author shisong
 * @date 2020/1/2
 */
@Component
@JobHandler("illegalInOrderQueryTask")
public class IllegalInOrderQueryTask extends IJobHandler{

    private Logger logger = LoggerFactory.getLogger(IllegalInOrderQueryTask.class);

    @Resource
    private OrderSearchRemoteService orderSearchRemoteService;

    @Resource
    private RenterOrderWzEverydayTodoService renterOrderWzEverydayTodoService;

    @Resource
    private IllegalToDoService illegalToDoService;

    @Value("${start.flag}")
    private int START_FLAG;

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        Transaction t = Cat.getProducer().newTransaction(CatConstants.XXL_JOB_CALL, "每天定时查询当前进行中的订单，查询是否有违章记录");
        try {
            Cat.logEvent(CatConstants.XXL_JOB_METHOD,"IllegalInOrderQueryTask.execute");
            Cat.logEvent(CatConstants.XXL_JOB_PARAM,null);
            logger.info("开始执行 每天定时查询当前进行中的订单，查询是否有违章记录  定时器");
            XxlJobLogger.log("开始执行 每天定时查询当前进行中的订单，查询是否有违章记录 定时器");

            logger.info("查询 远程调用 查询当前进行中的订单列表,查询开始");
            XxlJobLogger.log("查询 远程调用 查询当前进行中的订单列表,查询开始" );

            List<IllegalToDO> list = orderSearchRemoteService.violateProcessOrder();

            logger.info("查询 远程调用 查询当前进行中的订单列表,查询结果 models:[{}]", list);
            XxlJobLogger.log("查询 远程调用 查询当前进行中的订单列表,查询结果 models:" + list);

            if(CollectionUtils.isNotEmpty(list)){
                list = renterOrderWzEverydayTodoService.queryTodo(list);
                if(START_FLAG == 1) {
                    illegalToDoService.queryNewDetail(list, true, "everyday");
                    logger.info("订单内违章记录查询并处理完成,todoList.size={},todoList={}", list.size(), list);
                }
            }
            logger.info("结束执行 每天定时查询当前进行中的订单，查询是否有违章记录 ");
            XxlJobLogger.log("结束执行 每天定时查询当前进行中的订单，查询是否有违章记录 ");
            return SUCCESS;
        } catch (Exception e) {
            XxlJobLogger.log("执行 每天定时查询当前进行中的订单，查询是否有违章记录 异常:",e);
            logger.error("执行 每天定时查询当前进行中的订单，查询是否有违章记录 异常",e);
            Cat.logError("执行 每天定时查询当前进行中的订单，查询是否有违章记录 异常",e);
            return new ReturnT(FAIL.getCode(),e.toString());
        } finally {
            if (t != null) {
                t.complete();
            }
        }
    }
}
