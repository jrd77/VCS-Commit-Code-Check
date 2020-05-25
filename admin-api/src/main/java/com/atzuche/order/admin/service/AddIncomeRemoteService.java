package com.atzuche.order.admin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.commons.ResponseCheckUtil;
import com.atzuche.order.commons.entity.dto.AddIncomeExcelConsoleDTO;
import com.atzuche.order.commons.entity.dto.AddIncomeExcelOptDTO;
import com.atzuche.order.commons.entity.dto.AddIncomeExcelVO;
import com.atzuche.order.commons.entity.dto.AddIncomeImportDTO;
import com.atzuche.order.open.service.FeignAddIncomeService;
import com.autoyol.commons.web.ResponseData;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AddIncomeRemoteService {

	@Autowired
	private FeignAddIncomeService feignAddIncomeService;
	
	/**
     * 获取追加收益文件列表
     * @param modifyOrderReq
     * @return ModifyOrderConsoleDTO
     */
    public AddIncomeExcelVO getAddIncomeExcelVO(AddIncomeExcelConsoleDTO req) {
        ResponseData<AddIncomeExcelVO> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "获取追加收益文件列表");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"feignAddIncomeService.getAddIncomeExcelVO");
            log.info("Feign 获取追加收益文件列表,req={}", JSON.toJSONString(req));
            Cat.logEvent(CatConstants.FEIGN_PARAM,JSON.toJSONString(req));
            responseObject = feignAddIncomeService.getAddIncomeExcelVO(req);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            ResponseCheckUtil.checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
            return responseObject.getData();
        }catch (Exception e){
            log.error("Feign 获取追加收益文件列表异常,responseObject={},modifyOrderReq={}",JSON.toJSONString(responseObject),JSON.toJSONString(req),e);
            Cat.logError("Feign 获取追加收益文件列表异常",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }
    }
    
    
    /**
     * 保存追加收益
     * @param modifyOrderReq
     * @return ModifyOrderConsoleDTO
     */
    public void saveAddIncomeExcel(AddIncomeImportDTO req) {
        ResponseData<?> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "保存追加收益");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"feignAddIncomeService.saveAddIncomeExcel");
            log.info("Feign 保存追加收益,req={}", JSON.toJSONString(req));
            Cat.logEvent(CatConstants.FEIGN_PARAM,JSON.toJSONString(req));
            responseObject = feignAddIncomeService.saveAddIncomeExcel(req);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            ResponseCheckUtil.checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
        }catch (Exception e){
            log.error("Feign 保存追加收益异常,responseObject={},modifyOrderReq={}",JSON.toJSONString(responseObject),JSON.toJSONString(req),e);
            Cat.logError("Feign 保存追加收益异常",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }
    }
    
    
    /**
     * 追加收益操作
     * @param modifyOrderReq
     * @return ModifyOrderConsoleDTO
     */
    public void updateStatus(AddIncomeExcelOptDTO req) {
        ResponseData<?> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "追加收益操作");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"feignAddIncomeService.updateStatus");
            log.info("Feign 追加收益操作,req={}", JSON.toJSONString(req));
            Cat.logEvent(CatConstants.FEIGN_PARAM,JSON.toJSONString(req));
            responseObject = feignAddIncomeService.updateStatus(req);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            ResponseCheckUtil.checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
        }catch (Exception e){
            log.error("Feign 追加收益操作异常,responseObject={},modifyOrderReq={}",JSON.toJSONString(responseObject),JSON.toJSONString(req),e);
            Cat.logError("Feign 追加收益操作异常",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }
    }
}
