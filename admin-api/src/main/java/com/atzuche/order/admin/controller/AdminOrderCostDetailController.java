/**
 * 
 */
package com.atzuche.order.admin.controller;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.admin.constant.AdminOpTypeEnum;
import com.atzuche.order.admin.service.OrderCostDetailService;
import com.atzuche.order.admin.service.OrderCostRemoteService;
import com.atzuche.order.admin.service.log.AdminLogService;
import com.atzuche.order.admin.util.CompareBeanUtils;
import com.atzuche.order.admin.vo.req.cost.*;
import com.atzuche.order.admin.vo.resp.cost.AdditionalDriverInsuranceVO;
import com.atzuche.order.admin.vo.resp.income.RenterToPlatformVO;
import com.atzuche.order.admin.vo.resp.order.cost.detail.OrderRenterFineAmtDetailResVO;
import com.atzuche.order.admin.vo.resp.order.cost.detail.PlatformToRenterSubsidyResVO;
import com.atzuche.order.admin.vo.resp.order.cost.detail.ReductionDetailResVO;
import com.atzuche.order.admin.vo.resp.order.cost.detail.RenterPriceAdjustmentResVO;
import com.atzuche.order.commons.StringUtil;
import com.atzuche.order.commons.entity.ownerOrderDetail.RenterRentDetailDTO;
import com.atzuche.order.commons.entity.rentCost.RenterCostDetailDTO;
import com.atzuche.order.commons.enums.cashcode.FineTypeCashCodeEnum;
import com.atzuche.order.commons.vo.rentercost.RenterAndConsoleFineVO;
import com.atzuche.order.commons.vo.req.AdditionalDriverInsuranceIdsReqVO;
import com.atzuche.order.commons.vo.req.RenterAdjustCostReqVO;
import com.atzuche.order.commons.vo.res.rentcosts.ConsoleRenterOrderFineDeatailEntity;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
import com.dianping.cat.Cat;
import lombok.extern.slf4j.Slf4j;
import lombok.extern.slf4j.XSlf4j;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author jing.huang
 *
 */
@Slf4j
@AutoDocVersion(version = "管理后台租客租车费用 租客费用页面 相关明细接口文档")
@RestController
@RequestMapping("/console/ordercost/detail/")
public class AdminOrderCostDetailController {
	private static final Logger logger = LoggerFactory.getLogger(AdminOrderCostDetailController.class);
	
	@Autowired
	OrderCostDetailService orderCostDetailService;
    @Autowired
    AdminLogService adminLogService;
    @Autowired
    OrderCostRemoteService orderCostRemoteService;
	
	@AutoDocMethod(description = "违约罚金 修改违约罚金", value = "违约罚金 修改违约罚金",response = ResponseData.class)
    @PostMapping("fineAmt/update")
    public ResponseData<?> updatefineAmtListByOrderNo(@RequestBody @Validated com.atzuche.order.commons.vo.rentercost.RenterFineCostReqVO renterCostReqVO, BindingResult bindingResult) {
    	logger.info("updatefineAmtListByOrderNo controller params={}",renterCostReqVO.toString());
		if (bindingResult.hasErrors()) {
            return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), ErrorCode.INPUT_ERROR.getText());
        }
        
        try {
        	//无需返回值
        	orderCostDetailService.updatefineAmtListByOrderNo(renterCostReqVO);
            //记录日志
        	try{
                RenterAndConsoleFineVO fineList = orderCostRemoteService.getRenterAndConsoleFineVO(renterCostReqVO.getOrderNo(), renterCostReqVO.getRenterOrderNo());
                List<ConsoleRenterOrderFineDeatailEntity> list = fineList == null ? null : fineList.getConsoleFineList();
                // 累计求和
                int renterBeforeReturnCarFineAmount = 0;
                int renterDelayReturnCarFineAmount = 0;
                for (ConsoleRenterOrderFineDeatailEntity consoleRenterOrderFineDeatailEntity : list) {
                    if (consoleRenterOrderFineDeatailEntity.getFineType().intValue() == FineTypeCashCodeEnum.MODIFY_ADVANCE.getFineType().intValue()) {
                        renterBeforeReturnCarFineAmount = consoleRenterOrderFineDeatailEntity.getFineAmount().intValue();
                    }else if (consoleRenterOrderFineDeatailEntity.getFineType().intValue() == FineTypeCashCodeEnum.DELAY_FINE.getFineType().intValue()) {
                        renterDelayReturnCarFineAmount = consoleRenterOrderFineDeatailEntity.getFineAmount().intValue();
                    }
                }
                String desc = FineTypeCashCodeEnum.MODIFY_ADVANCE.getFineTypeDesc()+":原值 "+renterBeforeReturnCarFineAmount+" 修改为 " +renterCostReqVO.getRenterBeforeReturnCarFineAmt()+"\n"
                                + FineTypeCashCodeEnum.DELAY_FINE.getFineTypeDesc() + ": 原值 "+ renterDelayReturnCarFineAmount+ " 修改为 "+ renterCostReqVO.getRenterDelayReturnCarFineAmt();
                adminLogService.insertLog(AdminOpTypeEnum.RENTER_UPDATE_FINE, renterCostReqVO.getOrderNo(), desc);
            }catch (Exception e){
        	    log.error("记录日志失败",e);
            }
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
    @PostMapping("fineAmt/list")
    public ResponseData<OrderRenterFineAmtDetailResVO> findfineAmtListByOrderNo(@RequestBody @Validated RenterCostReqVO renterCostReqVO, HttpServletRequest request, HttpServletResponse response,BindingResult bindingResult) {
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
    @PostMapping("reductionDetails/list")
    public ResponseData<ReductionDetailResVO> findReductionDetailsListByOrderNo(@RequestBody @Validated RenterCostReqVO renterCostReqVO, HttpServletRequest request, HttpServletResponse response,BindingResult bindingResult) {
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
    @PostMapping("additionalDriverInsurance/list")
    public ResponseData<?> findAdditionalDriverInsuranceByOrderNo(@RequestBody @Validated RenterCostReqVO renterCostReqVO, HttpServletRequest request, HttpServletResponse response,BindingResult bindingResult) {
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
    @PostMapping("additionalDriverInsurance/add")
    public ResponseData<?> insertAdditionalDriverInsuranceByOrderNo(@RequestBody @Validated AdditionalDriverInsuranceIdsReqVO renterCostReqVO,BindingResult bindingResult) {
    	logger.info("insertAdditionalDriverInsuranceByOrderNo controller params={}",renterCostReqVO.toString());
		if (bindingResult.hasErrors()) {
            return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), ErrorCode.INPUT_ERROR.getText());
        }
        
        try {
        	orderCostDetailService.insertAdditionalDriverInsuranceByOrderNo(renterCostReqVO);
            //日志记录
            try{
                adminLogService.insertLog(AdminOpTypeEnum.ADDITIONAL_DRIVER_ADD, renterCostReqVO.getOrderNo(), JSON.toJSONString(renterCostReqVO));
            }catch (Exception e){
                log.error("记录日志失败",e);
            }
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
    @PostMapping("platFormToRenter/list")
    public ResponseData<PlatformToRenterSubsidyResVO> findPlatFormToRenterListByOrderNo(@RequestBody @Validated RenterCostReqVO renterCostReqVO, HttpServletRequest request, HttpServletResponse response,BindingResult bindingResult) {
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
     * 200119
     * @param renterCostReqVO
     * @param request
     * @param response
     * @param bindingResult
     * @return
     */
    @AutoDocMethod(description = "平台给租客的补贴 平台给租客的补贴修改", value = "平台给租客的补贴 平台给租客的补贴修改",response = ResponseData.class)
    @PostMapping("platFormToRenter/update")
    public ResponseData<?> updatePlatFormToRenterListByOrderNo(@RequestBody @Validated com.atzuche.order.commons.vo.rentercost.PlatformToRenterSubsidyReqVO renterCostReqVO,BindingResult bindingResult) {
    	logger.info("updatePlatFormToRenterListByOrderNo controller params={}",renterCostReqVO.toString());
		if (bindingResult.hasErrors()) {
            return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), ErrorCode.INPUT_ERROR.getText());
        }
        
        try {
        	orderCostDetailService.updatePlatFormToRenterListByOrderNo(renterCostReqVO);
        	try{
                RenterCostReqVO req = new RenterCostReqVO();
                req.setOrderNo(renterCostReqVO.getOrderNo());
                req.setRenterOrderNo(renterCostReqVO.getRenterOrderNo());
                PlatformToRenterSubsidyResVO newData = orderCostDetailService.findPlatFormToRenterListByOrderNo(req);
                PlatformToRenterSubsidyResVO oldData = new PlatformToRenterSubsidyResVO();
                oldData.setDispatchingSubsidy(renterCostReqVO.getDispatchingSubsidy());
                oldData.setOilSubsidy(renterCostReqVO.getOilSubsidy());
                oldData.setCleanCarSubsidy(renterCostReqVO.getCleanCarSubsidy());
                oldData.setGetReturnDelaySubsidy(renterCostReqVO.getGetReturnDelaySubsidy());
                oldData.setDelaySubsidy(renterCostReqVO.getDelaySubsidy());
                oldData.setTrafficSubsidy(renterCostReqVO.getTrafficSubsidy());
                oldData.setInsureSubsidy(renterCostReqVO.getInsureSubsidy());
                oldData.setRentAmtSubsidy(renterCostReqVO.getRentAmtSubsidy());
                oldData.setOtherSubsidy(renterCostReqVO.getOtherSubsidy());
                oldData.setAbatementSubsidy(renterCostReqVO.getAbatementSubsidy());
                oldData.setFeeSubsidy(renterCostReqVO.getFeeSubsidy());
                adminLogService.insertLog(AdminOpTypeEnum.PLATFORM_TO_RENTER,renterCostReqVO.getOrderNo(),renterCostReqVO.getRenterOrderNo(),null,CompareBeanUtils.newInstance(oldData,newData).compare());
            }catch (Exception e){
        	    log.error("平台给租客的补贴修改日志记录异常",e);
            }
        	return ResponseData.success();
		} catch (Exception e) {
			Cat.logError("updatePlatFormToRenterListByOrderNo exception params="+renterCostReqVO.toString(),e);
			logger.error("updatePlatFormToRenterListByOrderNo exception params="+renterCostReqVO.toString(),e);
			return ResponseData.error();
		}
    }
    
    /**
     * 200120 
     * @param renterCostReqVO
     * @param request
     * @param response
     * @param bindingResult
     * @return
     */
    @AutoDocMethod(description = "平台给车主的补贴 平台给车主的补贴修改", value = "平台给车主的补贴 平台给车主的补贴修改",response = ResponseData.class)
    @PostMapping("platFormToOwner/update")
    public ResponseData<?> updatePlatFormToOwnerListByOrderNo(@RequestBody @Validated com.atzuche.order.commons.vo.rentercost.PlatformToOwnerSubsidyReqVO ownerCostReqVO, HttpServletRequest request, HttpServletResponse response,BindingResult bindingResult) {
    	logger.info("updatePlatFormToOwnerListByOrderNo controller params={}",ownerCostReqVO.toString());
		if (bindingResult.hasErrors()) {
            return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), ErrorCode.INPUT_ERROR.getText());
        }
        try {
        	orderCostDetailService.updatePlatFormToOwnerListByOrderNo(ownerCostReqVO);
        	return ResponseData.success();
		} catch (Exception e) {
			Cat.logError("updatePlatFormToOwnerListByOrderNo exception params="+ownerCostReqVO.toString(),e);
			logger.error("updatePlatFormToOwnerListByOrderNo exception params="+ownerCostReqVO.toString(),e);
			return ResponseData.error();
		}
    }
    
    

    /**
     *租客车主互相调价  租客和车主都是调用同一个接口
     * @param priceAdjustmentVO
     * @return
     */
    @AutoDocMethod(description = "租客车主互相调价 车主租客互相调价操作", value = "租客车主互相调价 车主租客互相调价操作",response = ResponseData.class)
    @PostMapping("renterPriceAdjustment/update")
    public ResponseData<?> updateRenterPriceAdjustmentByOrderNo(@RequestBody RenterAdjustCostReqVO renterCostReqVO, HttpServletRequest request, HttpServletResponse response, BindingResult bindingResult) {
    	logger.info("updateRenterPriceAdjustmentByOrderNo controller params={}",renterCostReqVO.toString());  //@Validated
		if (bindingResult.hasErrors()) {
            return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), ErrorCode.INPUT_ERROR.getText());
        }
        
        try {
        	/**
        	 * 全局补贴
        	 */
        	orderCostDetailService.updateRenterPriceAdjustmentByOrderNo(renterCostReqVO);
            try{
                RenterCostReqVO req = new RenterCostReqVO();
                req.setOrderNo(renterCostReqVO.getOrderNo());
                req.setOwnerOrderNo(renterCostReqVO.getOwnerOrderNo());
                req.setRenterOrderNo(renterCostReqVO.getRenterOrderNo());
                RenterPriceAdjustmentResVO resp = orderCostDetailService.findRenterPriceAdjustmentByOrderNo(req);
                if(StringUtils.isNotBlank(renterCostReqVO.getOwnerToRenterAdjustAmt())){
                    String oldData = resp.getOwnerToRenterAdjustAmt();
                    String desc = "车主给租客调价 由 " + oldData +" 修改为 " + renterCostReqVO.getOwnerToRenterAdjustAmt();
                    adminLogService.insertLog(AdminOpTypeEnum.RENTER_PRICE_ADJUSTMENT,renterCostReqVO.getOrderNo(),
                            renterCostReqVO.getRenterOrderNo(),renterCostReqVO.getOwnerOrderNo(),desc);
                }
                if(StringUtils.isNotBlank(renterCostReqVO.getRenterToOwnerAdjustAmt())){
                    String oldData = resp.getOwnerToRenterAdjustAmt();
                    String desc = "租客给车主的调价 由 " + oldData +" 修改为 " + renterCostReqVO.getRenterToOwnerAdjustAmt();
                    adminLogService.insertLog(AdminOpTypeEnum.RENTER_PRICE_ADJUSTMENT,renterCostReqVO.getOrderNo(),
                            renterCostReqVO.getRenterOrderNo(),renterCostReqVO.getOwnerOrderNo(),desc);
                }
            }catch (Exception e){
                log.error("租客车主互相调价 车主租客互相调价操作 日志记录 异常",e);
            }


        	return ResponseData.success();
		} catch (Exception e) {
			Cat.logError("updateRenterPriceAdjustmentByOrderNo exception params="+renterCostReqVO.toString(),e);
			logger.error("updateRenterPriceAdjustmentByOrderNo exception params="+renterCostReqVO.toString(),e);
			return ResponseData.error();
		}
    }
    
    /**
     *租客车主互相调价   租客和车主都是调用同一个接口
     * @param rentalCostReqVO
     * @return    PriceAdjustmentVO
     */
    @AutoDocMethod(description = "租客车主互相调价 车主租客互相调价展示", value = "租客车主互相调价 车主租客互相调价展示",response = RenterPriceAdjustmentResVO.class)
    @PostMapping("renterPriceAdjustment/list")
    public ResponseData<RenterPriceAdjustmentResVO> findRenterPriceAdjustmentByOrderNo(@RequestBody  RenterCostReqVO renterCostReqVO, HttpServletRequest request, HttpServletResponse response,BindingResult bindingResult) {
    	logger.info("findRenterPriceAdjustmentByOrderNo controller params={}",renterCostReqVO.toString());  //@Validated
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
    
    
    

    /**
     * 租客需支付给平台的费用
     * @param rentalCostReqVO
     * @return
     */
    @AutoDocMethod(description = "租客需支付给平台的费用 查询接口", value = "租客需支付给平台的费用 查询接口",response = RenterToPlatformVO.class)
    @PostMapping("renterToPlatForm/list")
    public ResponseData<RenterToPlatformVO> findRenterToPlatFormListByOrderNo(@RequestBody @Validated RenterCostReqVO renterCostReqVO, HttpServletRequest request, HttpServletResponse response,BindingResult bindingResult) {
    	logger.info("findRenterToPlatFormListByOrderNo controller params={}",renterCostReqVO.toString());
		if (bindingResult.hasErrors()) {
            return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), ErrorCode.INPUT_ERROR.getText());
        }
        
        try {
        	/**
        	 * 全局费用
        	 */
        	RenterToPlatformVO resp = orderCostDetailService.findRenterToPlatFormListByOrderNo(renterCostReqVO);
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
    @AutoDocMethod(description = "租客需支付给平台的费用 修改接口", value = "租客需支付给平台的费用 修改接口",response = ResponseData.class)
    @PostMapping("renterToPlatForm/update")
    public ResponseData<?> updateRenterToPlatFormListByOrderNo(@RequestBody @Validated com.atzuche.order.commons.vo.rentercost.RenterToPlatformCostReqVO renterCostReqVO, HttpServletRequest request, HttpServletResponse response,BindingResult bindingResult) {
    	logger.info("updateRenterToPlatFormListByOrderNo controller params={}",renterCostReqVO.toString());
		if (bindingResult.hasErrors()) {
            return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), ErrorCode.INPUT_ERROR.getText());
        }
        try {
        	orderCostDetailService.updateRenterToPlatFormListByOrderNo(renterCostReqVO);
        	try{
                RenterCostReqVO req = new RenterCostReqVO();
                RenterToPlatformVO oldData = orderCostDetailService.findRenterToPlatFormListByOrderNo(req);
                RenterToPlatformVO newData = new RenterToPlatformVO();
                newData.setOliAmt(renterCostReqVO.getOliAmt());
                newData.setTimeOut(renterCostReqVO.getTimeOut());
                newData.setModifyOrderTimeAndAddrAmt(renterCostReqVO.getModifyOrderTimeAndAddrAmt());
                newData.setCarWash(renterCostReqVO.getCarWash());
                newData.setDlayWait(renterCostReqVO.getDlayWait());
                newData.setStopCar(renterCostReqVO.getStopCar());
                newData.setExtraMileage(renterCostReqVO.getExtraMileage());
                adminLogService.insertLog(AdminOpTypeEnum.RENTER_TO_PLATFORM,renterCostReqVO.getOrderNo(),
                        renterCostReqVO.getRenterOrderNo(),null, CompareBeanUtils.newInstance(oldData,newData).compare());
            }catch (Exception e){
        	    log.error("租客需支付给平台的费用日志记录异常",e);
            }


        	return ResponseData.success();
		} catch (Exception e) {
			Cat.logError("updateRenterToPlatFormListByOrderNo exception params="+renterCostReqVO.toString(),e);
			logger.error("updateRenterToPlatFormListByOrderNo exception params="+renterCostReqVO.toString(),e);
			return ResponseData.error();
		}
    }
    
    /**
     *租客租金明细, 参考车主的租金组成明细。
     * @param rentalCostReqVO
     * @return
     */
    @AutoDocMethod(description = "租客租金 租金明细接口", value = "租客租金 租金明细接口",response = RenterRentDetailDTO.class)
    @PostMapping("renterRentAmt/list")
    public ResponseData<RenterRentDetailDTO> findRenterRentAmtListByOrderNo(@RequestBody @Validated com.atzuche.order.commons.vo.rentercost.RenterCostReqVO renterCostReqVO, HttpServletRequest request, HttpServletResponse response,BindingResult bindingResult) {
    	logger.info("findTenantRentListByOrderNo controller params={}",renterCostReqVO.toString());
		if (bindingResult.hasErrors()) {
            return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), ErrorCode.INPUT_ERROR.getText());
        }
        
        try {
        	RenterRentDetailDTO resp = orderCostDetailService.findRenterRentAmtListByOrderNo(renterCostReqVO);
        	return ResponseData.success(resp);
		} catch (Exception e) {
			Cat.logError("findRenterRentAmtListByOrderNo exception params="+renterCostReqVO.toString(),e);
			logger.error("findRenterRentAmtListByOrderNo exception params="+renterCostReqVO.toString(),e);
			return ResponseData.error();
		}
    }
    
    // --------------------------------------------------------------------------------------------------- 第二阶段 

    
    /**
     * 车主需支付给平台的费用 @张斌
     * @param ownerToPlatFormVO
     * @return
     */
    @AutoDocMethod(description = "车主需支付给平台的费用 修改接口", value = "车主需支付给平台的费用 修改接口",response = ResponseData.class)
    @PostMapping("ownerToPlatForm/update")
    public ResponseData<?> updateOwnerToPlatFormListByOrderNo(@RequestBody @Validated com.atzuche.order.commons.vo.rentercost.OwnerToPlatformCostReqVO ownerCostReqVO, HttpServletRequest request, HttpServletResponse response,BindingResult bindingResult) {
    	logger.info("updateOwnerToPlatFormListByOrderNo controller params={}",ownerCostReqVO.toString());
		if (bindingResult.hasErrors()) {
            return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), ErrorCode.INPUT_ERROR.getText());
        }
        
        try {
        	orderCostDetailService.updateOwnerToPlatFormListByOrderNo(ownerCostReqVO);
        	return ResponseData.success();
		} catch (Exception e) {
			Cat.logError("updateOwnerToPlatFormListByOrderNo exception params="+ownerCostReqVO.toString(),e);
			logger.error("updateOwnerToPlatFormListByOrderNo exception params="+ownerCostReqVO.toString(),e);
			return ResponseData.error();
		}
    }
    
    @AutoDocMethod(description = "车主给租客的租金补贴 修改接口", value = "车主给租客的租金补贴 修改接口",response = ResponseData.class)
    @PostMapping("ownerToRenterRentAmtSubsidy/update")
    public ResponseData<?> ownerToRenterRentAmtSubsidy(@RequestBody @Validated com.atzuche.order.commons.vo.rentercost.OwnerToRenterSubsidyReqVO ownerCostReqVO, HttpServletRequest request, HttpServletResponse response,BindingResult bindingResult) {
    	logger.info("ownerToRenterRentAmtSubsidy controller params={}",ownerCostReqVO.toString());
		if (bindingResult.hasErrors()) {
            return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), ErrorCode.INPUT_ERROR.getText());
        }
        
        try {
        	orderCostDetailService.ownerToRenterRentAmtSubsidy(ownerCostReqVO);
        	return ResponseData.success();
		} catch (Exception e) {
			Cat.logError("ownerToRenterRentAmtSubsidy exception params="+ownerCostReqVO.toString(),e);
			logger.error("ownerToRenterRentAmtSubsidy exception params="+ownerCostReqVO.toString(),e);
			return ResponseData.error();
		}
    }


    @AutoDocMethod(description = "租客费用详情-弹窗", value = "租客费用详情-弹窗",response = RenterCostDetailDTO.class)
    @GetMapping("/renterOrderCostDetail")
    public ResponseData<RenterCostDetailDTO> renterOrderCostDetail(@RequestParam("orderNo") String orderNo) {
        logger.info("renterOrderCostDetail controller orderNo={}",orderNo);
        try {
            RenterCostDetailDTO renterCostDetailDTO = orderCostDetailService.renterOrderCostDetail(orderNo);
            return ResponseData.success(renterCostDetailDTO);
        } catch (Exception e) {
            Cat.logError("renterOrderCostDetail exception orderNo="+orderNo,e);
            logger.error("renterOrderCostDetail exception orderNo="+orderNo,e);
            return ResponseData.error();
        }
    }
}
