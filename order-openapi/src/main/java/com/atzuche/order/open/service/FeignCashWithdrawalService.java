package com.atzuche.order.open.service;

import java.util.List;

import javax.validation.Valid;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.atzuche.order.commons.entity.dto.AccountOwnerCashExamineDTO;
import com.atzuche.order.commons.entity.dto.SearchCashWithdrawalReqDTO;
import com.atzuche.order.commons.vo.DebtDetailVO;
import com.atzuche.order.commons.vo.req.AccountOwnerCashExamineReqVO;
import com.atzuche.order.commons.vo.req.SearchMemberOrderDebtListReqVO;
import com.atzuche.order.commons.vo.req.income.AcctOwnerWithdrawalRuleReqVO;
import com.atzuche.order.commons.vo.res.account.income.AcctOwnerWithdrawalRuleResVO;
import com.autoyol.commons.web.ResponseData;

@FeignClient(name="order-center-api")
public interface FeignCashWithdrawalService {

	@PostMapping("/account/withdraw")
	public ResponseData<?> cashWithdraw(@Valid @RequestBody AccountOwnerCashExamineReqVO req);
	
	@GetMapping("/account/withdraw/list")
    public ResponseData<List<AccountOwnerCashExamineDTO>> listCashWithdrawal(@Valid SearchCashWithdrawalReqDTO req);

    /**
     * 判断提现金额是否包含二清
     *
     * @param req 请求参数
     * @return ResponseData<AcctOwnerWithdrawalRuleResVO>
     */
    @PostMapping("/account/withdraw/pre/rule")
    public ResponseData<AcctOwnerWithdrawalRuleResVO> getAcctOwnerWithdrawalRule(@RequestBody AcctOwnerWithdrawalRuleReqVO req);

	/**
	 * 获取用户总欠款
	 * @param req
	 * @return ResponseData<?>
	 */
	@GetMapping("/debt/get")
    public ResponseData<DebtDetailVO> getDebtAmt(@RequestParam(value="memNo",required = true) String memNo);



	@PostMapping("/debt/queryDebtOrderList")
	public ResponseData<?> queryDebtOrderList(@RequestBody SearchMemberOrderDebtListReqVO req);

}
