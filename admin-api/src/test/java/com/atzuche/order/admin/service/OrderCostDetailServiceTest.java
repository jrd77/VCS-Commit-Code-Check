package com.atzuche.order.admin.service;

import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.atzuche.order.admin.AdminSpringBoot;
import com.atzuche.order.admin.vo.req.cost.RenterCostReqVO;
import com.atzuche.order.admin.vo.resp.cost.AdditionalDriverInsuranceVO;
import com.atzuche.order.commons.entity.dto.CommUseDriverInfoDTO;

@RunWith(SpringJUnit4ClassRunner.class)
//@RunWith(SpringRunner.class)
@SpringBootTest(classes=AdminSpringBoot.class)
@WebAppConfiguration
public class OrderCostDetailServiceTest {
	@Autowired
	OrderCostDetailService orderCostDetailService;
	
	@Test
	public void testFindReductionDetailsListByOrderNo() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindAdditionalDriverInsuranceByOrderNo() {
		RenterCostReqVO renterCostReqVO = new RenterCostReqVO();
		renterCostReqVO.setOrderNo("79509160200299");
		renterCostReqVO.setRenterOrderNo("7950916020029910010");
		try {
			AdditionalDriverInsuranceVO vo = orderCostDetailService.findAdditionalDriverInsuranceByOrderNo(renterCostReqVO);
			System.err.println("vo="+vo.toString());
			List<CommUseDriverInfoDTO> listCommUseDriverInfoDTO = vo.getListCommUseDriverInfoDTO();
			for (CommUseDriverInfoDTO commUseDriverInfoDTO : listCommUseDriverInfoDTO) {
				System.err.println("commUseDriverInfoDTO="+commUseDriverInfoDTO.toString());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testInsertAdditionalDriverInsuranceByOrderNo() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindPlatFormToRenterListByOrderNo() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindRenterPriceAdjustmentByOrderNo() {
		fail("Not yet implemented");
	}

	@Test
	public void testUpdateRenterPriceAdjustmentByOrderNo() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindRenterToPlatFormListByOrderNo() {
		fail("Not yet implemented");
	}

	@Test
	public void testUpdateRenterToPlatFormListByOrderNo() {
		fail("Not yet implemented");
	}

	@Test
	public void testUpdateOwnerToPlatFormListByOrderNo() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindRenterRentAmtListByOrderNo() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindfineAmtListByOrderNo() {
		fail("Not yet implemented");
	}

	@Test
	public void testUpdatefineAmtListByOrderNo() {
		fail("Not yet implemented");
	}

	@Test
	public void testUpdatePlatFormToRenterListByOrderNo() {
		fail("Not yet implemented");
	}

	@Test
	public void testOwnerToRenterRentAmtSubsidy() {
		fail("Not yet implemented");
	}

	@Test
	public void testUpdatePlatFormToOwnerListByOrderNo() {
		fail("Not yet implemented");
	}

}
