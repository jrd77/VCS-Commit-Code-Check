package com.atzuche.order.admin.service;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.atzuche.order.admin.AdminSpringBoot;
import com.atzuche.order.admin.vo.req.cost.RenterCostReqVO;
import com.atzuche.order.admin.vo.resp.order.cost.OrderRenterCostResVO;

@RunWith(SpringJUnit4ClassRunner.class)
//@RunWith(SpringRunner.class)
@SpringBootTest(classes=AdminSpringBoot.class)
@WebAppConfiguration
public class OrderCostServiceTest {
	@Autowired
	OrderCostService orderCostService;
	@Test
	public void testCalculateRenterOrderCost() {
		RenterCostReqVO renterCostReqVO = new RenterCostReqVO();
		renterCostReqVO.setOrderNo("28804131200299");
		renterCostReqVO.setRenterOrderNo("2880413120029910010");
		try {
			OrderRenterCostResVO res = orderCostService.calculateRenterOrderCost(renterCostReqVO);
			System.err.println("res toString="+res.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testCalculateOwnerOrderCost() {
		fail("Not yet implemented");
	}

}
