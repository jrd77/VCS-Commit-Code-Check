package com.atzuche.order.admin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atzuche.order.admin.constant.AdminOpTypeEnum;
import com.atzuche.order.admin.service.log.AdminLogService;
import com.atzuche.order.admin.vo.req.remark.OrderCarServiceRemarkRequestVO;
import com.atzuche.order.admin.vo.resp.remark.OrderRemarkResponseVO;
import com.atzuche.order.commons.entity.dto.RentCityAndRiskAccidentReqDTO;
import com.atzuche.order.commons.entity.orderDetailDto.OrderDTO;
import com.atzuche.order.commons.entity.orderDetailDto.OrderStatusDTO;
import com.atzuche.order.commons.enums.BuyInsurKeyEnum;
import com.atzuche.order.commons.vo.req.ModifyInsurFlagVO;
import com.autoyol.commons.web.ResponseData;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OperatorLogService {
	
	@Autowired
	private AdminLogService adminLogService;

	/**
     * 修改用车城市操作日志
     * @param req
     * @param orderDTO
     */
    public void saveUpdRentCityLog(RentCityAndRiskAccidentReqDTO req, OrderDTO orderDTO) {
    	if (req == null) {
    		return;
    	}
    	if (orderDTO == null) {
			return;
		}
    	try {
			// 描述
			String desc = "将【用车城市】从'"+orderDTO.getRentCity()+"'改成'"+req.getRentCity()+"';";
			// 保存
			adminLogService.insertLog(AdminOpTypeEnum.UPDATE_RENT_CITY, req.getOrderNo(), desc);
		} catch (Exception e) {
			log.error("修改用车城市操作日志异常  req=[{}]",req,e);
		}
    }
    
    
    /**
     * 修改是否风控事故操作日志
     * @param req
     * @param orderStatusDTO
     */
    public void saveUpdateRiskStatusLog(RentCityAndRiskAccidentReqDTO req, OrderStatusDTO orderStatusDTO) {
    	if (req == null) {
    		return;
    	}
    	if (orderStatusDTO == null) {
			return;
		}
    	try {
    		String oldRisk = orderStatusDTO.getIsRiskAccident()!=null && orderStatusDTO.getIsRiskAccident().equals(1)?"是":"否";
			String updRisk = req.getIsRiskAccident()!=null && req.getIsRiskAccident().equals(1)?"是":"否";
    		// 描述
			String desc = "将【是否风控事故】从'"+oldRisk+"'改成'"+updRisk+"';";
			// 保存
			adminLogService.insertLog(AdminOpTypeEnum.UPDATE_RISK_STATUS, req.getOrderNo(), desc);
		} catch (Exception e) {
			log.error("修改是否风控事故日志异常  req=[{}]",req,e);
		}
    }
    
    
    /**
     * 修改取送车备注操作日志
     * @param req
     * @param orderRemarkResponseVO
     */
    public void saveUpdateGetReturnCarRemarkLog(OrderCarServiceRemarkRequestVO req, OrderRemarkResponseVO orderRemarkResponseVO) {
    	if (req == null) {
    		return;
    	}
    	if (orderRemarkResponseVO == null) {
			return;
		}
    	try {
    		// 描述
			String desc = "将【取送车备注】从'"+orderRemarkResponseVO.getRemarkContent()+"'改成'"+req.getRemarkContent()+"';";
			// 保存
			adminLogService.insertLog(AdminOpTypeEnum.UPDATE_GETRETURN_REMARK, req.getOrderNo(), desc);
		} catch (Exception e) {
			log.error("修改取送车备注操作日志异常  req=[{}]",req,e);
		}
    }
    
    
    /**
     * 购买保障服务记录日志
     * @param modifyInsurFlagVO
     */
    public void saveBuyAbatementLog(ModifyInsurFlagVO modifyInsurFlagVO) {
    	if (modifyInsurFlagVO == null) {
    		return;
    	}
    	try {
    		String tex = modifyInsurFlagVO.getBuyValue() != null && modifyInsurFlagVO.getBuyValue() == 1 ? "购买":"未购买";
    		// 描述
    		String desc = "";
    		AdminOpTypeEnum opType = AdminOpTypeEnum.ABATEMENTFLAG;
    		if (BuyInsurKeyEnum.ABATEMENTFLAG.getKey().equals(modifyInsurFlagVO.getBuyKey())) {
    			desc = "将【"+AdminOpTypeEnum.ABATEMENTFLAG.getOpType()+"】从'未购买'改成'"+tex+"';";
    			opType = AdminOpTypeEnum.ABATEMENTFLAG;
            } else if (BuyInsurKeyEnum.TYREINSURFLAG.getKey().equals(modifyInsurFlagVO.getBuyKey())) {
            	desc = "将【"+AdminOpTypeEnum.TYREINSURFLAG.getOpType()+"】从'未购买'改成'"+tex+"';";
            	opType = AdminOpTypeEnum.TYREINSURFLAG;
            } else if (BuyInsurKeyEnum.DRIVERINSURFLAG.getKey().equals(modifyInsurFlagVO.getBuyKey())) {
            	desc = "将【"+AdminOpTypeEnum.DRIVERINSURFLAG.getOpType()+"】从'未购买'改成'"+tex+"';";
            	opType = AdminOpTypeEnum.DRIVERINSURFLAG;
            }
			// 保存
			adminLogService.insertLog(opType, modifyInsurFlagVO.getOrderNo(), desc);
		} catch (Exception e) {
			log.error("购买保障服务记录日志异常  modifyInsurFlagVO=[{}]",modifyInsurFlagVO,e);
		}
    }
}
