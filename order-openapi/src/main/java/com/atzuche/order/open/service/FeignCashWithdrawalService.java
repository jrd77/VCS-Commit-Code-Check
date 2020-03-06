package com.atzuche.order.open.service;

import java.util.List;

import javax.validation.Valid;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.atzuche.order.commons.entity.dto.AccountOwnerCashExamineDTO;
import com.atzuche.order.commons.entity.dto.SearchCashWithdrawalReqDTO;
import com.atzuche.order.commons.vo.req.AccountOwnerCashExamineReqVO;
import com.autoyol.commons.web.ResponseData;

@FeignClient(name="order-center-api")
public interface FeignCashWithdrawalService {

	@PostMapping("/account/withdraw")
	public ResponseData<?> cashWithdraw(@Valid @RequestBody AccountOwnerCashExamineReqVO req, BindingResult bindingResult);
	
	@GetMapping("/account/withdraw/list")
    public ResponseData<List<AccountOwnerCashExamineDTO>> listCashWithdrawal(@Valid @RequestBody SearchCashWithdrawalReqDTO req, BindingResult bindingResult);
}
