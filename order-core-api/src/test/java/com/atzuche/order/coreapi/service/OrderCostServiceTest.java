package com.atzuche.order.coreapi.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.atzuche.order.commons.vo.req.OrderCostReqVO;
import com.atzuche.order.commons.vo.res.OrderOwnerCostResVO;
import com.atzuche.order.commons.vo.res.OrderRenterCostResVO;
import com.atzuche.order.coreapi.TemplateApplication;

/**
 * 
 * @author jing.huang
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
//@RunWith(SpringRunner.class)
@SpringBootTest(classes=TemplateApplication.class)
@WebAppConfiguration
public class OrderCostServiceTest {
	@Autowired
	OrderCostService orderCostService;
	
	@Test
	public void testOrderCostRenterGet() {
		OrderCostReqVO req = new OrderCostReqVO();
		//封装参数
		req.setMemNo("");
		req.setOrderNo("");
		req.setSubOrderNo("");
		OrderRenterCostResVO res = orderCostService.orderCostRenterGet(req);
		System.err.println("renter cost res toString="+res.toString());
	}

	@Test
	public void testOrderCostOwnerGet() {
		OrderCostReqVO req = new OrderCostReqVO();
		//封装参数
		req.setMemNo("");
		req.setOrderNo("");
		req.setSubOrderNo("");
		OrderOwnerCostResVO res = orderCostService.orderCostOwnerGet(req);
		System.err.println("owner cost res toString="+res.toString());
	}

}
