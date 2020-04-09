/**
 * 
 */
package com.atzuche.order.coreapi.service;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.atzuche.order.cashieraccount.entity.CashierRefundApplyEntity;
import com.atzuche.order.cashieraccount.service.CashierPayService;
import com.atzuche.order.cashieraccount.service.notservice.CashierRefundApplyNoTService;
import com.atzuche.order.coreapi.TemplateApplication;
import com.autoyol.commons.utils.GsonUtils;

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
	@Autowired
	CashierRefundApplyNoTService cashierRefundApplyNoTService;
	@Autowired
	CashierPayService cashierPayService;
	
	@Test
	public void testTask() {
		List<CashierRefundApplyEntity> list = cashierRefundApplyNoTService.selectorderNoWaitingAll();
        if (CollectionUtils.isNotEmpty(list)) {
        	log.info("开始执行 退款订单任务 查询需要退换的 记录list={}", GsonUtils.toJson(list));
        	for (int i = 0; i < list.size(); i++) {
                try {
                    cashierPayService.refundOrderPay(list.get(i));
                } catch (Exception e) {
                	log.error("执行 退款操作异常 异常 {},{}", GsonUtils.toJson(list.get(i)), e);
                }
            }
        }else{
        	log.info("开始执行 退款订单任务 未查询需要退换的 记录list=0");
        }
        
	}
}
