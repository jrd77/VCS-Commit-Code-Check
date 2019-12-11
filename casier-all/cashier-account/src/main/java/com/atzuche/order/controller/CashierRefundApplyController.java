package com.atzuche.order.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.atzuche.order.service.CashierRefundApplyService;



/**
 * 退款申请表
 * 
 * @author ZhangBin
 * @date 2019-12-11 11:17:59
 */
@RestController
@RequestMapping("cashierrefundapply")
public class CashierRefundApplyController {
	@Autowired
	private CashierRefundApplyService cashierRefundApplyService;
	
	
}
