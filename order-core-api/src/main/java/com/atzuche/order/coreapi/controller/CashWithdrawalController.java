package com.atzuche.order.coreapi.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.atzuche.order.cashieraccount.entity.AccountOwnerCashExamine;
import com.atzuche.order.commons.BindingResultUtil;
import com.atzuche.order.commons.entity.dto.SearchCashWithdrawalReqDTO;
import com.atzuche.order.commons.vo.req.AccountOwnerCashExamineReqVO;
import com.atzuche.order.coreapi.service.CashWithdrawalService;
import com.autoyol.commons.web.ResponseData;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class CashWithdrawalController {
	
	@Autowired
	private CashWithdrawalService cashWithdrawalService;

	/**
	 * 提现
	 * @param req
	 * @param bindingResult
	 * @return ResponseData<?>
	 */
	@PostMapping("/account/withdraw")
	public ResponseData<?> cashWithdraw(@Valid @RequestBody AccountOwnerCashExamineReqVO req, BindingResult bindingResult) {
		log.info("提现AccountOwnerCashExamineReqVO=[{}]", req);
		BindingResultUtil.checkBindingResult(bindingResult);
		cashWithdrawalService.cashWithdrawal(req);
        return ResponseData.success();
    }
	
	
	/**
	 * 获取提现记录
	 * @param req
	 * @return ResponseData<List<AccountOwnerCashExamine>>
	 */
	@GetMapping("/account/withdraw/list")
    public ResponseData<List<AccountOwnerCashExamine>> listCashWithdrawal(@Valid @RequestBody SearchCashWithdrawalReqDTO req, BindingResult bindingResult) {
		log.info("获取提现记录 req=[{}]", req);
		BindingResultUtil.checkBindingResult(bindingResult);
		List<AccountOwnerCashExamine> list = cashWithdrawalService.listCashWithdrawal(req);
    	return ResponseData.success(list);
    }
}
