package com.atzuche.order.admin.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.admin.dto.AdminOwnerOrderDetailDTO;
import com.atzuche.order.admin.exception.OrderHistoryErrException;
import com.atzuche.order.admin.exception.OrderHistoryFailException;
import com.atzuche.order.car.RenterCarDetailFailException;
import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.commons.entity.orderDetailDto.OrderHistoryReqDTO;
import com.atzuche.order.commons.entity.orderDetailDto.OrderHistoryRespDTO;
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
        }catch (RenterCarDetailFailException e){
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

    public ResponseData<AdminOwnerOrderDetailDTO> ownerOrderDetail(String ownerOrderNo) {

        return null;
    }
}
