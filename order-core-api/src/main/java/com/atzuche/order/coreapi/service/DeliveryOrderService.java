package com.atzuche.order.coreapi.service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.TransProgressDTO;
import com.atzuche.order.commons.enums.SrvGetReturnEnum;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.commons.vo.RenterOwnerSummarySectionDeliveryVO;
import com.atzuche.order.commons.vo.delivery.DistributionCostVO;
import com.atzuche.order.commons.vo.delivery.OrderCarTrusteeshipVO;
import com.atzuche.order.commons.vo.delivery.SimpleOrderInfoVO;
import com.atzuche.order.commons.vo.req.handover.req.HandoverCarInfoReqDTO;
import com.atzuche.order.commons.vo.req.handover.req.HandoverCarInfoReqVO;
import com.atzuche.order.delivery.entity.OrderCarTrusteeshipEntity;
import com.atzuche.order.delivery.entity.RenterOrderDeliveryEntity;
import com.atzuche.order.delivery.entity.TransSimpleMode;
import com.atzuche.order.delivery.enums.CarTypeEnum;
import com.atzuche.order.delivery.enums.OilCostTypeEnum;
import com.atzuche.order.delivery.service.OrderCarTrusteeshipService;
import com.atzuche.order.delivery.service.RenterOrderDeliveryModeService;
import com.atzuche.order.delivery.service.RenterOrderDeliveryService;
import com.atzuche.order.delivery.service.delivery.DeliveryCarInfoService;
import com.atzuche.order.delivery.service.handover.HandoverCarInfoService;
import com.atzuche.order.delivery.utils.CommonUtil;
import com.atzuche.order.delivery.vo.delivery.rep.DeliveryCarVO;
import com.atzuche.order.delivery.vo.delivery.rep.OwnerGetAndReturnCarDTO;
import com.atzuche.order.delivery.vo.delivery.rep.RenterGetAndReturnCarDTO;
import com.atzuche.order.delivery.vo.delivery.req.DeliveryCarRepVO;
import com.atzuche.order.delivery.vo.delivery.req.DeliveryReqVO;
import com.atzuche.order.delivery.vo.trusteeship.OrderCarTrusteeshipReqVO;
import com.atzuche.order.owner.mem.service.OwnerMemberService;
import com.atzuche.order.parentorder.entity.OrderEntity;
import com.atzuche.order.parentorder.service.OrderService;
import com.atzuche.order.rentercommodity.service.RenterCommodityService;
import com.atzuche.order.rentercost.entity.RenterOrderCostDetailEntity;
import com.atzuche.order.rentercost.service.RenterOrderCostDetailService;
import com.atzuche.order.rentercost.utils.RenterOrderCostDetailUtils;
import com.atzuche.order.rentermem.service.RenterMemberService;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.service.RenterOrderService;
import com.autoyol.commons.utils.DateUtil;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.platformcost.DateUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DeliveryOrderService {
	
	@Autowired
	private RenterOrderService renterOrderService;
	@Autowired
	private RenterCommodityService renterCommodityService;
	@Autowired
	private DeliveryCarInfoService deliveryCarInfoService;
	@Autowired
	private HandoverCarInfoService handoverCarInfoService;
	@Autowired
	private RenterOrderCostDetailService renterOrderCostDetailService;
	@Autowired
	private OrderService orderService;
	@Autowired
	private RenterMemberService renterMemberService;
	@Autowired
	private OwnerMemberService ownerMemberService;
	@Autowired
	private RenterOrderDeliveryService renterOrderDeliveryService;
	@Autowired
	private OrderCarTrusteeshipService orderCarTrusteeshipService;
	@Autowired
	private RenterOrderDeliveryModeService renterOrderDeliveryModeService;

	/**
     * 获取配送相关信息
     * @param deliveryCarDTO
     * @return
     */
    public DeliveryCarVO findDeliveryListByOrderNo(DeliveryCarRepVO deliveryCarDTO) {
        log.info("入参deliveryCarDTO：[{}]", deliveryCarDTO.toString());
        //获取有效的子订单号
        RenterOrderEntity renterOrderEntity = renterOrderService.getRenterOrderByOrderNoAndIsEffective(deliveryCarDTO.getOrderNo());
        RenterGoodsDetailDTO renterGoodsDetailDTO = renterCommodityService.getRenterGoodsDetail(renterOrderEntity.getRenterOrderNo(), false);
        OwnerGetAndReturnCarDTO ownerGetAndReturnCarDTO = OwnerGetAndReturnCarDTO.builder().build();
        ownerGetAndReturnCarDTO.setRanLiao(String.valueOf(OilCostTypeEnum.getOilCostType(renterGoodsDetailDTO.getCarEngineType())));
        String daykM = renterGoodsDetailDTO.getCarDayMileage().intValue() == 0 ? "不限" : String.valueOf(renterGoodsDetailDTO.getCarDayMileage());
        ownerGetAndReturnCarDTO.setDayKM(daykM);
        ownerGetAndReturnCarDTO.setOilContainer(String.valueOf(renterGoodsDetailDTO.getCarOilVolume()) + "L");
        boolean isEscrowCar = CarTypeEnum.isCarType(renterGoodsDetailDTO.getCarType());
        int carType = renterGoodsDetailDTO.getCarType();
        int oilTotalCalibration = renterGoodsDetailDTO.getOilTotalCalibration() == null ? 16 : renterGoodsDetailDTO.getOilTotalCalibration();
        DeliveryCarVO deliveryCarVO = deliveryCarInfoService.findDeliveryListByOrderNo(renterOrderEntity.getRenterOrderNo(), deliveryCarDTO, ownerGetAndReturnCarDTO, isEscrowCar, renterGoodsDetailDTO.getCarEngineType(), carType, renterGoodsDetailDTO);
        deliveryCarVO.setMaxOilNumber(String.valueOf(oilTotalCalibration));
        // 获取区间配送信息
        RenterOwnerSummarySectionDeliveryVO summary = getRenterOwnerSummarySectionDeliveryVO(renterOrderEntity, deliveryCarVO);
        deliveryCarVO.setSectionDelivery(summary);
        return deliveryCarVO;
    }
    
    
    /**
     * 更新交接车信息
     * @param deliveryCarVO
     * @throws Exception
     */
    public void updateHandoverCarInfo(DeliveryCarVO deliveryCarVO) throws Exception {
        log.info("入参handoverCarReqVO：[{}]", deliveryCarVO.toString());
        handoverCarInfoService.updateNewHandoverCarInfo(createHandoverCarInfoParams(deliveryCarVO));
    }
    
    
    /**
     * 更新取还车备注信息
     * @param deliveryReqVO
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateDeliveryRemark(DeliveryReqVO deliveryReqVO) throws Exception {
    	if(Objects.nonNull(deliveryReqVO.getGetDeliveryReqDTO())){
            handoverCarInfoService.updateDeliveryCarRemarkInfo(deliveryReqVO.getGetDeliveryReqDTO(),1);
        }
        if(Objects.nonNull(deliveryReqVO.getRenterDeliveryReqDTO())){
            handoverCarInfoService.updateDeliveryCarRemarkInfo(deliveryReqVO.getRenterDeliveryReqDTO(),2);
        }
    }
    
    
    
    /**
     * 获取配送取还车信息
     * @param deliveryCarDTO
     * @return
     */
    public DistributionCostVO findDeliveryCostByOrderNo(DeliveryCarRepVO deliveryCarDTO) {
        log.info("入参deliveryCarDTO：[{}]", deliveryCarDTO.toString());
        DistributionCostVO distributionCostVO = DistributionCostVO.builder().build();
        distributionCostVO.setReturnCarChaoYunNeng("0");
        distributionCostVO.setGetCarChaoYunNeng("0");
        distributionCostVO.setRenturnCarAmt("0");
        distributionCostVO.setGetCarAmt("0");
        List<RenterOrderCostDetailEntity> list = renterOrderCostDetailService.listRenterOrderCostDetail(deliveryCarDTO.getOrderNo(), deliveryCarDTO.getRenterOrderNo());
        if (CollectionUtils.isEmpty(list)) {
            return distributionCostVO;
        }
        list.stream().filter(r -> r.getCostCode().equals(RenterCashCodeEnum.SRV_GET_COST.getCashNo())).findFirst().ifPresent(getCrashVO -> {
            distributionCostVO.setGetCarAmt(String.valueOf(getCrashVO.getUnitPrice() * getCrashVO.getCount()));
        });
        list.stream().filter(r -> r.getCostCode().equals(RenterCashCodeEnum.SRV_RETURN_COST.getCashNo())).findFirst().ifPresent(getCrashVO -> {
            distributionCostVO.setRenturnCarAmt(String.valueOf(getCrashVO.getUnitPrice() * getCrashVO.getCount()));
        });
        list.stream().filter(r -> r.getCostCode().equals(RenterCashCodeEnum.GET_BLOCKED_RAISE_AMT.getCashNo())).findFirst().ifPresent(getCrashVO -> {
            distributionCostVO.setGetCarChaoYunNeng(String.valueOf(getCrashVO.getUnitPrice() * getCrashVO.getCount()));
        });
        list.stream().filter(r -> r.getCostCode().equals(RenterCashCodeEnum.RETURN_BLOCKED_RAISE_AMT.getCashNo())).findFirst().ifPresent(getCrashVO -> {
            distributionCostVO.setReturnCarChaoYunNeng(String.valueOf(getCrashVO.getUnitPrice() * getCrashVO.getCount()));
        });
        distributionCostVO.setAccurateGetSrvAmt(-RenterOrderCostDetailUtils.getAccurateGetSrvAmt(list));
        distributionCostVO.setAccurateReturnSrvAmt(-RenterOrderCostDetailUtils.getAccurateReturnSrvAmt(list));
        return distributionCostVO;
    }
    
    
    /**
     * 获取简单订单信息
     * @param orderNo
     * @return SimpleOrderInfoVO
     */
    public SimpleOrderInfoVO getSimpleOrderInfoVO(String orderNo) {
    	// 获取主订单信息
    	OrderEntity orderEntity = orderService.getOrderEntity(orderNo);
    	// 获取租客会员号
    	String renterMemNo = renterMemberService.getRenterNoByOrderNo(orderNo);
    	// 获取车主会员号
    	String ownerMemNo = ownerMemberService.getOwnerNoByOrderNo(orderNo);
    	SimpleOrderInfoVO simp = new SimpleOrderInfoVO();
    	simp.setOwnerMemNo(ownerMemNo);
    	simp.setRenterMemNo(renterMemNo);
    	if (orderEntity != null) {
    		simp.setCityCode(orderEntity.getCityCode());
    	}
    	return simp;
    }
    
    /**
     * 托管车新增
     * @param orderCarTrusteeshipVO
     */
    @Transactional(rollbackFor = Exception.class)
    public void addOrderCarTrusteeship(OrderCarTrusteeshipVO orderCarTrusteeshipVO) {
    	// 获取租客商品信息
        RenterOrderDeliveryEntity renterOrderDeliveryEntity = renterOrderDeliveryService.findRenterOrderByrOrderNo(orderCarTrusteeshipVO.getOrderNo(), 1);
        RenterGoodsDetailDTO renterGoodsDetailDTO = renterCommodityService.getRenterGoodsDetail(renterOrderDeliveryEntity.getRenterOrderNo(), false);
        if (Objects.nonNull(renterGoodsDetailDTO) && StringUtils.isNotBlank(renterGoodsDetailDTO.getCarStewardPhone())) {
            orderCarTrusteeshipVO.setTrusteeshipTelephone(renterGoodsDetailDTO.getCarStewardPhone());
        }
        OrderCarTrusteeshipEntity orderCarTrusteeshipEntity = new OrderCarTrusteeshipEntity();
        BeanUtils.copyProperties(orderCarTrusteeshipVO, orderCarTrusteeshipEntity);
        orderCarTrusteeshipEntity.setOutDepotTime(DateUtil.asLocalDateTime(orderCarTrusteeshipVO.getOutDepotTime()));
        orderCarTrusteeshipEntity.setInDepotTime(DateUtil.asLocalDateTime(orderCarTrusteeshipVO.getInDepotTime()));
        OrderCarTrusteeshipEntity orderNoAndCar = orderCarTrusteeshipService.selectObjectByOrderNoAndCar(orderCarTrusteeshipVO.getOrderNo(),orderCarTrusteeshipVO.getCarNo());
        if(Objects.nonNull(orderNoAndCar)) {
            CommonUtil.copyPropertiesIgnoreNull(orderCarTrusteeshipVO,orderNoAndCar);
            orderCarTrusteeshipService.updateOrderCarTrusteeship(orderNoAndCar);
        }
        orderCarTrusteeshipService.insertOrderCarTrusteeship(orderCarTrusteeshipEntity);
    }
    
    
    /**
     * 托管车信息
     * @param orderCarTrusteeshipReqVO
     * @return OrderCarTrusteeshipEntity
     */
    public OrderCarTrusteeshipEntity getOrderCarTrusteeshipEntity(OrderCarTrusteeshipReqVO orderCarTrusteeshipReqVO) {
    	OrderCarTrusteeshipEntity orderCarTrusteeshipEntity = orderCarTrusteeshipService.selectObjectByOrderNoAndCar(orderCarTrusteeshipReqVO.getOrderNo(),orderCarTrusteeshipReqVO.getCarNo());
    	return orderCarTrusteeshipEntity;
    }
    
    
    /**
     * 构造交接车数据
     * @param deliveryCarVO
     * @return
     */
    public HandoverCarInfoReqVO createHandoverCarInfoParams(DeliveryCarVO deliveryCarVO) {
        HandoverCarInfoReqVO handoverCarReqVO = new HandoverCarInfoReqVO();
        if (Objects.nonNull(deliveryCarVO.getRenterGetAndReturnCarDTO())) {
            RenterGetAndReturnCarDTO renterGetAndReturnCarDTO = deliveryCarVO.getRenterGetAndReturnCarDTO();
            HandoverCarInfoReqDTO handoverCarInfoReqDTO = new HandoverCarInfoReqDTO();
            handoverCarInfoReqDTO.setOrderNo(deliveryCarVO.getOrderNo());
            handoverCarInfoReqDTO.setOwnReturnKM(renterGetAndReturnCarDTO.getReturnKM());
            handoverCarInfoReqDTO.setOwnReturnOil(renterGetAndReturnCarDTO.getReturnCarOil());
            handoverCarInfoReqDTO.setRenterRetrunKM(renterGetAndReturnCarDTO.getGetKM());
            handoverCarInfoReqDTO.setRenterReturnOil(renterGetAndReturnCarDTO.getGetCarOil());
            handoverCarReqVO.setRenterHandoverCarDTO(handoverCarInfoReqDTO);

        }
        if (Objects.nonNull(deliveryCarVO.getOwnerGetAndReturnCarDTO())) {
            OwnerGetAndReturnCarDTO ownerGetAndReturnCarDTO = deliveryCarVO.getOwnerGetAndReturnCarDTO();
            HandoverCarInfoReqDTO handoverCarInfoReqDTO = new HandoverCarInfoReqDTO();
            handoverCarInfoReqDTO.setOrderNo(deliveryCarVO.getOrderNo());
            handoverCarInfoReqDTO.setOwnReturnKM(ownerGetAndReturnCarDTO.getReturnKM());
            handoverCarInfoReqDTO.setOwnReturnOil(ownerGetAndReturnCarDTO.getReturnCarOil());
            handoverCarInfoReqDTO.setRenterRetrunKM(ownerGetAndReturnCarDTO.getGetKM());
            handoverCarInfoReqDTO.setRenterReturnOil(ownerGetAndReturnCarDTO.getGetCarOil());
            handoverCarReqVO.setOwnerHandoverCarDTO(handoverCarInfoReqDTO);
        }
        return handoverCarReqVO;
    }
    
    
    /**
     * 获取区间配送信息
     * @param renterOrderEntity
     * @param deliveryCarVO
     * @return
     */
    public RenterOwnerSummarySectionDeliveryVO getRenterOwnerSummarySectionDeliveryVO(RenterOrderEntity renterOrderEntity, DeliveryCarVO deliveryCarVO) {
    	String orderNo = renterOrderEntity.getOrderNo();
    	String renterOrderNo = renterOrderEntity.getRenterOrderNo();
    	Integer getCarBeforeTime = 0;
		Integer returnCarAfterTime = 0;
		List<RenterOrderDeliveryEntity> deliveryList = renterOrderDeliveryService.listRenterOrderDeliveryByRenterOrderNo(renterOrderNo);
		Map<Integer, RenterOrderDeliveryEntity> deliveryMap = null;
		if (deliveryList != null && !deliveryList.isEmpty()) {
			deliveryMap = deliveryList.stream().collect(Collectors.toMap(RenterOrderDeliveryEntity::getType, deliver -> {return deliver;}));
		}
		if (deliveryMap != null) {
			RenterOrderDeliveryEntity srvGetDelivery = deliveryMap.get(SrvGetReturnEnum.SRV_GET_TYPE.getCode());
			RenterOrderDeliveryEntity srvReturnDelivery = deliveryMap.get(SrvGetReturnEnum.SRV_RETURN_TYPE.getCode());
			if (srvGetDelivery != null) {
				getCarBeforeTime = srvGetDelivery.getAheadOrDelayTime();
			}
			if (srvReturnDelivery != null) {
				returnCarAfterTime = srvReturnDelivery.getAheadOrDelayTime();
			}
		}
		// 获取主订单信息
    	OrderEntity orderEntity = orderService.getOrderEntity(orderNo);
    	Integer cityCode = StringUtils.isBlank(orderEntity.getCityCode()) ? 0:Integer.valueOf(orderEntity.getCityCode());
    	TransSimpleMode simpMode = new TransSimpleMode(orderNo, cityCode, null, renterOrderEntity.getExpRentTime(), 
    			renterOrderEntity.getExpRevertTime(), getCarBeforeTime, returnCarAfterTime);
    	OwnerGetAndReturnCarDTO owner = deliveryCarVO.getOwnerGetAndReturnCarDTO();
    	RenterGetAndReturnCarDTO renter = deliveryCarVO.getRenterGetAndReturnCarDTO();
    	TransProgressDTO pro = getTransProgress(renter, owner);
    	return renterOrderDeliveryModeService.getSectionDeliveryDetail(simpMode, renterOrderEntity, pro);
    }
    
    /**
     * 获取实际取还车时间
     * @param renter
     * @param owner
     * @return TransProgressDTO
     */
    public TransProgressDTO getTransProgress(RenterGetAndReturnCarDTO renter, OwnerGetAndReturnCarDTO owner) {
		String atRentTime = null;//取车人员取车时间
		String memRentTime = null;//租客实际取车时间
		String memRevertTime = null;//租客实际还车时间
		String atRevertTime= null;//还车人员还车时间
		if (owner != null) {
			atRentTime = StringUtils.isBlank(owner.getRealGetTime()) ? null:DateUtils.formate(DateUtils.parseLocalDateTime(owner.getRealGetTime(), DateUtils.DATE_DEFAUTE_4), DateUtils.DATE_DEFAUTE_1);
		    atRevertTime = StringUtils.isBlank(owner.getRealReturnTime()) ? null:DateUtils.formate(DateUtils.parseLocalDateTime(owner.getRealReturnTime(), DateUtils.DATE_DEFAUTE_4), DateUtils.DATE_DEFAUTE_1);
		}
		if (renter != null) {
			memRentTime = StringUtils.isBlank(renter.getRealGetTime()) ? null:DateUtils.formate(DateUtils.parseLocalDateTime(renter.getRealGetTime(), DateUtils.DATE_DEFAUTE_4), DateUtils.DATE_DEFAUTE_1);
			memRevertTime = StringUtils.isBlank(renter.getRealReturnTime()) ? null:DateUtils.formate(DateUtils.parseLocalDateTime(renter.getRealReturnTime(), DateUtils.DATE_DEFAUTE_4), DateUtils.DATE_DEFAUTE_1);
		}
		TransProgressDTO pro = new TransProgressDTO(atRentTime, memRentTime, memRevertTime, atRevertTime);
		return pro;
	}
}
