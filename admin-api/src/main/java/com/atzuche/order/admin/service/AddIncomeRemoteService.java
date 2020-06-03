package com.atzuche.order.admin.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.commons.ResponseCheckUtil;
import com.atzuche.order.commons.entity.dto.AddIncomeExamineDTO;
import com.atzuche.order.commons.entity.dto.AddIncomeExamineOptDTO;
import com.atzuche.order.commons.entity.dto.AddIncomeExcelConsoleDTO;
import com.atzuche.order.commons.entity.dto.AddIncomeExcelOptDTO;
import com.atzuche.order.commons.entity.dto.AddIncomeExcelVO;
import com.atzuche.order.commons.entity.dto.AddIncomeImportDTO;
import com.atzuche.order.commons.vo.AddIncomeExamine;
import com.atzuche.order.commons.vo.AddIncomeExamineLog;
import com.atzuche.order.commons.vo.AddIncomeExamineVO;
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
     * @param req
     * @return AddIncomeExcelVO
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
            log.error("Feign 获取追加收益文件列表异常,responseObject={},req={}",JSON.toJSONString(responseObject),JSON.toJSONString(req),e);
            Cat.logError("Feign 获取追加收益文件列表异常",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }
    }
    
    
    /**
     * 保存追加收益
     * @param req
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
            log.error("Feign 保存追加收益异常,responseObject={},req={}",JSON.toJSONString(responseObject),JSON.toJSONString(req),e);
            Cat.logError("Feign 保存追加收益异常",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }
    }
    
    
    /**
     * 追加收益操作
     * @param req
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
            log.error("Feign 追加收益操作异常,responseObject={},req={}",JSON.toJSONString(responseObject),JSON.toJSONString(req),e);
            Cat.logError("Feign 追加收益操作异常",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }
    }
    
    
    /**
     * 获取追加收益审核列表(分页)
     * @param req
     * @return AddIncomeExamineVO
     */
    public AddIncomeExamineVO getAddIncomeExamineVO(AddIncomeExamineDTO req) {
        ResponseData<AddIncomeExamineVO> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "获取追加收益审核列表(分页)");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"feignAddIncomeService.getAddIncomeExamineVO");
            log.info("Feign 获取追加收益审核列表(分页),req={}", JSON.toJSONString(req));
            Cat.logEvent(CatConstants.FEIGN_PARAM,JSON.toJSONString(req));
            responseObject = feignAddIncomeService.getAddIncomeExamineVO(req);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            ResponseCheckUtil.checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
            return responseObject.getData();
        }catch (Exception e){
            log.error("Feign 获取追加收益审核列表(分页)异常,responseObject={},req={}",JSON.toJSONString(responseObject),JSON.toJSONString(req),e);
            Cat.logError("Feign 获取追加收益审核列表(分页)异常",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }
    }
    
    
    /**
     * 获取追加收益审核列表(非分页)
     * @param req
     * @return List<AddIncomeExamine>
     */
    public List<AddIncomeExamine> listAllAddIncomeExamine(AddIncomeExamineDTO req) {
        ResponseData<List<AddIncomeExamine>> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "获取追加收益审核列表(非分页)");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"feignAddIncomeService.listAllAddIncomeExamine");
            log.info("Feign 获取追加收益审核列表(非分页),req={}", JSON.toJSONString(req));
            Cat.logEvent(CatConstants.FEIGN_PARAM,JSON.toJSONString(req));
            responseObject = feignAddIncomeService.listAllAddIncomeExamine(req);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            ResponseCheckUtil.checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
            return responseObject.getData();
        }catch (Exception e){
            log.error("Feign 获取追加收益审核列表(非分页)异常,responseObject={},req={}",JSON.toJSONString(responseObject),JSON.toJSONString(req),e);
            Cat.logError("Feign 获取追加收益审核列表(非分页)异常",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }
    }
    
    
    /**
     * 获取追加收益审核日志
     * @param id
     * @return List<AddIncomeExamineLog>
     */
    public List<AddIncomeExamineLog> listAddIncomeExamineLog(Integer id) {
        ResponseData<List<AddIncomeExamineLog>> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "获取追加收益审核日志");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"feignAddIncomeService.listAddIncomeExamineLog");
            log.info("Feign 获取追加收益审核日志,id={}", id);
            Cat.logEvent(CatConstants.FEIGN_PARAM,"id="+id);
            responseObject = feignAddIncomeService.listAddIncomeExamineLog(id);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            ResponseCheckUtil.checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
            return responseObject.getData();
        }catch (Exception e){
            log.error("Feign 获取追加收益审核日志异常,responseObject={},id={}",JSON.toJSONString(responseObject),id,e);
            Cat.logError("Feign 获取追加收益审核日志异常",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }
    }
    
    /**
     * 追加收益审核操作
     * @param req
     */
    public void examineOpt(AddIncomeExamineOptDTO req) {
        ResponseData<?> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "追加收益审核操作");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"feignAddIncomeService.examineOpt");
            log.info("Feign 追加收益审核操作,req={}", JSON.toJSONString(req));
            Cat.logEvent(CatConstants.FEIGN_PARAM,JSON.toJSONString(req));
            responseObject = feignAddIncomeService.examineOpt(req);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            ResponseCheckUtil.checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
        }catch (Exception e){
            log.error("Feign 追加收益审核操作异常,responseObject={},req={}",JSON.toJSONString(responseObject),JSON.toJSONString(req),e);
            Cat.logError("Feign 追加收益审核操作异常",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }
    }
}
