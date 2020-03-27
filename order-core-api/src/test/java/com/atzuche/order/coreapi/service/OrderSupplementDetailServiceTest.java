package com.atzuche.order.coreapi.service;

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
import com.atzuche.order.rentercost.entity.OrderSupplementDetailEntity;
import com.atzuche.order.rentercost.service.OrderSupplementDetailService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
//@RunWith(SpringRunner.class)
@SpringBootTest(classes=TemplateApplication.class)
@WebAppConfiguration
public class OrderSupplementDetailServiceTest {
	@Autowired
	OrderSupplementDetailService orderSupplementDetailService;
	
	String memNo = "";
	@Test
	public void testListOrderSupplementDetailByMemNo() {
		List<OrderSupplementDetailEntity> lsEntity = orderSupplementDetailService.listOrderSupplementDetailByMemNo(memNo);
    	if(lsEntity == null) {
    		lsEntity = new ArrayList<OrderSupplementDetailEntity>();
    	}
    	for (OrderSupplementDetailEntity entity : lsEntity) {
			log.info("entity="+entity.toString());
		}
    	log.info("===========================补付记录OK");
	}
	
	
	@Test
	public void testListOrderSupplementDetailByOrderNoAndMemNo() {
		fail("Not yet implemented");
	}


	@Test
	public void testHandleConsoleData() {
		fail("Not yet implemented");
	}

	@Test
	public void testHandleDebtData() {
		fail("Not yet implemented");
	}

	@Test
	public void testListOrderSupplementDetailByMemNoAndOrderNos() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetTotalSupplementAmt() {
		fail("Not yet implemented");
	}

	@Test
	public void testSaveOrderSupplementDetail() {
		fail("Not yet implemented");
	}

	@Test
	public void testUpdatePayFlagByIdIntegerIntegerDateInteger() {
		fail("Not yet implemented");
	}

	@Test
	public void testUpdatePayFlagByIdIntegerIntegerDate() {
		fail("Not yet implemented");
	}

	@Test
	public void testListOrderSupplementDetailByOrderNo() {
		fail("Not yet implemented");
	}

	@Test
	public void testUpdateDeleteById() {
		fail("Not yet implemented");
	}

	@Test
	public void testQueryNotPaySupplementByOrderNoAndMemNo() {
		fail("Not yet implemented");
	}

	@Test
	public void testUpdateOpStatusByPrimaryKey() {
		fail("Not yet implemented");
	}

}
