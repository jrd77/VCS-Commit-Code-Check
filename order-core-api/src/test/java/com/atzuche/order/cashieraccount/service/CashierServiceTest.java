package com.atzuche.order.cashieraccount.service;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.atzuche.order.coreapi.TemplateApplication;

import junit.framework.Assert;  
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
//@RunWith(SpringRunner.class)
@SpringBootTest(classes=TemplateApplication.class)
@WebAppConfiguration
public class CashierServiceTest {
	@Autowired
	CashierService cashierService;
	

	@Test
	public void testGetWalletDeductAmt() {
		String orderNo = "69995251200299";
		List<String> payKind = new ArrayList<String>();
		payKind.add("11");
		payKind.add("01");
		int amt = cashierService.getWalletDeductAmt(orderNo, payKind);
		System.out.println(amt);
//		Assert.assertEquals(amt, 464);
		org.junit.Assert.assertEquals(amt, 73);
	}
	
	@Test
	public void testGetAccountRenterCostSettle() {
		fail("Not yet implemented");
	}

	@Test
	public void testInsertRenterDeposit() {
		fail("Not yet implemented");
	}

	@Test
	public void testDetainRenterDeposit() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetRenterDepositEntity() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetRenterDeposit() {
		fail("Not yet implemented");
	}

	@Test
	public void testInsertRenterWZDeposit() {
		fail("Not yet implemented");
	}

	@Test
	public void testDetainRenterWZDeposit() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetRenterDetain() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetRenterWZDeposit() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetRenterWZDepositEntity() {
		fail("Not yet implemented");
	}

	@Test
	public void testDeductDebt() {
		fail("Not yet implemented");
	}

	@Test
	public void testDeductDebtByRentCost() {
		fail("Not yet implemented");
	}

	@Test
	public void testDeductDebtByOwnerIncome() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetAccountDebtByMemNo() {
		fail("Not yet implemented");
	}

	@Test
	public void testCreateDebt() {
		fail("Not yet implemented");
	}

	@Test
	public void testRefundDeposit() {
		fail("Not yet implemented");
	}

	@Test
	public void testRefundDepositPreAuth() {
		fail("Not yet implemented");
	}

	@Test
	public void testRefundRentCost() {
		fail("Not yet implemented");
	}

	@Test
	public void testRefundRentCostWallet() {
		fail("Not yet implemented");
	}

	@Test
	public void testRefundWZDeposit() {
		fail("Not yet implemented");
	}

	@Test
	public void testRefundWZDepositPreAuth() {
		fail("Not yet implemented");
	}

	@Test
	public void testRefundDepositPreAuthAll() {
		fail("Not yet implemented");
	}

	@Test
	public void testRefundDepositPurchase() {
		fail("Not yet implemented");
	}

	@Test
	public void testRefundWzDepositPreAuthAll() {
		fail("Not yet implemented");
	}

	@Test
	public void testRefundWzDepositPurchase() {
		fail("Not yet implemented");
	}

	@Test
	public void testInsertOwnerIncomeExamine() {
		fail("Not yet implemented");
	}

	@Test
	public void testExamineOwnerIncomeExamine() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetOwnerIncomeByOrderAndType() {
		fail("Not yet implemented");
	}

	@Test
	public void testChangeWZDepositCost() {
		fail("Not yet implemented");
	}

	@Test
	public void testCallBackSuccess() {
		fail("Not yet implemented");
	}

	@Test
	public void testRefundCallBackSuccess() {
		fail("Not yet implemented");
	}

	@Test
	public void testSaveWalletPaylOrderStatusInfo() {
		fail("Not yet implemented");
	}

	@Test
	public void testSaveCancelOrderStatusInfo() {
		fail("Not yet implemented");
	}

	@Test
	public void testPayOrderCallBackSuccess() {
		fail("Not yet implemented");
	}

	@Test
	public void testRefundOrderCallBackSuccess() {
		fail("Not yet implemented");
	}

	@Test
	public void testSendOrderPayDepositSuccess() {
		fail("Not yet implemented");
	}

	@Test
	public void testSendOrderPayRentCostSuccess() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetCashierRentCostsByOrderNo() {
		fail("Not yet implemented");
	}

	@Test
	public void testSaveRentCostDebt() {
		fail("Not yet implemented");
	}

	@Test
	public void testSaveDepositDebt() {
		fail("Not yet implemented");
	}

	@Test
	public void testSaveOwnerIncomeDebt() {
		fail("Not yet implemented");
	}


}
