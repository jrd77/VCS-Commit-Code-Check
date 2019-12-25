package com.atzuche.order.coreapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atzuche.order.commons.entity.dto.OwnerGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.OwnerMemberDTO;
import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.atzuche.order.coreapi.service.CarService.CarDetailReqVO;
import com.atzuche.order.entity.OwnerOrderEntity;
import com.atzuche.order.owner.commodity.service.CommodityService;
import com.atzuche.order.owner.mem.service.OwnerMemberService;
import com.atzuche.order.service.OwnerOrderService;
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

	/**
	 * 租客修改订单重新下车主订单逻辑
	 * @param orderNo 主订单号
	 * @return ResponseData
	 */
	public ResponseData<?> modifyOrderForOwner(String orderNo, RenterGoodsDetailDTO renterGoodsDetailDTO) {
		log.info("租客修改订单重新下车主订单orderNo=[{}]",orderNo);
		// 获取修改前有效车主订单信息
		OwnerOrderEntity ownerOrderEntity = ownerOrderService.getOwnerOrderByOrderNoAndIsEffective(orderNo);
		// 获取车主会员信息
		OwnerMemberDTO ownerMemberDTO = ownerMemberService.selectownerMemberByMemNo(ownerOrderEntity.getOwnerOrderNo(), true);
		// 获取车主端商品详情
		if (renterGoodsDetailDTO == null) {
			// 调远程获取
			renterGoodsDetailDTO = carService.getRenterGoodsDetail(convertToCarDetailReqVO(ownerOrderEntity));
		}
		// 获取经过组装的商品信息
		
		
		return ResponseData.success();
	}
	
	
	/**
	 * 数据转化为CarDetailReqVO
	 * @param ownerOrderNo
	 * @return CarDetailReqVO
	 */
	public CarDetailReqVO convertToCarDetailReqVO(OwnerOrderEntity ownerOrderEntity) {
		// 车主子订单号
		String ownerOrderNo = ownerOrderEntity.getOwnerOrderNo();
		// 获取车主商品信息
		OwnerGoodsDetailDTO ownerGoodsDetailDTO = commodityService.getOwnerGoodsDetail(ownerOrderNo, false);
		CarDetailReqVO carDetailReqVO = new CarDetailReqVO(); 
		carDetailReqVO.setAddrIndex(ownerGoodsDetailDTO.getCarAddrIndex());
		carDetailReqVO.setCarNo(String.valueOf(ownerGoodsDetailDTO.getCarNo()));
		carDetailReqVO.setRentTime(ownerOrderEntity.getExpRentTime());
		carDetailReqVO.setRevertTime(ownerOrderEntity.getExpRevertTime());
		Integer useSpecialPriceFlag = ownerOrderEntity.getIsUseSpecialPrice();
		Boolean useSpecialPrice = (useSpecialPriceFlag != null && useSpecialPriceFlag == 1)?true:false;
		carDetailReqVO.setUseSpecialPrice(useSpecialPrice);
		return carDetailReqVO;
	}
}
