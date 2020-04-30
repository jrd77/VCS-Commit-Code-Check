package com.atzuche.order.admin.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.commons.ResponseCheckUtil;
import com.atzuche.order.commons.entity.orderDetailDto.OrderDTO;
import com.atzuche.order.open.service.FeignGoodsService;
import com.atzuche.order.open.service.FeignMemberService;
import com.atzuche.order.open.service.FeignOrderService;
import com.autoyol.commons.web.ResponseData;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RemoteFeignService {
    @Autowired
    FeignOrderService feignOrderService;
    @Autowired
    FeignGoodsService feignGoodsService;
    @Autowired
    private FeignMemberService feignMemberService;

    /*
     * @Author ZhangBin
     * @Date 2020/4/30 10:38
     * @Description: 订单号获取主订单信息
     *
     **/
    public OrderDTO queryOrderByOrderNoFromRemote(String orderNo){
        ResponseData<OrderDTO> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "获取主订单信息");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"feignOrderService.queryOrderByOrderNo");
            log.info("Feign 开始获取主订单信息,orderNo={}", orderNo);
            Cat.logEvent(CatConstants.FEIGN_PARAM,orderNo);
            responseObject =  feignOrderService.queryOrderByOrderNo(orderNo);
            Cat.logEvent(CatConstants.FEIGN_RESULT,orderNo);
            ResponseCheckUtil.checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
            return responseObject.getData();
        }catch (Exception e){
            log.error("Feign 获取主订单信息异常,responseObject={},orderNo={}", JSON.toJSONString(responseObject),orderNo,e);
            Cat.logError("Feign 获取主订单信息异常",e);
            throw e;
        }finally {
            t.complete();
        }
    }

    /*
     * @Author ZhangBin
     * @Date 2020/4/30 10:38
     * @Description: 主订单号获取车辆号
     * 
     **/
    public String getCarNumFromRemot(String orderNo){
        ResponseData<String> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "获取车辆号");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"feignOrderUpdateService.cancelOrder");
            log.info("Feign 开始获取车辆号,orderNo={}", orderNo);
            Cat.logEvent(CatConstants.FEIGN_PARAM,orderNo);
            responseObject =  feignGoodsService.queryCarNumByOrderNo(orderNo);
            Cat.logEvent(CatConstants.FEIGN_RESULT,orderNo);
            ResponseCheckUtil.checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
            return responseObject.getData();
        }catch (Exception e){
            log.error("Feign 获取车辆号异常,responseObject={},orderNo={}",JSON.toJSONString(responseObject),orderNo,e);
            Cat.logError("Feign 获取车辆号异常",e);
            throw e;
        }finally {
            t.complete();
        }
    }

    /*
     * @Author ZhangBin
     * @Date 2020/4/30 10:44
     * @Description: 订单号获取车辆号
     *
     **/
    public String getRenterMemberFromRemote(String orderNo){
        ResponseData<String> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "获取会员号");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"feignOrderUpdateService.cancelOrder");
            log.info("Feign 开始获取会员号,orderNo={}", orderNo);
            Cat.logEvent(CatConstants.FEIGN_PARAM,orderNo);
            responseObject =  feignMemberService.getRenterMemberByOrderNo(orderNo);
            Cat.logEvent(CatConstants.FEIGN_RESULT,orderNo);
            ResponseCheckUtil.checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
            return responseObject.getData();
        }catch (Exception e){
            log.error("Feign 获取获取会员号异常,responseObject={},orderNo={}", JSON.toJSONString(responseObject),orderNo,e);
            Cat.logError("Feign 获取获取会员号异常",e);
            throw e;
        }finally {
            t.complete();
        }
    }
}
