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
		
		orderSettle.settleOrder("28261181200299");
	}
	
	@Test
	public void testSettleWzOrder() {
		//orderSettle.settleWzOrder("78133341200299");
		//52083341200299
//		orderSettle.settleWzOrder("52083341200299");
		//52125202200299
		//orderSettle.settleWzOrder("52125202200299");
		//61175312200299
		orderSettle.settleWzOrder("61175312200299");
	}

}
