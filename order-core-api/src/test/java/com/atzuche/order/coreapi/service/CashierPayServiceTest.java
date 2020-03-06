package com.atzuche.order.coreapi.service;

import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.atzuche.order.cashieraccount.service.CashierPayService;
import com.atzuche.order.cashieraccount.vo.req.pay.OrderPayReqVO;
import com.atzuche.order.cashieraccount.vo.res.AccountPayAbleResVO;
import com.atzuche.order.cashieraccount.vo.res.OrderPayableAmountResVO;
import com.atzuche.order.coreapi.TemplateApplication;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
//@RunWith(SpringRunner.class)
@SpringBootTest(classes=TemplateApplication.class)
@WebAppConfiguration
public class CashierPayServiceTest {
	@Autowired
	CashierPayService cashierPayService;
	
	@Test
	public void testPayCallBack() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetPaySignStr() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetOrderPayableAmount() {
		OrderPayReqVO orderPayReqVO = new OrderPayReqVO();
		orderPayReqVO.setIsUseWallet(0);
		orderPayReqVO.setMenNo("802589690");
		orderPayReqVO.setOrderNo("47035250300299");
		List<String> payKinds = Lists.newArrayList();
		payKinds.add("03");
		orderPayReqVO.setPayKind(payKinds);
		OrderPayableAmountResVO vo = cashierPayService.getOrderPayableAmount(orderPayReqVO);
		log.info("vo="+vo.toString());
		List<AccountPayAbleResVO> ls = vo.getAccountPayAbles();
		for (AccountPayAbleResVO accountPayAbleResVO : ls) {
			log.info("accountPayAbleResVO = "+accountPayAbleResVO.toString());
		}
	}

	@Test
	public void testGetRentCostBufu() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetRealRentCost() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetOrderPayVO() {
		fail("Not yet implemented");
	}

	@Test
	public void testRefundOrderPay() {
		fail("Not yet implemented");
	}

}
