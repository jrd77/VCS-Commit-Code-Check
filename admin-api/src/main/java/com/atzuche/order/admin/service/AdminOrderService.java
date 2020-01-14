package com.atzuche.order.admin.service;

import com.atzuche.order.admin.vo.req.order.CancelOrderVO;
import com.atzuche.order.commons.vo.req.CancelOrderReqVO;
import com.atzuche.order.open.service.FeignOrderUpdateService;
import com.autoyol.commons.web.ResponseData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AdminOrderService {
    @Autowired
    private FeignOrderUpdateService feignOrderUpdateService;

    public ResponseData cancelOrder(CancelOrderVO cancelOrderVO) {
        CancelOrderReqVO cancelOrderReqVO = new CancelOrderReqVO();
        cancelOrderReqVO.setCancelReason(cancelOrderVO.getCancelReason());
        cancelOrderReqVO.setMemRole(cancelOrderVO.getCancelSrcCode());
        cancelOrderReqVO.setOrderNo(cancelOrderVO.getOrderNo());

        /*ResponseData<?> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "租客商品信息");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"feignOrderUpdateService.cancelOrder");
            log.info("Feign 开始取消订单,cancelOrderReqVO={}", JSON.toJSONString(cancelOrderReqVO));
            Cat.logEvent(CatConstants.FEIGN_PARAM,JSON.toJSONString(cancelOrderReqVO));
            responseObject = feignOrderUpdateService.cancelOrder(cancelOrderReqVO);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            if(responseObject == null || !ErrorCode.SUCCESS.getCode().equals(responseObject.getResCode())){
                log.error("Feign 取消订单失败,responseObject={},orderCarInfoParamDTO={}",JSON.toJSONString(responseObject),JSON.toJSONString(orderCarInfoParamDTO));
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
           log.error("Feign 取消订单异常,responseObject={},orderCarInfoParamDTO={}",JSON.toJSONString(responseObject),JSON.toJSONString(orderCarInfoParamDTO),e);
            OrderCancelErrException err = new OrderCancelErrException();
            Cat.logError("Feign 取消订单异常",err);
            throw err;
        }finally {
            t.complete();
        }*/

        return null;
    }
}
