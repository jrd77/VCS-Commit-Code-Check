package com.atzuche.order.coreapi.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.coreapi.submitOrder.exception.*;
import com.autoyol.car.api.CarRentalTimeApi;
import com.autoyol.car.api.model.dto.OrderInfoDTO;
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
public class StockService {
    @Autowired
    private CarRentalTimeApi carRentalTimeApi;
    
    /*
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
            if(responseObject == null){
                log.error("Feign 校验库存信息失败,responseObject={},orderInfoDTO={}",JSON.toJSONString(responseObject),JSON.toJSONString(orderInfoDTO));
                CheckCarStockFailException failException = new CheckCarStockFailException();
                Cat.logError("Feign 校验库存信息失败",failException);
                throw failException;
            }
            t.setStatus(Transaction.SUCCESS);
        }catch (RenterCarDetailFailException e){
            Cat.logError("Feign 校验库存信息失败",e);
            t.setStatus(e);
            throw e;
        }catch (Exception e){
            log.error("Feign 校验库存信息异常,responseObject={},orderInfoDTO={}",JSON.toJSONString(responseObject),JSON.toJSONString(orderInfoDTO),e);
            CheckCarStockErrException errException = new CheckCarStockErrException();
            Cat.logError("Feign 校验库存信息异常",errException);
            throw errException;
        }finally {
            t.complete();
        }
        if(!ErrorCode.SUCCESS.getCode().equals(responseObject.getResCode())){
            NotStockException notStockException = new NotStockException(com.atzuche.order.commons.enums.ErrorCode.NOT_STOCK_EXCEPTION.getCode(), responseObject.getResMsg());
            log.error("校验库存-库存不足",notStockException);
            throw notStockException;
        }
        if(responseObject.getData()==null || !responseObject.getData()){
            NotStockException notStockException = new NotStockException(com.atzuche.order.commons.enums.ErrorCode.NOT_STOCK_EXCEPTION.getCode(), responseObject.getResMsg());
            log.error("校验库存-库存不足",notStockException);
            throw notStockException;
        }
    }
    
    /*
     * @Author ZhangBin
     * @Date 2020/1/8 17:34
     * @Description: 扣库存
     * 
     **/
    public void cutCarStock(OrderInfoDTO orderInfoDTO){
        ResponseObject<Boolean> responseObject  = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "库存扣减");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"CarRentalTimeApi.checkCarStock");
            log.info("Feign 开始扣减库存信息,orderInfoDTO={}", JSON.toJSONString(orderInfoDTO));
            Cat.logEvent(CatConstants.FEIGN_PARAM,JSON.toJSONString(orderInfoDTO));
            responseObject = carRentalTimeApi.cutCarStock(orderInfoDTO);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            if(responseObject == null || !ErrorCode.SUCCESS.getCode().equals(responseObject.getResCode())){
                log.error("Feign 扣减库存信息失败,responseObject={},orderInfoDTO={}",JSON.toJSONString(responseObject),JSON.toJSONString(orderInfoDTO));
                CheckCarStockFailException failException = new CheckCarStockFailException();
                Cat.logError("Feign 扣减库存信息失败",failException);
                throw failException;
            }
            t.setStatus(Transaction.SUCCESS);
        }catch (RenterCarDetailFailException e){
            Cat.logError("Feign 扣减库存信息失败",e);
            t.setStatus(e);
            throw e;
        }catch (Exception e){
            log.error("Feign 扣减库存信息异常,responseObject={},orderInfoDTO={}",JSON.toJSONString(responseObject),JSON.toJSONString(orderInfoDTO),e);
            CutCarStockErrException errException = new CutCarStockErrException();
            Cat.logError("Feign 扣减库存信息异常",errException);
            throw errException;
        }finally {
            t.complete();
        }
        if(responseObject.getData()==null || !responseObject.getData()){
            LocakStockException locakStockException = new LocakStockException();
            log.error("扣减库存-扣减库存失败",locakStockException);
            throw locakStockException;
        }
    }

    /*
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
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            if(responseObject == null || !ErrorCode.SUCCESS.getCode().equals(responseObject.getResCode())){
                log.error("Feign 释放库存信息失败,responseObject={},orderNo={}，carNo={}",JSON.toJSONString(responseObject),orderNo,carNo);
                ReleaseCarStockFailException failException = new ReleaseCarStockFailException();
                Cat.logError("Feign 释放库存信息失败",failException);
                throw failException;
            }
            t.setStatus(Transaction.SUCCESS);
        }catch (RenterCarDetailFailException e){
            Cat.logError("Feign 释放库存信息失败",e);
            t.setStatus(e);
            throw e;
        }catch (Exception e){
            log.error("Feign 释放库存信息异常,responseObject={},orderNo={}，carNo={}",JSON.toJSONString(responseObject),orderNo,carNo);
            ReleaseCarStockErrException errException = new ReleaseCarStockErrException();
            Cat.logError("Feign 释放库存信息异常",errException);
            throw errException;
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
}
