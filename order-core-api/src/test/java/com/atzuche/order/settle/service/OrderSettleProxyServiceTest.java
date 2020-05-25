package com.atzuche.order.settle.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.atzuche.order.coreapi.TemplateApplication;
import com.atzuche.order.settle.service.notservice.OrderSettleProxyService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
//@RunWith(SpringRunner.class)
@SpringBootTest(classes=TemplateApplication.class)
@WebAppConfiguration
public class OrderSettleProxyServiceTest {
	@Autowired
	OrderSettleProxyService orderSettleProxyService;
	
	@Test
	public void testCheckMileageData() {
		//完整
		boolean f1 = orderSettleProxyService.checkMileageData("1547424140029910020");
		assertTrue(f1);
		//缺的，根据租客的来计算。
		boolean f2 = orderSettleProxyService.checkMileageData("9311918150029910050");
		assertFalse(f2);
		//没有记录
		boolean f3 = orderSettleProxyService.checkMileageData("57391181500299");
		assertFalse(f3);
		
	}
	
	
	@Test
	public void testGetCostTypeEnumByConsoleCost() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetCostTypeEnumBySubsidy() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetCostTypeEnumByFine() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetCostBaseRent() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetMileageAmtDTO() {
		fail("Not yet implemented");
	}


	@Test
	public void testGetCashierRefundApply() {
		fail("Not yet implemented");
	}

}
