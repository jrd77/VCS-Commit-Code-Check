/**
 * 
 */
package com.atzuche.order.coreapi.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.atzuche.order.accountrenterrentcost.service.notservice.AccountRenterCostDetailNoTService;
import com.atzuche.order.coreapi.TemplateApplication;

import lombok.extern.slf4j.Slf4j;

/**
 * @author jing.huang
 *
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
//@RunWith(SpringRunner.class)
@SpringBootTest(classes=TemplateApplication.class)
@WebAppConfiguration
public class AccountRenterCostDetailNoTServiceTest {
	@Autowired
	AccountRenterCostDetailNoTService accountRenterCostDetailNoTService;
	
	@Test
	public void testRefund() {
		int amt = accountRenterCostDetailNoTService.getRentCostRefundByWallet("90607132400299", "751661812");
		log.info("钱包退款amt="+amt);
	}
}
