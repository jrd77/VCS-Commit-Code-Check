package com.atzuche.order.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atzuche.order.owner.cost.entity.OwnerOrderSubsidyDetailEntity;
import com.atzuche.order.owner.cost.mapper.OwnerOrderSubsidyDetailMapper;

@Service
public class OwnerOrderSubsidyDetailService {

	@Autowired
	private OwnerOrderSubsidyDetailMapper ownerOrderSubsidyDetailMapper;
	
	/**
	 * 获取车主补贴明细列表
	 * @param orderNo 主订单号
	 * @param ownerOrderNo 车主订单号
	 * @return List<OwnerOrderSubsidyDetailEntity>
	 */
	public List<OwnerOrderSubsidyDetailEntity> listOwnerOrderSubsidyDetail(String orderNo, String ownerOrderNo) {
		return ownerOrderSubsidyDetailMapper.listOwnerOrderSubsidyDetail(orderNo, ownerOrderNo);
	}
	
	/**
	 * 保存车主补贴明细
	 * @param ownerOrderSubsidyDetailEntity 车主补贴明细
	 * @return Integer
	 */
	public Integer saveOwnerOrderSubsidyDetail(OwnerOrderSubsidyDetailEntity ownerOrderSubsidyDetailEntity) {
		return ownerOrderSubsidyDetailMapper.saveOwnerOrderSubsidyDetail(ownerOrderSubsidyDetailEntity);
	}
	
	/**
	 * 批量保存车主补贴明细
	 * @param costList 车主补贴明细列表
	 * @return Integer
	 */
	public Integer saveOwnerOrderSubsidyDetailBatch(List<OwnerOrderSubsidyDetailEntity> costList) {
		return ownerOrderSubsidyDetailMapper.saveOwnerOrderSubsidyDetailBatch(costList);
	}
}
