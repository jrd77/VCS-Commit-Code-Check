package com.atzuche.order.settle.service;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.commons.ResponseCheckUtil;
import com.atzuche.order.wallet.api.DebtFeignService;
import com.atzuche.order.wallet.api.DeductDebtVO;
import com.atzuche.order.wallet.api.MemDebtVO;
import com.autoyol.commons.web.ResponseData;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RemoteOldSysDebtService {
	
	private DebtFeignService debtFeignService;

	/**
     * 获取用户的欠款，为正值
     * @param memNo
     * @return Integer
     */
    public Integer getMemBalance(String memNo) {
        ResponseData<MemDebtVO> responseData = null;
        log.info("Feign 获取用户的欠款,memNo={}",memNo);
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "钱包服务");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"RemoteOldSysDebtService.getMemBalance");
            String parameter = "memNo="+memNo;
            Cat.logEvent(CatConstants.FEIGN_PARAM,parameter);
            responseData = debtFeignService.getMemBalance(memNo);
            ResponseCheckUtil.checkResponse(responseData);
            t.setStatus(Transaction.SUCCESS);
        }catch (Exception e){
            log.error("Feign 获取用户的欠款失败,ResponseData={},memNo={}",responseData,memNo,e);
            Cat.logError("Feign 获取用户的欠款失败",e);
            t.setStatus(e);
        }finally {
            t.complete();
        }
        MemDebtVO memDebtVO = responseData.getData();
        if (memDebtVO != null) {
        	return memDebtVO.getDebt();
        }
        return null;
    }
    
    
    /**
     * 扣减用户的欠款
     * @param memNo
     * @param deduct
     */
    public void deductBalance(String memNo, Integer deduct) {
        ResponseData responseData = null;
        log.info("Feign 扣减用户的欠款,memNo={}, deduct={}",memNo, deduct);
        if (StringUtils.isBlank(memNo) || deduct == null || deduct <= 0) {
    		return;
    	}
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "钱包服务");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"RemoteOldSysDebtService.getMemBalance");
            String parameter = "memNo="+memNo;
            Cat.logEvent(CatConstants.FEIGN_PARAM,parameter);
            DeductDebtVO deductDebtVO = new DeductDebtVO();
            deductDebtVO.setDeduct(deduct);
            deductDebtVO.setMemNo(memNo);
            responseData = debtFeignService.deductBalance(deductDebtVO);
            ResponseCheckUtil.checkResponse(responseData);
            t.setStatus(Transaction.SUCCESS);
        }catch (Exception e){
            log.error("Feign 扣减用户的欠款失败,ResponseData={},memNo={}",responseData,memNo,e);
            Cat.logError("Feign 扣减用户的欠款失败",e);
            t.setStatus(e);
        }finally {
            t.complete();
        }
    }
}
