package com.atzuche.order.service;

import com.atzuche.order.entity.CashierEntity;

import java.util.List;

/**
 * 收银表
 * 
 * @author ZhangBin
 * @date 2019-12-11 11:17:59
 */
public interface CashierService {
	
	CashierEntity selectByPrimaryKey(Integer id);
	
	List<CashierEntity> selectALL();

	int insert(CashierEntity record);
	
	int insertSelective(CashierEntity record);
	
	int updateByPrimaryKey(CashierEntity record);
	
	int updateByPrimaryKeySelective(CashierEntity record);


}
