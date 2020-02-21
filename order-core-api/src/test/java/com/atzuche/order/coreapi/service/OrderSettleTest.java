package com.atzuche.order.coreapi.service;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.atzuche.order.coreapi.TemplateApplication;

@RunWith(SpringJUnit4ClassRunner.class)
//@RunWith(SpringRunner.class)
@SpringBootTest(classes=TemplateApplication.class)
@WebAppConfiguration
public class OrderSettleTest {
	@Autowired
	OrderSettle orderSettle; 
	
	@Test
	public void testSettleOrder() {
		fail("Not yet implemented");
	}
	
	@Test
	public void testSettleWzOrder() {
		//orderSettle.settleWzOrder("78133341200299");
		//52083341200299
		orderSettle.settleWzOrder("52083341200299");
	}

}
