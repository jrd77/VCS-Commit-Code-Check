package com.atzuche.order.wallet.server.controller;

import com.atzuche.order.wallet.api.AccountVO;
import com.atzuche.order.wallet.api.MemAccount;
import com.atzuche.order.wallet.api.TotalWalletVO;
import com.atzuche.order.wallet.server.service.AccountService;
import com.autoyol.commons.web.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/3/5 2:09 下午
 **/
@Controller
public class AccountController {

    @Autowired
    private AccountService accountService;

    @RequestMapping(value = "account/get",method = RequestMethod.GET)
    public ResponseData<MemAccount> findAccountByMemNo(@RequestParam("memNo") String memNo)throws Exception{
        List<AccountVO> accountVOList = accountService.findByMemNo(memNo);
        MemAccount memAccount = new MemAccount();
        memAccount.setMemNo(memNo);
        memAccount.setAccounts(accountVOList);
        return ResponseData.success(memAccount);
    }
}
