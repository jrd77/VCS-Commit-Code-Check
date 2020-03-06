package com.atzuche.order.cashieraccount.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.atzuche.order.cashieraccount.entity.CashierBindCardEntity;
import com.atzuche.order.cashieraccount.mapper.CashierBindCardMapper;

@Service
public class CashierBindCardService {

	private CashierBindCardMapper cashierBindCardMapper;
	
	/**
	 * 保存银行卡
	 * @param record
	 * @return
	 */
	public Integer saveCashierBindCard(CashierBindCardEntity record) {
		if (record == null) {
			return 0;
		}
		return cashierBindCardMapper.insertSelective(record);
	}
	
	/**
	 * 获取会员银行卡
	 * @param memNo
	 * @return
	 */
	public List<CashierBindCardEntity> listCashierBindCardByMemNo(String memNo) {
		return cashierBindCardMapper.listCashierBindCardByMemNo(memNo);
	}
}
