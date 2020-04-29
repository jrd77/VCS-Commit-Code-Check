package com.atzuche.order.admin.service;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.atzuche.order.admin.AdminSpringBoot;
import com.atzuche.order.admin.vo.req.cost.AdditionalDriverInsuranceIdsReqVO;
import com.atzuche.order.admin.vo.req.cost.OwnerToPlatformCostReqVO;
import com.atzuche.order.admin.vo.req.cost.OwnerToRenterSubsidyReqVO;
import com.atzuche.order.admin.vo.req.cost.RenterCostReqVO;
import com.atzuche.order.admin.vo.req.cost.RenterFineCostReqVO;
import com.atzuche.order.admin.vo.resp.cost.AdditionalDriverInsuranceVO;
import com.atzuche.order.admin.vo.resp.order.cost.detail.OrderRenterFineAmtDetailResVO;
import com.atzuche.order.commons.entity.dto.CommUseDriverInfoSimpleDTO;
import com.atzuche.order.commons.entity.dto.CommUseDriverInfoStringDateDTO;

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
	public void testFindfineAmtListByOrderNo() {
		RenterCostReqVO renterCostReqVO = new RenterCostReqVO();
		renterCostReqVO.setOrderNo("97525121200299");
		renterCostReqVO.setRenterOrderNo("9752512120029910010");
		try {
			OrderRenterFineAmtDetailResVO res= orderCostDetailService.findfineAmtListByOrderNo(renterCostReqVO);
			System.err.println("res toString="+res.toString());
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testUpdatefineAmtListByOrderNo() {
		com.atzuche.order.commons.vo.rentercost.RenterFineCostReqVO renterCostReqVO = new com.atzuche.order.commons.vo.rentercost.RenterFineCostReqVO();
		renterCostReqVO.setOrderNo("28804131200299");
		renterCostReqVO.setRenterOrderNo("2880413120029910010");
		renterCostReqVO.setRenterBeforeReturnCarFineAmt("20");
		renterCostReqVO.setRenterDelayReturnCarFineAmt("30");
		try {
			orderCostDetailService.updatefineAmtListByOrderNo(renterCostReqVO);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void testUpdateOwnerToPlatFormListByOrderNo() {
		com.atzuche.order.commons.vo.rentercost.OwnerToPlatformCostReqVO ownerCostReqVO = new com.atzuche.order.commons.vo.rentercost.OwnerToPlatformCostReqVO();
		ownerCostReqVO.setOrderNo("28804131200299");
		ownerCostReqVO.setOwnerOrderNo("2880413120029910011");
		ownerCostReqVO.setOliAmt("10");
		ownerCostReqVO.setTimeOut("30");
		try {
			orderCostDetailService.updateOwnerToPlatFormListByOrderNo(ownerCostReqVO);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	@Test
	public void testFindAdditionalDriverInsuranceByOrderNo() {
		RenterCostReqVO renterCostReqVO = new RenterCostReqVO();
//		renterCostReqVO.setOrderNo("79509160200299");
//		renterCostReqVO.setRenterOrderNo("7950916020029910010");
		
		 String orderNo = "28804131200299";
	     String renterOrderNo = "2880413120029910010";
	     renterCostReqVO.setOrderNo(orderNo);
	     renterCostReqVO.setRenterOrderNo(renterOrderNo);
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
		 String orderNo = "28804131200299";
	     String renterOrderNo = "2880413120029910010";
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
	public void testFindRenterRentAmtListByOrderNo() {
		fail("Not yet implemented");
	}





	@Test
	public void testUpdatePlatFormToRenterListByOrderNo() {
		fail("Not yet implemented");
	}

	@Test
	public void testOwnerToRenterRentAmtSubsidy() {
		com.atzuche.order.commons.vo.rentercost.OwnerToRenterSubsidyReqVO ownerCostReqVO = new com.atzuche.order.commons.vo.rentercost.OwnerToRenterSubsidyReqVO();
		ownerCostReqVO.setOrderNo("86392311200299");
		ownerCostReqVO.setOwnerOrderNo("8639231120029910011");
		ownerCostReqVO.setOwnerSubsidyRentAmt("0");
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
