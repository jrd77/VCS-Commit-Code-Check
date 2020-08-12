package com.atzuche.order.cashieraccount.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.atzuche.order.coreapi.TemplateApplication;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
//@RunWith(SpringRunner.class)
@SpringBootTest(classes=TemplateApplication.class)
@WebAppConfiguration
public class MemberSecondSettleServiceTest {
	@Autowired
	MemberSecondSettleService  memberSecondSettleService;
	
	String orderNo =  "45666669999";
	Integer memNo = 1111;
	@Test
	public void testInitDepositMemberSecondSettle() {
		memberSecondSettleService.initDepositMemberSecondSettle(orderNo, memNo);
	}

	@Test
	public void testInitDepositWzMemberSecondSettle() {
		memberSecondSettleService.initDepositWzMemberSecondSettle(orderNo, memNo);
	}

}
