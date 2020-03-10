package com.atzuche.order.coreapi.service;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.atzuche.order.coreapi.TemplateApplication;
import com.atzuche.order.settle.service.OrderSettleService;
import com.atzuche.order.settle.vo.req.OwnerCosts;
import com.atzuche.order.settle.vo.req.RentCosts;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
//@RunWith(SpringRunner.class)
@SpringBootTest(classes=TemplateApplication.class)
@WebAppConfiguration
public class OrderSettleServiceTest {
	@Autowired
	OrderSettleService orderSettleService;
	
	@Test
	public void testGetRenterCostByOrderNo() {
		fail("Not yet implemented");
	}

	@Test
	public void testPreRenterSettleOrder() {
		//28261181200299
		RentCosts cost = orderSettleService.preRenterSettleOrder("28261181200299", "2826118120029910010");
		log.info("租客应收（未抵扣欠款）:"+cost.getRenterCostAmtFinal());
		log.info("租客应收（超里程）:"+cost.getMileageAmt().toString());
	}
	
	@Test
	public void testPreOwnerSettleOrder() {
//		OwnerCosts cost = orderSettleService.preOwnerSettleOrder("13004340300299", "1300434030029910011");
//		log.info("预计收益（未抵扣欠款）:"+cost.getOwnerCostAmtFinal());
//		//28804131200299
		OwnerCosts cost = orderSettleService.preOwnerSettleOrder("28804131200299", "2880413120029910011");
		log.info("预计收益（未抵扣欠款）:"+cost.getOwnerCostAmtFinal());
		
	}

	@Test
	public void testSettleOrder() {
		fail("Not yet implemented");
	}

	@Test
	public void testSettleOrderCancel() {
		fail("Not yet implemented");
	}

}
