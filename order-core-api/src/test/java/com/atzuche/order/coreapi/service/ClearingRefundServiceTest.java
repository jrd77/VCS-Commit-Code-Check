package com.atzuche.order.coreapi.service;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.atzuche.order.cashieraccount.entity.CashierEntity;
import com.atzuche.order.cashieraccount.service.notservice.CashierNoTService;
import com.atzuche.order.commons.vo.req.ClearingRefundReqVO;
import com.atzuche.order.coreapi.TemplateApplication;

import io.swagger.annotations.Authorization;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
//@RunWith(SpringRunner.class)
@SpringBootTest(classes=TemplateApplication.class)
@WebAppConfiguration
public class ClearingRefundServiceTest {
	@Autowired
	ClearingRefundService  clearingRefundService;
	@Autowired
	CashierNoTService cashierNoTService;
	
	@Test
	public void testClearingRefundToPerformance() {
		fail("Not yet implemented");
	}

	@Test
	public void testClearingRefundToQuery() {
		fail("Not yet implemented");
	}

	@Test
	public void testClearingRefundSubmitToRefund() {
		String orderNo = "72336210600299";
		String  payTransNo = "200103061272336210600299966";
		
		ClearingRefundReqVO clearingRefundReqVO = new ClearingRefundReqVO();
		clearingRefundReqVO.setAmt(1);
		clearingRefundReqVO.setOperateName("admin");
		clearingRefundReqVO.setOrderNo(orderNo);
		clearingRefundReqVO.setPaySource("06");
		clearingRefundReqVO.setPayTransNo(payTransNo);
		clearingRefundReqVO.setPayType("04");
		CashierEntity cashierEntity = cashierNoTService.getCashierBypayTransNo(orderNo, payTransNo);
		 Integer response = clearingRefundService.clearingRefundSubmitToRefund(clearingRefundReqVO,cashierEntity);
		
	}

	@Test
	public void testRoutingRulesQueryQueryVo() {
		fail("Not yet implemented");
	}

	@Test
	public void testRoutingRulesQueryPreRoutingPayRequest() {
		fail("Not yet implemented");
	}

}
