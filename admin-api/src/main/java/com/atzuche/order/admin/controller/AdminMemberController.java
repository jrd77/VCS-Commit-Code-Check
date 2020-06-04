package com.atzuche.order.admin.controller;

import com.atzuche.order.admin.vo.resp.wallet.WalletBalanceVO;
import com.atzuche.order.commons.entity.dto.MemberDebtListReqDTO;
import com.atzuche.order.open.service.FeignCashWithdrawalService;
import com.autoyol.commons.utils.Page;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/console")
@RestController
@AutoDocVersion(version = "钱包接口文档")
public class AdminMemberController {

    private static final Logger logger = LoggerFactory.getLogger(AdminMemberController.class);

    @Autowired
    private FeignCashWithdrawalService feignCashWithdrawalService;


	@AutoDocMethod(description = "获取钱包余额", value = "获取钱包余额", response = WalletBalanceVO.class)
	@PostMapping("/member/mmeberDebt")
	public ResponseData walletBalance(@RequestBody MemberDebtListReqDTO req) {
        ResponseData<Page> pageResponseData = feignCashWithdrawalService.queryList(req);
        return pageResponseData;
	}






}
