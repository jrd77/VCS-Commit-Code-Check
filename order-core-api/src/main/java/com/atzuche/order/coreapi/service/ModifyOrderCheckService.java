package com.atzuche.order.coreapi.service;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atzuche.order.commons.LocalDateTimeUtils;
import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.RenterGoodsPriceDetailDTO;
import com.atzuche.order.commons.entity.dto.RenterMemberDTO;
import com.atzuche.order.commons.enums.OrderChangeItemEnum;
import com.atzuche.order.coreapi.entity.dto.ModifyOrderDTO;
import com.atzuche.order.coreapi.modifyorder.exception.ModifyOrderDataNoChangeException;
import com.atzuche.order.coreapi.modifyorder.exception.ModifyOrderExistTODOChangeApplyException;
import com.atzuche.order.coreapi.modifyorder.exception.ModifyOrderGoodNotExistException;
import com.atzuche.order.coreapi.modifyorder.exception.ModifyOrderGoodPriceNotExistException;
import com.atzuche.order.coreapi.modifyorder.exception.ModifyOrderMemberNotExistException;
import com.atzuche.order.coreapi.modifyorder.exception.ModifyOrderParentOrderNotFindException;
import com.atzuche.order.coreapi.modifyorder.exception.ModifyOrderRentOrRevertTimeException;
import com.atzuche.order.coreapi.modifyorder.exception.TransferCarException;
import com.atzuche.order.coreapi.modifyorder.exception.TransferUseOwnerCouponException;
import com.atzuche.order.owner.commodity.entity.OwnerGoodsEntity;
import com.atzuche.order.owner.commodity.service.OwnerGoodsService;
import com.atzuche.order.parentorder.entity.OrderEntity;
import com.atzuche.order.renterorder.entity.RenterOrderChangeApplyEntity;
import com.atzuche.order.renterorder.entity.dto.OrderChangeItemDTO;
import com.atzuche.order.renterorder.service.RenterOrderChangeApplyService;
import com.autoyol.car.api.model.dto.LocationDTO;
import com.autoyol.car.api.model.dto.OrderInfoDTO;
import com.autoyol.car.api.model.enums.OrderOperationTypeEnum;
import com.dianping.cat.Cat;

@Service
public class ModifyOrderCheckService {
	
	@Autowired
	private ModifyOrderConfirmService modifyOrderConfirmService;
	@Autowired
	private StockService stockService;
	@Autowired
	private RenterOrderChangeApplyService renterOrderChangeApplyService;
	@Autowired
	private OwnerGoodsService ownerGoodsService;
	
	/**
	 * 库存校验
	 * @param modifyOrderDTO
	 */
	public void checkCarStock(ModifyOrderDTO modifyOrderDTO) {
		// 修改项目
		List<OrderChangeItemDTO> changeItemList = modifyOrderDTO.getChangeItemList();
		checkDataChange(changeItemList);
		List<String> changeCodeList = modifyOrderConfirmService.listChangeCode(changeItemList);
		if (!changeCodeList.contains(OrderChangeItemEnum.MODIFY_RENTTIME.getCode()) && 
				!changeCodeList.contains(OrderChangeItemEnum.MODIFY_REVERTTIME.getCode())) {
			// 未修改租期,不需要校验库存
			return;
		}
		OrderInfoDTO orderInfoDTO = new OrderInfoDTO();
		orderInfoDTO.setOrderNo(modifyOrderDTO.getOrderNo());
		orderInfoDTO.setCityCode(modifyOrderDTO.getCityCode() != null ? Integer.valueOf(modifyOrderDTO.getCityCode()):null);
		// 商品信息
		RenterGoodsDetailDTO renterGoodsDetailDTO = modifyOrderDTO.getRenterGoodsDetailDTO();
		orderInfoDTO.setCarNo(renterGoodsDetailDTO.getCarNo());
		orderInfoDTO.setStartDate(LocalDateTimeUtils.localDateTimeToDate(modifyOrderDTO.getRentTime()));
		orderInfoDTO.setEndDate(LocalDateTimeUtils.localDateTimeToDate(modifyOrderDTO.getRevertTime()));
		LocationDTO getCarAddress = new LocationDTO();
		getCarAddress.setCarAddress(modifyOrderDTO.getGetCarAddress());
		getCarAddress.setFlag(modifyOrderDTO.getSrvGetFlag());
		getCarAddress.setLat(modifyOrderDTO.getGetCarLat() != null ? Double.valueOf(modifyOrderDTO.getGetCarLat()):null);
		getCarAddress.setLon(modifyOrderDTO.getGetCarLon() != null ? Double.valueOf(modifyOrderDTO.getGetCarLon()):null);
		orderInfoDTO.setGetCarAddress(getCarAddress);
		LocationDTO returnCarAddress = new LocationDTO();
		returnCarAddress.setCarAddress(modifyOrderDTO.getRevertCarAddress());
		returnCarAddress.setFlag(modifyOrderDTO.getSrvReturnFlag());
		returnCarAddress.setLat(modifyOrderDTO.getRevertCarLat() != null ? Double.valueOf(modifyOrderDTO.getRevertCarLat()):null);
		returnCarAddress.setLon(modifyOrderDTO.getRevertCarLon() != null ? Double.valueOf(modifyOrderDTO.getRevertCarLon()):null);
		orderInfoDTO.setReturnCarAddress(returnCarAddress);
		orderInfoDTO.setOperationType(OrderOperationTypeEnum.DDXGZQ.getType());
		stockService.checkCarStock(orderInfoDTO);
	}
	
	/**
	 * 修改校验主方法
	 * @param modifyOrderDTO
	 */
	public void modifyMainCheck(ModifyOrderDTO modifyOrderDTO) {
		// 修改项目
		List<OrderChangeItemDTO> changeItemList = modifyOrderDTO.getChangeItemList();
		// 校验是否修改数据
		checkDataChange(changeItemList);
		// 校验时间
		checkTime(modifyOrderDTO);
		// 主订单信息
		OrderEntity orderEntity = modifyOrderDTO.getOrderEntity();
		// 校验主订单
		checkParentOrder(orderEntity);
		// 商品信息
		RenterGoodsDetailDTO renterGoodsDetailDTO = modifyOrderDTO.getRenterGoodsDetailDTO();
		// 校验商品信息
		checkGoods(renterGoodsDetailDTO);
		// 会员信息
		RenterMemberDTO renterMemberDTO = modifyOrderDTO.getRenterMemberDTO();
		// 校验会员
		checkMember(renterMemberDTO);
		if (modifyOrderDTO.getConsoleFlag() == null || !modifyOrderDTO.getConsoleFlag()) {
			// 校验是否有未处理的申请
			checkChangeApply(modifyOrderDTO.getOrderNo());
		}
		// 使用车主券不允许换车
		checkUserOwnerCoupon(modifyOrderDTO);
		// 换车校验车辆
		checkTransferCar(modifyOrderDTO);
	}
	
	/**
	 * 校验是否修改数据
	 * @param changeItemList
	 */
	public void checkDataChange(List<OrderChangeItemDTO> changeItemList) {
		if (changeItemList == null || changeItemList.isEmpty()) {
			Cat.logError("ModifyOrderCheckService.checkDataChange校验是否修改数据", new ModifyOrderDataNoChangeException());
			throw new ModifyOrderDataNoChangeException();
		}
	}
	
	/**
	 * 校验主订单
	 * @param orderEntity
	 */
	public void checkParentOrder(OrderEntity orderEntity) {
		if (orderEntity == null) {
			Cat.logError("ModifyOrderCheckService.checkParentOrder校验主订单", new ModifyOrderParentOrderNotFindException());
			throw new ModifyOrderParentOrderNotFindException();
		}
	}
	
	
	/**
	 * 校验时间
	 * @param modifyOrderDTO
	 */
	public void checkTime(ModifyOrderDTO modifyOrderDTO) {
		// 取车时间
		LocalDateTime rentTime = modifyOrderDTO.getRentTime();
		// 还车时间
		LocalDateTime revertTime = modifyOrderDTO.getRevertTime();
		// 当前时间
		LocalDateTime now = LocalDateTime.now();
		// 修改项目
		List<OrderChangeItemDTO> changeItemList = modifyOrderDTO.getChangeItemList();
		checkDataChange(changeItemList);
		// 主订单信息
		OrderEntity orderEntity = modifyOrderDTO.getOrderEntity();
		checkParentOrder(orderEntity);
		// 修改前取车时间
		LocalDateTime initRentTime = orderEntity.getExpRentTime();
		List<String> changeCodeList = modifyOrderConfirmService.listChangeCode(changeItemList);
		if (changeCodeList.contains(OrderChangeItemEnum.MODIFY_RENTTIME.getCode())) {
			if (initRentTime != null && initRentTime.isBefore(now)) {
				throw new ModifyOrderRentOrRevertTimeException("订单开始不能修改取车时间");
			}
			if (rentTime.isBefore(now)) {
				throw new ModifyOrderRentOrRevertTimeException("取车时间不能早于当前时间");
			}
		}
		if (rentTime != null && revertTime != null && !rentTime.isBefore(revertTime)) {
			throw new ModifyOrderRentOrRevertTimeException("还车时间不能早于或等于取车时间");
		}
	}
	
	/**
	 * 校验商品信息
	 * @param renterGoodsDetailDTO
	 */
	public void checkGoods(RenterGoodsDetailDTO renterGoodsDetailDTO) {
		if (renterGoodsDetailDTO == null) {
			Cat.logError("ModifyOrderCheckService.checkGoods校验商品信息", new ModifyOrderGoodNotExistException());
			throw new ModifyOrderGoodNotExistException();
		}
		List<RenterGoodsPriceDetailDTO> renterGoodsPriceDetailDTOList = renterGoodsDetailDTO.getRenterGoodsPriceDetailDTOList();
		if (renterGoodsPriceDetailDTOList == null || renterGoodsPriceDetailDTOList.isEmpty()) {
			Cat.logError("ModifyOrderCheckService.checkGoods校验商品价格信息", new ModifyOrderGoodPriceNotExistException());
			throw new ModifyOrderGoodPriceNotExistException();
		}
	}
	
	/**
	 * 校验会员
	 * @param renterMemberDTO
	 */
	public void checkMember(RenterMemberDTO renterMemberDTO) {
		if (renterMemberDTO == null) {
			Cat.logError("ModifyOrderCheckService.checkMember校验会员信息", new ModifyOrderMemberNotExistException());
			throw new ModifyOrderMemberNotExistException();
		}
	}
	
	/**
	 * 校验是否有未处理的申请
	 * @param orderNo
	 */
	public void checkChangeApply(String orderNo) {
		Integer changeApplyCount = renterOrderChangeApplyService.getRenterOrderChangeApplyCountByOrderNo(orderNo);
		if (changeApplyCount != null && changeApplyCount > 0) {
			Cat.logError("ModifyOrderCheckService.checkChangeApply校验是否有未处理的申请", new ModifyOrderExistTODOChangeApplyException());
			throw new ModifyOrderExistTODOChangeApplyException();
		}
	}
	
	/**
	 * 使用车主券不允许换车
	 * @param modifyOrderDTO
	 */
	public void checkUserOwnerCoupon(ModifyOrderDTO modifyOrderDTO) {
		if (modifyOrderDTO.getTransferFlag() != null && modifyOrderDTO.getTransferFlag()) {
			// 换车操作
			if (StringUtils.isNotBlank(modifyOrderDTO.getCarOwnerCouponId())) {
				// 使用车主券不允许换车
				Cat.logError("ModifyOrderCheckService.checkUserOwnerCoupon校验使用车主券不允许换车", new TransferUseOwnerCouponException());
				throw new TransferUseOwnerCouponException();
			}
		}
	}
	
	/**
	 * 换车校验车辆
	 * @param modifyOrderDTO
	 */
	public void checkTransferCar(ModifyOrderDTO modifyOrderDTO) {
		if (modifyOrderDTO.getTransferFlag() != null && modifyOrderDTO.getTransferFlag()) {
			// 获取最新的车主商品信息
			OwnerGoodsEntity ownerGoodsEntity = ownerGoodsService.getLastOwnerGoodsByOrderNo(modifyOrderDTO.getOrderNo());
			String initCarNo = (ownerGoodsEntity != null && ownerGoodsEntity.getCarNo() != null) ? String.valueOf(ownerGoodsEntity.getCarNo()):null;  
			if (initCarNo != null && initCarNo.equals(modifyOrderDTO.getCarNo())) {
				// 使用车主券不允许换车
				Cat.logError("ModifyOrderCheckService.checkUserOwnerCoupon校验使用车主券不允许换车", new TransferCarException());
				throw new TransferCarException();
			}
		}
	}
}
