package com.atzuche.order.admin.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.commons.LocalDateTimeUtils;
import com.atzuche.order.commons.ResponseCheckUtil;
import com.atzuche.order.commons.entity.dto.*;
import com.atzuche.order.commons.entity.dto.RenterMemberDTO;
import com.atzuche.order.commons.entity.orderDetailDto.*;
import com.atzuche.order.commons.entity.orderDetailDto.OwnerMemberDTO;
import com.atzuche.order.commons.vo.OrderStopFreightInfo;
import com.atzuche.order.commons.vo.OwnerTransAddressReqVO;
import com.atzuche.order.commons.vo.req.*;
import com.atzuche.order.commons.vo.req.consolecost.GetTempCarDepositInfoReqVO;
import com.atzuche.order.commons.vo.req.consolecost.SaveTempCarDepositInfoReqVO;
import com.atzuche.order.commons.vo.res.*;
import com.atzuche.order.commons.vo.res.consolecost.GetTempCarDepositInfoResVO;
import com.atzuche.order.open.service.*;
import com.atzuche.order.open.vo.RenterGoodWithoutPriceVO;
import com.autoyol.autopay.gateway.vo.Response;
import com.autoyol.commons.web.ResponseData;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

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
    @Autowired
    private FeignBusinessService feignBusinessService;
    @Autowired
    private FeignOrderSettleService feignOrderSettleService;
    @Autowired
    private FeignOrderCouponService feignOrderCouponService;
    @Autowired
    private FeignOrderFlowService feignOrderFlowService;
    @Autowired
    private FeignOrderDetailService feignOrderDetailService;
    @Autowired
    private FeignOrderCostService feignOrderCostService;
    @Autowired
    FeignOrderUpdateService feignOrderUpdateService;
    @Autowired
    FeignOrderModifyService feignOrderModifyService;
    @Autowired
    FeignModifyOwnerAddrService feignModifyOwnerAddrService;
    @Autowired
    FeignClearingRefundService feignClearingRefundService;

    /*
     * @Author ZhangBin
     * @Date 2020/6/4 13:42
     * @Description: 清算退款
     *
     **/
    public Response<?> clearingRefundFromRemote(ClearingRefundReqVO clearingRefundReqVO ){
        Response<?> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "清算退款");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"feignOrderService.queryOrderStatusByOrderNo");
            log.info("Feign 清算退款,clearingRefundReqVO={}", JSON.toJSONString(clearingRefundReqVO));
            Cat.logEvent(CatConstants.FEIGN_PARAM,JSON.toJSONString(clearingRefundReqVO));
            responseObject= feignClearingRefundService.clearingRefundSubmitToRefund(clearingRefundReqVO);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(clearingRefundReqVO));
            t.setStatus(Transaction.SUCCESS);
            return responseObject;
        }catch (Exception e){
            log.error("Feign 清算退款异常,responseObject={},clearingRefundReqVO={}", JSON.toJSONString(responseObject),JSON.toJSONString(clearingRefundReqVO),e);
            Cat.logError("Feign 清算退款异常",e);
            throw e;
        }finally {
            t.complete();
        }
    }

    public OrderStatusDTO queryOrderStatusByOrderNo(String orderNo){
        ResponseData<OrderStatusDTO> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "根据订单号查询订单状态");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"feignOrderService.queryOrderStatusByOrderNo");
            log.info("Feign 根据订单号查询订单状态,orderNo={}", orderNo);
            Cat.logEvent(CatConstants.FEIGN_PARAM,orderNo);
            responseObject =  feignOrderService.queryOrderStatusByOrderNo(orderNo);
            Cat.logEvent(CatConstants.FEIGN_RESULT,orderNo);
            ResponseCheckUtil.checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
            return responseObject.getData();
        }catch (Exception e){
            log.error("Feign 根据订单号查询订单状态异常,responseObject={},orderNo={}", JSON.toJSONString(responseObject),orderNo,e);
            Cat.logError("Feign 根据订单号查询订单状态异常",e);
            throw e;
        }finally {
            t.complete();
        }
    }

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
    public OwnerGoodsDetailDTO queryOwnerGoods(boolean isNeedPrice, String ownerOrderNo){
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "订单中车主商品信息");
        ResponseData<OwnerGoodsDetailDTO> responseObject = null;
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"feignGoodsService.queryOwnerGoodsDetail");
            log.info("Feign 开始获取订单中车主商品信息,[isNeedPrice={},ownerOrderNo={}]", isNeedPrice,ownerOrderNo);
            Cat.logEvent(CatConstants.FEIGN_PARAM,JSON.toJSONString(ownerOrderNo));
            responseObject = feignGoodsService.queryOwnerGoodsDetail(ownerOrderNo,isNeedPrice);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            ResponseCheckUtil.checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
            return responseObject.getData();
        }catch (Exception e){
            log.error("Feign 订单中车主商品信息,responseObject={},ownerOrderNo={}",JSON.toJSONString(responseObject),ownerOrderNo,e);
            Cat.logError("Feign 获取订单中车主商品信息异常",e);
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

    /*
     * @Author ZhangBin
     * @Date 2020/4/30 11:02
     * @Description: 查询附加驾驶人
     *
     **/
    public List<String> queryAdditionalDriverFromRemot(String renterOrderNo){
        ResponseData<List<String>> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "查询附加驾驶人");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"feignAdditionDriverService.queryAdditionalDriver");
            log.info("Feign renterOrderNo={}", renterOrderNo);
            Cat.logEvent(CatConstants.FEIGN_PARAM,renterOrderNo);
            responseObject =  feignAdditionDriverService.queryAdditionalDriver(renterOrderNo);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            ResponseCheckUtil.checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
            return responseObject.getData();
        }catch (Exception e){
            log.error("Feign 查询附加驾驶人异常,responseObject={},renterOrderNo={}",JSON.toJSONString(responseObject),renterOrderNo,e);
            Cat.logError("Feign 查询附加驾驶人异常",e);
            throw e;
        }finally {
            t.complete();
        }
    }
    /*
     * @Author ZhangBin
     * @Date 2020/4/30 11:02
     * @Description: 查询附加驾驶人
     *
     **/
    public List<RenterAdditionalDriverDTO> queryAdditionalDriverListFromRemot(String renterOrderNo){
        ResponseData<List<RenterAdditionalDriverDTO>> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "查询附加驾驶人");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"feignAdditionDriverService.queryAdditionalDriver");
            log.info("Feign renterOrderNo={}", renterOrderNo);
            Cat.logEvent(CatConstants.FEIGN_PARAM,renterOrderNo);
            responseObject =  feignAdditionDriverService.queryAdditionalDriverList(renterOrderNo);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            ResponseCheckUtil.checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
            return responseObject.getData();
        }catch (Exception e){
            log.error("Feign 查询附加驾驶人异常,responseObject={},renterOrderNo={}",JSON.toJSONString(responseObject),renterOrderNo,e);
            Cat.logError("Feign 查询附加驾驶人异常",e);
            throw e;
        }finally {
            t.complete();
        }
    }
    /*
     * @Author ZhangBin
     * @Date 2020/4/30 14:58
     * @Description: 获取支付信息
     *
     **/
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

    /*
     * @Author ZhangBin
     * @Date 2020/4/30 14:58
     * @Description: 获取押金比例
     *
     **/
    public RenterDepositDetailDTO queryrenterDepositDetail(String orderNo){
        ResponseData<RenterDepositDetailDTO> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "押金比例");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"feignPaymentService.queryrenterDepositDetail");
            log.info("Feign 开始押金比例orderNo={}", orderNo);
            Cat.logEvent(CatConstants.FEIGN_PARAM,orderNo);
            responseObject = feignBusinessService.queryrenterDepositDetail(orderNo);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            ResponseCheckUtil.checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
            return responseObject.getData();
        }catch (Exception e){
            log.error("Feign 押金比例异常,responseObject={},orderNo={}",JSON.toJSONString(responseObject),orderNo,e);
            Cat.logError("Feign 押金比例异常",e);
            throw e;
        }finally {
            t.complete();
        }
    }
    /*
     * @Author ZhangBin
     * @Date 2020/5/7 14:45
     * @Description: 手动结算违章押金
     *
     **/
    public void settleOrderWzFromRemote(String orderNo){
        ResponseData<?> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "手动结算违章押金");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"feignOrderSettleService.settleOrderWz");
            log.info("Feign 开始手动结算违章押金orderNo={}", orderNo);
            Cat.logEvent(CatConstants.FEIGN_PARAM,orderNo);
            responseObject = feignOrderSettleService.settleOrderWz(orderNo);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            ResponseCheckUtil.checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
        }catch (Exception e){
            log.error("Feign 手动结算违章押金异常,responseObject={},orderNo={}",JSON.toJSONString(responseObject),orderNo,e);
            Cat.logError("Feign 手动结算违章押金异常",e);
            throw e;
        }finally {
            t.complete();
        }
    }

    /*
     * @Author ZhangBin
     * @Date 2020/4/30 14:58
     * @Description: 获取订单内租客优惠抵扣信息接口
     *
     **/
    public ResponseData<AdminGetDisCouponListResVO> queryDisCouponByOrderNoFromRemote(AdminGetDisCouponListReqVO req){
        ResponseData<AdminGetDisCouponListResVO> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "获取订单内租客优惠抵扣信息接口");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"feignOrderCouponService.getDisCouponListByOrderNo");
            log.info("Feign 开始获取订单内租客优惠抵扣信息接口req={}", JSON.toJSONString(req));
            Cat.logEvent(CatConstants.FEIGN_PARAM,JSON.toJSONString(req));
            responseObject = feignOrderCouponService.getDisCouponListByOrderNo(req);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            ResponseCheckUtil.checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
            return responseObject;
        }catch (Exception e){
            log.error("Feign 获取订单内租客优惠抵扣信息接口异常,responseObject={},req={}",JSON.toJSONString(responseObject),JSON.toJSONString(req),e);
            Cat.logError("Feign 获取订单内租客优惠抵扣信息接口异常",e);
            throw e;
        }finally {
            t.complete();
        }
    }


    /*
     * @Author ZhangBin
     * @Date 2020/4/30 14:58
     * @Description: 手动车辆结算接口
     *
     **/
    public ResponseData<?> depositSettleFromRemote(String orderNo){
        ResponseData<?> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "手动车辆结算接口");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"feignOrderCouponService.getDisCouponListByOrderNo");
            log.info("Feign 开始手动车辆结算接口orderNo={}", orderNo);
            Cat.logEvent(CatConstants.FEIGN_PARAM,orderNo);
            responseObject = feignOrderSettleService.depositSettle(orderNo);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            ResponseCheckUtil.checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
            return responseObject;
        }catch (Exception e){
            log.error("Feign 手动车辆结算接口异常,responseObject={},orderNo={}",JSON.toJSONString(responseObject),orderNo,e);
            Cat.logError("Feign 手动车辆结算接口异常",e);
            throw e;
        }finally {
            t.complete();
        }
    }



    /*
     * @Author ZhangBin
     * @Date 2020/4/30 14:58
     * @Description: 订单状态流转列表
     *
     **/
    public ResponseData<OrderFlowListResponseDTO> selectOrderFlowListFromRemote(OrderFlowRequestDTO req){
        ResponseData<OrderFlowListResponseDTO> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "订单状态流转列表");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"feignOrderCouponService.getDisCouponListByOrderNo");
            log.info("Feign 开始订单状态流转列表req={}", JSON.toJSONString(req));
            Cat.logEvent(CatConstants.FEIGN_PARAM,JSON.toJSONString(req));
            responseObject = feignOrderFlowService.selectOrderFlowList(req);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            ResponseCheckUtil.checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
            return responseObject;
        }catch (Exception e){
            log.error("Feign 订单状态流转列表异常,responseObject={},req={}",JSON.toJSONString(responseObject),JSON.toJSONString(req),e);
            Cat.logError("Feign 订单状态流转列表异常",e);
            throw e;
        }finally {
            t.complete();
        }
    }

    /*
     * @Author ZhangBin
     * @Date 2020/4/30 14:58
     * @Description: 获取订单当前状态描述
     *
     **/
    public ResponseData<OrderStatusRespDTO> getOrderStatusFromRemote(OrderDetailReqDTO req){
        ResponseData<OrderStatusRespDTO> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "获取订单当前状态描述");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"feignOrderCouponService.getDisCouponListByOrderNo");
            log.info("Feign 开始获取订单当前状态描述req={}", JSON.toJSONString(req));
            Cat.logEvent(CatConstants.FEIGN_PARAM,JSON.toJSONString(req));
            responseObject = feignOrderDetailService.getOrderStatus(req);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            ResponseCheckUtil.checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
            return responseObject;
        }catch (Exception e){
            log.error("Feign 获取订单当前状态描述异常,responseObject={},req={}",JSON.toJSONString(responseObject),JSON.toJSONString(req),e);
            Cat.logError("Feign 获取订单当前状态描述异常",e);
            throw e;
        }finally {
            t.complete();
        }
    }
    /*
     * @Author ZhangBin
     * @Date 2020/5/8 10:15
     * @Description: 获取订单车辆押金暂扣扣款明细接口
     *
     **/
    public ResponseData<GetTempCarDepositInfoResVO>  getTempCarDepoistsFromRemote(GetTempCarDepositInfoReqVO req){
        ResponseData<GetTempCarDepositInfoResVO> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "获取订单车辆押金暂扣扣款明细接口");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"feignOrderCostService.getTempCarDepoists");
            log.info("Feign 开始获取订单车辆押金暂扣扣款明细接口req={}", JSON.toJSONString(req));
            Cat.logEvent(CatConstants.FEIGN_PARAM,JSON.toJSONString(req));
            responseObject = feignOrderCostService.getTempCarDepoists(req);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            ResponseCheckUtil.checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
            return responseObject;
        }catch (Exception e){
            log.error("Feign 获取订单车辆押金暂扣扣款明细接口异常,responseObject={},req={}",JSON.toJSONString(responseObject),JSON.toJSONString(req),e);
            Cat.logError("Feign 获取订单车辆押金暂扣扣款明细接口异常",e);
            throw e;
        }finally {
            t.complete();
        }
    }

    /*
     * @Author ZhangBin
     * @Date 2020/5/8 10:15
     * @Description: 保存订单车辆押金暂扣扣款信息接口
     *
     **/
    public ResponseData  saveTempCarDepoistsFromRemote(SaveTempCarDepositInfoReqVO req){
        ResponseData responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "保存订单车辆押金暂扣扣款信息接口");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"feignOrderCostService.saveTempCarDepoist");
            log.info("Feign 开始保存订单车辆押金暂扣扣款信息接口req={}", JSON.toJSONString(req));
            Cat.logEvent(CatConstants.FEIGN_PARAM,JSON.toJSONString(req));
            responseObject = feignOrderCostService.saveTempCarDepoist(req);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            ResponseCheckUtil.checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
            return responseObject;
        }catch (Exception e){
            log.error("Feign 保存订单车辆押金暂扣扣款信息接口异常,responseObject={},req={}",JSON.toJSONString(responseObject),JSON.toJSONString(req),e);
            Cat.logError("Feign 保存订单车辆押金暂扣扣款信息接口异常",e);
            throw e;
        }finally {
            t.complete();
        }
    }


    /*
     * @Author ZhangBin
     * @Date 2020/5/8 10:15
     * @Description: 修改用车城市和风控事故状态
     *
     **/
    public ResponseData  updateRentCityAndRiskAccidentFromRemote(RentCityAndRiskAccidentReqDTO req){
        ResponseData responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "修改用车城市和风控事故状态");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"feignOrderUpdateService.updateRentCityAndRiskAccident");
            log.info("Feign 开始修改用车城市和风控事故状态req={}", JSON.toJSONString(req));
            Cat.logEvent(CatConstants.FEIGN_PARAM,JSON.toJSONString(req));
            responseObject = feignOrderUpdateService.updateRentCityAndRiskAccident(req);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            ResponseCheckUtil.checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
            return responseObject;
        }catch (Exception e){
            log.error("Feign 修改用车城市和风控事故状态异常,responseObject={},req={}",JSON.toJSONString(responseObject),JSON.toJSONString(req),e);
            Cat.logError("Feign 修改用车城市和风控事故状态异常",e);
            throw e;
        }finally {
            t.complete();
        }
    }
    /*
     * @Author ZhangBin
     * @Date 2020/5/8 11:19
     * @Description: 修改订单
     *
     **/
    public ResponseData modifyOrder(ModifyOrderReqVO modifyOrderReq) {
        ResponseData<?> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "修改订单");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"feignOrderModifyService.modifyOrderForConsole");
            log.info("Feign 开始修改订单,modifyOrderReq={}", JSON.toJSONString(modifyOrderReq));
            Cat.logEvent(CatConstants.FEIGN_PARAM,JSON.toJSONString(modifyOrderReq));
            responseObject = feignOrderModifyService.modifyOrderForConsole(modifyOrderReq);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            ResponseCheckUtil.checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
            return responseObject;
        }catch (Exception e){
            log.error("Feign 修改订单异常,responseObject={},modifyOrderReq={}",JSON.toJSONString(responseObject),JSON.toJSONString(modifyOrderReq),e);
            Cat.logError("Feign 修改订单异常",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }
    }
    /*
     * @Author ZhangBin
     * @Date 2020/5/8 11:19
     * @Description: 修改配送订单
     *
     **/
    public ResponseData updateOwnerAddrInfoFromRemote(OwnerTransAddressReqVO req) {
        ResponseData<?> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "修改配送订单");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"feignModifyOwnerAddrService.updateOwnerAddrInfo");
            log.info("Feign 开始修改配送订单,req={}", JSON.toJSONString(req));
            Cat.logEvent(CatConstants.FEIGN_PARAM,JSON.toJSONString(req));
            responseObject = feignModifyOwnerAddrService.updateOwnerAddrInfo(req);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            ResponseCheckUtil.checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
            return responseObject;
        }catch (Exception e){
            log.error("Feign 修改配送订单异常,responseObject={},req={}",JSON.toJSONString(responseObject),JSON.toJSONString(req),e);
            Cat.logError("Feign 修改配送订单异常",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }
    }
    /*
     * @Author ZhangBin
     * @Date 2020/5/8 11:08
     * @Description: 获取订单详情
     *
     **/
    public ResponseData<OrderDetailRespDTO> getOrderdetailFromRemote(String orderNo){
        OrderDetailReqDTO req = new OrderDetailReqDTO();
        req.setOrderNo(orderNo);
        ResponseData<OrderDetailRespDTO> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "获取订单详情");

        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"feignOrderDetailService.getOrderDetail");
            log.info("Feign 获取订单详情,param={}", JSON.toJSONString(req));
            Cat.logEvent(CatConstants.FEIGN_PARAM,JSON.toJSONString(req));
            responseObject =feignOrderDetailService.getOrderDetail(req);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            ResponseCheckUtil.checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
            return responseObject;
        }catch (Exception e){
            log.error("Feign 获取订单详情,responseObject={},orderNo={}",JSON.toJSONString(responseObject),orderNo,e);
            Cat.logError("Feign 获取订单详情",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }
    }


    /*
     * @Author ZhangBin
     * @Date 2020/5/8 11:08
     * @Description: 获取修改订单信息
     *
     **/
    public ResponseData<ModifyOrderMainResVO> getOrderdetailFromRemote(ModifyOrderMainQueryReqVO req){
        ResponseData<ModifyOrderMainResVO> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "获取修改订单信息");

        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"feignOrderModifyService.getModifyOrderMain");
            log.info("Feign 获取修改订单信息,param={}", JSON.toJSONString(req));
            Cat.logEvent(CatConstants.FEIGN_PARAM,JSON.toJSONString(req));
            responseObject =feignOrderModifyService.getModifyOrderMain(req);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            ResponseCheckUtil.checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
            return responseObject;
        }catch (Exception e){
            log.error("Feign 获取修改订单信息异常,responseObject={},orderNo={}",JSON.toJSONString(responseObject),JSON.toJSONString(req),e);
            Cat.logError("Feign 获取修改订单信息异常",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }
    }


    /*
     * @Author ZhangBin
     * @Date 2020/5/8 11:08
     * @Description: 获取修改订单列表信息
     *
     **/
    public ResponseData<ModifyOrderResVO> queryModifyOrderListFromRemote(ModifyOrderQueryReqVO req){
        ResponseData<ModifyOrderResVO> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "获取修改订单列表信息");

        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"feignOrderModifyService.queryModifyOrderList");
            log.info("Feign 获取修改订单列表信息,param={}", JSON.toJSONString(req));
            Cat.logEvent(CatConstants.FEIGN_PARAM,JSON.toJSONString(req));
            responseObject =feignOrderModifyService.queryModifyOrderList(req);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            ResponseCheckUtil.checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
            return responseObject;
        }catch (Exception e){
            log.error("Feign 获取修改订单列表信息异常,responseObject={},orderNo={}",JSON.toJSONString(responseObject),JSON.toJSONString(req),e);
            Cat.logError("Feign 获取修改订单列表信息异常",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }
    }




    /*
     * @Author ZhangBin
     * @Date 2020/5/8 11:08
     * @Description: 租客子订单费用详细
     *
     **/
    public ResponseData<OrderRenterCostResVO> orderCostRenterGetFromRemote(OrderCostReqVO req){
        ResponseData<OrderRenterCostResVO> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "获取租客子订单费用详细");

        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"feignOrderCostService.orderCostRenterGet");
            log.info("Feign 获取租客子订单费用详细,param={}", JSON.toJSONString(req));
            Cat.logEvent(CatConstants.FEIGN_PARAM,JSON.toJSONString(req));
            responseObject =feignOrderCostService.orderCostRenterGet(req);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            ResponseCheckUtil.checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
            return responseObject;
        }catch (Exception e){
            log.error("Feign 获取租客子订单费用详细异常,responseObject={},orderNo={}",JSON.toJSONString(responseObject),JSON.toJSONString(req),e);
            Cat.logError("Feign 获取租客子订单费用详细异常",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }
    }


    /*
     * @Author ZhangBin
     * @Date 2020/5/8 11:08
     * @Description: 车主子订单费用详细
     *
     **/
    public ResponseData<OrderOwnerCostResVO> orderCostOwnerGetFromRemote(OrderCostReqVO req){
        ResponseData<OrderOwnerCostResVO> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "获取车主子订单费用详细");

        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"feignOrderCostService.orderCostOwnerGet");
            log.info("Feign 获取车主子订单费用详细,param={}", JSON.toJSONString(req));
            Cat.logEvent(CatConstants.FEIGN_PARAM,JSON.toJSONString(req));
            responseObject =feignOrderCostService.orderCostOwnerGet(req);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            ResponseCheckUtil.checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
            return responseObject;
        }catch (Exception e){
            log.error("Feign 获取车主子订单费用详细异常,responseObject={},orderNo={}",JSON.toJSONString(responseObject),JSON.toJSONString(req),e);
            Cat.logError("Feign 获取车主子订单费用详细异常",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }
    }
    
    
    /**
     * 获取车辆停运费信息
     * @param orderNo
     * @return OrderStopFreightInfo
     */
    public OrderStopFreightInfo getStopFreightInfo(String orderNo){
        ResponseData<OrderStopFreightInfo> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "获取车辆停运费信息");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"feignGoodsService.getStopFreightInfo");
            log.info("Feign 获取车辆停运费信息,orderNo={}", orderNo);
            Cat.logEvent(CatConstants.FEIGN_PARAM,orderNo);
            responseObject =  feignGoodsService.getStopFreightInfo(orderNo);
            Cat.logEvent(CatConstants.FEIGN_RESULT,orderNo);
            ResponseCheckUtil.checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
            return responseObject.getData();
        }catch (Exception e){
            log.error("Feign 获取车辆停运费信息异常,responseObject={},orderNo={}",JSON.toJSONString(responseObject),orderNo,e);
            Cat.logError("Feign 获取车辆停运费信息异常",e);
        }finally {
            t.complete();
        }
        return null;
    }
    

    /**
     * 获取修改前数据
     * @param modifyOrderReq
     * @return ModifyOrderConsoleDTO
     */
    public ModifyOrderConsoleDTO getInitModifyOrderDTO(ModifyOrderReqVO modifyOrderReq) {
        ResponseData<ModifyOrderConsoleDTO> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "获取修改前数据");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"feignOrderModifyService.getInitModifyOrderDTO");
            log.info("Feign 获取修改前数据,modifyOrderReq={}", JSON.toJSONString(modifyOrderReq));
            Cat.logEvent(CatConstants.FEIGN_PARAM,JSON.toJSONString(modifyOrderReq));
            responseObject = feignOrderModifyService.getInitModifyOrderDTO(modifyOrderReq);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            ResponseCheckUtil.checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
            return responseObject.getData();
        }catch (Exception e){
            log.error("Feign 获取修改前数据异常,responseObject={},modifyOrderReq={}",JSON.toJSONString(responseObject),JSON.toJSONString(modifyOrderReq),e);
            Cat.logError("Feign 获取修改前数据异常",e);
            t.setStatus(e);
        }finally {
            t.complete();
        }
        return null;
    }


    /**
     * 根据订单号获取车牌号
     *
     * @param orderNo
     * @return String
     */
    public String getCarPlateNum(String orderNo) {
        ResponseData<String> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "根据订单号获取车牌号");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"feignOrderModifyService.getCarPlateNum");
            log.info("Feign 根据订单号获取车牌号,orderNo={}", orderNo);
            Cat.logEvent(CatConstants.FEIGN_PARAM,orderNo);
            responseObject = feignOrderModifyService.getCarPlateNum(orderNo);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            ResponseCheckUtil.checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
            return responseObject.getData();
        }catch (Exception e){
            log.error("Feign 根据订单号获取车牌号异常,responseObject={},orderNo={}",JSON.toJSONString(responseObject),orderNo,e);
            Cat.logError("Feign 根据订单号获取车牌号异常",e);
            t.setStatus(e);
        }finally {
            t.complete();
        }
        return null;
    }


    /*
     * @Author ZhangBin
     * @Date 2020/5/11 16:58
     * @Description: 获取优惠券信息
     *
     **/
    public List<OrderCouponDTO> queryCouponByOrderNoFromRemote(String orderNo){
        ResponseData<List<OrderCouponDTO>> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "获取优惠券信息");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"feignOrderCostService.orderCostOwnerGet");
            log.info("Feign 获取优惠券信息,orderNo={}", JSON.toJSONString(orderNo));
            Cat.logEvent(CatConstants.FEIGN_PARAM,JSON.toJSONString(orderNo));
            responseObject =feignOrderCouponService.queryCouponByOrderNo(orderNo);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            ResponseCheckUtil.checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
            return responseObject.getData();
        }catch (Exception e){
            log.error("Feign 获取优惠券信息异常,responseObject={},orderNo={}",JSON.toJSONString(responseObject),orderNo,e);
            Cat.logError("Feign 获取优惠券信息异常",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }
    }

    /*
     * @Author ZhangBin
     * @Date 2020/5/15 11:42
     * @Description: 查询车主补贴
     *
     **/
    public List<OwnerOrderSubsidyDetailDTO> queryOwnerSubsidyByownerOrderNo(String orderNo,String ownerOrderNo){
        ResponseData<List<OwnerOrderSubsidyDetailDTO>> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "查询车主补贴");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"feignPaymentService.queryrenterDepositDetail");
            log.info("Feign 查询车主补贴orderNo={},ownerOrderNo={}", orderNo,ownerOrderNo);
            Cat.logEvent(CatConstants.FEIGN_PARAM,orderNo);
            responseObject = feignBusinessService.queryOwnerSubsidyByownerOrderNo(orderNo,ownerOrderNo);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            ResponseCheckUtil.checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
            return responseObject.getData();
        }catch (Exception e){
            log.error("Feign 查询车主补贴,responseObject={},orderNo={},ownerOrderNo={}",JSON.toJSONString(responseObject),orderNo,ownerOrderNo,e);
            Cat.logError("Feign 查询车主补贴",e);
            throw e;
        }finally {
            t.complete();
        }
    }


    /**
     * 管理后台修改时间校验接口
     * @param modifyOrderConsoleCheckReq
     * @return ResponseData
     */
    public ResponseData modifyOrderCheckForConsole(ModifyOrderConsoleCheckReq modifyOrderConsoleCheckReq) {
        log.info("Feign 修改时间校验,modifyOrderConsoleCheckReq={}", JSON.toJSONString(modifyOrderConsoleCheckReq));
        ResponseData<?> responseObject = feignOrderModifyService.modifyOrderCheckForConsole(modifyOrderConsoleCheckReq);
        return responseObject;
    }

    public RenterGoodsDetailDTO getRenterGoodsFromRemot(String renterOrderNo,boolean isNeedPrice){
        ResponseData<RenterGoodsDetailDTO> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "获取租客商品信息");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"feignGoodsService.queryRenterGoodsDetail");
            log.info("Feign 开始获取租客商品信息,renterOrderNo={}", renterOrderNo);
            Cat.logEvent(CatConstants.FEIGN_PARAM,renterOrderNo);
            responseObject =  feignGoodsService.queryRenterGoodsDetail(renterOrderNo,  isNeedPrice);
            Cat.logEvent(CatConstants.FEIGN_RESULT,renterOrderNo);
            ResponseCheckUtil.checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
            return responseObject.getData();
        }catch (Exception e){
            log.error("Feign 获取租客商品信息异常,responseObject={},renterOrderNo={}",JSON.toJSONString(responseObject),renterOrderNo,e);
            Cat.logError("Feign 获取租客商品信息异常",e);
            throw e;
        }finally {
            t.complete();
        }
    }

    public RenterOrderDTO getRenterOrderFromRemot(String orderNo){
        ResponseData<RenterOrderDTO> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "获取租客商品信息");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"feignGoodsService.queryRenterOrderByOrderNo");
            log.info("Feign 开始获取租客有效子订单,orderNo={}", orderNo);
            Cat.logEvent(CatConstants.FEIGN_PARAM,orderNo);
            responseObject =  feignOrderService.queryRenterOrderByOrderNo(orderNo);
            Cat.logEvent(CatConstants.FEIGN_RESULT,orderNo);
            ResponseCheckUtil.checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
            return responseObject.getData();
        }catch (Exception e){
            log.error("Feign 获取租客商品信息异常,responseObject={},orderNo={}",JSON.toJSONString(responseObject),orderNo,e);
            Cat.logError("Feign 获取租客有效子订单异常",e);
            throw e;
        }finally {
            t.complete();
        }
    }
}
