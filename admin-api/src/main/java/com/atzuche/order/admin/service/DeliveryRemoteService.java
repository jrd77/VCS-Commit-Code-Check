package com.atzuche.order.admin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.commons.ResponseCheckUtil;
import com.atzuche.order.commons.vo.delivery.DeliveryCarRepVO;
import com.atzuche.order.commons.vo.delivery.DeliveryCarVO;
import com.atzuche.order.commons.vo.delivery.DeliveryReqVO;
import com.atzuche.order.commons.vo.delivery.DistributionCostVO;
import com.atzuche.order.commons.vo.delivery.OrderCarTrusteeshipEntity;
import com.atzuche.order.commons.vo.delivery.OrderCarTrusteeshipReqVO;
import com.atzuche.order.commons.vo.delivery.OrderCarTrusteeshipVO;
import com.atzuche.order.commons.vo.delivery.SimpleOrderInfoVO;
import com.atzuche.order.open.service.FeignDeliveryCarInfoService;
import com.autoyol.commons.web.ResponseData;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DeliveryRemoteService {
	
	@Autowired
	private FeignDeliveryCarInfoService feignDeliveryCarInfoService;
	
	/**
	 * 获取配送相关信息
	 * @param deliveryCarDTO
	 */
	public DeliveryCarVO getDeliveryCarVO(DeliveryCarRepVO req) {
        ResponseData<DeliveryCarVO> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "CoreAPI服务");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"DeliveryRemoteService.getDeliveryCarVO");
            log.info("Feign 获取配送相关信息,param={}", JSON.toJSONString(req));
            Cat.logEvent(CatConstants.FEIGN_PARAM,JSON.toJSONString(req));
            responseObject = feignDeliveryCarInfoService.getDeliveryCarVO(req);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            ResponseCheckUtil.checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
        }catch (Exception e){
            log.error("Feign 获取配送相关信息,responseObject={},modifyOrderReq={}",JSON.toJSONString(responseObject),JSON.toJSONString(req),e);
            Cat.logError("Feign 获取配送相关信息",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }
        return responseObject.getData();
    }

	/**
	 * 更新交接车信息
	 * @param req
	 */
	public void updateHandoverCarInfo(DeliveryCarVO req) {
        ResponseData<?> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "CoreAPI服务");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"DeliveryRemoteService.updateHandoverCarInfo");
            log.info("Feign 更新交接车信息,param={}", JSON.toJSONString(req));
            Cat.logEvent(CatConstants.FEIGN_PARAM,JSON.toJSONString(req));
            responseObject = feignDeliveryCarInfoService.updateHandoverCarInfo(req);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            ResponseCheckUtil.checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
        }catch (Exception e){
            log.error("Feign 更新交接车信息,responseObject={},modifyOrderReq={}",JSON.toJSONString(responseObject),JSON.toJSONString(req),e);
            Cat.logError("Feign 更新交接车信息",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }
    }
	
	
	/**
	 * 更新取还车备注信息
	 * @param req
	 */
	public void updateDeliveryRemark(DeliveryReqVO req) {
        ResponseData<?> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "CoreAPI服务");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"DeliveryRemoteService.updateDeliveryRemark");
            log.info("Feign 更新取还车备注信息,param={}", JSON.toJSONString(req));
            Cat.logEvent(CatConstants.FEIGN_PARAM,JSON.toJSONString(req));
            responseObject = feignDeliveryCarInfoService.updateDeliveryRemark(req);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            ResponseCheckUtil.checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
        }catch (Exception e){
            log.error("Feign 更新取还车备注信息,responseObject={},modifyOrderReq={}",JSON.toJSONString(responseObject),JSON.toJSONString(req),e);
            Cat.logError("Feign 更新取还车备注信息",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }
    }
	
	
	/**
	 * 获取配送相关信息
	 * @param deliveryCarDTO
	 */
	public DistributionCostVO getDistributionCostVO(DeliveryCarRepVO req) {
        ResponseData<DistributionCostVO> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "CoreAPI服务");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"DeliveryRemoteService.getDistributionCostVO");
            log.info("Feign 获取配送相关信息,param={}", JSON.toJSONString(req));
            Cat.logEvent(CatConstants.FEIGN_PARAM,JSON.toJSONString(req));
            responseObject = feignDeliveryCarInfoService.getDistributionCostVO(req);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            ResponseCheckUtil.checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
        }catch (Exception e){
            log.error("Feign 获取配送相关信息,responseObject={},modifyOrderReq={}",JSON.toJSONString(responseObject),JSON.toJSONString(req),e);
            Cat.logError("Feign 获取配送相关信息",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }
        return responseObject.getData();
    }
	
	
	/**
	 * 获取订单简单信息
	 * @param orderNo
	 */
	public SimpleOrderInfoVO getSimpleOrderInfoVO(String orderNo) {
        ResponseData<SimpleOrderInfoVO> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "CoreAPI服务");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"DeliveryRemoteService.getSimpleOrderInfoVO");
            log.info("Feign 获取订单简单信息,orderNo={}", orderNo);
            Cat.logEvent(CatConstants.FEIGN_PARAM,orderNo);
            responseObject = feignDeliveryCarInfoService.getSimpleOrderInfoVO(orderNo);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            ResponseCheckUtil.checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
        }catch (Exception e){
            log.error("Feign 获取订单简单信息,responseObject={},modifyOrderReq={}",JSON.toJSONString(responseObject),orderNo,e);
            Cat.logError("Feign 获取订单简单信息",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }
        return responseObject.getData();
    }
	
	
	/**
	 * 托管车新增
	 * @param req
	 */
	public void addOrderCarTrusteeship(OrderCarTrusteeshipVO req) {
        ResponseData<?> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "CoreAPI服务");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"DeliveryRemoteService.addOrderCarTrusteeship");
            log.info("Feign 托管车新增,param={}", JSON.toJSONString(req));
            Cat.logEvent(CatConstants.FEIGN_PARAM,JSON.toJSONString(req));
            responseObject = feignDeliveryCarInfoService.addOrderCarTrusteeship(req);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            ResponseCheckUtil.checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
        }catch (Exception e){
            log.error("Feign 托管车新增,responseObject={},modifyOrderReq={}",JSON.toJSONString(responseObject),JSON.toJSONString(req),e);
            Cat.logError("Feign 托管车新增",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }
    }
	
	
	/**
	 * 获取托管车信息
	 * @param req
	 */
	public OrderCarTrusteeshipEntity getOrderCarTrusteeshipEntity(OrderCarTrusteeshipReqVO req) {
        ResponseData<OrderCarTrusteeshipEntity> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "CoreAPI服务");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"DeliveryRemoteService.getOrderCarTrusteeshipEntity");
            log.info("Feign 获取托管车信息,param={}", JSON.toJSONString(req));
            Cat.logEvent(CatConstants.FEIGN_PARAM,JSON.toJSONString(req));
            responseObject = feignDeliveryCarInfoService.getOrderCarTrusteeshipEntity(req);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            ResponseCheckUtil.checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
        }catch (Exception e){
            log.error("Feign 获取托管车信息,responseObject={},modifyOrderReq={}",JSON.toJSONString(responseObject),JSON.toJSONString(req),e);
            Cat.logError("Feign 获取托管车信息",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }
        return responseObject.getData();
    }
	
	
}
