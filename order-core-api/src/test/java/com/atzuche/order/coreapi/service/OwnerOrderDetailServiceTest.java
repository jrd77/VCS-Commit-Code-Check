package com.atzuche.order.coreapi.service;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.atzuche.order.commons.entity.ownerOrderDetail.PlatformToOwnerDTO;
import com.atzuche.order.coreapi.TemplateApplication;

@RunWith(SpringJUnit4ClassRunner.class)
//@RunWith(SpringRunner.class)
@SpringBootTest(classes=TemplateApplication.class)
@WebAppConfiguration
public class OwnerOrderDetailServiceTest {
	@Autowired
	OwnerOrderDetailService ownerOrderDetailService;
	
	@Test
	public void testOwnerRentDetail() {
		fail("Not yet implemented");
	}

	@Test
	public void testRenterOwnerPrice() {
		fail("Not yet implemented");
	}

	@Test
	public void testServiceDetail() {
		fail("Not yet implemented");
	}

	@Test
	public void testPlatformToOwnerSubsidy() {
		fail("Not yet implemented");
	}

	@Test
	public void testFienAmtDetail() {
		fail("Not yet implemented");
	}

	@Test
	public void testPlatformToOwner() {
		PlatformToOwnerDTO dto = ownerOrderDetailService.platformToOwner("12616160200299", "");
		System.err.println("dto="+dto.toString());
	}

	@Test
	public void testUpdateFien() {
		fail("Not yet implemented");
	}

}
