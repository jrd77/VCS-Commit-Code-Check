package com.atzuche.order.admin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.commons.ResponseCheckUtil;
import com.atzuche.order.commons.entity.dto.ExtraDriverDTO;
import com.atzuche.order.commons.vo.rentercost.GetReturnAndOverFeeDetailVO;
import com.atzuche.order.commons.vo.rentercost.GetReturnAndOverFeeVO;
import com.atzuche.order.commons.vo.rentercost.RenterAndConsoleSubsidyVO;
import com.atzuche.order.commons.vo.rentercost.RenterOrderCostDetailEntity;
import com.atzuche.order.open.service.FeignOrderCostService;
import com.autoyol.commons.web.ResponseData;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrderCostRemoteService {
	
	@Autowired
	private FeignOrderCostService feignOrderCostService;

	/**
	 * 获取取还车费用和超运能费用
	 * @param req
	 */
	public GetReturnAndOverFeeDetailVO getGetReturnFeeDetail(GetReturnAndOverFeeVO req) {
        ResponseData<?> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "CoreAPI服务");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"OrderCostRemoteService.getGetReturnFeeDetail");
            log.info("Feign 获取取还车费用和超运能费用,param={}", JSON.toJSONString(req));
            Cat.logEvent(CatConstants.FEIGN_PARAM,JSON.toJSONString(req));
            responseObject = feignOrderCostService.getGetReturnFeeDetail(req);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            ResponseCheckUtil.checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
        }catch (Exception e){
            log.error("Feign 获取取还车费用和超运能费用,responseObject={},modifyOrderReq={}",JSON.toJSONString(responseObject),JSON.toJSONString(req),e);
            Cat.logError("Feign 获取取还车费用和超运能费用",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }
        return (GetReturnAndOverFeeDetailVO) responseObject.getData();
    }
	
	
	/**
	 * 获取附加驾驶员保障费
	 * @param req
	 */
	public RenterOrderCostDetailEntity getExtraDriverInsureDetail(ExtraDriverDTO req) {
        ResponseData<RenterOrderCostDetailEntity> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "CoreAPI服务");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"OrderCostRemoteService.getExtraDriverInsureDetail");
            log.info("Feign 获取附加驾驶员保障费,param={}", JSON.toJSONString(req));
            Cat.logEvent(CatConstants.FEIGN_PARAM,JSON.toJSONString(req));
            responseObject = feignOrderCostService.getExtraDriverInsureDetail(req);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            ResponseCheckUtil.checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
        }catch (Exception e){
            log.error("Feign 获取附加驾驶员保障费,responseObject={},modifyOrderReq={}",JSON.toJSONString(responseObject),JSON.toJSONString(req),e);
            Cat.logError("Feign 获取附加驾驶员保障费",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }
        return responseObject.getData();
    }
	
	
	/**
	 * 获取租客补贴
	 * @param req
	 */
	public RenterAndConsoleSubsidyVO getRenterAndConsoleSubsidyVO(String orderNo, String renterOrderNo) {
        ResponseData<RenterAndConsoleSubsidyVO> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "CoreAPI服务");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"OrderCostRemoteService.getRenterAndConsoleSubsidyVO");
            log.info("Feign 获取租客补贴,orderNo=[{}],renterOrderNo=[{}]", orderNo, renterOrderNo);
            Cat.logEvent(CatConstants.FEIGN_PARAM,"orderNo="+orderNo+"&renterOrderNo="+renterOrderNo);
            responseObject = feignOrderCostService.getRenterAndConsoleSubsidyVO(orderNo, renterOrderNo);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            ResponseCheckUtil.checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
        }catch (Exception e){
            log.error("Feign 获取租客补贴,responseObject={}",JSON.toJSONString(responseObject),e);
            Cat.logError("Feign 获取租客补贴",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }
        return responseObject.getData();
    }
	
}
