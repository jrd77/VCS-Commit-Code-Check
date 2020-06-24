package com.atzuche.order.open.service;

import com.atzuche.order.commons.entity.dto.AccountOwnerCashExamineDTO;
import com.atzuche.order.commons.entity.dto.MemberDebtListReqDTO;
import com.atzuche.order.commons.entity.dto.SearchCashWithdrawalReqDTO;
import com.atzuche.order.commons.vo.DebtDetailVO;
import com.atzuche.order.commons.vo.req.AccountOwnerCashExamineReqVO;
import com.atzuche.order.commons.vo.req.income.AcctOwnerWithdrawalRuleReqVO;
import com.atzuche.order.commons.vo.res.account.income.AcctOwnerWithdrawalRuleResVO;
import com.autoyol.commons.utils.Page;
import com.autoyol.commons.web.ResponseData;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.List;

@FeignClient(name="order-center-api")
public interface FeignCashWithdrawalService {

	@PostMapping("/account/withdraw")
	public ResponseData<?> cashWithdraw(@Valid @RequestBody AccountOwnerCashExamineReqVO req);
	
	@GetMapping("/account/withdraw/list")
    public ResponseData<List<AccountOwnerCashExamineDTO>> listCashWithdrawal(@Valid SearchCashWithdrawalReqDTO req);

	/**
	 * 获取用户总欠款
	 * @param req
	 * @return ResponseData<?>
	 */
	@GetMapping("/debt/get")
    public ResponseData<DebtDetailVO> getDebtAmt(@RequestParam(value="memNo",required = true) String memNo);
	/**
	 * 获取用户总欠款列表
	 * @param req
	 * @return ResponseData<?>
	 */
	@PostMapping("/debt/queryList")
	public ResponseData<Page> queryList(@Valid @RequestBody MemberDebtListReqDTO req);


    @PostMapping("/account/withdraw/pre/rule")
    public ResponseData<AcctOwnerWithdrawalRuleResVO> getAcctOwnerWithdrawalRule(@RequestBody AcctOwnerWithdrawalRuleReqVO req);
}
