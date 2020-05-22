package com.atzuche.order.settle.service;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.atzuche.order.cashieraccount.service.notservice.CashierRefundApplyNoTService;
import com.atzuche.order.coreapi.TemplateApplication;
import com.atzuche.order.rentercost.service.OrderSupplementDetailService;
import com.atzuche.order.wallet.api.DebtDetailVO;
import com.autoyol.commons.utils.GsonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
//@RunWith(SpringRunner.class)
@SpringBootTest(classes=TemplateApplication.class)
@WebAppConfiguration
public class AccountDebtServiceTest {
	@Autowired
	AccountDebtService accountDebtService;
	@Autowired
	OrderSupplementDetailService orderSupplementDetailService;
	@Autowired
	CashierRefundApplyNoTService cashierRefundApplyNoTService;
	
	String renterNo = "474031391";
	@Test
	public void testGetTotalNewDebtAndOldDebtAmt() {
        DebtDetailVO debtDetailVO = accountDebtService.getTotalNewDebtAndOldDebtAmt(renterNo);
        if(debtDetailVO != null) {
        	log.info("1debtDetailVO result=[{}]",GsonUtils.toJson(debtDetailVO));
			debtDetailVO.setNoPaySupplementAmt(orderSupplementDetailService.getSumNoPaySupplementAmt(renterNo));
			log.info("2debtDetailVO result=[{}]",GsonUtils.toJson(debtDetailVO));
			 //4小时
			Integer sum = cashierRefundApplyNoTService.getCashierRefundApplyByTimeForPreAuthSum(renterNo);
			log.info("debtDetailVO sum=[{}]",sum);
			debtDetailVO.setOrderDebtAmt(debtDetailVO.getOrderDebtAmt().intValue() + Math.abs(sum));
			log.info("3debtDetailVO result=[{}]",GsonUtils.toJson(debtDetailVO));
        }else {
        	log.info("null.........");
        }
	}
	
	@Test
	public void testGetAccountDebtByMemNo() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetAccountDebtNumByMemNo() {
		fail("Not yet implemented");
	}

	@Test
	public void testDeductDebt() {
		fail("Not yet implemented");
	}

	@Test
	public void testInsertDebt() {
		fail("Not yet implemented");
	}

	@Test
	public void testDeductOldDebt() {
		fail("Not yet implemented");
	}

	@Test
	public void testCalDeductOldDebt() {
		fail("Not yet implemented");
	}

	@Test
	public void testConvertToDebtReceivableaDetail() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetAccountDebtReceivableaDetailEntity() {
		fail("Not yet implemented");
	}



}
