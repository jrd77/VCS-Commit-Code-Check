/**
 * 
 */
package com.atzuche.order.coreapi.service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import com.atzuche.order.cashieraccount.entity.CashierEntity;
import com.atzuche.order.cashieraccount.service.CashierService;
import com.atzuche.order.commons.vo.res.CashierResVO;

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
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	public List<CashierResVO> queryPaymentList(String orderNo)  {
		List<CashierResVO> lstRet = new ArrayList<CashierResVO>();
		List<CashierEntity> lst = cashierService.getCashierRentCostsByOrderNo(orderNo);
		for (CashierEntity cashierEntity : lst) {
			CashierResVO vo = new CashierResVO();
			//数据转换
			BeanUtils.copyProperties(cashierEntity,vo);
			lstRet.add(vo);
		}
		return lstRet;
	}
	
}