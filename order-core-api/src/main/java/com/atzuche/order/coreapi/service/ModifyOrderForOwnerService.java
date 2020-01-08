package com.atzuche.order.coreapi.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.atzuche.order.ownercost.entity.OwnerOrderCostEntity;
import com.atzuche.order.ownercost.entity.OwnerOrderEntity;
import com.atzuche.order.ownercost.entity.OwnerOrderIncrementDetailEntity;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atzuche.order.commons.entity.dto.CostBaseDTO;
import com.atzuche.order.commons.entity.dto.OwnerGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.OwnerGoodsPriceDetailDTO;
import com.atzuche.order.commons.entity.dto.OwnerMemberDTO;
import com.atzuche.order.commons.entity.dto.OwnerMemberRightDTO;
import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.atzuche.order.commons.enums.SrvGetReturnEnum;
import com.atzuche.order.coreapi.entity.dto.ModifyOrderDTO;
import com.atzuche.order.coreapi.entity.dto.ModifyOrderOwnerDTO;
import com.atzuche.order.coreapi.entity.vo.req.CarRentTimeRangeReqVO;
import com.atzuche.order.coreapi.entity.vo.res.CarRentTimeRangeResVO;
import com.atzuche.order.coreapi.modifyorder.exception.ModifyOrderParameterException;
import com.atzuche.order.coreapi.service.GoodsService.CarDetailReqVO;
import com.atzuche.order.delivery.entity.RenterOrderDeliveryEntity;
import com.atzuche.order.owner.commodity.service.OwnerCommodityService;
import com.atzuche.order.ownercost.entity.OwnerOrderPurchaseDetailEntity;
import com.atzuche.order.ownercost.entity.OwnerOrderSubsidyDetailEntity;
import com.atzuche.order.owner.mem.service.OwnerMemberService;
import com.atzuche.order.rentercost.entity.RenterOrderSubsidyDetailEntity;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.ownercost.service.OwnerOrderCostCombineService;
import com.atzuche.order.ownercost.service.OwnerOrderCostService;
import com.atzuche.order.ownercost.service.OwnerOrderIncrementDetailService;
import com.atzuche.order.ownercost.service.OwnerOrderPurchaseDetailService;
import com.atzuche.order.ownercost.service.OwnerOrderService;
import com.atzuche.order.ownercost.service.OwnerOrderSubsidyDetailService;
import com.atzuche.order.parentorder.entity.OrderEntity;
import com.atzuche.order.parentorder.service.OrderService;
import com.autoyol.commons.web.ResponseData;
import com.dianping.cat.Cat;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ModifyOrderForOwnerService {
	
	@Autowired
	private OwnerOrderService ownerOrderService;
	@Autowired
	private OwnerMemberService ownerMemberService;
	@Autowired
	private GoodsService goodsService;
	@Autowired
	private OwnerCommodityService ownerCommodityService;
	@Autowired
	private OwnerOrderCostCombineService ownerOrderCostCombineService;
	@Autowired
	private OwnerOrderPurchaseDetailService ownerOrderPurchaseDetailService;
	@Autowired
	private OwnerOrderSubsidyDetailService ownerOrderSubsidyDetailService;
	@Autowired
	private OwnerOrderIncrementDetailService ownerOrderIncrementDetailService;
	@Autowired
	private OwnerOrderCostService ownerOrderCostService;
	@Autowired
	private UniqueOrderNoService uniqueOrderNoService;
	@Autowired
	private OrderService orderService;

	/**
	 * 租客修改订单重新下车主订单逻辑(相当于车主同意)
	 * @param orderNo 主订单号
	 * @return ResponseData
	 */
	public void modifyOrderForOwner(ModifyOrderOwnerDTO modifyOrderOwnerDTO, RenterOrderSubsidyDetailEntity renterSubsidy) {
		log.info("租客修改订单重新下车主订单modifyOrderOwnerDTO=[{}], renterSubsidy=[{}]", modifyOrderOwnerDTO, renterSubsidy);
		if (modifyOrderOwnerDTO == null) {
			throw new ModifyOrderParameterException();
		}
		// 主订单号
		String orderNo = modifyOrderOwnerDTO.getOrderNo();
		// 生成车主订单号
		String ownerOrderNo = uniqueOrderNoService.getOwnerOrderNo(orderNo);
		// 赋值车主订单号
		modifyOrderOwnerDTO.setOwnerOrderNo(ownerOrderNo);
		// 获取主订单信息
		OrderEntity orderEntity = orderService.getOrderEntity(orderNo);
		// 设置城市编号
		modifyOrderOwnerDTO.setCityCode(orderEntity.getCityCode());
		// 获取修改前有效车主订单信息
		OwnerOrderEntity ownerOrderEntity = ownerOrderService.getOwnerOrderByOrderNoAndIsEffective(orderNo);
		// 获取车主会员信息
		OwnerMemberDTO ownerMemberDTO = getOwnerMemberDTO(ownerOrderEntity.getOwnerOrderNo(), ownerOrderNo);
		// 设置车主会员信息
		modifyOrderOwnerDTO.setOwnerMemberDTO(ownerMemberDTO);
		// 获取车主端商品详情
		OwnerGoodsDetailDTO ownerGoodsDetailDTO = getOwnerGoodsDetailDTO(modifyOrderOwnerDTO, ownerOrderEntity);
		// 设置商品信息
		modifyOrderOwnerDTO.setOwnerGoodsDetailDTO(ownerGoodsDetailDTO);
		// 封装新的车主子订单对象
		OwnerOrderEntity ownerOrderEffective = convertToOwnerOrderEntity(modifyOrderOwnerDTO, ownerOrderEntity);
		// 设置车主订单信息
		modifyOrderOwnerDTO.setOwnerOrderEffective(ownerOrderEffective);
		// 封装基本对象
		CostBaseDTO costBaseDTO = convertToCostBaseDTO(modifyOrderOwnerDTO, ownerMemberDTO);
		// 获取租金费用信息
		List<OwnerOrderPurchaseDetailEntity> purchaseList = getOwnerOrderPurchaseDetailEntityList(costBaseDTO, ownerGoodsDetailDTO);
		// 获取增值服务费用信息
		List<OwnerOrderIncrementDetailEntity> incrementList = getOwnerOrderIncrementDetailEntityList(costBaseDTO, modifyOrderOwnerDTO, ownerGoodsDetailDTO);
		// 获取车主补贴（车主券）
		OwnerOrderSubsidyDetailEntity subsidyEntity = getOwnerOrderSubsidyDetailEntity(modifyOrderOwnerDTO, ownerMemberDTO, renterSubsidy);
		// 获取车主费用总账信息
		OwnerOrderCostEntity ownerOrderCostEntity = getOwnerOrderCostEntity(costBaseDTO, purchaseList, incrementList, subsidyEntity);
		
		// 保存商品信息
		ownerCommodityService.saveCommodity(ownerGoodsDetailDTO);
		// 保存会员信息
		ownerMemberService.save(ownerMemberDTO);
		// 保存采购信息
		ownerOrderPurchaseDetailService.saveOwnerOrderPurchaseDetailBatch(purchaseList);
		// 保存增值服务费用信息
		ownerOrderIncrementDetailService.saveOwnerOrderIncrementDetailBatch(incrementList);
		// 保存补贴费用信息
		ownerOrderSubsidyDetailService.saveOwnerOrderSubsidyDetail(subsidyEntity);
		// 保存车主费用总表
		ownerOrderCostService.saveOwnerOrderCost(ownerOrderCostEntity);
		// 保存新车主子单
		ownerOrderService.saveOwnerOrder(ownerOrderEffective);
		// 上笔车主子订单置为无效
		ownerOrderService.updateOwnerOrderInvalidById(ownerOrderEntity.getId());
	}
	
	
	/**
	 * 对象转换
	 * @param renterOrder
	 * @param deliveryList
	 * @return ModifyOrderOwnerDTO
	 */
	public ModifyOrderOwnerDTO getModifyOrderOwnerDTO(RenterOrderEntity renterOrder, List<RenterOrderDeliveryEntity> deliveryList) {
		ModifyOrderOwnerDTO modifyOwner = new ModifyOrderOwnerDTO();
		if (renterOrder != null) {
			modifyOwner.setOrderNo(renterOrder.getOrderNo());
			modifyOwner.setRentTime(renterOrder.getExpRentTime());
			modifyOwner.setRevertTime(renterOrder.getExpRevertTime());
			modifyOwner.setSrvGetFlag(renterOrder.getIsGetCar());
			modifyOwner.setSrvReturnFlag(renterOrder.getIsReturnCar());
		}
		Map<Integer, RenterOrderDeliveryEntity> deliveryMap = null;
		if (deliveryList != null && !deliveryList.isEmpty()) {
			deliveryMap = deliveryList.stream().collect(Collectors.toMap(RenterOrderDeliveryEntity::getType, deliver -> {return deliver;}));
		}
		if (deliveryMap != null) {
			RenterOrderDeliveryEntity srvGetDelivery = deliveryMap.get(SrvGetReturnEnum.SRV_GET_TYPE.getCode());
			RenterOrderDeliveryEntity srvReturnDelivery = deliveryMap.get(SrvGetReturnEnum.SRV_RETURN_TYPE.getCode());
			if (srvGetDelivery != null) {
				modifyOwner.setGetCarAddress(srvGetDelivery.getRenterGetReturnAddr());
				modifyOwner.setGetCarLat(srvGetDelivery.getRenterGetReturnAddrLat());
				modifyOwner.setGetCarLon(srvGetDelivery.getRenterGetReturnAddrLon());
			}
			if (srvReturnDelivery != null) {
				modifyOwner.setRevertCarAddress(srvReturnDelivery.getRenterGetReturnAddr());
				modifyOwner.setRevertCarLat(srvReturnDelivery.getRenterGetReturnAddrLat());
				modifyOwner.setRevertCarLon(srvReturnDelivery.getRenterGetReturnAddrLon());
			}
		}
		return modifyOwner;
	}
	
	
	/**
	 * 获取租金费用信息
	 * @param costBaseDTO
	 * @param modifyOrderOwnerDTO
	 * @param ownerGoodsDetailDTO
	 * @return List<OwnerOrderPurchaseDetailEntity>
	 */
	public List<OwnerOrderPurchaseDetailEntity> getOwnerOrderPurchaseDetailEntityList(CostBaseDTO costBaseDTO, OwnerGoodsDetailDTO ownerGoodsDetailDTO) {
		// 计算车主租金收益
		List<OwnerOrderPurchaseDetailEntity> purchaseList = ownerOrderCostCombineService.listOwnerRentAmtEntity(costBaseDTO, ownerGoodsDetailDTO.getOwnerGoodsPriceDetailDTOList());
		return purchaseList;
	}
	
	
	/**
	 * 获取增值服务
	 * @param costBaseDTO
	 * @param modifyOrderOwnerDTO
	 * @param ownerGoodsDetailDTO
	 * @return
	 */
	public List<OwnerOrderIncrementDetailEntity> getOwnerOrderIncrementDetailEntityList(CostBaseDTO costBaseDTO, ModifyOrderOwnerDTO modifyOrderOwnerDTO, OwnerGoodsDetailDTO ownerGoodsDetailDTO) {
		List<OwnerOrderIncrementDetailEntity> incrementList = new ArrayList<OwnerOrderIncrementDetailEntity>();
		// 获取车主取车费用
		OwnerOrderIncrementDetailEntity srvGetFeeEntity = ownerOrderCostCombineService.getOwnerSrvGetAmtEntity(costBaseDTO, ownerGoodsDetailDTO.getCarOwnerType(), modifyOrderOwnerDTO.getSrvGetFlag());
		// 获取车主还车费用
		OwnerOrderIncrementDetailEntity srvReturnFeeEntity = ownerOrderCostCombineService.getOwnerSrvReturnAmtEntity(costBaseDTO, ownerGoodsDetailDTO.getCarOwnerType(), modifyOrderOwnerDTO.getSrvReturnFlag());
		incrementList.add(srvGetFeeEntity);
		incrementList.add(srvReturnFeeEntity);
		return incrementList;
	}
	
	
	/**
	 * 获取车主补贴
	 * @param modifyOrderOwnerDTO
	 * @param ownerMemberDTO
	 * @param renterSubsidy
	 * @return OwnerOrderSubsidyDetailEntity
	 */
	public OwnerOrderSubsidyDetailEntity getOwnerOrderSubsidyDetailEntity(ModifyOrderOwnerDTO modifyOrderOwnerDTO, OwnerMemberDTO ownerMemberDTO, RenterOrderSubsidyDetailEntity renterSubsidy) {
		if (modifyOrderOwnerDTO == null || ownerMemberDTO == null || renterSubsidy == null) {
			return null;
		}
		OwnerOrderSubsidyDetailEntity subsidyEntity = new OwnerOrderSubsidyDetailEntity();
		subsidyEntity.setMemNo(ownerMemberDTO.getMemNo());
		subsidyEntity.setOrderNo(modifyOrderOwnerDTO.getOrderNo());
		subsidyEntity.setOwnerOrderNo(modifyOrderOwnerDTO.getOwnerOrderNo());
		subsidyEntity.setSubsidyTypeName(renterSubsidy.getSubsidyTypeName());
		subsidyEntity.setSubsidyAmount(-renterSubsidy.getSubsidyAmount());
		subsidyEntity.setSubsidyTargetCode(renterSubsidy.getSubsidyTargetCode());
		subsidyEntity.setSubsidyDesc(renterSubsidy.getSubsidyDesc());
		subsidyEntity.setSubsidyTargetName(renterSubsidy.getSubsidyTargetName());
		subsidyEntity.setSubsidySourceName(renterSubsidy.getSubsidySourceName());
		subsidyEntity.setSubsidySourceCode(renterSubsidy.getSubsidySourceCode());
		subsidyEntity.setSubsidyTypeCode(renterSubsidy.getSubsidyTypeCode());
		subsidyEntity.setSubsidyVoucher(renterSubsidy.getSubsidyVoucher());
		subsidyEntity.setSubsidyDesc(renterSubsidy.getSubsidyDesc());
		subsidyEntity.setSubsidyCostCode(renterSubsidy.getSubsidyCostCode());
		subsidyEntity.setSubsidyCostName(renterSubsidy.getSubsidyCostName());
		return subsidyEntity;
	}
	
	
	/**
	 * 获取车主费用总账
	 * @param costBaseDTO
	 * @param purchaseList
	 * @param incrementList
	 * @param subsidyEntity
	 * @return OwnerOrderCostEntity
	 */
	public OwnerOrderCostEntity getOwnerOrderCostEntity(CostBaseDTO costBaseDTO, List<OwnerOrderPurchaseDetailEntity> purchaseList,List<OwnerOrderIncrementDetailEntity> incrementList, OwnerOrderSubsidyDetailEntity subsidyEntity) {
		// 增值服务费总和
		Integer incrementAmount = 0;
		if (incrementList != null && !incrementList.isEmpty()) {
			incrementAmount = incrementList.stream().mapToInt(OwnerOrderIncrementDetailEntity::getTotalAmount).sum();
		}
		// 租金总和
		Integer purchaseAmount = 0;
		if (purchaseList != null && !purchaseList.isEmpty()) {
			purchaseAmount = purchaseList.stream().mapToInt(OwnerOrderPurchaseDetailEntity::getTotalAmount).sum();
		}
		// 补贴总和
		Integer subsidyAmount = 0;
		if (subsidyEntity != null) {
			subsidyAmount = subsidyEntity.getSubsidyAmount() == null ? 0:subsidyEntity.getSubsidyAmount();
		}
		OwnerOrderCostEntity cost = new OwnerOrderCostEntity();
		cost.setOrderNo(costBaseDTO.getOrderNo());
		cost.setOwnerOrderNo(costBaseDTO.getOwnerOrderNo());
		cost.setMemNo(costBaseDTO.getMemNo());
		cost.setIncrementAmount(incrementAmount);
		cost.setPurchaseAmount(purchaseAmount);
		cost.setSubsidyAmount(subsidyAmount);
		return cost;
	}
	
	
	/**
	 * 数据转化为CarDetailReqVO
	 * @param ownerOrderNo
	 * @return CarDetailReqVO
	 */
	public GoodsService.CarDetailReqVO convertToCarDetailReqVO(ModifyOrderOwnerDTO modifyOrderOwnerDTO, OwnerOrderEntity ownerOrderEntity) {
		// 车主子订单号
		String ownerOrderNo = ownerOrderEntity.getOwnerOrderNo();
		// 获取车主商品信息
		OwnerGoodsDetailDTO ownerGoodsDetailDTO = ownerCommodityService.getOwnerGoodsDetail(ownerOrderNo, false);
		GoodsService.CarDetailReqVO carDetailReqVO = new GoodsService.CarDetailReqVO(); 
		if (ownerGoodsDetailDTO.getCarAddrIndex() != null) {
			carDetailReqVO.setAddrIndex(ownerGoodsDetailDTO.getCarAddrIndex());
		}
		carDetailReqVO.setCarNo(String.valueOf(ownerGoodsDetailDTO.getCarNo()));
		carDetailReqVO.setRentTime(modifyOrderOwnerDTO.getRentTime());
		carDetailReqVO.setRevertTime(modifyOrderOwnerDTO.getRevertTime());
		Integer useSpecialPriceFlag = ownerOrderEntity.getIsUseSpecialPrice();
		Boolean useSpecialPrice = (useSpecialPriceFlag != null && useSpecialPriceFlag == 1)?true:false;
		carDetailReqVO.setUseSpecialPrice(useSpecialPrice);
		return carDetailReqVO;
	}
	
	
	/**
	 * 封装新的车主子订单对象
	 * @param modifyOrderOwnerDTO
	 * @param ownerOrderEntity
	 * @param carLon
	 * @param carLat
	 * @return OwnerOrderEntity
	 */
	public OwnerOrderEntity convertToOwnerOrderEntity(ModifyOrderOwnerDTO modifyOrderOwnerDTO, OwnerOrderEntity ownerOrderEntity) {
		// 获取提前延后时间
		CarRentTimeRangeResVO carRentTimeRangeResVO = getCarRentTimeRangeResVO(modifyOrderOwnerDTO);
		// 封装新的车主子订单
		OwnerOrderEntity ownerOrderEntityEffective = new OwnerOrderEntity();
		// 数据拷贝
		BeanUtils.copyProperties(ownerOrderEntity, ownerOrderEntityEffective);
		ownerOrderEntityEffective.setOwnerOrderNo(modifyOrderOwnerDTO.getOwnerOrderNo());
		ownerOrderEntityEffective.setId(null);
		ownerOrderEntityEffective.setIsEffective(1);
		ownerOrderEntityEffective.setExpRentTime(modifyOrderOwnerDTO.getRentTime());
		ownerOrderEntityEffective.setExpRevertTime(modifyOrderOwnerDTO.getRevertTime());
		ownerOrderEntityEffective.setShowRentTime(modifyOrderOwnerDTO.getRentTime());
		ownerOrderEntityEffective.setShowRevertTime(modifyOrderOwnerDTO.getRevertTime());
		if (carRentTimeRangeResVO != null) {
			// 提前时间
			LocalDateTime showRentTime = carRentTimeRangeResVO.getAdvanceStartDate();
			// 延后时间
			LocalDateTime showRevertTime = carRentTimeRangeResVO.getDelayEndDate();
			ownerOrderEntityEffective.setShowRentTime(showRentTime);
			ownerOrderEntityEffective.setShowRevertTime(showRevertTime);
			ownerOrderEntityEffective.setBeforeMinutes(carRentTimeRangeResVO.getGetMinutes());
			ownerOrderEntityEffective.setAfterMinutes(carRentTimeRangeResVO.getReturnMinutes());
		}
		return ownerOrderEntityEffective;
	}
	
	
	/**
	 * 计算提前延后时间
	 * @param modifyOrderDTO
	 * @return CarRentTimeRangeResVO
	 */
	public CarRentTimeRangeResVO getCarRentTimeRangeResVO(ModifyOrderOwnerDTO modifyOrderDTO) {
		if (modifyOrderDTO == null) {
			return null;
		}
		if ((modifyOrderDTO.getSrvGetFlag() == null || modifyOrderDTO.getSrvGetFlag() == 0) &&
				(modifyOrderDTO.getSrvReturnFlag() == null || modifyOrderDTO.getSrvReturnFlag() == 0)) {
			return null;
		}
		CarRentTimeRangeReqVO carRentTimeRangeReqVO = getCarRentTimeRangeReqVO(modifyOrderDTO);
		if (carRentTimeRangeReqVO == null) {
			return null;
		}
		CarRentTimeRangeResVO carRentTimeRangeResVO = goodsService.getCarRentTimeRange(carRentTimeRangeReqVO);
		return carRentTimeRangeResVO;
	}
	
	/**
	 * 封装获取提前延后的对象
	 * @param modifyOrderDTO
	 * @return CarRentTimeRangeReqVO
	 */
	public CarRentTimeRangeReqVO getCarRentTimeRangeReqVO(ModifyOrderOwnerDTO modifyOrderDTO) {
		if (modifyOrderDTO == null) {
			return null;
		}
		OwnerGoodsDetailDTO ownerGoodsDetailDTO = modifyOrderDTO.getOwnerGoodsDetailDTO();
		if (ownerGoodsDetailDTO == null || ownerGoodsDetailDTO.getCarNo() == null) {
			return null;
		}
		CarRentTimeRangeReqVO carRentTimeRangeReqVO = new CarRentTimeRangeReqVO();
		carRentTimeRangeReqVO.setCarNo(String.valueOf(ownerGoodsDetailDTO.getCarNo()));
		carRentTimeRangeReqVO.setCityCode(modifyOrderDTO.getCityCode()+"");
		carRentTimeRangeReqVO.setRentTime(modifyOrderDTO.getRentTime());
		carRentTimeRangeReqVO.setRevertTime(modifyOrderDTO.getRevertTime());
		carRentTimeRangeReqVO.setSrvGetAddr(modifyOrderDTO.getGetCarAddress());
		carRentTimeRangeReqVO.setSrvGetFlag(modifyOrderDTO.getSrvGetFlag());
		carRentTimeRangeReqVO.setSrvGetLat(modifyOrderDTO.getGetCarLat());
		carRentTimeRangeReqVO.setSrvGetLon(modifyOrderDTO.getGetCarLon());
		carRentTimeRangeReqVO.setSrvReturnAddr(modifyOrderDTO.getRevertCarAddress());
		carRentTimeRangeReqVO.setSrvReturnFlag(modifyOrderDTO.getSrvReturnFlag());
		carRentTimeRangeReqVO.setSrvReturnLat(modifyOrderDTO.getRevertCarLat());
		carRentTimeRangeReqVO.setSrvReturnLon(modifyOrderDTO.getRevertCarLon());
		return carRentTimeRangeReqVO;
	}
	
	
	/**
	 * 获取车主商品信息
	 * @param modifyOrderOwnerDTO
	 * @param renterGoodsDetailDTO
	 * @param ownerOrderEntity
	 * @return OwnerGoodsDetailDTO
	 */
	public OwnerGoodsDetailDTO getOwnerGoodsDetailDTO(ModifyOrderOwnerDTO modifyOrderOwnerDTO, OwnerOrderEntity ownerOrderEntity) {
		// 获取车主端商品详情
		// 调远程获取
		RenterGoodsDetailDTO renterGoodsDetailDTO = goodsService.getRenterGoodsDetail(convertToCarDetailReqVO(modifyOrderOwnerDTO, ownerOrderEntity));
		if (renterGoodsDetailDTO == null) {
			log.error("getOwnerGoodsDetailDTO renterGoodsDetailDTO为空");
			Cat.logError("getOwnerGoodsDetailDTO renterGoodsDetailDTO为空",new ModifyOrderParameterException());
			throw new ModifyOrderParameterException();
		}
		// 获取经过组装的商品信息
		OwnerGoodsDetailDTO ownerGoodsDetailDTO = goodsService.getOwnerGoodsDetail(renterGoodsDetailDTO);
		ownerGoodsDetailDTO.setOrderNo(modifyOrderOwnerDTO.getOrderNo());
		ownerGoodsDetailDTO.setOwnerOrderNo(ownerOrderEntity.getOwnerOrderNo());
		ownerGoodsDetailDTO.setMemNo(ownerOrderEntity.getMemNo());
		ownerGoodsDetailDTO.setRentTime(modifyOrderOwnerDTO.getRentTime());
		ownerGoodsDetailDTO.setRevertTime(modifyOrderOwnerDTO.getRevertTime());
		ownerGoodsDetailDTO.setOldRentTime(ownerOrderEntity.getExpRentTime());
		ownerGoodsDetailDTO = ownerCommodityService.setPriceAndGroup(ownerGoodsDetailDTO);
		// 设置最新的车主订单号
		ownerGoodsDetailDTO.setOwnerOrderNo(modifyOrderOwnerDTO.getOwnerOrderNo());
		List<OwnerGoodsPriceDetailDTO> ownerGoodsPriceDetailDTOList = ownerGoodsDetailDTO.getOwnerGoodsPriceDetailDTOList();
		if (ownerGoodsPriceDetailDTOList == null || ownerGoodsPriceDetailDTOList.isEmpty()) {
			log.error("getOwnerGoodsDetailDTO ownerGoodsPriceDetailDTOList为空");
			Cat.logError("getOwnerGoodsDetailDTO ownerGoodsPriceDetailDTOList为空",new ModifyOrderParameterException());
			return ownerGoodsDetailDTO;
		}
		for (OwnerGoodsPriceDetailDTO gp:ownerGoodsPriceDetailDTOList) {
			gp.setOrderNo(modifyOrderOwnerDTO.getOrderNo());
			gp.setOwnerOrderNo(modifyOrderOwnerDTO.getOwnerOrderNo());
		}
		return ownerGoodsDetailDTO;
	}
	
	
	/**
	 * 获取车主会员信息根据车主订单号
	 * @param ownerOrderNo 修改前车主订单号
	 * @param updOwnerOrderNo 修改后车主订单号
	 * @return OwnerMemberDTO
	 */
	public OwnerMemberDTO getOwnerMemberDTO(String ownerOrderNo, String updOwnerOrderNo) {
		OwnerMemberDTO ownerMemberDTO = ownerMemberService.selectownerMemberByMemNo(ownerOrderNo, true);
		ownerMemberDTO.setOwnerOrderNo(updOwnerOrderNo);
		List<OwnerMemberRightDTO> ownerMemberRightDTOList = ownerMemberDTO.getOwnerMemberRightDTOList();
		if (ownerMemberRightDTOList == null || ownerMemberRightDTOList.isEmpty()) {
			return ownerMemberDTO;
		}
		for (OwnerMemberRightDTO mr:ownerMemberRightDTOList) {
			mr.setOwnerOrderNo(updOwnerOrderNo);
		}
		return ownerMemberDTO;
	}
	
	
	/**
	 * 转化成CostBaseDTO对象
	 * @param modifyOrderOwnerDTO
	 * @param ownerMemberDTO
	 * @return CostBaseDTO
	 */
	public CostBaseDTO convertToCostBaseDTO(ModifyOrderOwnerDTO modifyOrderOwnerDTO, OwnerMemberDTO ownerMemberDTO) {
		CostBaseDTO costBaseDTO = new CostBaseDTO();
		costBaseDTO.setEndTime(modifyOrderOwnerDTO.getRevertTime());
		costBaseDTO.setMemNo(ownerMemberDTO.getMemNo());
		costBaseDTO.setOrderNo(modifyOrderOwnerDTO.getOrderNo());
		costBaseDTO.setOwnerOrderNo(modifyOrderOwnerDTO.getOwnerOrderNo());
		costBaseDTO.setStartTime(modifyOrderOwnerDTO.getRentTime());
		return costBaseDTO;
	}
}
