package com.atzuche.order.coreapi.service;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atzuche.order.cashieraccount.entity.AccountOwnerCashExamine;
import com.atzuche.order.cashieraccount.service.AccountOwnerCashExamineService;
import com.atzuche.order.commons.entity.dto.CashWithdrawalSimpleMemberDTO;
import com.atzuche.order.commons.entity.dto.SearchCashWithdrawalReqDTO;
import com.atzuche.order.commons.vo.req.AccountOwnerCashExamineReqVO;
import com.atzuche.order.mem.MemProxyService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CashWithdrawalService {

	@Autowired
	private AccountOwnerCashExamineService accountOwnerCashExamineService;
	@Autowired
	private MemProxyService memProxyService;
	
	/**
	 * 提现功能
	 * @param req
	 */
	public void cashWithdrawal(AccountOwnerCashExamineReqVO req) {
		log.info("提现开始CashWithdrawalService.cashWithdrawal accountOwnerCashExamineReqVO=[{}]",req);
		// 获取会员信息
		CashWithdrawalSimpleMemberDTO simpleMem = memProxyService.getSimpleMemberInfo(req.getMemNo());
		// 提现主逻辑
		accountOwnerCashExamineService.saveAccountOwnerCashExamine(req, simpleMem);
	}
	
	
	/**
	 * 获取新提现列表根据会员号
	 * @param req
	 * @return List<AccountOwnerCashExamine>
	 */
	public List<AccountOwnerCashExamine> listCashWithdrawal(SearchCashWithdrawalReqDTO req) {
		log.info("提现开始CashWithdrawalService.listCashWithdrawal searchCashWithdrawalReqDTO=[{}]",req);
		Integer memNo = StringUtils.isBlank(req.getMemNo()) ? null:Integer.valueOf(req.getMemNo());
		return accountOwnerCashExamineService.listAccountOwnerCashExamineByMemNo(memNo);
	}
}
