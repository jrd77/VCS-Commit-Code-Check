/**
 * 
 */
package com.atzuche.order.admin.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.atzuche.order.admin.vo.req.cost.RenterAddDriveCostReqVO;
import com.atzuche.order.admin.vo.req.cost.RenterAdjustCostReqVO;
import com.atzuche.order.admin.vo.req.cost.RenterCostReqVO;
import com.atzuche.order.admin.vo.req.cost.RenterToPlatformCostReqVO;
import com.atzuche.order.admin.vo.resp.cost.AdditionalDriverInsuranceVO;
import com.atzuche.order.admin.vo.resp.income.OwnerToPlatFormVO;
import com.atzuche.order.admin.vo.resp.order.cost.detail.OrderRenterFineAmtDetailResVO;
import com.atzuche.order.admin.vo.resp.order.cost.detail.PlatformToRenterSubsidyResVO;
import com.atzuche.order.admin.vo.resp.order.cost.detail.ReductionDetailResVO;
import com.atzuche.order.admin.vo.resp.order.cost.detail.RenterPriceAdjustmentResVO;
import com.atzuche.order.admin.vo.resp.order.cost.detail.RenterRentAmtResVO;

/**
 * @author jing.huang
 *
 */
@Service
public class OrderCostDetailService {
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	public OrderRenterFineAmtDetailResVO findfineAmtListByOrderNo(RenterCostReqVO renterCostReqVO) {
		// TODO Auto-generated method stub
		return null;
	}

	public ReductionDetailResVO findReductionDetailsListByOrderNo(RenterCostReqVO renterCostReqVO) {
		// TODO Auto-generated method stub
		return null;
	}

	public PlatformToRenterSubsidyResVO findPlatFormToRenterListByOrderNo(RenterCostReqVO renterCostReqVO) {
		// TODO Auto-generated method stub
		return null;
	}

	public RenterRentAmtResVO findRenterRentAmtListByOrderNo(RenterCostReqVO renterCostReqVO) {
		// TODO Auto-generated method stub
		return null;
	}

	public AdditionalDriverInsuranceVO findAdditionalDriverInsuranceByOrderNo(RenterCostReqVO renterCostReqVO) {
		// TODO Auto-generated method stub
		return null;
	}

	public AdditionalDriverInsuranceVO insertAdditionalDriverInsuranceByOrderNo(
			RenterAddDriveCostReqVO renterCostReqVO) {
		// TODO Auto-generated method stub
		return null;
	}

	public RenterPriceAdjustmentResVO updateRenterPriceAdjustmentByOrderNo(RenterAdjustCostReqVO renterCostReqVO) {
		// TODO Auto-generated method stub
		return null;
	}

	public RenterPriceAdjustmentResVO findRenterPriceAdjustmentByOrderNo(RenterCostReqVO renterCostReqVO) {
		// TODO Auto-generated method stub
		return null;
	}

	public OwnerToPlatFormVO findRenterToPlatFormListByOrderNo(RenterCostReqVO renterCostReqVO) {
		// TODO Auto-generated method stub
		return null;
	}

	public OwnerToPlatFormVO findRenterToPlatFormListByOrderNo(RenterToPlatformCostReqVO renterCostReqVO) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
