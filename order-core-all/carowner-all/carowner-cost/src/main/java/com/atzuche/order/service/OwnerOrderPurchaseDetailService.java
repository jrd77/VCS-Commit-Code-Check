package com.atzuche.order.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atzuche.order.ownercost.entity.OwnerOrderPurchaseDetailEntity;
import com.atzuche.order.ownercost.mapper.OwnerOrderPurchaseDetailMapper;


@Service
public class OwnerOrderPurchaseDetailService {

	@Autowired
	private OwnerOrderPurchaseDetailMapper ownerOrderPurchaseDetailMapper;
	
	/**
	 * 获取车主费用列表
	 * @param orderNo 主订单号
	 * @param ownerOrderNo 车主订单号
	 * @return List<OwnerOrderPurchaseDetailEntity>
	 */
	public List<OwnerOrderPurchaseDetailEntity> listOwnerOrderPurchaseDetail(String orderNo, String ownerOrderNo) {
		return ownerOrderPurchaseDetailMapper.listOwnerOrderPurchaseDetail(orderNo, ownerOrderNo);
	}
	
	/**
	 * 保存车主费用明细
	 * @param ownerOrderPurchaseDetailEntity 车主费用明细
	 * @return Integer
	 */
	public Integer saveOwnerOrderPurchaseDetail(OwnerOrderPurchaseDetailEntity ownerOrderPurchaseDetailEntity) {
		return ownerOrderPurchaseDetailMapper.saveOwnerOrderPurchaseDetail(ownerOrderPurchaseDetailEntity);
	}
	
	/**
	 * 批量保存车主费用明细
	 * @param costList 车主费用明细列表
	 * @return Integer
	 */
	public Integer saveOwnerOrderPurchaseDetailBatch(List<OwnerOrderPurchaseDetailEntity> costList) {
		if (costList == null || costList.isEmpty()) {
			return 1;
		}
		return ownerOrderPurchaseDetailMapper.saveOwnerOrderPurchaseDetailBatch(costList);
	}
}
