package com.atzuche.order.admin.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.admin.exception.OrderCancelErrException;
import com.atzuche.order.admin.exception.OrderCancelFailException;
import com.atzuche.order.admin.exception.OrderModifyErrException;
import com.atzuche.order.admin.exception.OrderModifyFailException;
import com.atzuche.order.admin.vo.req.order.CancelOrderVO;
import com.atzuche.order.car.RenterCarDetailFailException;
import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.commons.vo.req.CancelOrderReqVO;
import com.atzuche.order.commons.vo.req.ModifyOrderReqVO;
import com.atzuche.order.open.service.FeignOrderModifyService;
import com.atzuche.order.open.service.FeignOrderUpdateService;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AdminOrderService {
    @Autowired
    private FeignOrderUpdateService feignOrderUpdateService;
    @Autowired
    private FeignOrderModifyService feignOrderModifyService;

    public ResponseData cancelOrder(CancelOrderVO cancelOrderVO) {
        CancelOrderReqVO cancelOrderReqVO = new CancelOrderReqVO();
        BeanUtils.copyProperties(cancelOrderVO,cancelOrderReqVO);
        ResponseData<?> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "租客商品信息");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"feignOrderUpdateService.cancelOrder");
            log.info("Feign 开始取消订单,cancelOrderReqVO={}", JSON.toJSONString(cancelOrderReqVO));
            Cat.logEvent(CatConstants.FEIGN_PARAM,JSON.toJSONString(cancelOrderReqVO));
            responseObject = feignOrderUpdateService.cancelOrder(cancelOrderReqVO);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            if(responseObject == null || !ErrorCode.SUCCESS.getCode().equals(responseObject.getResCode())){
                log.error("Feign 取消订单失败,responseObject={},cancelOrderReqVO={}",JSON.toJSONString(responseObject),JSON.toJSONString(cancelOrderReqVO));
                OrderCancelFailException failException = new OrderCancelFailException();
                Cat.logError("Feign 取消订单失败",failException);
                throw failException;
            }
            t.setStatus(Transaction.SUCCESS);
        }catch (RenterCarDetailFailException e){
            Cat.logError("Feign 取消订单失败",e);
            t.setStatus(e);
            throw e;
        }catch (Exception e){
           log.error("Feign 取消订单异常,responseObject={},cancelOrderReqVO={}",JSON.toJSONString(responseObject),JSON.toJSONString(cancelOrderReqVO),e);
            OrderCancelErrException err = new OrderCancelErrException();
            Cat.logError("Feign 取消订单异常",err);
            throw err;
        }finally {
            t.complete();
        }
        return responseObject;
    }
    public ResponseData modifyOrder(ModifyOrderReqVO modifyOrderReq) {
        ResponseData<?> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "租客商品信息");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"feignOrderUpdateService.cancelOrder");
            log.info("Feign 开始修改订单,modifyOrderReq={}", JSON.toJSONString(modifyOrderReq));
            Cat.logEvent(CatConstants.FEIGN_PARAM,JSON.toJSONString(modifyOrderReq));
            responseObject = feignOrderModifyService.modifyOrderForConsole(modifyOrderReq);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            if(responseObject == null || !ErrorCode.SUCCESS.getCode().equals(responseObject.getResCode())){
                log.error("Feign 修改订单失败,responseObject={},modifyOrderReq={}",JSON.toJSONString(responseObject),JSON.toJSONString(modifyOrderReq));
                OrderModifyFailException failException = new OrderModifyFailException();
                Cat.logError("Feign 修改订单失败",failException);
                throw failException;
            }
            t.setStatus(Transaction.SUCCESS);
        }catch (RenterCarDetailFailException e){
            Cat.logError("Feign 修改订单失败",e);
            t.setStatus(e);
            throw e;
        }catch (Exception e){
            log.error("Feign 修改订单异常,responseObject={},modifyOrderReq={}",JSON.toJSONString(responseObject),JSON.toJSONString(modifyOrderReq),e);
            OrderModifyErrException err = new OrderModifyErrException();
            Cat.logError("Feign 修改订单异常",err);
            throw err;
        }finally {
            t.complete();
        }
        return responseObject;
    }
}
