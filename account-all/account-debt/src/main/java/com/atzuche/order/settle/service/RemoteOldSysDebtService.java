package com.atzuche.order.settle.service;

import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.commons.ResponseCheckUtil;
import com.atzuche.order.commons.entity.dto.MemberDebtListReqDTO;
import com.atzuche.order.wallet.api.DebtDetailVO;
import com.atzuche.order.wallet.api.DebtFeignService;
import com.atzuche.order.wallet.api.DeductDebtVO;
import com.atzuche.order.wallet.api.MemDebtVO;
import com.autoyol.commons.utils.GsonUtils;
import com.autoyol.commons.utils.Page;
import com.autoyol.commons.web.ResponseData;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RemoteOldSysDebtService {
	
	@Autowired
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
    
    
    /**
     * 返回用户名下的欠款(区分历史欠款和订单欠款)
     * @param memNo
     * @return Integer
     */
    public DebtDetailVO getDebtDetailVO(String memNo) {
        ResponseData<DebtDetailVO> responseData = null;
        log.info("Feign 获取用户的欠款,memNo={}",memNo);
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "钱包服务");
        DebtDetailVO debtDetailVO = null;
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"RemoteOldSysDebtService.getDebtDetailVO");
            String parameter = "memNo="+memNo;
            Cat.logEvent(CatConstants.FEIGN_PARAM,parameter);
            responseData = debtFeignService.getDebtDetailVO(memNo);
            ResponseCheckUtil.checkResponse(responseData);
            t.setStatus(Transaction.SUCCESS);
            
            debtDetailVO = responseData.getData();
            
        }catch (Exception e){
            log.error("Feign 获取用户的欠款失败,ResponseData={},memNo={}",responseData,memNo,e);
            Cat.logError("Feign 获取用户的欠款失败",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }   
        
        return debtDetailVO;
    }


    public Page queryList(MemberDebtListReqDTO req) {
        ResponseData<Page> responseData = null;
        log.info("Feign 获取欠款用户,MemberDebtListReqDTO={}",req);
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "钱包服务");
        Page data;
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"RemoteOldSysDebtService.queryList");
            Cat.logEvent(CatConstants.FEIGN_PARAM, GsonUtils.toJson(req));
            String s = GsonUtils.toJson(req);
            MemberDebtListReqDTO memberDebtListReqDTO = GsonUtils.convertObj(s, MemberDebtListReqDTO.class);
            log.info("111111111111111"+GsonUtils.toJson(memberDebtListReqDTO));
            responseData = debtFeignService.queryList(memberDebtListReqDTO);
            log.info("22222222222222"+GsonUtils.toJson(responseData));
            ResponseCheckUtil.checkResponse(responseData);
            t.setStatus(Transaction.SUCCESS);
            data = responseData.getData();
        }catch (Exception e){
            log.error("Feign 获取用户的欠款失败,ResponseData={},MemberDebtListReqDTO={}",GsonUtils.toJson(responseData),GsonUtils.toJson(req),e);
            Cat.logError("Feign 获取用户的欠款失败",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }
        return data;
    }

}
