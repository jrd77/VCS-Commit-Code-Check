/**
 * 
 */
package com.atzuche.order.admin.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.atzuche.order.admin.service.OrderCostDetailService;
import com.atzuche.order.admin.vo.req.cost.AdditionalDriverInsuranceIdsReqVO;
import com.atzuche.order.admin.vo.req.cost.RenterAdjustCostReqVO;
import com.atzuche.order.admin.vo.req.cost.RenterCostReqVO;
import com.atzuche.order.admin.vo.req.cost.RenterFineCostReqVO;
import com.atzuche.order.admin.vo.req.cost.RenterToPlatformCostReqVO;
import com.atzuche.order.admin.vo.resp.cost.AdditionalDriverInsuranceVO;
import com.atzuche.order.admin.vo.resp.income.OwnerToPlatFormVO;
import com.atzuche.order.admin.vo.resp.order.cost.detail.OrderRenterFineAmtDetailResVO;
import com.atzuche.order.admin.vo.resp.order.cost.detail.PlatformToRenterSubsidyResVO;
import com.atzuche.order.admin.vo.resp.order.cost.detail.ReductionDetailResVO;
import com.atzuche.order.admin.vo.resp.order.cost.detail.RenterPriceAdjustmentResVO;
import com.atzuche.order.admin.vo.resp.order.cost.detail.RenterRentAmtResVO;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
import com.dianping.cat.Cat;

/**
 * @author jing.huang
 *
 */
@AutoDocVersion(version = "管理后台租客租车费用 租客费用页面 相关明细接口文档")
@RestController
@RequestMapping("/console/ordercost/detail/")
public class OrderRentalCostDetailController {
	private static final Logger logger = LoggerFactory.getLogger(OrderRentalCostDetailController.class);
	
	@Autowired
	OrderCostDetailService orderCostDetailService;
	
	@AutoDocMethod(description = "违约罚金 修改违约罚金", value = "违约罚金 修改违约罚金",response = ResponseData.class)
    @PostMapping("/fineAmt/list")
    public ResponseData<?> updatefineAmtListByOrderNo(@RequestBody RenterFineCostReqVO renterCostReqVO, HttpServletRequest request, HttpServletResponse response,BindingResult bindingResult) {
    	logger.info("updatefineAmtListByOrderNo controller params={}",renterCostReqVO.toString());
		if (bindingResult.hasErrors()) {
            return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), ErrorCode.INPUT_ERROR.getText());
        }
        
        try {
        	//无需返回值
        	orderCostDetailService.updatefineAmtListByOrderNo(renterCostReqVO);
        	return ResponseData.success();
		} catch (Exception e) {
			Cat.logError("updatefineAmtListByOrderNo exception params="+renterCostReqVO.toString(),e);
			logger.error("updatefineAmtListByOrderNo exception params="+renterCostReqVO.toString(),e);
			return ResponseData.error();
		}
    }
	
    /**
     * 违约罚金
     * @param ownerInComeReqVO
     * @return
     */
    @AutoDocMethod(description = "违约罚金 违约罚金明细", value = "违约罚金 违约罚金明细",response = OrderRenterFineAmtDetailResVO.class)
    @PostMapping("/fineAmt/list")
    public ResponseData<OrderRenterFineAmtDetailResVO> findfineAmtListByOrderNo(@RequestBody RenterCostReqVO renterCostReqVO, HttpServletRequest request, HttpServletResponse response,BindingResult bindingResult) {
    	logger.info("findfineAmtListByOrderNo controller params={}",renterCostReqVO.toString());
		if (bindingResult.hasErrors()) {
            return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), ErrorCode.INPUT_ERROR.getText());
        }
        
        try {
        	OrderRenterFineAmtDetailResVO resp = orderCostDetailService.findfineAmtListByOrderNo(renterCostReqVO);
        	return ResponseData.success(resp);
		} catch (Exception e) {
			Cat.logError("findfineAmtListByOrderNo exception params="+renterCostReqVO.toString(),e);
			logger.error("findfineAmtListByOrderNo exception params="+renterCostReqVO.toString(),e);
			return ResponseData.error();
		}
    }
    
    
    /**
     *减免明细
     * @param rentalCostReqVO
     * @return
     */
    @AutoDocMethod(description = "减免明细 押金减免明细", value = "减免明细 押金减免明细",response = ReductionDetailResVO.class)
    @PostMapping("/reductionDetails/list")
    public ResponseData<ReductionDetailResVO> findReductionDetailsListByOrderNo(@RequestBody RenterCostReqVO renterCostReqVO, HttpServletRequest request, HttpServletResponse response,BindingResult bindingResult) {
    	logger.info("findReductionDetailsListByOrderNo controller params={}",renterCostReqVO.toString());
		if (bindingResult.hasErrors()) {
            return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), ErrorCode.INPUT_ERROR.getText());
        }
        
        try {
        	ReductionDetailResVO resp = orderCostDetailService.findReductionDetailsListByOrderNo(renterCostReqVO);
        	return ResponseData.success(resp);
		} catch (Exception e) {
			Cat.logError("findReductionDetailsListByOrderNo exception params="+renterCostReqVO.toString(),e);
			logger.error("findReductionDetailsListByOrderNo exception params="+renterCostReqVO.toString(),e);
			return ResponseData.error();
		}
    }
    
    /**
     *附加驾驶员险
     * @param rentalCostReqVO
     * @return
     */
    @AutoDocMethod(description = "附加驾驶员险 附加驾驶人明细", value = "附加驾驶员险 附加驾驶人明细",response = AdditionalDriverInsuranceVO.class)
    @PostMapping("/additionalDriverInsurance/list")
    public ResponseData<?> findAdditionalDriverInsuranceByOrderNo(@RequestBody RenterCostReqVO renterCostReqVO, HttpServletRequest request, HttpServletResponse response,BindingResult bindingResult) {
    	logger.info("findAdditionalDriverInsuranceByOrderNo controller params={}",renterCostReqVO.toString());
		if (bindingResult.hasErrors()) {
            return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), ErrorCode.INPUT_ERROR.getText());
        }
        
        try {
        	AdditionalDriverInsuranceVO resp = orderCostDetailService.findAdditionalDriverInsuranceByOrderNo(renterCostReqVO);
        	return ResponseData.success(resp);
		} catch (Exception e) {
			Cat.logError("findAdditionalDriverInsuranceByOrderNo exception params="+renterCostReqVO.toString(),e);
			logger.error("findAdditionalDriverInsuranceByOrderNo exception params="+renterCostReqVO.toString(),e);
			return ResponseData.error();
		}
    }

    /**
     *附加驾驶员险
     * @param additionalDriverInsuranceVO
     * @return
     */
    @AutoDocMethod(description = "新增附加驾驶员险  新增附加驾驶人", value = "新增附加驾驶员险  新增附加驾驶人",response = ResponseData.class)
    @PostMapping("/additionalDriverInsurance/add")
    public ResponseData<?> insertAdditionalDriverInsuranceByOrderNo(@RequestBody AdditionalDriverInsuranceIdsReqVO renterCostReqVO, HttpServletRequest request, HttpServletResponse response,BindingResult bindingResult) {
    	logger.info("insertAdditionalDriverInsuranceByOrderNo controller params={}",renterCostReqVO.toString());
		if (bindingResult.hasErrors()) {
            return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), ErrorCode.INPUT_ERROR.getText());
        }
        
        try {
        	orderCostDetailService.insertAdditionalDriverInsuranceByOrderNo(renterCostReqVO);
        	return ResponseData.success();
		} catch (Exception e) {
			Cat.logError("insertAdditionalDriverInsuranceByOrderNo exception params="+renterCostReqVO.toString(),e);
			logger.error("insertAdditionalDriverInsuranceByOrderNo exception params="+renterCostReqVO.toString(),e);
			return ResponseData.error();
		}
    }
    
    /**
     *平台给租客的补贴
     * @param rentalCostReqVO
     * @return
     */
    @AutoDocMethod(description = "平台给租客的补贴 平台给租客的补贴明细", value = "平台给租客的补贴 平台给租客的补贴明细",response = PlatformToRenterSubsidyResVO.class)
    @PostMapping("/platFormToRenter/list")
    public ResponseData<PlatformToRenterSubsidyResVO> findPlatFormToRenterListByOrderNo(@RequestBody RenterCostReqVO renterCostReqVO, HttpServletRequest request, HttpServletResponse response,BindingResult bindingResult) {
    	logger.info("findPlatFormToRenterListByOrderNo controller params={}",renterCostReqVO.toString());
		if (bindingResult.hasErrors()) {
            return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), ErrorCode.INPUT_ERROR.getText());
        }
        
        try {
        	PlatformToRenterSubsidyResVO resp = orderCostDetailService.findPlatFormToRenterListByOrderNo(renterCostReqVO);
        	return ResponseData.success(resp);
		} catch (Exception e) {
			Cat.logError("findPlatFormToRenterListByOrderNo exception params="+renterCostReqVO.toString(),e);
			logger.error("findPlatFormToRenterListByOrderNo exception params="+renterCostReqVO.toString(),e);
			return ResponseData.error();
		}
    }
    

    /**
     *租客车主互相调价
     * @param priceAdjustmentVO
     * @return
     */
    @AutoDocMethod(description = "租客车主互相调价 车主租客互相调价操作", value = "租客车主互相调价 车主租客互相调价操作",response = ResponseData.class)
    @PostMapping("/renterPriceAdjustment/update")
    public ResponseData<?> updateRenterPriceAdjustmentByOrderNo(@RequestBody RenterAdjustCostReqVO renterCostReqVO, HttpServletRequest request, HttpServletResponse response,BindingResult bindingResult) {
    	logger.info("updateRenterPriceAdjustmentByOrderNo controller params={}",renterCostReqVO.toString());
		if (bindingResult.hasErrors()) {
            return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), ErrorCode.INPUT_ERROR.getText());
        }
        
        try {
        	orderCostDetailService.updateRenterPriceAdjustmentByOrderNo(renterCostReqVO);
        	return ResponseData.success();
		} catch (Exception e) {
			Cat.logError("updateRenterPriceAdjustmentByOrderNo exception params="+renterCostReqVO.toString(),e);
			logger.error("updateRenterPriceAdjustmentByOrderNo exception params="+renterCostReqVO.toString(),e);
			return ResponseData.error();
		}
    }
    
    /**
     *租客车主互相调价
     * @param rentalCostReqVO
     * @return    PriceAdjustmentVO
     */
    @AutoDocMethod(description = "租客车主互相调价 车主租客互相调价展示", value = "租客车主互相调价 车主租客互相调价展示",response = RenterPriceAdjustmentResVO.class)
    @PostMapping("/renterPriceAdjustment/list")
    public ResponseData<RenterPriceAdjustmentResVO> findRenterPriceAdjustmentByOrderNo(@RequestBody RenterCostReqVO renterCostReqVO, HttpServletRequest request, HttpServletResponse response,BindingResult bindingResult) {
    	logger.info("findRenterPriceAdjustmentByOrderNo controller params={}",renterCostReqVO.toString());
		if (bindingResult.hasErrors()) {
            return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), ErrorCode.INPUT_ERROR.getText());
        }
        
        try {
        	RenterPriceAdjustmentResVO resp = orderCostDetailService.findRenterPriceAdjustmentByOrderNo(renterCostReqVO);
        	return ResponseData.success(resp);
		} catch (Exception e) {
			Cat.logError("findRenterPriceAdjustmentByOrderNo exception params="+renterCostReqVO.toString(),e);
			logger.error("findRenterPriceAdjustmentByOrderNo exception params="+renterCostReqVO.toString(),e);
			return ResponseData.error();
		}
    }
    
    
    
    // --------------------------------------------------------------------------------------------------- 第二阶段 
    
    
    /**
     * 租客需支付给平台的费用
     * @param rentalCostReqVO
     * @return
     */
    @AutoDocMethod(description = "租客需支付给平台的费用 查询接口", value = "租客需支付给平台的费用 查询接口",response = OwnerToPlatFormVO.class)
    @PostMapping("/renterToPlatForm/list")
    public ResponseData<?> findRenterToPlatFormListByOrderNo(@RequestBody RenterCostReqVO renterCostReqVO, HttpServletRequest request, HttpServletResponse response,BindingResult bindingResult) {
    	logger.info("findRenterToPlatFormListByOrderNo controller params={}",renterCostReqVO.toString());
		if (bindingResult.hasErrors()) {
            return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), ErrorCode.INPUT_ERROR.getText());
        }
        
        try {
        	OwnerToPlatFormVO resp = orderCostDetailService.findRenterToPlatFormListByOrderNo(renterCostReqVO);
        	return ResponseData.success(resp);
		} catch (Exception e) {
			Cat.logError("findRenterToPlatFormListByOrderNo exception params="+renterCostReqVO.toString(),e);
			logger.error("findRenterToPlatFormListByOrderNo exception params="+renterCostReqVO.toString(),e);
			return ResponseData.error();
		}
    }

    /**
     * 租客需支付给平台的费用
     * @param ownerToPlatFormVO
     * @return
     */
    @AutoDocMethod(description = "租客需支付给平台的费用 修改接口", value = "租客需支付给平台的费用 修改接口",response = OwnerToPlatFormVO.class)
    @PostMapping("/ownerToPlatForm/update")
    public ResponseData<OwnerToPlatFormVO> updateRenterToPlatFormListByOrderNo(@RequestBody RenterToPlatformCostReqVO renterCostReqVO, HttpServletRequest request, HttpServletResponse response,BindingResult bindingResult) {
    	logger.info("findRenterToPlatFormListByOrderNo controller params={}",renterCostReqVO.toString());
		if (bindingResult.hasErrors()) {
            return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), ErrorCode.INPUT_ERROR.getText());
        }
        
        try {
        	OwnerToPlatFormVO resp = orderCostDetailService.findRenterToPlatFormListByOrderNo(renterCostReqVO);
        	return ResponseData.success(resp);
		} catch (Exception e) {
			Cat.logError("findRenterToPlatFormListByOrderNo exception params="+renterCostReqVO.toString(),e);
			logger.error("findRenterToPlatFormListByOrderNo exception params="+renterCostReqVO.toString(),e);
			return ResponseData.error();
		}
    }
    
    /**
     *租客租金明细
     * @param rentalCostReqVO
     * @return
     */
    @AutoDocMethod(description = "租客租金 租金明细接口", value = "租客租金 租金明细接口",response = RenterRentAmtResVO.class)
    @PostMapping("/renterRentAmt/list")
    public ResponseData<RenterRentAmtResVO> findRenterRentAmtListByOrderNo(@RequestBody RenterCostReqVO renterCostReqVO, HttpServletRequest request, HttpServletResponse response,BindingResult bindingResult) {
    	logger.info("findTenantRentListByOrderNo controller params={}",renterCostReqVO.toString());
		if (bindingResult.hasErrors()) {
            return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), ErrorCode.INPUT_ERROR.getText());
        }
        
        try {
        	RenterRentAmtResVO resp = orderCostDetailService.findRenterRentAmtListByOrderNo(renterCostReqVO);
        	return ResponseData.success(resp);
		} catch (Exception e) {
			Cat.logError("findRenterRentAmtListByOrderNo exception params="+renterCostReqVO.toString(),e);
			logger.error("findRenterRentAmtListByOrderNo exception params="+renterCostReqVO.toString(),e);
			return ResponseData.error();
		}
    }
    

}
