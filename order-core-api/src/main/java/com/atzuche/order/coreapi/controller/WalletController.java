package com.atzuche.order.coreapi.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.atzuche.order.wallet.WalletProxyService;
import com.atzuche.order.wallet.vo.WalletBalanceVO;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;

@RequestMapping("/wallet")
@RestController
@AutoDocVersion(version = "钱包接口文档")
public class WalletController {
    private static final Logger logger = LoggerFactory.getLogger(WalletController.class);

    @Autowired
    private WalletProxyService walletProxyService;


	@AutoDocMethod(description = "获取钱包余额", value = "获取钱包余额", response = WalletBalanceVO.class)
	@GetMapping("/balance")
	public ResponseData walletBalance(@RequestParam("memNo") String  memNo) {
        int left = walletProxyService.getWalletByMemNo(memNo);
        WalletBalanceVO walletResponseVO = new WalletBalanceVO();
        walletResponseVO.setBalance(left);
        walletResponseVO.setMemNo(memNo);
		return ResponseData.success(walletResponseVO);
	}
	
}
