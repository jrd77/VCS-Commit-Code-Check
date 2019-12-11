package com.atzuche.order.service;

import com.atzuche.order.entity.CashierBindCardEntity;

import java.util.List;

/**
 * 个人免押绑卡信息表
 * 
 * @author ZhangBin
 * @date 2019-12-11 11:17:59
 */
public interface CashierBindCardService {
	
	CashierBindCardEntity selectByPrimaryKey(Integer id);
	
	List<CashierBindCardEntity> selectALL();

	int insert(CashierBindCardEntity record);
	
	int insertSelective(CashierBindCardEntity record);
	
	int updateByPrimaryKey(CashierBindCardEntity record);
	
	int updateByPrimaryKeySelective(CashierBindCardEntity record);


}
