package com.atzuche.order.admin.controller;

import com.atzuche.order.admin.vo.resp.wallet.WalletBalanceVO;
import com.atzuche.order.wallet.WalletProxyService;
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
public class AdminWalletController {

    private static final Logger logger = LoggerFactory.getLogger(AdminWalletController.class);

    @Autowired
    private WalletProxyService walletProxyService;


	@AutoDocMethod(description = "获取钱包余额", value = "获取钱包余额", response = WalletBalanceVO.class)
	@GetMapping("/wallet/balance")
	public ResponseData walletBalance(@RequestParam("memNo") String  memNo) {
        int left = walletProxyService.getWalletByMemNo(memNo);
        WalletBalanceVO walletResponseVO = new WalletBalanceVO();
        walletResponseVO.setBalance(String.valueOf(left));
		return ResponseData.success(walletResponseVO);
	}






}
