package com.atzuche.order.coreapi.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.commons.ResponseCheckUtil;
import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.coreapi.entity.dto.ModifyOrderDTO;
import com.atzuche.order.coreapi.entity.vo.req.SubmitOrderRiskCheckReqVO;
import com.atzuche.order.parentorder.entity.OrderEntity;
import com.atzuche.order.parentorder.entity.OrderSourceStatEntity;
import com.atzuche.order.parentorder.service.OrderSourceStatService;
import com.atzuche.order.rentercost.entity.RenterOrderCostDetailEntity;
import com.atzuche.order.renterorder.entity.dto.RenterOrderCostRespDTO;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.riskCheckService.api.RiskCheckServiceFeignService;
import com.autoyol.riskCheckService.api.VO.ChangeOrderCarRiskCheckReponseVO;
import com.autoyol.riskCheckService.api.VO.ChangeOrderCarRiskCheckRequestVO;
import com.autoyol.riskCheckService.api.VO.CreateOrderRiskCheckReponseVO;
import com.autoyol.riskCheckService.api.VO.CreateOrderRiskCheckRequestVO;
import com.autoyol.riskCheckService.api.VO.DelayOrderRiskCheckReponseVO;
import com.autoyol.riskCheckService.api.VO.DelayOrderRiskCheckRequestVO;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ModifyOrderRiskService {

	@Autowired
	private RiskCheckServiceFeignService riskCheckServiceFeignService;
	@Autowired
	private OrderSourceStatService orderSourceStatService;
	
	public void checkModifyRisk(ModifyOrderDTO modifyOrderDTO, RenterOrderCostRespDTO renterOrderCostRespDTO) {
		if (modifyOrderDTO.getScanCodeFlag() != null && modifyOrderDTO.getScanCodeFlag()) {
			// 扫码还车不校验
			return;
		}
		LocalDateTime initRevertTime = modifyOrderDTO.getOrderEntity().getExpRevertTime();
		LocalDateTime updRevertTime = modifyOrderDTO.getRevertTime();
		if (modifyOrderDTO.getTransferFlag() != null && modifyOrderDTO.getTransferFlag()) {
			// 换车
			checkWhenChangeOrderCar(modifyOrderDTO, renterOrderCostRespDTO);
		} else if (initRevertTime != null && updRevertTime != null && updRevertTime.isAfter(initRevertTime)) {
			// 延时
			checkWhenDelayOrder(modifyOrderDTO, renterOrderCostRespDTO);
		}
	}
	
	/**
     * 风控审核（换车）
     *
     * @param submitOrderRiskCheckReqVO 请求参数
     * @return Integer riskAuditId
     */
    public ChangeOrderCarRiskCheckReponseVO checkWhenChangeOrderCar(ModifyOrderDTO modifyOrderDTO, RenterOrderCostRespDTO renterOrderCostRespDTO) {
        log.info("ModifyOrderRiskService.checkWhenChangeOrderCar param is,modifyOrderDTO:[{}]",
                JSON.toJSONString(modifyOrderDTO));
        String orderNo = modifyOrderDTO.getOrderNo();
        // 获取订单来源信息
        OrderSourceStatEntity osse = orderSourceStatService.selectByOrderNo(orderNo);
        // 获取日均价
        Integer averagePrice = 0;
        List<RenterOrderCostDetailEntity> costList = renterOrderCostRespDTO.getRenterOrderCostDetailDTOList();
        for (RenterOrderCostDetailEntity cost:costList) {
        	if (RenterCashCodeEnum.RENT_AMT.getCashNo().equals(cost.getCostCode())) {
        		averagePrice = cost.getUnitPrice();
        	}
        }
        ChangeOrderCarRiskCheckRequestVO req = convertToChangeOrderCarRiskCheckRequestVO(modifyOrderDTO, osse, averagePrice);
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "风控服务");
        try {
            Cat.logEvent(CatConstants.FEIGN_METHOD, "riskCheckServiceFeignService.checkWhenChangeOrderCar");
            Cat.logEvent(CatConstants.FEIGN_PARAM, JSON.toJSONString(req));
            log.info("Invoke ModifyOrderRiskService.checkWhenChangeOrderCar api. param is, req:[{}]", JSON.toJSONString(req));
            ResponseData<ChangeOrderCarRiskCheckReponseVO> responseData =
                    riskCheckServiceFeignService.checkWhenChangeOrderCar(req);
            log.info("Invoke ModifyOrderRiskService.checkWhenChangeOrderCar api. result is, responseData:[{}]",
                    JSON.toJSONString(responseData));
            Cat.logEvent(CatConstants.FEIGN_RESULT, JSON.toJSONString(responseData));

            ResponseCheckUtil.checkResponse(responseData);

            t.setStatus(Transaction.SUCCESS);
            return responseData.getData();

        } catch (Exception e) {
            log.error("换车调用风控服务异常.param is, reqVo:[{}]", req, e);
            Cat.logError("换车调用风控服务异常.", e);
            t.setStatus(e);
            throw e;
        } finally {
            t.complete();
        }
    }
    
    
    /**
     * 风控审核（订单延时）
     *
     * @param submitOrderRiskCheckReqVO 请求参数
     * @return Integer riskAuditId
     */
    public DelayOrderRiskCheckReponseVO checkWhenDelayOrder(ModifyOrderDTO modifyOrderDTO, RenterOrderCostRespDTO renterOrderCostRespDTO) {
        log.info("ModifyOrderRiskService.checkWhenDelayOrder param is,modifyOrderDTO:[{}]",
                JSON.toJSONString(modifyOrderDTO));
        String orderNo = modifyOrderDTO.getOrderNo();
        // 获取订单来源信息
        OrderSourceStatEntity osse = orderSourceStatService.selectByOrderNo(orderNo);
        // 获取日均价
        Integer averagePrice = 0;
        List<RenterOrderCostDetailEntity> costList = renterOrderCostRespDTO.getRenterOrderCostDetailDTOList();
        for (RenterOrderCostDetailEntity cost:costList) {
        	if (RenterCashCodeEnum.RENT_AMT.getCashNo().equals(cost.getCostCode())) {
        		averagePrice = cost.getUnitPrice();
        	}
        }
        DelayOrderRiskCheckRequestVO req = convertToDelayOrderRiskCheckRequestVO(modifyOrderDTO, osse, averagePrice);
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "风控服务");
        try {
            Cat.logEvent(CatConstants.FEIGN_METHOD, "riskCheckServiceFeignService.checkWhenDelayOrder");
            Cat.logEvent(CatConstants.FEIGN_PARAM, JSON.toJSONString(req));
            log.info("Invoke ModifyOrderRiskService.checkWhenDelayOrder api. param is, req:[{}]", JSON.toJSONString(req));
            ResponseData<DelayOrderRiskCheckReponseVO> responseData =
                    riskCheckServiceFeignService.checkWhenDelayOrder(req);
            log.info("Invoke ModifyOrderRiskService.checkWhenDelayOrder api. result is, responseData:[{}]",
                    JSON.toJSONString(responseData));
            Cat.logEvent(CatConstants.FEIGN_RESULT, JSON.toJSONString(responseData));

            ResponseCheckUtil.checkResponse(responseData);

            t.setStatus(Transaction.SUCCESS);
            return responseData.getData();

        } catch (Exception e) {
            log.error("订单延时调用风控服务异常.param is, reqVo:[{}]", req, e);
            Cat.logError("订单延时调用风控服务异常.", e);
            t.setStatus(e);
            throw e;
        } finally {
            t.complete();
        }
    }
    
    
    /**
     * 对象转换
     * @param modify
     * @param osse
     * @param averagePrice
     * @return ChangeOrderCarRiskCheckRequestVO
     */
    public ChangeOrderCarRiskCheckRequestVO convertToChangeOrderCarRiskCheckRequestVO(ModifyOrderDTO modify, OrderSourceStatEntity osse, Integer averagePrice) {
    	ChangeOrderCarRiskCheckRequestVO change = new ChangeOrderCarRiskCheckRequestVO();
    	change.setOrderNo(modify.getOrderNo());
    	OrderEntity oe = modify.getOrderEntity();
    	change.setReqOrderTime(localDateTime2Date(oe.getReqTime()));
    	change.setMemNoRenter(Integer.valueOf(modify.getMemNo()));
    	change.setOrderInsideType(oe.getCategory() != null ? oe.getCategory().toString():null);
    	change.setOrderBusinessParentType(osse.getBusinessParentType());
    	change.setOrderBusinessChildType(osse.getBusinessChildType());
    	change.setOrderPlatformParentType(osse.getPlatformParentType());
    	change.setOrderPlatformChildType(osse.getPlatformChildType());
    	change.setMemClientIp(osse.getReqIp());
    	RenterGoodsDetailDTO goods = modify.getRenterGoodsDetailDTO();
    	change.setCarNo(goods.getCarNo());
    	change.setCarOwnerType(goods.getCarOwnerType() != null ? goods.getCarOwnerType().toString():null);
    	change.setRentTime(localDateTime2Date(modify.getRentTime()));
    	change.setRevertTime(localDateTime2Date(modify.getRevertTime()));
    	change.setAverageDailyPrice(averagePrice != null ? averagePrice.toString():null);
    	change.setGetFlag(modify.getSrvGetFlag());
    	change.setReturnFlag(modify.getSrvReturnFlag());
    	change.setRenterGetAddr(modify.getGetCarAddress());
    	change.setRenterGetAddrLat(modify.getGetCarLat());
    	change.setRenterGetAddrLon(modify.getGetCarLon());
    	change.setRenterReturnAddr(modify.getRevertCarAddress());
    	change.setRenterReturnAddrLat(modify.getRevertCarLat());
    	change.setRenterReturnAddrLon(modify.getRevertCarLon());
    	change.setRenterGpsAddr(osse.getReqAddr());
    	change.setRenterGpsAddrLat(osse.getPublicLatitude());
    	change.setRenterGpsAddrLon(osse.getPublicLongitude());
    	change.setUseCarCityCode(oe.getCityCode());
    	change.setUseCarCityName(oe.getCityName());
    	change.setRiskReqId(oe.getRiskAuditId());
    	return change;
    }
    
    /**
     * 延迟换车对象转换
     * @param modify
     * @param osse
     * @param averagePrice
     * @return DelayOrderRiskCheckRequestVO
     */
    public DelayOrderRiskCheckRequestVO convertToDelayOrderRiskCheckRequestVO(ModifyOrderDTO modify, OrderSourceStatEntity osse, Integer averagePrice) {
    	DelayOrderRiskCheckRequestVO change = new DelayOrderRiskCheckRequestVO();
    	change.setOrderNo(modify.getOrderNo());
    	OrderEntity oe = modify.getOrderEntity();
    	change.setReqOrderTime(localDateTime2Date(oe.getReqTime()));
    	change.setMemNoRenter(Integer.valueOf(modify.getMemNo()));
    	change.setOrderInsideType(oe.getCategory() != null ? oe.getCategory().toString():null);
    	change.setOrderBusinessParentType(osse.getBusinessParentType());
    	change.setOrderBusinessChildType(osse.getBusinessChildType());
    	change.setOrderPlatformParentType(osse.getPlatformParentType());
    	change.setOrderPlatformChildType(osse.getPlatformChildType());
    	change.setMemClientIp(osse.getReqIp());
    	RenterGoodsDetailDTO goods = modify.getRenterGoodsDetailDTO();
    	change.setCarNo(goods.getCarNo());
    	change.setCarOwnerType(goods.getCarOwnerType() != null ? goods.getCarOwnerType().toString():null);
    	change.setRentTime(localDateTime2Date(modify.getRentTime()));
    	change.setRevertTime(localDateTime2Date(modify.getRevertTime()));
    	change.setAverageDailyPrice(averagePrice != null ? averagePrice.toString():null);
    	change.setGetFlag(modify.getSrvGetFlag());
    	change.setReturnFlag(modify.getSrvReturnFlag());
    	change.setRenterGetAddr(modify.getGetCarAddress());
    	change.setRenterGetAddrLat(modify.getGetCarLat());
    	change.setRenterGetAddrLon(modify.getGetCarLon());
    	change.setRenterReturnAddr(modify.getRevertCarAddress());
    	change.setRenterReturnAddrLat(modify.getRevertCarLat());
    	change.setRenterReturnAddrLon(modify.getRevertCarLon());
    	change.setRenterGpsAddr(osse.getReqAddr());
    	change.setRenterGpsAddrLat(osse.getPublicLatitude());
    	change.setRenterGpsAddrLon(osse.getPublicLongitude());
    	change.setUseCarCityCode(oe.getCityCode());
    	change.setUseCarCityName(oe.getCityName());
    	change.setRiskReqId(oe.getRiskAuditId());
    	return change;
    }
    
    
    /**
     * LocalDateTime转换为Date
     * @param localDateTime
     */
    public Date localDateTime2Date(LocalDateTime localDateTime){
    	if (localDateTime == null) {
    		return null;
    	}
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = localDateTime.atZone(zoneId);
        Date date = Date.from(zdt.toInstant());
        return date;
    }

}
