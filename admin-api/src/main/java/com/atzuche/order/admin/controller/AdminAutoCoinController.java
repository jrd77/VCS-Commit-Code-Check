package com.atzuche.order.admin.controller;

import com.atzuche.order.admin.vo.resp.autocoin.AutoCoinResponseVO;
import com.atzuche.order.admin.vo.resp.wallet.WalletBalanceVO;
import com.atzuche.order.coin.service.AutoCoinProxyService;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/console/")
@RestController
@AutoDocVersion(version = "订单接口文档")
public class AdminAutoCoinController {

    private static final Logger logger = LoggerFactory.getLogger(AdminAutoCoinController.class);

    @Autowired
    private AutoCoinProxyService proxyService;

    @AutoDocMethod(description = "获取凹凸币余额", value = "获取钱包余额", response = AutoCoinResponseVO.class)
	@GetMapping("autocoin/balance")
	public ResponseData<WalletBalanceVO> autocoinBalance(@RequestParam("memNo") String memNo) {
          int total = proxyService.getCrmCustPoint(memNo);
          WalletBalanceVO walletResponseVO = new WalletBalanceVO();
          walletResponseVO.setBalance(total);
          walletResponseVO.setMemNo(memNo);
          return ResponseData.success(walletResponseVO);
	}




}
