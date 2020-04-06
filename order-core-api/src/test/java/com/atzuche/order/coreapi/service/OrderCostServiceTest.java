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
import com.autoyol.commons.utils.GsonUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author jing.huang
 *
 */
@Slf4j
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
		req.setMemNo("678365276");
		req.setOrderNo("70916330400299");
		req.setSubOrderNo("7091633040029910010");
		OrderRenterCostResVO res = orderCostService.orderCostRenterGet(req);
		System.err.println("renter cost res toString="+res.toString());
		log.info("renter cost res toString="+GsonUtils.toJson(res));
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
