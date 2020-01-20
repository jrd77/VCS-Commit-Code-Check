package com.atzuche.order.renterorder.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atzuche.order.renterorder.entity.OrderTransferRecordEntity;
import com.atzuche.order.renterorder.mapper.OrderTransferRecordMapper;

@Service
public class OrderTransferRecordService {

	@Autowired
	private OrderTransferRecordMapper orderTransferRecordMapper;
	
	/**
	 * 保存换车记录
	 * @param orderTransferRecordEntity
	 * @return Integer
	 */
	public Integer saveOrderTransferRecord(OrderTransferRecordEntity orderTransferRecordEntity) {
		if (orderTransferRecordEntity == null) {
			return 1;
		}
		return orderTransferRecordMapper.saveOrderTransferRecord(orderTransferRecordEntity);
	}
	
	/**
	 * 获取订单换车记录
	 * @param orderNo
	 * @return List<OrderTransferRecordEntity>
	 */
	public List<OrderTransferRecordEntity> listOrderTransferRecordByOrderNo(String orderNo) {
		return orderTransferRecordMapper.listOrderTransferRecordByOrderNo(orderNo);
	}
}
