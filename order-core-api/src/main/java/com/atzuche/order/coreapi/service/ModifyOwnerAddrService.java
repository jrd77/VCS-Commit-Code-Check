package com.atzuche.order.coreapi.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.atzuche.config.client.api.CityConfigSDK;
import com.atzuche.config.client.api.DefaultConfigContext;
import com.atzuche.config.client.api.SysConfigSDK;
import com.atzuche.config.common.entity.CityEntity;
import com.atzuche.config.common.entity.SysConfigEntity;
import com.atzuche.order.commons.GlobalConstant;
import com.atzuche.order.commons.entity.dto.CostBaseDTO;
import com.atzuche.order.commons.entity.dto.OwnerGoodsDetailDTO;
import com.atzuche.order.commons.enums.FineSubsidyCodeEnum;
import com.atzuche.order.commons.enums.FineSubsidySourceCodeEnum;
import com.atzuche.order.commons.enums.FineTypeEnum;
import com.atzuche.order.commons.enums.OrderStatusEnum;
import com.atzuche.order.commons.enums.SrvGetReturnEnum;
import com.atzuche.order.commons.vo.CarBasicInfo;
import com.atzuche.order.commons.vo.OwnerTransAddressReqVO;
import com.atzuche.order.coreapi.entity.dto.ModifyOrderOwnerDTO;
import com.atzuche.order.coreapi.entity.vo.GetReturnCarInfoVO;
import com.atzuche.order.coreapi.modifyorder.exception.ModifyOwnerAddrCheckException;
import com.atzuche.order.coreapi.modifyorder.exception.ModifyOwnerAddrOwnerOrderNotFindException;
import com.atzuche.order.delivery.entity.RenterOrderDeliveryEntity;
import com.atzuche.order.delivery.service.RenterOrderDeliveryService;
import com.atzuche.order.delivery.service.delivery.DeliveryCarService;
import com.atzuche.order.delivery.vo.delivery.UpdateFlowOrderDTO;
import com.atzuche.order.owner.commodity.service.OwnerCommodityService;
import com.atzuche.order.ownercost.entity.ConsoleOwnerOrderFineDeatailEntity;
import com.atzuche.order.ownercost.entity.OwnerOrderEntity;
import com.atzuche.order.ownercost.service.ConsoleOwnerOrderFineDeatailService;
import com.atzuche.order.ownercost.service.OwnerOrderService;
import com.atzuche.order.parentorder.entity.OrderEntity;
import com.atzuche.order.parentorder.entity.OrderStatusEntity;
import com.atzuche.order.parentorder.service.OrderService;
import com.atzuche.order.parentorder.service.OrderStatusService;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.service.RenterOrderChangeApplyService;
import com.atzuche.order.renterorder.service.RenterOrderService;
import com.autoyol.cardistance.CarDistanceUtil;
import com.autoyol.platformcost.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ModifyOwnerAddrService {
	@Autowired
    private CityConfigSDK cityConfigSDK;
    @Autowired
    private SysConfigSDK sysConfigSDK;
    @Autowired
    private OwnerOrderService ownerOrderService;
    @Autowired
    private RenterOrderService renterOrderService;
    @Autowired
    private RenterOrderDeliveryService renterOrderDeliveryService;
    @Autowired
    private OrderStatusService orderStatusService;
    @Autowired
    private OwnerCommodityService ownerCommodityService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private RenterOrderChangeApplyService renterOrderChangeApplyService;
    @Autowired
    private ConsoleOwnerOrderFineDeatailService consoleOwnerOrderFineDeatailService;
    @Autowired
    private DeliveryCarService deliveryCarService;
    @Autowired
    private ModifyOrderConfirmService modifyOrderConfirmService;
    
    private static final String YES = "1";
    private static final String NO = "0";
    private static final String CHARGE_HINT_TEXT = "超1公里增加取还车服务费";
    private static final String CHARGE_HINT_FREE_TEXT = "1公里内取还车服务免费";
    // 修改地址距离原地址>1公里需要收费
    private static final double MODIFY_ADDR_NEED_CHARGE_DISTANCE = 1.0;
    
    private static final Integer PROXY_CAR_OWNER_TYPE = 35; //代管车辆
    
    public GetReturnCarInfoVO getGetReturnCarInfoVO(OwnerTransAddressReqVO ownerTransAddressReqVO) {
    	// 主订单号
    	String orderNo = ownerTransAddressReqVO.getOrderNo();
    	// 车主会员号
    	String memNo = ownerTransAddressReqVO.getMemNo();
    	// 获取修改前有效车主订单信息
		OwnerOrderEntity ownerOrderEntity = ownerOrderService.getOwnerOrderByOrderNoAndIsEffective(orderNo);
		if (ownerOrderEntity == null || !memNo.equals(ownerOrderEntity.getMemNo())) {
			throw new ModifyOwnerAddrOwnerOrderNotFindException();
		}
		// 获取车主车辆信息
		OwnerGoodsDetailDTO ownerGoodsDetailDTO = ownerCommodityService.getOwnerGoodsDetail(ownerOrderEntity.getOwnerOrderNo(), false);
		// 获取修改前有效租客子订单信息
		RenterOrderEntity initRenterOrder = renterOrderService.getRenterOrderByOrderNoAndIsEffective(orderNo);
		// 获取租客配送订单信息
		List<RenterOrderDeliveryEntity> deliveryList = renterOrderDeliveryService.listRenterOrderDeliveryByRenterOrderNo(initRenterOrder.getRenterOrderNo());
		// 获取订单结算状态
		OrderStatusEntity orderStatusEntity = orderStatusService.getByOrderNo(orderNo);
		// 获取主订单信息
		OrderEntity orderEntity = orderService.getOrderEntity(orderNo);
		String getCarAddr = ownerGoodsDetailDTO.getCarRealAddr();
        String getCarLon = ownerGoodsDetailDTO.getCarRealLon();
        String getCarLat = ownerGoodsDetailDTO.getCarRealLat();
        int srvGetFlag = initRenterOrder.getIsGetCar() == null ? 0:initRenterOrder.getIsGetCar();
        int srvReturnFlag = initRenterOrder.getIsReturnCar() == null ? 0:initRenterOrder.getIsReturnCar();
        Integer orderStatus = orderStatusEntity.getStatus();//订单状态
        LocalDateTime realRentTime = initRenterOrder.getExpRentTime();
        LocalDateTime realReverTime = initRenterOrder.getExpRevertTime();
        LocalDateTime now = LocalDateTime.now();
        // 城市编码
        Integer cityCode = StringUtils.isBlank(orderEntity.getCityCode()) ? 0:Integer.valueOf(orderEntity.getCityCode());
        // 提前下单时间
        Integer beforeTransTimeSpan = getBeforeTransTimeSpan(cityCode);
        Map<Integer, RenterOrderDeliveryEntity> deliveryMap = null;
		if (deliveryList != null && !deliveryList.isEmpty()) {
			deliveryMap = deliveryList.stream().collect(Collectors.toMap(RenterOrderDeliveryEntity::getType, deliver -> {return deliver;}));
		}
		RenterOrderDeliveryEntity srvGetDelivery = null;
		RenterOrderDeliveryEntity srvReturnDelivery = null;
		if (deliveryMap != null) {
			srvGetDelivery = deliveryMap.get(SrvGetReturnEnum.SRV_GET_TYPE.getCode());
			srvReturnDelivery = deliveryMap.get(SrvGetReturnEnum.SRV_RETURN_TYPE.getCode());
		}
        // 订单当前的车主取还车地址经纬度信息
        String currentOwnerSrvGetAddr = srvGetDelivery == null || StringUtils.isEmpty(srvGetDelivery.getOwnerGetReturnAddr()) ? getCarAddr : srvGetDelivery.getOwnerGetReturnAddr();
        String currentOwnerSrvGetLon = srvGetDelivery == null || StringUtils.isEmpty(srvGetDelivery.getOwnerGetReturnAddrLon()) ? getCarLon : srvGetDelivery.getOwnerGetReturnAddrLon();
        String currentOwnerSrvGetLat = srvGetDelivery == null || StringUtils.isEmpty(srvGetDelivery.getOwnerGetReturnAddrLat()) ? getCarLat : srvGetDelivery.getOwnerGetReturnAddrLat();
        String currentOwnerSrvReturnAddr = srvReturnDelivery == null || StringUtils.isEmpty(srvReturnDelivery.getOwnerGetReturnAddr()) ? getCarAddr : srvReturnDelivery.getOwnerGetReturnAddr();
        String currentOwnerSrvReturnLon = srvReturnDelivery == null || StringUtils.isEmpty(srvReturnDelivery.getOwnerGetReturnAddrLon()) ? getCarLon : srvReturnDelivery.getOwnerGetReturnAddrLon();
        String currentOwnerSrvReturnLat = srvReturnDelivery == null || StringUtils.isEmpty(srvReturnDelivery.getOwnerGetReturnAddrLat()) ? getCarLat : srvReturnDelivery.getOwnerGetReturnAddrLat();
        // 车主计划修改的取还车经纬度地址信息
        String getCarAddressText = ownerTransAddressReqVO.getGetCarAddressText();
        String srvGetLon = ownerTransAddressReqVO.getSrvGetLon();
        String srvGetLat = ownerTransAddressReqVO.getSrvGetLat();
        String returnCarAddressText = ownerTransAddressReqVO.getReturnCarAddressText();
        String srvReturnLon = ownerTransAddressReqVO.getSrvReturnLon();
        String srvReturnLat = ownerTransAddressReqVO.getSrvReturnLat();
        String getCarAddressModifiable = NO;
        String returnCarAddressModifiable = NO;
        // 收费提示文案
        String chargePromptText = "";
        // 底部收费提示文案
        String chargeHintText = "";
        // 收费金额,默认不收费
        Integer chargeAmount = null;
        // 未修改取车地址标识
        Boolean isCurrentGetCarAddress = this.isCurrentAddress(currentOwnerSrvGetAddr, getCarAddressText);
        // 未修改还车地址标识
        Boolean isCurrentReturnCarAddress = this.isCurrentAddress(currentOwnerSrvReturnAddr, returnCarAddressText);
        int getCost = getGetFineAmt();
        int returnCost = getReturnFineAmt();
        if (this.isAllowModify(realRentTime, now, srvGetFlag)) {
            // 可以修改取车地址
            getCarAddressModifiable = YES;
            // 未修改地址标识
            if (!isCurrentGetCarAddress) {
                if (this.isNeedCharge(realRentTime, now, beforeTransTimeSpan, srvGetLon, srvGetLat, currentOwnerSrvGetLon, currentOwnerSrvGetLat,orderStatus)) {
                    chargePromptText = "您当前取车地址距离原取车地址大于1公里，产生额外修改费用。该费用将从本次订单收益中扣除";
                    chargeHintText = CHARGE_HINT_TEXT;
                    chargeAmount = getCost;
                } else {
                    if (this.isTimeNeedCharge(realRentTime, now, beforeTransTimeSpan, orderStatus)) {
                        chargeHintText = CHARGE_HINT_FREE_TEXT;
                    }
                    chargeAmount = 0;
                }
            }
        } else {
            getCarAddressText = null;
            srvGetLon = null;
            srvGetLat = null;
        }
        if (this.isAllowModify(realReverTime, now, srvReturnFlag, orderStatus)) {
            // 可以修改还车地址
            returnCarAddressModifiable = YES;
            if (!isCurrentReturnCarAddress) {
                if (this.isNeedCharge(realReverTime, now, beforeTransTimeSpan, srvReturnLon, srvReturnLat, currentOwnerSrvReturnLon, currentOwnerSrvReturnLat,orderStatus)) {
                    chargePromptText = "您当前还车地址距离原还车地址大于1公里，产生额外修改费用。该费用将从本次订单收益中扣除";
                    chargeHintText = CHARGE_HINT_TEXT;
                    if (chargeAmount == null) {
                    	chargeAmount = 0;
                    }
                    chargeAmount = returnCost + chargeAmount;
                } else {
                    if (this.isTimeNeedCharge(realReverTime, now, beforeTransTimeSpan,orderStatus)) {
                        chargeHintText = CHARGE_HINT_FREE_TEXT;
                    }
                    chargeAmount = chargeAmount != null && chargeAmount > 0 ? chargeAmount : 0;
                }
            }
        } else {
            returnCarAddressText = null;
            srvReturnLon = null;
            srvReturnLat = null;
        }
        ModifyOrderOwnerDTO modifyOwner = getModifyOrderOwnerDTO(ownerOrderEntity, initRenterOrder, ownerGoodsDetailDTO, deliveryList);
        CarBasicInfo carBasicInfo = getCarBasicInfo(ownerGoodsDetailDTO);
        GetReturnCarInfoVO getReturnCarInfoVO = new GetReturnCarInfoVO();
        getReturnCarInfoVO.setCityCode(orderEntity.getCityCode());
        getReturnCarInfoVO.setCityName(orderEntity.getCityName());
        getReturnCarInfoVO.setGetCarAddressText(getCarAddressText == null ? currentOwnerSrvGetAddr : getCarAddressText);
        getReturnCarInfoVO.setGetCarAddressModifiable(getCarAddressModifiable);
        getReturnCarInfoVO.setSrvGetLon(srvGetLon == null ? currentOwnerSrvGetLon : srvGetLon);
        getReturnCarInfoVO.setSrvGetLat(srvGetLat == null ? currentOwnerSrvGetLat : srvGetLat);
        getReturnCarInfoVO.setReturnCarAddressText(returnCarAddressText == null ? currentOwnerSrvReturnAddr : returnCarAddressText);
        getReturnCarInfoVO.setReturnCarAddressModifiable(returnCarAddressModifiable);
        getReturnCarInfoVO.setSrvReturnLon(srvReturnLon == null ? currentOwnerSrvReturnLon : srvReturnLon);
        getReturnCarInfoVO.setSrvReturnLat(srvReturnLat == null ? currentOwnerSrvReturnLat : srvReturnLat);
        getReturnCarInfoVO.setChargePromptText(chargePromptText);
        getReturnCarInfoVO.setChargeHintText(chargeHintText);
        getReturnCarInfoVO.setChargeAmount(chargeAmount == null ? null : chargeAmount.toString());
        getReturnCarInfoVO.setModifyRuleUrl(null);
        getReturnCarInfoVO.setThreePartyContractUrl(null);
        getReturnCarInfoVO.setCarBasicInfo(carBasicInfo);
        getReturnCarInfoVO.setCurrentGetCarAddress(isCurrentGetCarAddress);
        getReturnCarInfoVO.setCurrentReturnCarAddress(isCurrentReturnCarAddress);
        getReturnCarInfoVO.setModifyOrderOwnerDTO(modifyOwner);
        return getReturnCarInfoVO;
        
    }
    
    /**
     * 车主修改交接车地址
     * @param ownerTransAddressReqVO
     */
    @Transactional(rollbackFor=Exception.class)
    public void updateOwnerAddr(OwnerTransAddressReqVO ownerTransAddressReqVO) {
    	// 获取车主修改地址处理后信息
    	GetReturnCarInfoVO getReturnCarInfoVO = getGetReturnCarInfoVO(ownerTransAddressReqVO);
    	// 校验
    	validate(ownerTransAddressReqVO, getReturnCarInfoVO);
    	ModifyOrderOwnerDTO modifyOwner = getReturnCarInfoVO.getModifyOrderOwnerDTO();
        List<RenterOrderDeliveryEntity> deliveryList = modifyOwner.getDeliveryList();
    	// 修改车主取还车地址信息
        Map<Integer, RenterOrderDeliveryEntity> deliveryMap = null;
		if (deliveryList != null && !deliveryList.isEmpty()) {
			deliveryMap = deliveryList.stream().collect(Collectors.toMap(RenterOrderDeliveryEntity::getType, deliver -> {return deliver;}));
		}
		RenterOrderDeliveryEntity srvGetDelivery = null;
		RenterOrderDeliveryEntity srvReturnDelivery = null;
		if (deliveryMap != null) {
			srvGetDelivery = deliveryMap.get(SrvGetReturnEnum.SRV_GET_TYPE.getCode());
			srvReturnDelivery = deliveryMap.get(SrvGetReturnEnum.SRV_RETURN_TYPE.getCode());
		}
    	if (srvGetDelivery != null) {
    		RenterOrderDeliveryEntity renterOrderDeliveryEntity = new RenterOrderDeliveryEntity();
    		renterOrderDeliveryEntity.setId(srvGetDelivery.getId());
    		renterOrderDeliveryEntity.setOwnerGetReturnAddr(ownerTransAddressReqVO.getGetCarAddressText());
    		renterOrderDeliveryEntity.setOwnerGetReturnAddrLat(ownerTransAddressReqVO.getSrvGetLat());
    		renterOrderDeliveryEntity.setOwnerGetReturnAddrLon(ownerTransAddressReqVO.getSrvGetLon());
    		renterOrderDeliveryService.updateByPrimaryKeySelective(renterOrderDeliveryEntity);
    	}
    	if (srvReturnDelivery != null) {
    		RenterOrderDeliveryEntity renterOrderDeliveryEntity = new RenterOrderDeliveryEntity();
    		renterOrderDeliveryEntity.setId(srvReturnDelivery.getId());
    		renterOrderDeliveryEntity.setOwnerGetReturnAddr(ownerTransAddressReqVO.getReturnCarAddressText());
    		renterOrderDeliveryEntity.setOwnerGetReturnAddrLat(ownerTransAddressReqVO.getSrvReturnLat());
    		renterOrderDeliveryEntity.setOwnerGetReturnAddrLon(ownerTransAddressReqVO.getSrvReturnLon());
    		renterOrderDeliveryService.updateByPrimaryKeySelective(renterOrderDeliveryEntity);
    	}
    	// 保存车主修改地址罚金
    	if (StringUtils.isNotBlank(getReturnCarInfoVO.getChargeAmount())) {
    		Integer modifyAddrCost = NumberUtils.toInt(getReturnCarInfoVO.getChargeAmount());
    		if (modifyAddrCost > 0) {
    			CostBaseDTO costBaseDTO = new CostBaseDTO();
    			costBaseDTO.setOrderNo(ownerTransAddressReqVO.getOrderNo());
    			costBaseDTO.setMemNo(ownerTransAddressReqVO.getMemNo());
	        	ConsoleOwnerOrderFineDeatailEntity consoleOwnerOrderFineDeatailEntity = consoleOwnerOrderFineDeatailService.fineDataConvert(costBaseDTO, -modifyAddrCost, FineSubsidyCodeEnum.PLATFORM,
                        FineSubsidySourceCodeEnum.OWNER, FineTypeEnum.MODIFY_ADDRESS_FINE);
	        	consoleOwnerOrderFineDeatailService.addFineRecord(consoleOwnerOrderFineDeatailEntity);
    		}
    	}
    	// 修改仁云
    	Boolean isCurrentGetCarAddress = getReturnCarInfoVO.getCurrentGetCarAddress();
    	Boolean isCurrentReturnCarAddress = getReturnCarInfoVO.getCurrentReturnCarAddress();
    	if (!isCurrentGetCarAddress || !isCurrentReturnCarAddress) {
    		if (modifyOwner.getSrvGetFlag() != null && modifyOwner.getSrvGetFlag() == 1) {
    			UpdateFlowOrderDTO getUpdFlow = modifyOrderConfirmService.getUpdateFlowOrderDTO(modifyOwner, modifyOwner.getOwnerOrderEffective(), "take", "ownerAddr");
    			getUpdFlow.setOwnerGetAddr(getReturnCarInfoVO.getGetCarAddressText());
    			getUpdFlow.setOwnerGetLat(getReturnCarInfoVO.getSrvGetLat());
    			getUpdFlow.setOwnerGetLon(getReturnCarInfoVO.getSrvGetLon());
    			getUpdFlow.setOwnerReturnAddr(getReturnCarInfoVO.getReturnCarAddressText());
    			getUpdFlow.setOwnerReturnLat(getReturnCarInfoVO.getSrvReturnLat());
    			getUpdFlow.setOwnerReturnLon(getReturnCarInfoVO.getSrvReturnLon());
    			deliveryCarService.updateRenYunFlowOrderInfo(getUpdFlow);
        	}
    		if (modifyOwner.getSrvReturnFlag() != null && modifyOwner.getSrvReturnFlag() == 1) {
    			UpdateFlowOrderDTO getUpdFlow = modifyOrderConfirmService.getUpdateFlowOrderDTO(modifyOwner, modifyOwner.getOwnerOrderEffective(), "back", "ownerAddr");
    			getUpdFlow.setOwnerGetAddr(getReturnCarInfoVO.getGetCarAddressText());
    			getUpdFlow.setOwnerGetLat(getReturnCarInfoVO.getSrvGetLat());
    			getUpdFlow.setOwnerGetLon(getReturnCarInfoVO.getSrvGetLon());
    			getUpdFlow.setOwnerReturnAddr(getReturnCarInfoVO.getReturnCarAddressText());
    			getUpdFlow.setOwnerReturnLat(getReturnCarInfoVO.getSrvReturnLat());
    			getUpdFlow.setOwnerReturnLon(getReturnCarInfoVO.getSrvReturnLon());
    			deliveryCarService.updateRenYunFlowOrderInfo(getUpdFlow);
    		}
    	}
    }
    
    
    /**
     * 对象转换
     * @param ownerOrderEntity
     * @param initRenterOrder
     * @param ownerGoodsDetailDTO
     * @param deliveryList
     * @return ModifyOrderOwnerDTO
     */
    public ModifyOrderOwnerDTO getModifyOrderOwnerDTO(OwnerOrderEntity ownerOrderEntity,RenterOrderEntity initRenterOrder, OwnerGoodsDetailDTO ownerGoodsDetailDTO, List<RenterOrderDeliveryEntity> deliveryList) {
    	ModifyOrderOwnerDTO modify = new ModifyOrderOwnerDTO();
    	modify.setOrderNo(initRenterOrder.getOrderNo());
    	modify.setOwnerGoodsDetailDTO(ownerGoodsDetailDTO);
    	modify.setOwnerOrderEffective(ownerOrderEntity);
    	int srvGetFlag = initRenterOrder.getIsGetCar() == null ? 0:initRenterOrder.getIsGetCar();
        int srvReturnFlag = initRenterOrder.getIsReturnCar() == null ? 0:initRenterOrder.getIsReturnCar();
    	modify.setSrvGetFlag(srvGetFlag);
    	modify.setSrvReturnFlag(srvReturnFlag);
    	Map<Integer, RenterOrderDeliveryEntity> deliveryMap = null;
		if (deliveryList != null && !deliveryList.isEmpty()) {
			deliveryMap = deliveryList.stream().collect(Collectors.toMap(RenterOrderDeliveryEntity::getType, deliver -> {return deliver;}));
		}
		RenterOrderDeliveryEntity srvGetDelivery = null;
		RenterOrderDeliveryEntity srvReturnDelivery = null;
		if (deliveryMap != null) {
			srvGetDelivery = deliveryMap.get(SrvGetReturnEnum.SRV_GET_TYPE.getCode());
			srvReturnDelivery = deliveryMap.get(SrvGetReturnEnum.SRV_RETURN_TYPE.getCode());
		}
		modify.setRentTime(initRenterOrder.getExpRentTime());
		modify.setRevertTime(initRenterOrder.getExpRevertTime());
		if (srvGetDelivery != null) {
			modify.setGetCarAddress(srvGetDelivery.getRenterGetReturnAddr());
			modify.setGetCarLon(srvGetDelivery.getRenterGetReturnAddrLon());
			modify.setGetCarLat(srvGetDelivery.getRenterGetReturnAddrLat());
		}
    	if (srvReturnDelivery != null) {
    		modify.setRevertCarAddress(srvReturnDelivery.getRenterGetReturnAddr());
    		modify.setRevertCarLon(srvReturnDelivery.getRenterGetReturnAddrLon());
    		modify.setRevertCarLat(srvReturnDelivery.getRenterGetReturnAddrLat());
    	}
    	modify.setDeliveryList(deliveryList);
    	return modify;
    }
    
    
    /**
     * 校验
     * @param ownerTransAddressReqVO
     * @param getReturnCarInfoVO
     */
    public void validate(OwnerTransAddressReqVO ownerTransAddressReqVO, GetReturnCarInfoVO getReturnCarInfoVO) {
    	if (ownerTransAddressReqVO == null || getReturnCarInfoVO == null) {
    		return;
    	}
        //代管车权限校验
        String consoleInvoke = StringUtils.isNotBlank(ownerTransAddressReqVO.getConsoleInvoke()) ? ownerTransAddressReqVO.getConsoleInvoke() : "";
        //管理后台来源操作不做校验
        if (!(StringUtils.isNotBlank(consoleInvoke) && "1".equalsIgnoreCase(consoleInvoke))) {
        	CarBasicInfo carBasicInfo = getReturnCarInfoVO.getCarBasicInfo();
            if (carBasicInfo != null && PROXY_CAR_OWNER_TYPE.equals(carBasicInfo.getCarOwnerType())) {
            	throw new ModifyOwnerAddrCheckException("100569", "抱歉，代管车不可对订单进行操作");
            }
        }
        if (getReturnCarInfoVO.getCurrentReturnCarAddress() && getReturnCarInfoVO.getCurrentGetCarAddress()) {
           throw new ModifyOwnerAddrCheckException("510004", "修改地址与原地址相同，请重新选择");
        }
        // 取车地址在提交的时候是否已经不允许修改
        boolean isNotAllowModifyGetAddress = NO.equals(getReturnCarInfoVO.getGetCarAddressModifiable())
                && !getReturnCarInfoVO.getCurrentGetCarAddress();
        // 还车地址在提交的时候是否已经不允许修改
        boolean isNotAllowModifyReturnAddress = NO.equals(getReturnCarInfoVO.getReturnCarAddressModifiable())
                && !getReturnCarInfoVO.getCurrentReturnCarAddress();
        if (isNotAllowModifyGetAddress || isNotAllowModifyReturnAddress) {
            throw new ModifyOwnerAddrCheckException("510003", "抱歉，当前操作已超时，请重试！");
        }
        Integer beforeChargeAmount = StringUtils.isEmpty(ownerTransAddressReqVO.getChargeAmount()) ? 0 : NumberUtils.toInt(ownerTransAddressReqVO.getChargeAmount());
        Integer nowChargeAmount = StringUtils.isEmpty(getReturnCarInfoVO.getChargeAmount()) ? 0 : Integer.parseInt(getReturnCarInfoVO.getChargeAmount());
        boolean isAmountChange = !beforeChargeAmount.equals(nowChargeAmount);
        if (isAmountChange) {
            throw new ModifyOwnerAddrCheckException("510001","当前修改地址费用已发生变化，请重试！");
        }
        // 申请修改记录
        Integer applyCount = renterOrderChangeApplyService.getRenterOrderChangeApplyCountByOrderNo(ownerTransAddressReqVO.getOrderNo());
        if (applyCount != null && applyCount > 0) {
            // 当前订单是否存在待车主确认的租客修改请求
           throw new ModifyOwnerAddrCheckException("510002", "租客提交了新的修改订单请求需要您确认，处理结果可能会影响您修改地址的费用，请确认后重试！");
        }
    } 
    
    
    /**
     * 获取车辆基本信息
     * @param ownerGoodsDetailDTO
     * @return CarBasicInfo
     */
    private CarBasicInfo getCarBasicInfo(OwnerGoodsDetailDTO ownerGoodsDetailDTO) {
    	CarBasicInfo carBasicInfo = new CarBasicInfo();
        carBasicInfo.setPicPath(ownerGoodsDetailDTO.getCarImageUrl());
        carBasicInfo.setPlateNum(ownerGoodsDetailDTO.getCarPlateNum());
        carBasicInfo.setBrandInfo(ownerGoodsDetailDTO.getCarBrandTxt());
        carBasicInfo.setSweptVolum(ownerGoodsDetailDTO.getCarCylinderCapacity()+ownerGoodsDetailDTO.getCarCcUnit());
        if (ownerGoodsDetailDTO.getCarGearboxType() != null) {
        	carBasicInfo.setGbType(String.valueOf(ownerGoodsDetailDTO.getCarGearboxType()));
        }
        if (ownerGoodsDetailDTO.getCarNo() != null) {
        	carBasicInfo.setCarNo(String.valueOf(ownerGoodsDetailDTO.getCarNo()));
        }
        if (ownerGoodsDetailDTO.getCarDayMileage() == null || ownerGoodsDetailDTO.getCarDayMileage() == 0) {
            carBasicInfo.setLimitMileage("不限里程");
        } else {
            carBasicInfo.setLimitMileage("日均限" + String.valueOf(ownerGoodsDetailDTO.getCarDayMileage()) + "km");
        }
        carBasicInfo.setCarOwnerType(ownerGoodsDetailDTO.getCarOwnerType());
        return carBasicInfo;
    }
    
    
    
    /**
     * 获取提前小时数
     * @param cityCode
     * @return Integer
     */
    private Integer getBeforeTransTimeSpan(Integer cityCode) {
    	log.info("config-从城市配置中获取beforeTransTimeSpan,cityCode=[{}]", cityCode);
        CityEntity configByCityCode = cityConfigSDK.getConfigByCityCode(new DefaultConfigContext(),cityCode);
        log.info("config-从城市配置中获取beforeTransTimeSpan,configByCityCode=[{}]", JSON.toJSONString(configByCityCode));
        Integer beforeTransTimeSpan = configByCityCode.getBeforeTransTimeSpan()==null?CommonUtils.BEFORE_TRANS_TIME_SPAN:configByCityCode.getBeforeTransTimeSpan();
        return beforeTransTimeSpan;
    }
	
    /**
     * 获取修改取车违约金
     * @return Integer
     */
	private Integer getGetFineAmt() {
		List<SysConfigEntity> sysConfigSDKConfig = sysConfigSDK.getConfig(new DefaultConfigContext());
		List<SysConfigEntity> sysConfigEntityList = Optional.ofNullable(sysConfigSDKConfig)
                .orElseGet(ArrayList::new)
                .stream()
                .filter(x -> GlobalConstant.GET_RETURN_FINE_AMT.equals(x.getAppType()))
                .collect(Collectors.toList());
		SysConfigEntity sysGetFineAmt = sysConfigEntityList.stream().filter(x -> GlobalConstant.GET_FINE_AMT.equals(x.getItemKey())).findFirst().get();
        log.info("config-从配置中获取取车违约金getFineAmt=[{}]",JSON.toJSONString(sysGetFineAmt));
        Integer getCost = (sysGetFineAmt==null||sysGetFineAmt.getItemValue()==null) ? CommonUtils.MODIFY_GET_FINE_AMT : Integer.valueOf(sysGetFineAmt.getItemValue());
        return getCost;
        
	}
	
	/**
	 * 获取修改还车违约金
	 * @return Integer
	 */
	private Integer getReturnFineAmt() {
		List<SysConfigEntity> sysConfigSDKConfig = sysConfigSDK.getConfig(new DefaultConfigContext());
		List<SysConfigEntity> sysConfigEntityList = Optional.ofNullable(sysConfigSDKConfig)
                .orElseGet(ArrayList::new)
                .stream()
                .filter(x -> GlobalConstant.GET_RETURN_FINE_AMT.equals(x.getAppType()))
                .collect(Collectors.toList());

        SysConfigEntity sysReturnFineAmt = sysConfigEntityList.stream().filter(x -> GlobalConstant.RETURN_FINE_AMT.equals(x.getItemKey())).findFirst().get();
		log.info("config-从配置中获取还车违约金returnFineAmt=[{}]",JSON.toJSONString(sysReturnFineAmt));
        Integer returnCost = (sysReturnFineAmt==null||sysReturnFineAmt.getItemValue()==null) ? CommonUtils.MODIFY_RETURN_FINE_AMT : Integer.valueOf(sysReturnFineAmt.getItemValue());
        return returnCost;
	}
	
	
	/**
     * 计算是否未修改地址
     *
     * @param currentAddress
     * @param serviceAddress
     * @return
     */
    private boolean isCurrentAddress(String currentAddress, String serviceAddress) {
        return StringUtils.isEmpty(serviceAddress) || currentAddress.equalsIgnoreCase(serviceAddress);
    }

	/**
     * 是否可以修改该段取还车地址
     *
     * @param localDateTime
     * @param serviceFlag
     * @return boolean
     */
    private boolean isAllowModify(LocalDateTime localDateTime, LocalDateTime now, Integer serviceFlag) {
        if (serviceFlag == 1 && now.isBefore(localDateTime)) {
            // 只有在使用的取还车服务开关,并且当前时间在取还车时间之前才可以修改
            return true;
        }
        return false;
    }
    
    /**
     * 
     * @param localDateTime
     * @param now
     * @param serviceFlag
     * @param orderStatus
     * @return
     */
    private boolean isAllowModify(LocalDateTime localDateTime, LocalDateTime now, Integer serviceFlag,Integer orderStatus) {
        if (serviceFlag == 1 && 
        		(now.isBefore(localDateTime) || 
        				(orderStatus != null && orderStatus.intValue() == OrderStatusEnum.TO_RETURN_CAR.getStatus())) ) {  //now.isBefore(localDateTime)  当前时间小于还车时间
            // 只有在使用的取还车服务开关,并且当前时间在取还车时间之前才可以修改
            return true;
        }
        return false;
    }
    
    /**
     * 计算本次修改是否需要收费
     *
     * @param localDateTime
     * @param now
     * @param serviceLon
     * @param serviceLat
     * @return
     */
    private boolean isNeedCharge(LocalDateTime localDateTime, LocalDateTime now, Integer beforeHour,
                                 String serviceLon, String serviceLat, String currentServiceLon, String currentServiceLat,Integer orderStatus) {
        return this.isTimeNeedCharge(localDateTime, now, beforeHour, orderStatus)
                && this.isAddressNeedCharge(serviceLon, serviceLat, currentServiceLon, currentServiceLat);
    }
    
    /**
     * 时间是否命中收费规则,4小时前免费，4小时内收费。
     *
     * @param localDateTime
     * @param now
     * @param beforeHour
     * @return
     */
    private boolean isTimeNeedCharge(LocalDateTime localDateTime, LocalDateTime now, Integer beforeHour, Integer orderStatus) {
        return now.isAfter(localDateTime.minusHours(beforeHour)) || 
        		(now.isAfter(localDateTime) && orderStatus != null && 
        		orderStatus.intValue() == OrderStatusEnum.TO_RETURN_CAR.getStatus()) ;
    }
    
    /**
     * 地址是否命中收费规则
     * @param serviceLon
     * @param serviceLat
     * @param currentServiceLon
     * @param currentServiceLat
     * @return
     */
    private boolean isAddressNeedCharge(String serviceLon, String serviceLat, String currentServiceLon, String currentServiceLat) {
        if (StringUtils.isNotEmpty(serviceLon) && StringUtils.isNotEmpty(serviceLat)) {
            double realDistance = CarDistanceUtil.getDistance(NumberUtils.toDouble(serviceLat), NumberUtils.toDouble(serviceLon), 
            		NumberUtils.toDouble(currentServiceLat), NumberUtils.toDouble(currentServiceLon));
            
            log.info("calc owner modify address is need charge,realDistance={}", realDistance);
            return realDistance > MODIFY_ADDR_NEED_CHARGE_DISTANCE;
        }

        return false;
    }
}
