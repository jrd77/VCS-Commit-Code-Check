package com.atzuche.order.coreapi.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.atzuche.order.ownercost.entity.OwnerOrderEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.commons.entity.dto.CostBaseDTO;
import com.atzuche.order.commons.entity.dto.OwnerGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.OwnerGoodsPriceDetailDTO;
import com.atzuche.order.commons.entity.dto.OwnerMemberDTO;
import com.atzuche.order.commons.entity.dto.OwnerMemberRightDTO;
import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.atzuche.order.coreapi.entity.dto.ModifyOrderOwnerDTO;
import com.atzuche.order.coreapi.modifyorder.exception.ModifyOrderParameterException;
import com.atzuche.order.coreapi.service.CarService.CarDetailReqVO;
import com.atzuche.order.owner.commodity.service.CommodityService;
import com.atzuche.order.owner.cost.entity.OwnerOrderCostEntity;
import com.atzuche.order.owner.cost.entity.OwnerOrderIncrementDetailEntity;
import com.atzuche.order.owner.cost.entity.OwnerOrderPurchaseDetailEntity;
import com.atzuche.order.owner.cost.entity.OwnerOrderSubsidyDetailEntity;
import com.atzuche.order.ownercost.entity.OwnerOrderPurchaseDetailEntity;
import com.atzuche.order.owner.mem.service.OwnerMemberService;
import com.atzuche.order.rentercost.entity.RenterOrderSubsidyDetailEntity;
import com.atzuche.order.rentercost.service.RenterOrderSubsidyDetailService;
import com.atzuche.order.service.OwnerOrderCostCombineService;
import com.atzuche.order.service.OwnerOrderCostService;
import com.atzuche.order.service.OwnerOrderIncrementDetailService;
import com.atzuche.order.service.OwnerOrderPurchaseDetailService;
import com.atzuche.order.service.OwnerOrderService;
import com.atzuche.order.service.OwnerOrderSubsidyDetailService;
import com.atzuche.order.ownercost.service.OwnerOrderCostCombineService;
import com.atzuche.order.ownercost.service.OwnerOrderService;
import com.autoyol.commons.web.ResponseData;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ModifyOrderForOwnerService {
	
	@Autowired
	private OwnerOrderService ownerOrderService;
	@Autowired
	private OwnerMemberService ownerMemberService;
	@Autowired
	private CarService carService;
	@Autowired
	private CommodityService commodityService;
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

	/**
	 * 租客修改订单重新下车主订单逻辑(相当于车主同意)
	 * @param orderNo 主订单号
	 * @return ResponseData
	 */
	public ResponseData<?> modifyOrderForOwner(ModifyOrderOwnerDTO modifyOrderOwnerDTO, RenterGoodsDetailDTO renterGoodsDetailDTO, RenterOrderSubsidyDetailEntity renterSubsidy) {
		log.info("租客修改订单重新下车主订单modifyOrderOwnerDTO=[{}], renterGoodsDetailDTO=[{}]",modifyOrderOwnerDTO,renterGoodsDetailDTO);
		if (modifyOrderOwnerDTO == null) {
			throw new ModifyOrderParameterException();
		}
		// 主订单号
		String orderNo = modifyOrderOwnerDTO.getOrderNo();
		// 获取修改前有效车主订单信息
		OwnerOrderEntity ownerOrderEntity = ownerOrderService.getOwnerOrderByOrderNoAndIsEffective(orderNo);
		// 获取车主会员信息
		OwnerMemberDTO ownerMemberDTO = getOwnerMemberDTO(ownerOrderEntity.getOwnerOrderNo());
		// 获取车主端商品详情
		OwnerGoodsDetailDTO ownerGoodsDetailDTO = getOwnerGoodsDetailDTO(modifyOrderOwnerDTO, renterGoodsDetailDTO, ownerOrderEntity);
		// 封装新的车主子订单对象
		OwnerOrderEntity ownerOrderEffective =  convertToOwnerOrderEntity(modifyOrderOwnerDTO, ownerOrderEntity, renterGoodsDetailDTO.getCarRealLon(), renterGoodsDetailDTO.getCarRealLat());
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
		commodityService.saveCommodity(ownerGoodsDetailDTO);
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
		
		return ResponseData.success();
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
		//subsidyEntity.setSubsidType(renterSubsidy.getSubsidTypeName());
		subsidyEntity.setSubsidyAmount(-renterSubsidy.getSubsidyAmount());
		subsidyEntity.setSubsidyCode(renterSubsidy.getSubsidyTargetCode());
		subsidyEntity.setSubsidyDesc(renterSubsidy.getSubsidyDesc());
		subsidyEntity.setSubsidyName(renterSubsidy.getSubsidyTargetName());
		subsidyEntity.setSubsidySource(renterSubsidy.getSubsidySourceName());
		subsidyEntity.setSubsidySourceCode(renterSubsidy.getSubsidySourceCode());
		subsidyEntity.setSubsidyTypeCode(renterSubsidy.getSubsidyTypeCode());
		subsidyEntity.setSubsidyVoucher(String.valueOf(renterSubsidy.getId()));
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
	public CarDetailReqVO convertToCarDetailReqVO(ModifyOrderOwnerDTO modifyOrderOwnerDTO, OwnerOrderEntity ownerOrderEntity) {
		// 车主子订单号
		String ownerOrderNo = ownerOrderEntity.getOwnerOrderNo();
		// 获取车主商品信息
		OwnerGoodsDetailDTO ownerGoodsDetailDTO = commodityService.getOwnerGoodsDetail(ownerOrderNo, false);
		CarDetailReqVO carDetailReqVO = new CarDetailReqVO(); 
		carDetailReqVO.setAddrIndex(ownerGoodsDetailDTO.getCarAddrIndex());
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
	public OwnerOrderEntity convertToOwnerOrderEntity(ModifyOrderOwnerDTO modifyOrderOwnerDTO, OwnerOrderEntity ownerOrderEntity, String carLon, String carLat) {
		// TODO 计算提前时间
		LocalDateTime showRentTime = null;
		// TODO 计算延后时间
		LocalDateTime showRevertTime = null;
		// 封装新的车主子订单
		OwnerOrderEntity ownerOrderEntityEffective = new OwnerOrderEntity();
		// 数据拷贝
		BeanUtils.copyProperties(ownerOrderEntity, ownerOrderEntityEffective);
		ownerOrderEntityEffective.setOwnerOrderNo(modifyOrderOwnerDTO.getOwnerOrderNo());
		ownerOrderEntityEffective.setId(null);
		ownerOrderEntityEffective.setIsEffective(1);
		ownerOrderEntityEffective.setShowRentTime(showRentTime);
		ownerOrderEntityEffective.setShowRevertTime(showRevertTime);
		ownerOrderEntityEffective.setExpRentTime(modifyOrderOwnerDTO.getRentTime());
		ownerOrderEntityEffective.setExpRevertTime(modifyOrderOwnerDTO.getRevertTime());
		return ownerOrderEntityEffective;
	}
	
	
	/**
	 * 获取车主商品信息
	 * @param modifyOrderOwnerDTO
	 * @param renterGoodsDetailDTO
	 * @param ownerOrderEntity
	 * @return OwnerGoodsDetailDTO
	 */
	public OwnerGoodsDetailDTO getOwnerGoodsDetailDTO(ModifyOrderOwnerDTO modifyOrderOwnerDTO, RenterGoodsDetailDTO renterGoodsDetailDTO, OwnerOrderEntity ownerOrderEntity) {
		// 获取车主端商品详情
		if (renterGoodsDetailDTO == null) {
			// 调远程获取
			renterGoodsDetailDTO = carService.getRenterGoodsDetail(convertToCarDetailReqVO(modifyOrderOwnerDTO, ownerOrderEntity));
		}
		// 获取经过组装的商品信息
		OwnerGoodsDetailDTO ownerGoodsDetailDTO = carService.getOwnerGoodsDetail(renterGoodsDetailDTO);
		ownerGoodsDetailDTO.setOrderNo(modifyOrderOwnerDTO.getOrderNo());
		ownerGoodsDetailDTO.setOwnerOrderNo(modifyOrderOwnerDTO.getOwnerOrderNo());
		ownerGoodsDetailDTO.setMemNo(ownerOrderEntity.getMemNo());
		ownerGoodsDetailDTO.setRentTime(modifyOrderOwnerDTO.getRentTime());
		ownerGoodsDetailDTO.setRevertTime(modifyOrderOwnerDTO.getRevertTime());
		ownerGoodsDetailDTO = commodityService.setPriceAndGroup(ownerGoodsDetailDTO);
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
	 * @param ownerOrderNo 车主订单号
	 * @return OwnerMemberDTO
	 */
	public OwnerMemberDTO getOwnerMemberDTO(String ownerOrderNo) {
		OwnerMemberDTO ownerMemberDTO = ownerMemberService.selectownerMemberByMemNo(ownerOrderNo, true);
		ownerMemberDTO.setOwnerOrderNo(ownerOrderNo);
		List<OwnerMemberRightDTO> ownerMemberRightDTOList = ownerMemberDTO.getOwnerMemberRightDTOList();
		if (ownerMemberRightDTOList == null || ownerMemberRightDTOList.isEmpty()) {
			return ownerMemberDTO;
		}
		for (OwnerMemberRightDTO mr:ownerMemberRightDTOList) {
			mr.setOwnerOrderNo(ownerOrderNo);
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
