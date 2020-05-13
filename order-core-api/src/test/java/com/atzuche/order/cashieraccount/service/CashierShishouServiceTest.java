package com.atzuche.order.cashieraccount.service;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.atzuche.order.coreapi.TemplateApplication;
import com.atzuche.order.coreapi.service.CashierNoTServiceTest;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
//@RunWith(SpringRunner.class)
@SpringBootTest(classes=TemplateApplication.class)
@WebAppConfiguration
public class CashierShishouServiceTest {
	@Autowired
	CashierShishouService cashierShishouService;
	
//	String orderNo = "70010280500299";
//	String memNo = "643164996";
	
	String orderNo = "44772111500299";
	String memNo = "643164996";
	
	@Test
	public void testCheckRentAmountShishou() {
		boolean f = cashierShishouService.checkRentAmountShishou(orderNo, memNo);
		assertTrue(f);
	}

	@Test
	public void testCheckRentShishou() {
		boolean f = cashierShishouService.checkRentShishou(orderNo, memNo);
		assertTrue(f);
	}

	@Test
	public void testCheckDepositShishou() {
		boolean f = cashierShishouService.checkDepositShishou(orderNo, memNo);
		assertTrue(f);
	}

}
