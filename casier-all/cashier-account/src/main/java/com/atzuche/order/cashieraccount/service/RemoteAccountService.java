package com.atzuche.order.cashieraccount.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.commons.ResponseCheckUtil;
import com.atzuche.order.commons.entity.dto.BankCardDTO;
import com.atzuche.order.wallet.api.AccountFeignService;
import com.atzuche.order.wallet.api.AccountVO;
import com.atzuche.order.wallet.api.DeductBalanceVO;
import com.atzuche.order.wallet.api.MemBalanceVO;
import com.autoyol.commons.web.ResponseData;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RemoteAccountService {
	
	@Autowired
	private AccountFeignService accountFeignService;

	 /**
     * 获取老系统可提现余额
     * @param memNo
     * @return MemBalanceVO
     */
    public MemBalanceVO getMemBalance(String memNo)  {
        ResponseData<MemBalanceVO> responseData = null;
        log.info("Feign 开始获取老系统可提现余额,memNo={}",memNo);
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "账户服务");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"RemoteAccountService.getMemBalance");
            String parameter = "memNo="+memNo;
            Cat.logEvent(CatConstants.FEIGN_PARAM,parameter);
            responseData = accountFeignService.getMemBalance(memNo);
            ResponseCheckUtil.checkResponse(responseData);
            t.setStatus(Transaction.SUCCESS);
        }catch (Exception e){
            log.error("Feign 获取老系统可提现余额失败,ResponseData={},memNo={}",responseData,memNo,e);
            Cat.logError("Feign 获取老系统可提现余额失败",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }
        return responseData.getData();
    }
    
    
    /**
     * 抵扣老系统可提现余额
     * @param memNo
     * @return MemBalanceVO
     */
    public void deductBalance(String memNo, Integer deduct)  {
        ResponseData<MemBalanceVO> responseData = null;
        log.info("Feign 开始抵扣老系统可提现余额,memNo={}",memNo);
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "账户服务");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"RemoteAccountService.deductBalance");
            String parameter = "memNo="+memNo+"&deduct="+deduct;
            Cat.logEvent(CatConstants.FEIGN_PARAM,parameter);
            DeductBalanceVO deductBalanceVO = new DeductBalanceVO();
            deductBalanceVO.setMemNo(memNo);
            deductBalanceVO.setDeduct(deduct);
            responseData = accountFeignService.deductBalance(deductBalanceVO);
            ResponseCheckUtil.checkResponse(responseData);
            t.setStatus(Transaction.SUCCESS);
        }catch (Exception e){
            log.error("Feign 抵扣老系统可提现余额失败,ResponseData={},memNo={},deduct={}",responseData,memNo,deduct,e);
            Cat.logError("Feign 抵扣老系统可提现余额失败",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }
    }
    
    
    /**
     * 根据id获取银行卡
     * @param cardId
     * @return BankCardDTO
     */
    public BankCardDTO findAccountById(Integer cardId)  {
        ResponseData<AccountVO> responseData = null;
        log.info("Feign 开始根据id获取银行卡,cardId={}",cardId);
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "账户服务");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"RemoteAccountService.getMemBalance");
            String parameter = "cardId="+cardId;
            Cat.logEvent(CatConstants.FEIGN_PARAM,parameter);
            responseData = accountFeignService.findAccountById(cardId);
            ResponseCheckUtil.checkResponse(responseData);
            t.setStatus(Transaction.SUCCESS);
        }catch (Exception e){
            log.error("Feign 根据id获取银行卡失败,ResponseData={},cardId={}",responseData,cardId,e);
            Cat.logError("Feign 根据id获取银行卡失败",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }
        AccountVO accountVO = responseData.getData();
        BankCardDTO bankCardDTO = new BankCardDTO();
        if (accountVO != null) {
        	bankCardDTO.setId(accountVO.getId() == null ? null:String.valueOf(accountVO.getId()));
        	bankCardDTO.setCardNo(accountVO.getCardNo());
        	bankCardDTO.setCardHolder(accountVO.getCardHolder());
        	bankCardDTO.setBankName(accountVO.getBankName() == null ? null:String.valueOf(accountVO.getBankName()));
        	bankCardDTO.setBranchBankName(accountVO.getBranchBankName());
        	bankCardDTO.setProvince(accountVO.getProvince());
        	bankCardDTO.setCity(accountVO.getCity());
        }
        return bankCardDTO;
    }
}
