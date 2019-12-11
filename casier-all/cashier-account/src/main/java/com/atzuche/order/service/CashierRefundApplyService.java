package com.atzuche.order.service;

import com.atzuche.order.entity.CashierRefundApplyEntity;

import java.util.List;

/**
 * 退款申请表
 * 
 * @author ZhangBin
 * @date 2019-12-11 11:17:59
 */
public interface CashierRefundApplyService {
	
	CashierRefundApplyEntity selectByPrimaryKey(Integer id);
	
	List<CashierRefundApplyEntity> selectALL();

	int insert(CashierRefundApplyEntity record);
	
	int insertSelective(CashierRefundApplyEntity record);
	
	int updateByPrimaryKey(CashierRefundApplyEntity record);
	
	int updateByPrimaryKeySelective(CashierRefundApplyEntity record);


}
