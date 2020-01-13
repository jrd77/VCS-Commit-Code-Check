/**
 * 
 */
package com.atzuche.order.coreapi.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atzuche.order.cashieraccount.entity.CashierEntity;
import com.atzuche.order.cashieraccount.service.CashierService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author jing.huang
 *
 */
@Service
@Slf4j
public class PaymentCashierService {
	@Autowired
	CashierService cashierService;
	
	/**
	 * 根据订单号查询支付记录
	 */
	public List<CashierEntity> queryPaymentList(String orderNo) {
		return cashierService.getCashierRentCostsByOrderNo(orderNo);
	}
	
}
