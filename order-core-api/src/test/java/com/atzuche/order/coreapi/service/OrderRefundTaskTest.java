/**
 * 
 */
package com.atzuche.order.coreapi.service;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.atzuche.order.cashieraccount.entity.CashierRefundApplyEntity;
import com.atzuche.order.cashieraccount.service.CashierPayService;
import com.atzuche.order.cashieraccount.service.notservice.CashierRefundApplyNoTService;
import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.coreapi.TemplateApplication;
import com.atzuche.order.coreapi.task.OrderRefundTask;
import com.autoyol.commons.utils.GsonUtils;
import com.dianping.cat.Cat;
import com.xxl.job.core.log.XxlJobLogger;

import lombok.extern.slf4j.Slf4j;

/**
 * @author jing.huang
 *
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
//@RunWith(SpringRunner.class)
@SpringBootTest(classes=TemplateApplication.class)
@WebAppConfiguration
public class OrderRefundTaskTest {
	private Logger logger = LoggerFactory.getLogger(OrderRefundTask.class);
	
	@Autowired
	CashierRefundApplyNoTService cashierRefundApplyNoTService;
	@Autowired
	CashierPayService cashierPayService;
	
	@Test
	public void testTask() {
//		List<CashierRefundApplyEntity> list = cashierRefundApplyNoTService.selectorderNoWaitingAll();
//        if (CollectionUtils.isNotEmpty(list)) {
//        	log.info("开始执行 退款订单任务 查询需要退换的 记录list={}", GsonUtils.toJson(list));
//        	for (int i = 0; i < list.size(); i++) {
//                try {
//                    cashierPayService.refundOrderPay(list.get(i));
//                } catch (Exception e) {
//                	log.error("执行 退款操作异常 异常 {},{}", GsonUtils.toJson(list.get(i)), e);
//                }
//            }
//        }else{
//        	log.info("开始执行 退款订单任务 未查询需要退换的 记录list=0");
//        }
        
		//保持原样
		logger.info("开始执行 退款订单任务");
        List<CashierRefundApplyEntity> list = cashierRefundApplyNoTService.selectorderNoWaitingAll();
        if (CollectionUtils.isNotEmpty(list)) {
        	logger.info("开始执行 退款订单任务 查询需要退换的 记录list={}", GsonUtils.toJson(list));
        	for (int i = 0; i < list.size(); i++) {
                Cat.logEvent(CatConstants.XXL_JOB_PARAM, GsonUtils.toJson(list.get(i)));
                try {
                    cashierPayService.refundOrderPay(list.get(i));
                } catch (Exception e) {
                    logger.error("执行 退款操作异常 异常 {}", e); //GsonUtils.toJson(list.get(i)), 
                    Cat.logError("执行 退款操作异常 异常 {}", e);
                    XxlJobLogger.log("执行 退款操作异常 异常,params=[{}]"+GsonUtils.toJson(list.get(i)));  //GsonUtils.toJson(list.get(i))
                }
            }
        }else{
        	logger.info("开始执行 退款订单任务 未查询需要退换的 记录list=0");
        }
        logger.info("结束执行 退款 ");
        XxlJobLogger.log("结束执行 退款 ");
	        
	}
}
