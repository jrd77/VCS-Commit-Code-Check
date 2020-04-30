package com.atzuche.order.admin.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.commons.ResponseCheckUtil;
import com.atzuche.order.commons.entity.dto.OwnerGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.RenterMemberDTO;
import com.atzuche.order.commons.entity.orderDetailDto.OrderDTO;
import com.atzuche.order.commons.entity.orderDetailDto.OwnerMemberDTO;
import com.atzuche.order.commons.vo.req.AdditionalDriverInsuranceIdsReqVO;
import com.atzuche.order.commons.vo.req.PaymentReqVO;
import com.atzuche.order.commons.vo.res.PaymentRespVO;
import com.atzuche.order.open.service.*;
import com.atzuche.order.open.vo.RenterGoodWithoutPriceVO;
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
    private FeignOrderService feignOrderService;
    @Autowired
    private FeignGoodsService feignGoodsService;
    @Autowired
    private FeignMemberService feignMemberService;
    @Autowired
    private FeignAdditionDriverService feignAdditionDriverService;
    @Autowired
    private FeignPaymentService feignPaymentService;
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
            Cat.logEvent(CatConstants.FEIGN_METHOD,"feignGoodsService.queryCarNumByOrderNo");
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
            Cat.logEvent(CatConstants.FEIGN_METHOD,"feignMemberService.getRenterMemberByOrderNo");
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
    /*
     * @Author ZhangBin
     * @Date 2020/4/30 10:51
     * @Description: 主订单号获取车主商品信息
     *
     **/
    public OwnerGoodsDetailDTO getOwnerGoodsFromRemot(String orderNo){
        ResponseData<OwnerGoodsDetailDTO> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "获取车主商品信息");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"feignGoodsService.queryOwnerGoodsByOrderNo");
            log.info("Feign 开始获取车主商品信息,orderNo={}", orderNo);
            Cat.logEvent(CatConstants.FEIGN_PARAM,orderNo);
            responseObject =  feignGoodsService.queryOwnerGoodsByOrderNo(orderNo);
            Cat.logEvent(CatConstants.FEIGN_RESULT,orderNo);
            ResponseCheckUtil.checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
            return responseObject.getData();
        }catch (Exception e){
            log.error("Feign 获取车主商品信息异常,responseObject={},orderNo={}",JSON.toJSONString(responseObject),orderNo,e);
            Cat.logError("Feign 获取车主商品信息异常",e);
            throw e;
        }finally {
            t.complete();
        }
    }
    /*
     * @Author ZhangBin
     * @Date 2020/4/30 10:59 
     * @Description: 获取租客商品信息
     * 
     **/
    public RenterGoodWithoutPriceVO queryRenterGoods(String orderNo, String carNo){
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "订单中租客商品信息");
        ResponseData<RenterGoodWithoutPriceVO> responseObject = null;
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"feignRenterGoodsService.getRenterGoodsDetailWithoutPrice");
            log.info("Feign 开始获取订单中租客商品信息,[orderNo={},carNo={}]", orderNo,JSON.toJSONString(carNo));
            Cat.logEvent(CatConstants.FEIGN_PARAM,JSON.toJSONString(carNo));
            responseObject = feignGoodsService.getRenterGoodsDetailWithoutPrice(orderNo,carNo);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            ResponseCheckUtil.checkResponse(responseObject);
            RenterGoodWithoutPriceVO baseVO = responseObject.getData();
            log.info("baseVo is {}",baseVO);
            t.setStatus(Transaction.SUCCESS);
            return baseVO;
        }catch (Exception e){
            log.error("Feign 订单中租客商品信息,responseObject={},orderCarInfoParamDTO={}",JSON.toJSONString(responseObject),JSON.toJSONString(carNo),e);
            Cat.logError("Feign 获取订单中租客商品信息异常",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }
    }

    /*
     * @Author ZhangBin
     * @Date 2020/4/30 10:59
     * @Description: 获取车主会员信息
     *
     **/
    public OwnerMemberDTO remoteGetOwnerMember(String orderNo, String memNo){
        ResponseData<OwnerMemberDTO> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "获取车主会员信");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"feignMemberService.queryOwnerMemberByOrderNoAndOwnerNo");
            log.info("Feign 开始获取车主会员信息,orderNo={}", orderNo);
            Cat.logEvent(CatConstants.FEIGN_PARAM,orderNo);
            responseObject =  feignMemberService.queryOwnerMemberByOrderNoAndOwnerNo(orderNo, memNo);
            Cat.logEvent(CatConstants.FEIGN_RESULT,orderNo);
            ResponseCheckUtil.checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
            return responseObject.getData();
        }catch (Exception e){
            log.error("Feign 获取车主会员信息异常,responseObject={},orderNo={},memNo={}", JSON.toJSONString(responseObject),orderNo,memNo,e);
            Cat.logError("Feign 获取车主会员信息异常",e);
            throw e;
        }finally {
            t.complete();
        }
    }
    /*
     * @Author ZhangBin
     * @Date 2020/4/30 11:01
     * @Description: 获取租客会员和会员权益信息
     *
     **/
    public RenterMemberDTO getRenterMemeberFromRemot(String renterOrderNo, boolean isNeedRight){
        ResponseData<RenterMemberDTO> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "获取租客会员信息");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"feignOrderUpdateService.cancelOrder");
            log.info("Feign 开始获取租客会员信息,renterOrderNo={},isNeedRight={}", renterOrderNo,isNeedRight);
            Cat.logEvent(CatConstants.FEIGN_PARAM,renterOrderNo);
            responseObject =  feignMemberService.queryRenterMemberByOwnerOrderNo(renterOrderNo,isNeedRight);
            Cat.logEvent(CatConstants.FEIGN_RESULT,renterOrderNo);
            ResponseCheckUtil.checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
            return responseObject.getData();
        }catch (Exception e){
            log.error("Feign 获取租客会员信息异常,responseObject={},renterOrderNo={}",JSON.toJSONString(responseObject),renterOrderNo,e);
            Cat.logError("Feign 获取租客会员信息异常",e);
            throw e;
        }finally {
            t.complete();
        }
    }

    /*
     * @Author ZhangBin
     * @Date 2020/4/30 11:02
     * @Description: 添加附加驾驶人
     *
     **/
    public void insertAdditionalDriverFromRemot(AdditionalDriverInsuranceIdsReqVO renterCostReqVO){
        ResponseData<?> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "增加附加驾驶人");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"feignAdditionDriverService.insertAdditionalDriver");
            log.info("Feign 开始增加附加驾驶人renterCostReqVO={}", renterCostReqVO);
            Cat.logEvent(CatConstants.FEIGN_PARAM,JSON.toJSONString(renterCostReqVO));
            responseObject =  feignAdditionDriverService.insertAdditionalDriver(renterCostReqVO);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            ResponseCheckUtil.checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
        }catch (Exception e){
            log.error("Feign 增加附加驾驶人异常,responseObject={},renterCostReqVO={}",JSON.toJSONString(responseObject),JSON.toJSONString(renterCostReqVO),e);
            Cat.logError("Feign 增加附加驾驶人异常",e);
            throw e;
        }finally {
            t.complete();
        }
    }

    public PaymentRespVO queryPaymentFromRemote(PaymentReqVO paymentReqVO){
        ResponseData<PaymentRespVO> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "支付信息");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"feignPaymentService.queryByOrderNo");
            log.info("Feign 开始支付信息paymentReqVO={}", JSON.toJSONString(paymentReqVO));
            Cat.logEvent(CatConstants.FEIGN_PARAM,JSON.toJSONString(paymentReqVO));
            responseObject = feignPaymentService.queryByOrderNo(paymentReqVO);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            ResponseCheckUtil.checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
            return responseObject.getData();
        }catch (Exception e){
            log.error("Feign 支付信息异常,responseObject={},paymentReqVO={}",JSON.toJSONString(responseObject),JSON.toJSONString(paymentReqVO),e);
            Cat.logError("Feign 支付信息异常",e);
            throw e;
        }finally {
            t.complete();
        }
    }
}
