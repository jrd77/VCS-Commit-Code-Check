package com.atzuche.order.admin.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.admin.exception.OrderHistoryErrException;
import com.atzuche.order.admin.exception.OrderHistoryFailException;
import com.atzuche.order.admin.exception.OrderRHistoryErrException;
import com.atzuche.order.admin.exception.OrderRHistoryFailException;
import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.commons.entity.orderDetailDto.OrderHistoryListDTO;
import com.atzuche.order.commons.entity.orderDetailDto.OrderHistoryReqDTO;
import com.atzuche.order.commons.entity.orderDetailDto.OrderHistoryRespDTO;
import com.atzuche.order.commons.entity.ownerOrderDetail.AdminOwnerOrderDetailDTO;
import com.atzuche.order.open.service.FeignOrderDetailService;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OrderDetailService {
    @Autowired
    private FeignOrderDetailService feignOrderDetailService;

    public ResponseData<OrderHistoryRespDTO> listOrderHistory(String orderNo){
        OrderHistoryReqDTO orderHistoryReqDTO = new OrderHistoryReqDTO();
        orderHistoryReqDTO.setOrderNo(orderNo);
        orderHistoryReqDTO.setIsNeedRenterOrderHistory(true);
        orderHistoryReqDTO.setIsNeedOwnerOrderHistory(true);
        ResponseData<OrderHistoryRespDTO> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "获取历史订单列表");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"feignOrderUpdateService.cancelOrder");
            log.info("Feign 开始获取历史订单列表,orderNo={}", orderNo);
            Cat.logEvent(CatConstants.FEIGN_PARAM,orderNo);
            responseObject =  feignOrderDetailService.orderHistory(orderHistoryReqDTO);
            Cat.logEvent(CatConstants.FEIGN_RESULT,orderNo);
            if(responseObject == null || !ErrorCode.SUCCESS.getCode().equals(responseObject.getResCode())){
                log.error("Feign 获取历史订单列表失败,responseObject={},orderNo={}",JSON.toJSONString(responseObject),orderNo);
                OrderHistoryFailException failException = new OrderHistoryFailException();
                Cat.logError("Feign 获取历史订单列表失败",failException);
                throw failException;
            }
            t.setStatus(Transaction.SUCCESS);
        }catch (OrderHistoryFailException e){
            Cat.logError("Feign 获取历史订单列表失败",e);
            t.setStatus(e);
            throw e;
        }catch (Exception e){
            log.error("Feign 获取历史订单列表异常,responseObject={},orderNo={}",JSON.toJSONString(responseObject),orderNo,e);
            OrderHistoryErrException err = new OrderHistoryErrException();
            Cat.logError("Feign 获取历史订单列表异常",err);
            throw err;
        }finally {
            t.complete();
        }
        return responseObject;
    }

    public ResponseData<AdminOwnerOrderDetailDTO> ownerOrderDetail(String ownerOrderNo,String orderNo) {

        return null;
    }

    public ResponseData<OrderHistoryListDTO> dispatchHistory(String orderNo) {
        ResponseData<OrderHistoryListDTO> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "获取人工调度历史订单列表");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"feignOrderUpdateService.cancelOrder");
            log.info("Feign 开始获取人工调度历史订单列表,orderNo={}", orderNo);
            Cat.logEvent(CatConstants.FEIGN_PARAM,orderNo);
            responseObject =  feignOrderDetailService.dispatchHistory(orderNo);
            Cat.logEvent(CatConstants.FEIGN_RESULT,orderNo);
            if(responseObject == null || !ErrorCode.SUCCESS.getCode().equals(responseObject.getResCode())){
                log.error("Feign 获取人工调度历史订单列表失败,responseObject={},orderNo={}",JSON.toJSONString(responseObject),orderNo);
                OrderRHistoryFailException failException = new OrderRHistoryFailException();
                Cat.logError("Feign 获取人工调度历史订单列表失败",failException);
                throw failException;
            }
            t.setStatus(Transaction.SUCCESS);
        }catch (OrderRHistoryFailException e){
            Cat.logError("Feign 获取人工调度历史订单列表失败",e);
            t.setStatus(e);
            throw e;
        }catch (Exception e){
            log.error("Feign 获取人工调度历史订单列表异常,responseObject={},orderNo={}",JSON.toJSONString(responseObject),orderNo,e);
            OrderRHistoryErrException err = new OrderRHistoryErrException();
            Cat.logError("Feign 获取人工调度历史订单列表异常",err);
            throw err;
        }finally {
            t.complete();
        }
        return responseObject;
    }
}
