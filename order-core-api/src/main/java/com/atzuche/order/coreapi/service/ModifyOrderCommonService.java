package com.atzuche.order.coreapi.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atzuche.order.commons.entity.dto.CarRentTimeRangeDTO;
import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.RenterMemberDTO;
import com.atzuche.order.coreapi.entity.dto.ModifyOrderDTO;
import com.atzuche.order.coreapi.entity.request.ModifyOrderReq;
import com.atzuche.order.delivery.entity.RenterOrderDeliveryEntity;
import com.atzuche.order.delivery.service.RenterOrderDeliveryService;
import com.atzuche.order.parentorder.entity.OrderEntity;
import com.atzuche.order.parentorder.entity.OrderStatusEntity;
import com.atzuche.order.parentorder.service.OrderService;
import com.atzuche.order.parentorder.service.OrderStatusService;
import com.atzuche.order.rentercost.entity.RenterOrderCostDetailEntity;
import com.atzuche.order.rentercost.entity.RenterOrderSubsidyDetailEntity;
import com.atzuche.order.rentercost.service.RenterOrderCostDetailService;
import com.atzuche.order.rentercost.service.RenterOrderSubsidyDetailService;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.entity.dto.RenterOrderCostRespDTO;
import com.atzuche.order.renterorder.service.RenterOrderService;
import com.atzuche.order.renterorder.vo.RenterOrderReqVO;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ModifyOrderCommonService {
	
	@Autowired
	private RenterOrderService renterOrderService;
	@Autowired
	private RenterOrderDeliveryService renterOrderDeliveryService;
	@Autowired
	private ModifyOrderService modifyOrderService;
	@Autowired
	private OrderService orderService;
	@Autowired
	private OrderStatusService orderStatusService;
	@Autowired
	private ModifyOrderCheckService modifyOrderCheckService;
	@Autowired
	private RenterOrderCostDetailService renterOrderCostDetailService;
	@Autowired
	private RenterOrderSubsidyDetailService renterOrderSubsidyDetailService;
	@Autowired
	private ModifyOrderRiskService modifyOrderRiskService;

	/**
	 * 管理后台修改时间校验
	 * @param modifyOrderReq
	 */
	public void checkForConsole(ModifyOrderReq modifyOrderReq) {
		log.info("checkForConsole入参modifyOrderReq=[{}]", modifyOrderReq);
		// 主单号
		String orderNo = modifyOrderReq.getOrderNo();
		// 获取修改前有效租客子订单信息
		RenterOrderEntity initRenterOrder = renterOrderService.getRenterOrderByOrderNoAndIsEffective(orderNo);
		// 获取租客配送订单信息
		List<RenterOrderDeliveryEntity> deliveryList = renterOrderDeliveryService.listRenterOrderDeliveryByRenterOrderNo(initRenterOrder.getRenterOrderNo());
		// DTO包装
		ModifyOrderDTO modifyOrderDTO = modifyOrderService.getModifyOrderDTO(modifyOrderReq, null, initRenterOrder, deliveryList);
		log.info("ModifyOrderService.modifyOrder modifyOrderDTO=[{}]", modifyOrderDTO);
		// 获取租客会员信息
		RenterMemberDTO renterMemberDTO = modifyOrderService.getRenterMemberDTO(initRenterOrder.getRenterOrderNo(), null);
		// 设置租客会员信息
		modifyOrderDTO.setRenterMemberDTO(renterMemberDTO);
		// 获取租客商品信息
		RenterGoodsDetailDTO renterGoodsDetailDTO = modifyOrderService.getRenterGoodsDetailDTO(modifyOrderDTO, initRenterOrder);
		// 设置商品信息
		modifyOrderDTO.setRenterGoodsDetailDTO(renterGoodsDetailDTO);
		// 获取主订单信息
		OrderEntity orderEntity = orderService.getOrderByOrderNoAndMemNo(orderNo, modifyOrderReq.getMemNo());
		// 设置主订单信息
		modifyOrderDTO.setOrderEntity(orderEntity);
		// 查询主订单状态
		OrderStatusEntity orderStatusEntity = orderStatusService.getByOrderNo(orderNo);
		// 设置订单状态
		modifyOrderDTO.setOrderStatusEntity(orderStatusEntity);
		// 校验
		modifyOrderCheckService.checkForConsole(modifyOrderDTO);
		// 设置城市编号
		modifyOrderDTO.setCityCode(orderEntity.getCityCode());
		// 库存校验
		modifyOrderCheckService.checkCarStock(modifyOrderDTO);
		log.info("ModifyOrderService.modifyOrder again modifyOrderDTO=[{}]", modifyOrderDTO);
		// 获取修改前租客费用明细
		List<RenterOrderCostDetailEntity> initCostList = renterOrderCostDetailService.listRenterOrderCostDetail(modifyOrderDTO.getOrderNo(), initRenterOrder.getRenterOrderNo());
		// 获取修改前补贴信息
		List<RenterOrderSubsidyDetailEntity> initSubsidyList = renterOrderSubsidyDetailService.listRenterOrderSubsidyDetail(modifyOrderDTO.getOrderNo(), initRenterOrder.getRenterOrderNo());
		// 提前延后时间计算
		CarRentTimeRangeDTO carRentTimeRangeResVO = modifyOrderService.getCarRentTimeRangeResVO(modifyOrderDTO);
		// 设置提前延后时间
		modifyOrderDTO.setCarRentTimeRangeResVO(carRentTimeRangeResVO);
		// 封装计算用对象
		RenterOrderReqVO renterOrderReqVO = modifyOrderService.convertToRenterOrderReqVO(modifyOrderDTO, renterMemberDTO, renterGoodsDetailDTO, orderEntity, carRentTimeRangeResVO);
		// 基础费用计算包含租金，手续费，基础保障费用，补充保障服务费，附加驾驶人保障费用，取还车费用计算和超运能费用计算
		RenterOrderCostRespDTO renterOrderCostRespDTO = modifyOrderService.getRenterOrderCostRespDTO(modifyOrderDTO, renterOrderReqVO, initCostList, initSubsidyList);
		// 调用风控审核
		modifyOrderRiskService.checkModifyRisk(modifyOrderDTO, renterOrderCostRespDTO);
	}
}
