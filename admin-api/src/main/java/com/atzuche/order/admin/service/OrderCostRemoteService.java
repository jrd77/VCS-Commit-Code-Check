package com.atzuche.order.admin.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.commons.ResponseCheckUtil;
import com.atzuche.order.commons.entity.dto.ExtraDriverDTO;
import com.atzuche.order.commons.entity.ownerOrderDetail.RenterRentDetailDTO;
import com.atzuche.order.commons.vo.rentercost.GetReturnAndOverFeeDetailVO;
import com.atzuche.order.commons.vo.rentercost.GetReturnAndOverFeeVO;
import com.atzuche.order.commons.vo.rentercost.OwnerToPlatformCostReqVO;
import com.atzuche.order.commons.vo.rentercost.OwnerToRenterSubsidyReqVO;
import com.atzuche.order.commons.vo.rentercost.PlatformToOwnerSubsidyReqVO;
import com.atzuche.order.commons.vo.rentercost.PlatformToRenterSubsidyReqVO;
import com.atzuche.order.commons.vo.rentercost.RenterAndConsoleFineVO;
import com.atzuche.order.commons.vo.rentercost.RenterAndConsoleSubsidyVO;
import com.atzuche.order.commons.vo.rentercost.RenterCostReqVO;
import com.atzuche.order.commons.vo.rentercost.RenterFineCostReqVO;
import com.atzuche.order.commons.vo.rentercost.RenterOrderCostDetailEntity;
import com.atzuche.order.commons.vo.rentercost.RenterToPlatformCostReqVO;
import com.atzuche.order.commons.vo.req.RenterAdjustCostReqVO;
import com.atzuche.order.commons.vo.res.rentcosts.OrderConsoleCostDetailEntity;
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
            log.error("Feign 获取取还车费用和超运能费用,responseObject={},req={}",JSON.toJSONString(responseObject),JSON.toJSONString(req),e);
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
            log.error("Feign 获取附加驾驶员保障费,responseObject={},req={}",JSON.toJSONString(responseObject),JSON.toJSONString(req),e);
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
	
	
	/**
	 * 获取管理后台费用
	 * @param req
	 */
	public List<OrderConsoleCostDetailEntity> listOrderConsoleCostDetailEntity(String orderNo) {
        ResponseData<List<OrderConsoleCostDetailEntity>> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "CoreAPI服务");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"OrderCostRemoteService.listOrderConsoleCostDetailEntity");
            log.info("Feign 获取管理后台费用,orderNo=[{}]", orderNo);
            Cat.logEvent(CatConstants.FEIGN_PARAM,"orderNo="+orderNo);
            responseObject = feignOrderCostService.listOrderConsoleCostDetailEntity(orderNo);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            ResponseCheckUtil.checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
        }catch (Exception e){
            log.error("Feign 获取管理后台费用,responseObject={}",JSON.toJSONString(responseObject),e);
            Cat.logError("Feign 获取管理后台费用",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }
        return responseObject.getData();
    }
	
	
	/**
	 * 保存调价
	 * @param req
	 */
	public void updateRenterPriceAdjustmentByOrderNo(RenterAdjustCostReqVO req) {
        ResponseData<?> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "CoreAPI服务");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"OrderCostRemoteService.updateRenterPriceAdjustmentByOrderNo");
            log.info("Feign 保存调价,param={}", JSON.toJSONString(req));
            Cat.logEvent(CatConstants.FEIGN_PARAM,JSON.toJSONString(req));
            responseObject = feignOrderCostService.updateRenterPriceAdjustmentByOrderNo(req);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            ResponseCheckUtil.checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
        }catch (Exception e){
            log.error("Feign 保存调价,responseObject={},req={}",JSON.toJSONString(responseObject),JSON.toJSONString(req),e);
            Cat.logError("Feign 保存调价",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }
    }
	
	
	/**
	 * 修改租客需支付给平台的费用
	 * @param req
	 */
	public void updateRenterToPlatFormListByOrderNo(RenterToPlatformCostReqVO req) {
        ResponseData<?> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "CoreAPI服务");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"OrderCostRemoteService.updateRenterToPlatFormListByOrderNo");
            log.info("Feign 修改租客需支付给平台的费用,param={}", JSON.toJSONString(req));
            Cat.logEvent(CatConstants.FEIGN_PARAM,JSON.toJSONString(req));
            responseObject = feignOrderCostService.updateRenterToPlatFormListByOrderNo(req);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            ResponseCheckUtil.checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
        }catch (Exception e){
            log.error("Feign 修改租客需支付给平台的费用,responseObject={},req={}",JSON.toJSONString(responseObject),JSON.toJSONString(req),e);
            Cat.logError("Feign 修改租客需支付给平台的费用",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }
    }
	
	
	/**
	 * 添加，车主需支付给平台的费用
	 * @param req
	 */
	public void updateOwnerToPlatFormListByOrderNo(OwnerToPlatformCostReqVO req) {
        ResponseData<?> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "CoreAPI服务");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"OrderCostRemoteService.updateOwnerToPlatFormListByOrderNo");
            log.info("Feign 添加，车主需支付给平台的费用,param={}", JSON.toJSONString(req));
            Cat.logEvent(CatConstants.FEIGN_PARAM,JSON.toJSONString(req));
            responseObject = feignOrderCostService.updateOwnerToPlatFormListByOrderNo(req);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            ResponseCheckUtil.checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
        }catch (Exception e){
            log.error("Feign 添加，车主需支付给平台的费用,responseObject={},req={}",JSON.toJSONString(responseObject),JSON.toJSONString(req),e);
            Cat.logError("Feign 添加，车主需支付给平台的费用",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }
    }
	
	
	/**
	 * 租客租金明细
	 * @param req
	 */
	public RenterRentDetailDTO findRenterRentAmtListByOrderNo(RenterCostReqVO req) {
        ResponseData<RenterRentDetailDTO> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "CoreAPI服务");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"OrderCostRemoteService.findRenterRentAmtListByOrderNo");
            log.info("Feign 获取租客租金明细,param={}", JSON.toJSONString(req));
            Cat.logEvent(CatConstants.FEIGN_PARAM,JSON.toJSONString(req));
            responseObject = feignOrderCostService.findRenterRentAmtListByOrderNo(req);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            ResponseCheckUtil.checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
        }catch (Exception e){
            log.error("Feign 获取租客租金明细,responseObject={},req={}",JSON.toJSONString(responseObject),JSON.toJSONString(req),e);
            Cat.logError("Feign 获取租客租金明细",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }
        return responseObject.getData();
    }
	
	
	/**
	 * 获取租客罚金
	 * @param req
	 */
	public RenterAndConsoleFineVO getRenterAndConsoleFineVO(String orderNo, String renterOrderNo) {
        ResponseData<RenterAndConsoleFineVO> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "CoreAPI服务");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"OrderCostRemoteService.getRenterAndConsoleFineVO");
            log.info("Feign 获取租客罚金,orderNo=[{}],renterOrderNo=[{}]", orderNo, renterOrderNo);
            Cat.logEvent(CatConstants.FEIGN_PARAM,"orderNo="+orderNo+"&renterOrderNo="+renterOrderNo);
            responseObject = feignOrderCostService.getRenterAndConsoleFineVO(orderNo, renterOrderNo);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            ResponseCheckUtil.checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
        }catch (Exception e){
            log.error("Feign 获取租客罚金,responseObject={}",JSON.toJSONString(responseObject),e);
            Cat.logError("Feign 获取租客罚金",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }
        return responseObject.getData();
    }
	
	
	/**
	 * 违约罚金 修改违约罚金
	 * @param req
	 */
	public void updatefineAmtListByOrderNo(RenterFineCostReqVO req) {
        ResponseData<?> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "CoreAPI服务");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"OrderCostRemoteService.updatefineAmtListByOrderNo");
            log.info("Feign 违约罚金 修改违约罚金,param={}", JSON.toJSONString(req));
            Cat.logEvent(CatConstants.FEIGN_PARAM,JSON.toJSONString(req));
            responseObject = feignOrderCostService.updatefineAmtListByOrderNo(req);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            ResponseCheckUtil.checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
        }catch (Exception e){
            log.error("Feign 违约罚金 修改违约罚金,responseObject={},req={}",JSON.toJSONString(responseObject),JSON.toJSONString(req),e);
            Cat.logError("Feign 违约罚金 修改违约罚金",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }
    }
	
	
	/**
	 * 平台给租客的补贴
	 * @param req
	 */
	public void updatePlatFormToRenterListByOrderNo(PlatformToRenterSubsidyReqVO req) {
        ResponseData<?> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "CoreAPI服务");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"OrderCostRemoteService.updatePlatFormToRenterListByOrderNo");
            log.info("Feign 平台给租客的补贴,param={}", JSON.toJSONString(req));
            Cat.logEvent(CatConstants.FEIGN_PARAM,JSON.toJSONString(req));
            responseObject = feignOrderCostService.updatePlatFormToRenterListByOrderNo(req);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            ResponseCheckUtil.checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
        }catch (Exception e){
            log.error("Feign 平台给租客的补贴,responseObject={},req={}",JSON.toJSONString(responseObject),JSON.toJSONString(req),e);
            Cat.logError("Feign 平台给租客的补贴",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }
    }
	
	
	/**
	 * 租金补贴
	 * @param req
	 */
	public void ownerToRenterRentAmtSubsidy(OwnerToRenterSubsidyReqVO req) {
        ResponseData<?> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "CoreAPI服务");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"OrderCostRemoteService.ownerToRenterRentAmtSubsidy");
            log.info("Feign 租金补贴,param={}", JSON.toJSONString(req));
            Cat.logEvent(CatConstants.FEIGN_PARAM,JSON.toJSONString(req));
            responseObject = feignOrderCostService.ownerToRenterRentAmtSubsidy(req);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            ResponseCheckUtil.checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
        }catch (Exception e){
            log.error("Feign 租金补贴,responseObject={},req={}",JSON.toJSONString(responseObject),JSON.toJSONString(req),e);
            Cat.logError("Feign 租金补贴",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }
    }
	
	
	/**
	 * 平台给车主的补贴
	 * @param req
	 */
	public void updatePlatFormToOwnerListByOrderNo(PlatformToOwnerSubsidyReqVO req) {
        ResponseData<?> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "CoreAPI服务");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"OrderCostRemoteService.updatePlatFormToOwnerListByOrderNo");
            log.info("Feign 平台给车主的补贴,param={}", JSON.toJSONString(req));
            Cat.logEvent(CatConstants.FEIGN_PARAM,JSON.toJSONString(req));
            responseObject = feignOrderCostService.updatePlatFormToOwnerListByOrderNo(req);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            ResponseCheckUtil.checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
        }catch (Exception e){
            log.error("Feign 平台给车主的补贴,responseObject={},req={}",JSON.toJSONString(responseObject),JSON.toJSONString(req),e);
            Cat.logError("Feign 平台给车主的补贴",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }
    }
	
}
