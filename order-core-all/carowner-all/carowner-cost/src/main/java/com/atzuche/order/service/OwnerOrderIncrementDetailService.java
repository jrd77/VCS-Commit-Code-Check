package com.atzuche.order.service;

import com.atzuche.order.owner.cost.entity.OwnerOrderIncrementDetailEntity;
import com.atzuche.order.owner.cost.mapper.OwnerOrderIncrementDetailMapper;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OwnerOrderIncrementDetailService {

	@Autowired
	private OwnerOrderIncrementDetailMapper ownerOrderIncrementDetailMapper;
	
	/**
	 * 获取车主增值服务费用列表
	 * @param orderNo 主订单号
	 * @param ownerOrderNo 车主订单号
	 * @return List<OwnerOrderIncrementDetailEntity>
	 */
	public List<OwnerOrderIncrementDetailEntity> listOwnerOrderIncrementDetail(String orderNo, String ownerOrderNo) {
		return ownerOrderIncrementDetailMapper.listOwnerOrderIncrementDetail(orderNo, ownerOrderNo);
	}
	
	/**
	 * 保存车主增值服务费用
	 * @param ownerOrderIncrementDetailEntity 车主增值服务费用
	 * @return Integer
	 */
	public Integer saveOwnerOrderIncrementDetail(OwnerOrderIncrementDetailEntity ownerOrderIncrementDetailEntity) {
		return ownerOrderIncrementDetailMapper.saveOwnerOrderIncrementDetail(ownerOrderIncrementDetailEntity);
	}
	
	/**
	 * 批量保存车主增值服务费用
	 * @param costList 车主增值服务费用列表
	 * @return Integer
	 */
	public Integer saveOwnerOrderIncrementDetailBatch(List<OwnerOrderIncrementDetailEntity> costList) {
		return ownerOrderIncrementDetailMapper.saveOwnerOrderIncrementDetailBatch(costList);
	}
}
