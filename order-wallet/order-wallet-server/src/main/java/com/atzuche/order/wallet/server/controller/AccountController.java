package com.atzuche.order.wallet.server.controller;

import com.atzuche.order.commons.BindingResultUtil;
import com.atzuche.order.commons.exceptions.AccountNotExistException;
import com.atzuche.order.commons.exceptions.InputErrorException;
import com.atzuche.order.wallet.api.*;
import com.atzuche.order.wallet.server.service.AccountService;
import com.atzuche.order.wallet.server.service.MemBalanceService;
import com.autoyol.commons.web.ResponseData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/3/5 2:09 下午
 **/
@RestController
public class AccountController {
    private final static Logger logger = LoggerFactory.getLogger(AccountController.class);
    

    @Autowired
    private AccountService accountService;
    @Autowired
    private MemBalanceService memBalanceService;

    @RequestMapping(value = "account/get",method = RequestMethod.GET)
    public ResponseData<MemAccount> findAccountByMemNo(@RequestParam("memNo") String memNo)throws Exception{
        List<AccountVO> accountVOList = accountService.findByMemNo(memNo);
        MemAccount memAccount = new MemAccount();
        memAccount.setMemNo(memNo);
        memAccount.setAccounts(accountVOList);
        return ResponseData.success(memAccount);
    }

    @RequestMapping(value = "account/stat",method = RequestMethod.POST)
    public ResponseData<MemAccountStatRespVO> statMemAccount(@Valid @RequestBody MemAccountStatReqVO reqVO,BindingResult result){
        logger.info("statMemAccount param is [{}]",reqVO);
        BindingResultUtil.checkBindingResult(result);
        List<MemAccountStatVO> list = accountService.findByMemNo(reqVO.getMemNoList());
        MemAccountStatRespVO respVO = new MemAccountStatRespVO();
        respVO.setStatVOList(list);
        return ResponseData.success(respVO);
    }

    @RequestMapping(value = "account/id",method = RequestMethod.GET)
    public ResponseData<AccountVO> findAccountByMemNo(@RequestParam("id")Integer id)throws Exception{
        AccountVO accountVO = accountService.getById(id);
        if(accountVO==null){
            throw new AccountNotExistException("id="+id);
        }
        return ResponseData.success(accountVO);
    }

    @RequestMapping(value = "balance/deduct",method = RequestMethod.POST)
    public ResponseData deductBalance(@Valid @RequestBody DeductBalanceVO deductBalanceVO, BindingResult result){
        logger.info("deductBalance param is [{}]",deductBalanceVO);
        BindingResultUtil.checkBindingResult(result);
        if(deductBalanceVO.getDeduct()<=0){
            throw new InputErrorException("扣减金额不能为负数:"+deductBalanceVO.getDeduct());
        }
        memBalanceService.deductBalance(deductBalanceVO.getMemNo(),deductBalanceVO.getDeduct());
        return ResponseData.success();
    }

    @RequestMapping(value = "balance/get",method = RequestMethod.GET)
    public ResponseData<MemBalanceVO> getMemBalance(@RequestParam("memNo")String memNo){
        int total = memBalanceService.getTotalBalance(memNo);
        MemBalanceVO memBalanceVO = new MemBalanceVO();
        memBalanceVO.setBalance(total);
        memBalanceVO.setMemNo(memNo);
        return ResponseData.success(memBalanceVO);
    }
}
