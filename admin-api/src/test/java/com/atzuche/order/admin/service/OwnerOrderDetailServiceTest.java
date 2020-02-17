package com.atzuche.order.admin.service;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.atzuche.order.admin.AdminSpringBoot;
import com.atzuche.order.commons.entity.ownerOrderDetail.OwnerRentDetailDTO;
import com.atzuche.order.commons.entity.ownerOrderDetail.ServiceDetailDTO;
import com.autoyol.commons.web.ResponseData;

@RunWith(SpringJUnit4ClassRunner.class)
//@RunWith(SpringRunner.class)
@SpringBootTest(classes=AdminSpringBoot.class)
@WebAppConfiguration
public class OwnerOrderDetailServiceTest {
	@Autowired
	OwnerOrderDetailService ownerOrderDetailService;
	
	@Test
	public void testOwnerRentDetail() {
		ResponseData<OwnerRentDetailDTO> res = ownerOrderDetailService.ownerRentDetail("88063330200299", "8806333020029910011");
		OwnerRentDetailDTO dto = res.getData();
		System.err.println("dto toString="+dto.toString());
	}
	
	@Test
	public void testServiceDetail() {
		ResponseData<ServiceDetailDTO> dto = ownerOrderDetailService.serviceDetail("28804131200299", "2880413120029910011");
		ServiceDetailDTO dt = dto.getData();
		System.err.println("dt="+dt.toString());
	}
	
	@Test
	public void testUpdateFineAmt() {
//		ownerOrderDetailService.updateFineAmt(fienAmtUpdateReqDTO);
	}
	

	@Test
	public void testRenterOwnerPrice() {
		fail("Not yet implemented");
	}

	@Test
	public void testFienAmtDetail() {
		fail("Not yet implemented");
	}



	@Test
	public void testPlatformToOwner() {
		fail("Not yet implemented");
	}

	@Test
	public void testPlatformToOwnerSubsidy() {
		fail("Not yet implemented");
	}



}
