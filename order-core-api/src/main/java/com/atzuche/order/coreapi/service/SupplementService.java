package com.atzuche.order.coreapi.service;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atzuche.order.commons.entity.dto.OrderSupplementDetailDTO;
import com.atzuche.order.commons.enums.SupplementOpTypeEnum;
import com.atzuche.order.commons.enums.SupplementTypeEnum;
import com.atzuche.order.coreapi.modifyorder.exception.ModifyOrderParentOrderNotFindException;
import com.atzuche.order.parentorder.entity.OrderEntity;
import com.atzuche.order.parentorder.service.OrderService;
import com.atzuche.order.rentercost.entity.OrderSupplementDetailEntity;
import com.atzuche.order.rentercost.service.OrderSupplementDetailService;
import com.dianping.cat.Cat;

@Service
public class SupplementService {

	@Autowired
	private OrderSupplementDetailService orderSupplementDetailService;
	@Autowired
	private OrderService orderService;
	
	/**
	 * 保存补付记录
	 * @param orderSupplementDetailDTO
	 */
	public void saveSupplement(OrderSupplementDetailDTO orderSupplementDetailDTO) {
		if (orderSupplementDetailDTO == null) {
			return;
		}
		OrderSupplementDetailEntity supplementEntity = new OrderSupplementDetailEntity();
		BeanUtils.copyProperties(orderSupplementDetailDTO, supplementEntity);
		// 根据订单号获取主订单信息
		OrderEntity orderEntity = orderService.getOrderEntity(orderSupplementDetailDTO.getOrderNo());
		if (orderEntity == null) {
			Cat.logError("SupplementService.saveSupplement保存补付记录", new ModifyOrderParentOrderNotFindException());
			throw new ModifyOrderParentOrderNotFindException();
		}
		supplementEntity.setMemNo(orderEntity.getMemNoRenter());
		supplementEntity.setSupplementType(SupplementTypeEnum.MANUAL_CREATE.getCode());
		supplementEntity.setOpType(SupplementOpTypeEnum.MANUAL_CREATE.getCode());
		orderSupplementDetailService.saveOrderSupplementDetail(supplementEntity);
	}
	
	/**
	 * 根据订单号获取补付记录
	 * @param orderNo
	 * @return List<OrderSupplementDetailEntity>
	 */
	public List<OrderSupplementDetailEntity> listOrderSupplementDetailEntityByOrderNo(String orderNo) {
		return orderSupplementDetailService.listOrderSupplementDetailByOrderNo(orderNo);
	}
	
	/**
	 * 根据id删除补付记录
	 * @param id
	 */
	public void deleteSupplement(Integer id) {
		orderSupplementDetailService.updateDeleteById(id);
	}
}
