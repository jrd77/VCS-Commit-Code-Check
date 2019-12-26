package com.atzuche.order.coreapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atzuche.order.renterorder.entity.RenterOrderChangeApplyEntity;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.service.RenterOrderChangeApplyService;
import com.atzuche.order.renterorder.service.RenterOrderService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ModifyOrderForRenterService {

	@Autowired
	private RenterOrderService renterOrderService;
	@Autowired
	private RenterOrderChangeApplyService renterOrderChangeApplyService;
	
	public Integer INVALID_FLAG = 0;
	
	public Integer EFFECTIVE_FLAG = 1;
	
	public Integer AUDIT_STATUS_AGREE = 1;
	
	public Integer AUDIT_STATUS_REFUSE = 2;
	
	/**
	 * 车主同意后修改租客子单状态
	 * @param orderNo 主订单号
	 * @param renterOrderNo 租客修改申请表中已同意的租客子订单号
	 * @param renterOrderEntity 当前有效的租客子单
	 */
	public void updateRenterOrderStatus(String orderNo, String renterOrderNo, RenterOrderEntity renterOrderEntity) {
		log.info("车主同意后修改租客子单状态orderNo=[{}],renterOrderNo=[{}],renterOrderEntity=[{}]",orderNo, renterOrderNo, renterOrderEntity);
		// 获取当前有效的子订单
		if (renterOrderEntity == null) {
			renterOrderEntity = renterOrderService.getRenterOrderByOrderNoAndIsEffective(orderNo);
		}
		// 获取租客修改申请表中已同意的租客子订单
		RenterOrderEntity agreeRenterOrderEntity = renterOrderService.getRenterOrderByRenterOrderNo(renterOrderNo);
		// 修改租客上笔子单状态失效
		renterOrderService.updateRenterOrderEffective(renterOrderEntity.getId(), INVALID_FLAG);
		// 修改租客待确认的子单状态为有效
		renterOrderService.updateRenterOrderEffective(agreeRenterOrderEntity.getId(), EFFECTIVE_FLAG);
		// 获取申请记录
		RenterOrderChangeApplyEntity renterOrderChangeApplyEntity = renterOrderChangeApplyService.getRenterOrderChangeApplyByRenterOrderNo(renterOrderNo);
		// 修改租客申请状态为已同意
		renterOrderChangeApplyService.updateRenterOrderChangeApplyStatus(renterOrderChangeApplyEntity.getId(), AUDIT_STATUS_AGREE);
	}
}
