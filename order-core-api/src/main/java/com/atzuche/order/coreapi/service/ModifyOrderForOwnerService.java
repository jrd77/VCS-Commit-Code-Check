package com.atzuche.order.coreapi.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atzuche.order.commons.entity.dto.CostBaseDTO;
import com.atzuche.order.commons.entity.dto.OwnerGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.OwnerMemberDTO;
import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.atzuche.order.coreapi.entity.dto.ModifyOrderOwnerDTO;
import com.atzuche.order.coreapi.modifyorder.exception.ModifyOrderParameterException;
import com.atzuche.order.coreapi.service.CarService.CarDetailReqVO;
import com.atzuche.order.owner.commodity.service.CommodityService;
import com.atzuche.order.ownercost.entity.OwnerOrderPurchaseDetailEntity;
import com.atzuche.order.owner.mem.service.OwnerMemberService;
import com.atzuche.order.ownercost.service.OwnerOrderCostCombineService;
import com.atzuche.order.ownercost.service.OwnerOrderService;
import com.autoyol.commons.web.ResponseData;

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

	/**
	 * 租客修改订单重新下车主订单逻辑(相当于车主同意)
	 * @param orderNo 主订单号
	 * @return ResponseData
	 */
	public ResponseData<?> modifyOrderForOwner(ModifyOrderOwnerDTO modifyOrderOwnerDTO, RenterGoodsDetailDTO renterGoodsDetailDTO) {
		log.info("租客修改订单重新下车主订单modifyOrderOwnerDTO=[{}], renterGoodsDetailDTO=[{}]",modifyOrderOwnerDTO,renterGoodsDetailDTO);
		if (modifyOrderOwnerDTO == null) {
			throw new ModifyOrderParameterException();
		}
		// 主订单号
		String orderNo = modifyOrderOwnerDTO.getOrderNo();
		// 获取修改前有效车主订单信息
		OwnerOrderEntity ownerOrderEntity = ownerOrderService.getOwnerOrderByOrderNoAndIsEffective(orderNo);
		// 获取车主会员信息
		OwnerMemberDTO ownerMemberDTO = ownerMemberService.selectownerMemberByMemNo(ownerOrderEntity.getOwnerOrderNo(), true);
		// 获取车主端商品详情
		OwnerGoodsDetailDTO ownerGoodsDetailDTO = getOwnerGoodsDetailDTO(modifyOrderOwnerDTO, renterGoodsDetailDTO, ownerOrderEntity);
		// 封装新的车主子订单对象
		OwnerOrderEntity ownerOrderEffective =  convertToOwnerOrderEntity(modifyOrderOwnerDTO, ownerOrderEntity, renterGoodsDetailDTO.getCarRealLon(), renterGoodsDetailDTO.getCarRealLat());
		
		// 计算车主租金收益
		List<OwnerOrderPurchaseDetailEntity> purchaseList = ownerOrderCostCombineService.listOwnerRentAmtEntity(convertToCostBaseDTO(modifyOrderOwnerDTO, ownerMemberDTO), ownerGoodsDetailDTO.getOwnerGoodsPriceDetailDTOList());
		// TODO 计算车主补贴（车主券）
		
		// 保存新车主子单
		ownerOrderService.saveOwnerOrder(ownerOrderEffective);
		// TODO 上笔车主子订单置为无效
		ownerOrderService.updateOwnerOrderInvalidById(ownerOrderEntity.getId());
		
		return ResponseData.success();
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
		ownerGoodsDetailDTO.setRentTime(modifyOrderOwnerDTO.getRentTime());
		ownerGoodsDetailDTO.setRevertTime(modifyOrderOwnerDTO.getRevertTime());
		ownerGoodsDetailDTO = commodityService.setPriceAndGroup(ownerGoodsDetailDTO);
		return ownerGoodsDetailDTO;
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
