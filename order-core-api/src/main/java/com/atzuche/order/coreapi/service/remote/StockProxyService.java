package com.atzuche.order.coreapi.service.remote;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.commons.exceptions.RemoteCallException;
import com.atzuche.order.coreapi.submit.exception.*;
import com.autoyol.car.api.CarRentalTimeApi;
import com.autoyol.car.api.model.dto.OrderInfoDTO;
import com.autoyol.car.api.model.dto.OwnerCancelDTO;
import com.autoyol.car.api.model.vo.ResponseObject;
import com.autoyol.commons.web.ErrorCode;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/*
 * @Author ZhangBin
 * @Date 2020/1/8 17:30
 * @Description: 库存
 *
 **/
@Service
@Slf4j
public class StockProxyService {
    @Autowired
    private CarRentalTimeApi carRentalTimeApi;
    
    /**
     * @Author ZhangBin
     * @Date 2020/1/8 17:34
     * @Description: 库存校验
     * 
     **/
    public void checkCarStock(OrderInfoDTO orderInfoDTO){
        ResponseObject<Boolean> responseObject  = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "库存校验");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"CarRentalTimeApi.checkCarStock");
            log.info("Feign 开始校验库存信息,orderInfoDTO={}", JSON.toJSONString(orderInfoDTO));
            Cat.logEvent(CatConstants.FEIGN_PARAM,JSON.toJSONString(orderInfoDTO));
            responseObject = carRentalTimeApi.checkCarStock(orderInfoDTO);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
        }catch (Exception e){
            log.error("Feign 校验库存信息异常,responseObject={},orderInfoDTO={}",JSON.toJSONString(responseObject),JSON.toJSONString(orderInfoDTO),e);
            Cat.logError("Feign 校验库存信息异常",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }
    }
    
    /**
     * @Author ZhangBin
     * @Date 2020/1/8 17:34
     * @Description: 扣库存
     * 
     **/
    public void cutCarStock(OrderInfoDTO orderInfoDTO){
        ResponseObject<Boolean> responseObject  = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "库存扣减");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"carRentalTimeApi.cutCarStock");
            log.info("Feign 开始扣减库存信息,orderInfoDTO={}", JSON.toJSONString(orderInfoDTO));
            Cat.logEvent(CatConstants.FEIGN_PARAM,JSON.toJSONString(orderInfoDTO));
            responseObject = carRentalTimeApi.cutCarStock(orderInfoDTO);
            log.info("Fegin 开始扣减库存信息,返回结果:[{}]",JSON.toJSONString(responseObject));
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
        }catch (Exception e){
            log.error("Feign 扣减库存信息异常,responseObject={},orderInfoDTO={}",JSON.toJSONString(responseObject),JSON.toJSONString(orderInfoDTO),e);
            Cat.logError("Feign 扣减库存信息异常",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }

    }

    /**
     * @Author ZhangBin
     * @Date 2020/1/8 17:35
     * @Description: 减库存
     *
     **/
    public void releaseCarStock(String orderNo,Integer carNo){
        ResponseObject<Boolean> responseObject  = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "库存释放");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"CarRentalTimeApi.checkCarStock");
            log.info("Feign 开始释放库存信息,orderNo={}，carNo={}", orderNo,carNo);
            Cat.logEvent(CatConstants.FEIGN_PARAM,"orderNo="+orderNo+"&carNo="+carNo);
            responseObject = carRentalTimeApi.releaseCarStock(orderNo,carNo);
            log.info("Fegin 开始库存释放信息,返回结果:[{}]",JSON.toJSONString(responseObject));
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
        }catch (Exception e){
            log.error("Feign 释放库存信息异常,responseObject={},orderNo={}，carNo={}",JSON.toJSONString(responseObject),orderNo,carNo);
            Cat.logError("Feign 释放库存信息异常",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }
        boolean data = responseObject.getData();
        if(!data){
            ReleaseStockException releaseStockException = new ReleaseStockException();
            log.error("释放库存-释放库存失败",releaseStockException);
            throw releaseStockException;
        }
    }




    /**
     * @Author ZhangBin
     * @Date 2020/2/9 17:34
     * @Description: 锁定车辆可租时间(car_filter)
     **/
    public void ownerCancelStock(OwnerCancelDTO ownerCancel){
        ResponseObject<Boolean> responseObject  = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "锁定车辆可租时间");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"carRentalTimeApi.ownerCancelStock");
            log.info("Feign 锁定车辆可租时间,ownerCancel:[{}]", JSON.toJSONString(ownerCancel));
            Cat.logEvent(CatConstants.FEIGN_PARAM,JSON.toJSONString(ownerCancel));
            responseObject = carRentalTimeApi.ownerCancelStock(ownerCancel);
            log.info("Fegin 锁定车辆可租时间,返回结果:[{}]",JSON.toJSONString(responseObject));
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
        }catch (Exception e){
            log.error("Feign 锁定车辆可租时间异常,responseObject:[{}],ownerCancel:[{}]",JSON.toJSONString(responseObject),JSON.toJSONString(ownerCancel),e);
            Cat.logError("Feign 锁定车辆可租时间异常",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }

    }


    public static void  checkResponse(ResponseObject responseObject){
        if(responseObject==null||!ErrorCode.SUCCESS.getCode().equalsIgnoreCase(responseObject.getResCode())){
            RemoteCallException remoteCallException = null;
            if(responseObject!=null){
                remoteCallException = new RemoteCallException(responseObject.getResCode(),responseObject.getResMsg(),responseObject.getData());
            }else{
                remoteCallException = new RemoteCallException(com.atzuche.order.commons.enums.ErrorCode.REMOTE_CALL_FAIL.getCode(),
                        com.atzuche.order.commons.enums.ErrorCode.REMOTE_CALL_FAIL.getText());
            }
            throw remoteCallException;
        }
    }
}
