package com.atzuche.order.admin.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.admin.controller.RenterInfoController;
import com.atzuche.order.admin.exception.OrderCancelErrException;
import com.atzuche.order.admin.exception.OrderCancelFailException;
import com.atzuche.order.admin.exception.OrderModifyErrException;
import com.atzuche.order.admin.exception.OrderModifyFailException;
import com.atzuche.order.admin.vo.req.order.AdminModifyOrderReqVO;
import com.atzuche.order.admin.vo.req.order.CancelOrderByPlatVO;
import com.atzuche.order.admin.vo.req.order.CancelOrderVO;
import com.atzuche.order.admin.vo.req.order.OrderModifyConfirmReqVO;
import com.atzuche.order.admin.vo.resp.order.AdminModifyOrderFeeCompareVO;
import com.atzuche.order.admin.vo.resp.order.AdminModifyOrderFeeVO;
import com.atzuche.order.car.RenterCarDetailFailException;
import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.commons.entity.orderDetailDto.OrderDetailReqDTO;
import com.atzuche.order.commons.entity.orderDetailDto.OrderDetailRespDTO;
import com.atzuche.order.commons.vo.req.AdminOrderCancelReqVO;
import com.atzuche.order.commons.vo.req.CancelOrderReqVO;
import com.atzuche.order.commons.vo.req.ModifyApplyHandleReq;
import com.atzuche.order.commons.vo.req.ModifyOrderReqVO;
import com.atzuche.order.open.service.FeignOrderDetailService;
import com.atzuche.order.open.service.FeignOrderModifyService;
import com.atzuche.order.open.service.FeignOrderUpdateService;
import com.atzuche.order.open.vo.ModifyOrderAppReqVO;
import com.atzuche.order.open.vo.ModifyOrderCompareVO;
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

    @Autowired
    private FeignOrderDetailService feignOrderDetailService;

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

    /**
     * 平台取消订单
     * @param platVO
     */
    public void cancelOrderByAdmin(CancelOrderByPlatVO platVO){
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "订单CoreAPI");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"FeignOrderUpdateService.cancelOrder");
            log.info("Feign 开始修改订单,platVO={}", JSON.toJSONString(platVO));
            Cat.logEvent(CatConstants.FEIGN_PARAM,JSON.toJSONString(platVO));
            AdminOrderCancelReqVO adminOrderCancelReqVO = new AdminOrderCancelReqVO();
            BeanUtils.copyProperties(platVO,adminOrderCancelReqVO);
            ResponseData responseObject = feignOrderUpdateService.adminCancelOrder(adminOrderCancelReqVO);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            if(responseObject == null || !ErrorCode.SUCCESS.getCode().equals(responseObject.getResCode())){
                log.error("Feign 取消订单失败,responseObject={},cancelReq={}",JSON.toJSONString(responseObject),JSON.toJSONString(platVO));
                OrderModifyFailException failException = new OrderModifyFailException();
                Cat.logError("Feign 取消订单失败",failException);
                throw failException;
            }
            t.setStatus(Transaction.SUCCESS);
        }catch (RenterCarDetailFailException e){
            Cat.logError("Feign 取消订单失败",e);
            t.setStatus(e);
            throw e;
        }catch (Exception e){
            log.error("调用远程取消订单失败,param={}",platVO,e);
            Cat.logError("Feign 取消订单失败",e);
            throw e;
        }finally {
            t.complete();
        }
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

    public void modificationConfirm(OrderModifyConfirmReqVO reqVO) {
        ModifyApplyHandleReq req = new ModifyApplyHandleReq();
        BeanUtils.copyProperties(reqVO,req);
        ResponseData<?> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "订单CoreAPI服务");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"feignOrderUpdateService.ownerHandleModifyApplication");
            log.info("Feign 管理后台替车主操作修改申请,param={}", JSON.toJSONString(req));
            Cat.logEvent(CatConstants.FEIGN_PARAM,JSON.toJSONString(req));
            responseObject = feignOrderModifyService.ownerHandleModifyApplication(req);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            if(responseObject == null || !ErrorCode.SUCCESS.getCode().equals(responseObject.getResCode())){
                log.error("Feign 管理后台替车主操作修改申请,responseObject={},modifyOrderReq={}",JSON.toJSONString(responseObject),JSON.toJSONString(req));
                OrderModifyFailException failException = new OrderModifyFailException();
                Cat.logError("Feign 管理后台替车主操作修改申请",failException);
                throw failException;
            }
            t.setStatus(Transaction.SUCCESS);
        }catch (Exception e){
            log.error("Feign 管理后台替车主操作修改申请,responseObject={},modifyOrderReq={}",JSON.toJSONString(responseObject),JSON.toJSONString(req),e);
            Cat.logError("Feign 管理后台替车主操作修改申请",e);
            throw e;
        }finally {
            t.complete();
        }
    }

    public AdminModifyOrderFeeCompareVO preModifyOrderFee(AdminModifyOrderReqVO reqVO,String renterNo){
        log.info("preModifyOrderFee method reqVO={},renterNo={}",JSON.toJSONString(reqVO),renterNo);
        ResponseData<ModifyOrderCompareVO> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "订单CoreAPI服务");
        ModifyOrderAppReqVO modifyOrderAppReqVO = new ModifyOrderAppReqVO();
        BeanUtils.copyProperties(reqVO,modifyOrderAppReqVO);
        modifyOrderAppReqVO.setMemNo(renterNo);
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"feignOrderDetailService.getOrderDetail");
            log.info("Feign 获取订单修改前的费用,param={}", JSON.toJSONString(modifyOrderAppReqVO));
            Cat.logEvent(CatConstants.FEIGN_PARAM,JSON.toJSONString(modifyOrderAppReqVO));
            responseObject =feignOrderModifyService.preModifyOrderFee(modifyOrderAppReqVO);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            log.info("responseData is {}",JSON.toJSONString(responseObject));
            if(responseObject==null||!ErrorCode.SUCCESS.getCode().equalsIgnoreCase(responseObject.getResCode())){

                throw new RuntimeException("费用计算异常");
            }
            t.setStatus(Transaction.SUCCESS);
            ModifyOrderCompareVO modifyOrderCompareVO = responseObject.getData();

            AdminModifyOrderFeeCompareVO adminModifyOrderFeeCompareVO = new AdminModifyOrderFeeCompareVO();
            AdminModifyOrderFeeVO before = new AdminModifyOrderFeeVO();
            if (modifyOrderCompareVO.getInitModifyOrderFeeVO().getModifyOrderCostVO() != null) {
            	BeanUtils.copyProperties(modifyOrderCompareVO.getInitModifyOrderFeeVO().getModifyOrderCostVO(),before);
            }
            if (modifyOrderCompareVO.getInitModifyOrderFeeVO().getModifyOrderDeductVO() != null) {
            	BeanUtils.copyProperties(modifyOrderCompareVO.getInitModifyOrderFeeVO().getModifyOrderDeductVO(),before);
            }
            if (modifyOrderCompareVO.getInitModifyOrderFeeVO().getModifyOrderFineVO() != null) {
            	BeanUtils.copyProperties(modifyOrderCompareVO.getInitModifyOrderFeeVO().getModifyOrderFineVO(),before);
            }
            adminModifyOrderFeeCompareVO.setBefore(before);

            AdminModifyOrderFeeVO after = new AdminModifyOrderFeeVO();
            if (modifyOrderCompareVO.getUpdateModifyOrderFeeVO().getModifyOrderCostVO() != null) {
            	BeanUtils.copyProperties(modifyOrderCompareVO.getUpdateModifyOrderFeeVO().getModifyOrderCostVO(),after);
            }
            if (modifyOrderCompareVO.getUpdateModifyOrderFeeVO().getModifyOrderDeductVO() != null) {
            	BeanUtils.copyProperties(modifyOrderCompareVO.getUpdateModifyOrderFeeVO().getModifyOrderDeductVO(),after);
            }
            if (modifyOrderCompareVO.getUpdateModifyOrderFeeVO().getModifyOrderFineVO() != null) {
            	BeanUtils.copyProperties(modifyOrderCompareVO.getUpdateModifyOrderFeeVO().getModifyOrderFineVO(),after);
            }
            adminModifyOrderFeeCompareVO.setAfter(after);
            return  adminModifyOrderFeeCompareVO;

        }catch (Exception e){
            log.error("Feign 获取订单修改前的费用,responseObject={},modifyOrderReq={}",JSON.toJSONString(responseObject),JSON.toJSONString(reqVO),e);
            Cat.logError("Feign 获取订单修改前的费用",e);
            throw e;
        }finally {
            t.complete();
        }

    }

    public String getRenterMemNo(String orderNo){
        OrderDetailReqDTO req = new OrderDetailReqDTO();
        req.setOrderNo(orderNo);
        ResponseData<OrderDetailRespDTO> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "订单CoreAPI服务");

        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"feignOrderDetailService.getOrderDetail");
            log.info("Feign 获取订单详情,param={}", JSON.toJSONString(req));
            Cat.logEvent(CatConstants.FEIGN_PARAM,JSON.toJSONString(req));
             responseObject =feignOrderDetailService.getOrderDetail(req);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            if(responseObject==null||!ErrorCode.SUCCESS.getCode().equalsIgnoreCase(responseObject.getResCode())){
                throw new RenterInfoController.RenterNotFoundException(orderNo);
            }
            t.setStatus(Transaction.SUCCESS);
            String memNo = responseObject.getData().getRenterMember().getMemNo();
            return memNo;
        }catch (Exception e){
            log.error("Feign 管理后台替车主操作修改申请,responseObject={},modifyOrderReq={}",JSON.toJSONString(responseObject),JSON.toJSONString(req),e);
            Cat.logError("Feign 管理后台替车主操作修改申请",e);
            throw e;
        }finally {
            t.complete();
        }


    }
}
