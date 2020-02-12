package com.atzuche.order.admin.service;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.validator.constraints.NotBlank;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.atzuche.order.admin.AdminSpringBoot;
import com.atzuche.order.admin.vo.req.cost.AdditionalDriverInsuranceIdsReqVO;
import com.atzuche.order.admin.vo.req.cost.OwnerToRenterSubsidyReqVO;
import com.atzuche.order.admin.vo.req.cost.RenterCostReqVO;
import com.atzuche.order.admin.vo.resp.cost.AdditionalDriverInsuranceVO;
import com.atzuche.order.commons.entity.dto.CommUseDriverInfoDTO;
import com.atzuche.order.commons.entity.dto.CommUseDriverInfoSimpleDTO;
import com.atzuche.order.commons.entity.dto.CommUseDriverInfoStringDateDTO;
import com.autoyol.doc.annotation.AutoDocProperty;

import io.swagger.annotations.ApiModelProperty;

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
			List<CommUseDriverInfoStringDateDTO> listCommUseDriverInfoDTO = vo.getListCommUseDriverInfoDTO();
			for (CommUseDriverInfoStringDateDTO commUseDriverInfoDTO : listCommUseDriverInfoDTO) {
				System.err.println("CommUseDriverInfoStringDateDTO="+commUseDriverInfoDTO.toString());
			}
			//已经保存的
			System.err.println("---------------------------------------------------------------------------------");
			List<CommUseDriverInfoStringDateDTO> dto2 = vo.getListCommUseDriverInfoAlreadySaveDTO();
			for (CommUseDriverInfoStringDateDTO commUseDriverInfoDTO : dto2) {
				System.err.println("dto2 already="+commUseDriverInfoDTO.toString());
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testInsertAdditionalDriverInsuranceByOrderNo() {
		 String orderNo = "79509160200299";
	     String renterOrderNo = "7950916020029910010";
		List<CommUseDriverInfoSimpleDTO> listCommUseDriverIds = new ArrayList<CommUseDriverInfoSimpleDTO>();
		CommUseDriverInfoSimpleDTO dto = new CommUseDriverInfoSimpleDTO();
		dto.setId(42);
		dto.setMobile("15921231111");
		dto.setRealName("刘剑啸");
		listCommUseDriverIds.add(dto);
		
		AdditionalDriverInsuranceIdsReqVO renterCostReqVO = new AdditionalDriverInsuranceIdsReqVO();
		renterCostReqVO.setOrderNo(orderNo);
		renterCostReqVO.setRenterOrderNo(renterOrderNo);
		renterCostReqVO.setListCommUseDriverIds(listCommUseDriverIds);
		try {
			orderCostDetailService.insertAdditionalDriverInsuranceByOrderNo(renterCostReqVO);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		OwnerToRenterSubsidyReqVO ownerCostReqVO = new OwnerToRenterSubsidyReqVO();
		ownerCostReqVO.setOrderNo("86392311200299");
		ownerCostReqVO.setOwnerOrderNo("8639231120029910011");
		ownerCostReqVO.setOwnerSubsidyRentAmt("20");
		try {
			orderCostDetailService.ownerToRenterRentAmtSubsidy(ownerCostReqVO);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testUpdatePlatFormToOwnerListByOrderNo() {
		fail("Not yet implemented");
	}

}
