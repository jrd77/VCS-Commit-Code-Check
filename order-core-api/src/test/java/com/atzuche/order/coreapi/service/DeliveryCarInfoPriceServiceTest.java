package com.atzuche.order.coreapi.service;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.atzuche.order.coreapi.TemplateApplication;
import com.atzuche.order.delivery.service.delivery.DeliveryCarInfoPriceService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
//@RunWith(SpringRunner.class)
@SpringBootTest(classes=TemplateApplication.class)
@WebAppConfiguration
public class DeliveryCarInfoPriceServiceTest {
	@Autowired
	DeliveryCarInfoPriceService deliveryCarInfoPriceService;
	
	@Test
	public void testGetOwnerPlatFormOilServiceChargeByOrderNo() {
		int amt = deliveryCarInfoPriceService.getOwnerPlatFormOilServiceChargeByOrderNo("28804131200299");
		log.info("平台加油服务费::"+amt);
	}
	
	@Test
	public void testGetOilPriceByCityCodeAndType() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetOilCostByRenterOrderNo() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetDistanceKM() {
		fail("Not yet implemented");
	}

	@Test
	public void testCreateOwnerGetAndReturnCarDTO() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetDistributionCost() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetMileageAmtEntity() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetServiceExpense() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetDeliveryCarFee() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetOwnerPlatFormOilServiceCharge() {
		fail("Not yet implemented");
	}

	

}
