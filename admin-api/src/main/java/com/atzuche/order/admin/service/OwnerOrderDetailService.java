package com.atzuche.order.admin.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.admin.exception.*;
import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.commons.ResponseCheckUtil;
import com.atzuche.order.commons.entity.ownerOrderDetail.*;
import com.atzuche.order.open.service.FeignOwnerDetailService;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OwnerOrderDetailService {
    @Autowired
    private FeignOwnerDetailService feignOwnerDetailService;

    public ResponseData<OwnerRentDetailDTO> ownerRentDetail(String orderNo, String ownerOrderNo) {
        ResponseData<OwnerRentDetailDTO> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "获取车主租客调价明细");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"feignOrderUpdateService.cancelOrder");
            log.info("Feign 开始获取租金明细,orderNo={}", orderNo);
            Cat.logEvent(CatConstants.FEIGN_PARAM,orderNo);
            responseObject =  feignOwnerDetailService.ownerRentDetail(orderNo,ownerOrderNo);
            Cat.logEvent(CatConstants.FEIGN_RESULT,orderNo);
            ResponseCheckUtil.checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
        }catch (Exception e){
            log.error("Feign 获取租金明细异常,responseObject={},orderNo={}", JSON.toJSONString(responseObject),orderNo,e);
            Cat.logError("Feign 获取租金明细异常",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }
        return responseObject;
    }
    
    /**
     * 接口暂时不用，使用同租客的一套。
     * @param orderNo
     * @param ownerOrderNo
     * @return
     */
    public ResponseData<RenterOwnerPriceDTO> renterOwnerPrice(String orderNo, String ownerOrderNo) {
        ResponseData<RenterOwnerPriceDTO> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "获取车主租客调价明细");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"feignOrderUpdateService.cancelOrder");
            log.info("Feign 开始获取车主租客调价明细,orderNo={}", orderNo);
            Cat.logEvent(CatConstants.FEIGN_PARAM,orderNo);
            responseObject =  feignOwnerDetailService.renterOwnerPrice(orderNo,ownerOrderNo);
            Cat.logEvent(CatConstants.FEIGN_RESULT,orderNo);
            ResponseCheckUtil.checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
        }catch (Exception e){
            log.error("Feign 获取车主租客调价明细异常,responseObject={},orderNo={}", JSON.toJSONString(responseObject),orderNo,e);
            Cat.logError("Feign 获取车主租客调价明细异常",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }
        return responseObject;
    }

    public ResponseData<FienAmtDetailDTO> fienAmtDetail(String orderNo, String ownerOrderNo) {
        ResponseData<FienAmtDetailDTO> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "获取服务费明细");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"feignOrderUpdateService.cancelOrder");
            log.info("Feign 开始获取服务费明细,orderNo={}", orderNo);
            Cat.logEvent(CatConstants.FEIGN_PARAM,orderNo);
            responseObject =  feignOwnerDetailService.fienAmtDetail(orderNo,ownerOrderNo);
            Cat.logEvent(CatConstants.FEIGN_RESULT,orderNo);
            ResponseCheckUtil.checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
        }catch (Exception e){
            log.error("Feign 获取服务费明细异常,responseObject={},orderNo={}", JSON.toJSONString(responseObject),orderNo,e);
            Cat.logError("Feign 获取服务费明细异常",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }
        return responseObject;
    }

    public ResponseData<ServiceDetailDTO> serviceDetail(String orderNo, String ownerOrderNo) {
        ResponseData<ServiceDetailDTO> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "获取服务费明细");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"feignOrderUpdateService.cancelOrder");
            log.info("Feign 开始获取服务费明细,orderNo={}", orderNo);
            Cat.logEvent(CatConstants.FEIGN_PARAM,orderNo);
            responseObject =  feignOwnerDetailService.serviceDetail(orderNo,ownerOrderNo);
            Cat.logEvent(CatConstants.FEIGN_RESULT,orderNo);
            ResponseCheckUtil.checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
        }catch (Exception e){
            log.error("Feign 获取服务费明细异常,responseObject={},orderNo={}", JSON.toJSONString(responseObject),orderNo,e);
            Cat.logError("Feign 获取服务费明细异常",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }
        return responseObject;
    }

    public ResponseData<PlatformToOwnerDTO> platformToOwner(String orderNo, String ownerOrderNo) {
        ResponseData<PlatformToOwnerDTO> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "获取车主付给平台的费用明细");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"feignOrderUpdateService.cancelOrder");
            log.info("Feign 开始获取车主付给平台的费用明细,orderNo={}", orderNo);
            Cat.logEvent(CatConstants.FEIGN_PARAM,orderNo);
            responseObject =  feignOwnerDetailService.platformToOwner(orderNo,ownerOrderNo);
            Cat.logEvent(CatConstants.FEIGN_RESULT,orderNo);
            ResponseCheckUtil.checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
        }catch (Exception e){
            log.error("Feign 获取车主付给平台的费用明细异常,responseObject={},orderNo={}", JSON.toJSONString(responseObject),orderNo,e);
            Cat.logError("Feign 获取车主付给平台的费用明细异常",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }
        return responseObject;
    }

    public ResponseData<PlatformToOwnerSubsidyDTO> platformToOwnerSubsidy(String orderNo, String ownerOrderNo) {
        ResponseData<PlatformToOwnerSubsidyDTO> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "获取平台给车主的补贴明细");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"feignOrderUpdateService.cancelOrder");
            log.info("Feign 开始获取平台给车主的补贴明细,orderNo={}", orderNo);
            Cat.logEvent(CatConstants.FEIGN_PARAM,orderNo);
            responseObject =  feignOwnerDetailService.platformToOwnerSubsidy(orderNo,ownerOrderNo);
            Cat.logEvent(CatConstants.FEIGN_RESULT,orderNo);
            ResponseCheckUtil.checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
        }catch (Exception e){
            log.error("Feign 获取平台给车主的补贴明细异常,responseObject={},orderNo={}", JSON.toJSONString(responseObject),orderNo,e);
            Cat.logError("Feign 获取平台给车主的补贴明细异常",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }
        return responseObject;
    }

    public ResponseData<?> updateFineAmt(FienAmtUpdateReqDTO fienAmtUpdateReqDTO) {
        ResponseData<?> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "获取修改罚金");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"feignOrderUpdateService.cancelOrder");
            log.info("Feign 开始获取修改罚金,fienAmtUpdateReqDTO={}", JSON.toJSONString(fienAmtUpdateReqDTO));
            Cat.logEvent(CatConstants.FEIGN_PARAM,JSON.toJSONString(fienAmtUpdateReqDTO));
            responseObject =  feignOwnerDetailService.updateFineAmt(fienAmtUpdateReqDTO);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(fienAmtUpdateReqDTO));
            ResponseCheckUtil.checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
        }catch (Exception e){
            log.error("Feign 获取修改罚金异常,responseObject={},fienAmtUpdateReqDTO={}", JSON.toJSONString(responseObject),JSON.toJSONString(fienAmtUpdateReqDTO),e);
            Cat.logError("Feign 获取修改罚金异常",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }
        return responseObject;
    }
}
