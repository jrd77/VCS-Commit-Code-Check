package com.atzuche.order.coreapi.service;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.atzuche.order.commons.vo.req.AgreeOrderReqVO;
import com.atzuche.order.coreapi.TemplateApplication;
import com.netflix.discovery.converters.Auto;


@RunWith(SpringJUnit4ClassRunner.class)
//@RunWith(SpringRunner.class)
@SpringBootTest(classes=TemplateApplication.class)
@WebAppConfiguration
public class OwnerAgreeOrderServiceTest {
	@Autowired
	OwnerAgreeOrderService ownerAgreeOrderService;
	@Test
	public void testAgree() {
		AgreeOrderReqVO reqVO = new AgreeOrderReqVO();
		reqVO.setOrderNo("46512262300299");
		reqVO.setIsConsoleInvoke(0);
		reqVO.setOperatorName("admin");
		ownerAgreeOrderService.agree(reqVO);
	}

	@Test
	public void testAgreeOrderConflictInfoHandle() {
		fail("Not yet implemented");
	}

	@Test
	public void testBuildReqVO() {
		fail("Not yet implemented");
	}

}
