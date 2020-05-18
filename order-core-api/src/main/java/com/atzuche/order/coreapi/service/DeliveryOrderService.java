package com.atzuche.order.coreapi.service;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.commons.vo.delivery.DistributionCostVO;
import com.atzuche.order.commons.vo.delivery.OrderCarTrusteeshipVO;
import com.atzuche.order.commons.vo.delivery.SimpleOrderInfoVO;
import com.atzuche.order.commons.vo.req.handover.req.HandoverCarInfoReqDTO;
import com.atzuche.order.commons.vo.req.handover.req.HandoverCarInfoReqVO;
import com.atzuche.order.delivery.entity.OrderCarTrusteeshipEntity;
import com.atzuche.order.delivery.entity.RenterOrderDeliveryEntity;
import com.atzuche.order.delivery.enums.CarTypeEnum;
import com.atzuche.order.delivery.enums.OilCostTypeEnum;
import com.atzuche.order.delivery.service.OrderCarTrusteeshipService;
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
import com.atzuche.order.rentermem.service.RenterMemberService;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.service.RenterOrderService;
import com.autoyol.commons.utils.DateUtil;
import com.autoyol.commons.web.ResponseData;

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
        String daykM = renterGoodsDetailDTO.getCarDayMileage().intValue() == 0 ? "不限" :String.valueOf(renterGoodsDetailDTO.getCarDayMileage());
        ownerGetAndReturnCarDTO.setDayKM(daykM);
        ownerGetAndReturnCarDTO.setOilContainer(String.valueOf(renterGoodsDetailDTO.getCarOilVolume())+"L");
        boolean isEscrowCar = CarTypeEnum.isCarType(renterGoodsDetailDTO.getCarType());
        int carType = renterGoodsDetailDTO.getCarType();
        return deliveryCarInfoService.findDeliveryListByOrderNo(renterOrderEntity.getRenterOrderNo(),deliveryCarDTO,ownerGetAndReturnCarDTO,isEscrowCar,renterGoodsDetailDTO.getCarEngineType(),carType,renterGoodsDetailDTO);
    }
    
    
    /**
     * 更新交接车信息
     * @param deliveryCarVO
     * @throws Exception
     */
    public void updateHandoverCarInfo(DeliveryCarVO deliveryCarVO) throws Exception {
        log.info("入参handoverCarReqVO：[{}]", deliveryCarVO.toString());
        handoverCarInfoService.updateHandoverCarInfo(createHandoverCarInfoParams(deliveryCarVO));
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
}
