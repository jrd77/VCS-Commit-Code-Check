package com.atzuche.order.admin.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.admin.vo.req.order.AdminModifyOrderReqVO;
import com.atzuche.order.admin.vo.req.order.CancelOrderByPlatVO;
import com.atzuche.order.admin.vo.req.order.CancelOrderVO;
import com.atzuche.order.admin.vo.req.order.OrderModifyConfirmReqVO;
import com.atzuche.order.admin.vo.resp.order.AdminModifyOrderFeeCompareVO;
import com.atzuche.order.admin.vo.resp.order.AdminModifyOrderFeeVO;
import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.commons.entity.orderDetailDto.OrderDetailReqDTO;
import com.atzuche.order.commons.entity.orderDetailDto.OrderDetailRespDTO;
import com.atzuche.order.commons.exceptions.RemoteCallException;
import com.atzuche.order.commons.vo.req.AdminOrderCancelReqVO;
import com.atzuche.order.commons.vo.req.CancelOrderReqVO;
import com.atzuche.order.commons.vo.req.ModifyApplyHandleReq;
import com.atzuche.order.commons.vo.req.ModifyOrderReqVO;
import com.atzuche.order.open.service.FeignOrderDetailService;
import com.atzuche.order.open.service.FeignOrderModifyService;
import com.atzuche.order.open.service.FeignOrderUpdateService;
import com.atzuche.order.open.vo.ModifyOrderAppReqVO;
import com.atzuche.order.open.vo.ModifyOrderCompareVO;
import com.atzuche.order.open.vo.ModifyOrderFeeVO;
import com.atzuche.order.open.vo.request.TransferReq;
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
        if("1".equalsIgnoreCase(cancelOrderVO.getMemRole())){
            String renterNo = getRenterMemNo(cancelOrderVO.getOrderNo());
            cancelOrderReqVO.setMemNo(renterNo);
        }else{
            String ownerNo = getOwnerMemNo(cancelOrderReqVO.getOrderNo());
            cancelOrderReqVO.setMemNo(ownerNo);
        }
        ResponseData<?> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "租客商品信息");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"feignOrderUpdateService.cancelOrder");
            log.info("Feign 开始取消订单,cancelOrderReqVO={}", JSON.toJSONString(cancelOrderReqVO));
            Cat.logEvent(CatConstants.FEIGN_PARAM,JSON.toJSONString(cancelOrderReqVO));
            responseObject = feignOrderUpdateService.cancelOrder(cancelOrderReqVO);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
        }catch (Exception e){
           log.error("Feign 取消订单异常,responseObject={},cancelOrderReqVO={}",JSON.toJSONString(responseObject),JSON.toJSONString(cancelOrderReqVO),e);
            Cat.logError("Feign 取消订单异常",e);
            t.setStatus(e);
            throw e;
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
            checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
        }catch (Exception e){
            log.error("调用远程取消订单失败,param={}",platVO,e);
            Cat.logError("Feign 取消订单失败",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }
    }


    public void modifyOrder(ModifyOrderReqVO modifyOrderReq) {
        ResponseData<?> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "租客商品信息");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"feignOrderUpdateService.cancelOrder");
            log.info("Feign 开始修改订单,modifyOrderReq={}", JSON.toJSONString(modifyOrderReq));
            Cat.logEvent(CatConstants.FEIGN_PARAM,JSON.toJSONString(modifyOrderReq));
            responseObject = feignOrderModifyService.modifyOrderForConsole(modifyOrderReq);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
        }catch (Exception e){
            log.error("Feign 修改订单异常,responseObject={},modifyOrderReq={}",JSON.toJSONString(responseObject),JSON.toJSONString(modifyOrderReq),e);
            Cat.logError("Feign 修改订单异常",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }
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
            checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
        }catch (Exception e){
            log.error("Feign 管理后台替车主操作修改申请,responseObject={},modifyOrderReq={}",JSON.toJSONString(responseObject),JSON.toJSONString(req),e);
            Cat.logError("Feign 管理后台替车主操作修改申请",e);
            t.setStatus(e);
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
            checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
            ModifyOrderCompareVO modifyOrderCompareVO = responseObject.getData();

            log.info("modifyOrderCompareVo is {}",modifyOrderCompareVO);

            ModifyOrderFeeVO initModifyOrderFeeVO = modifyOrderCompareVO.getInitModifyOrderFeeVO();
            if(initModifyOrderFeeVO==null){
                throw new RuntimeException("初始费用计算异常");
            }
            ModifyOrderFeeVO  updateModifyOrderFeeVO = modifyOrderCompareVO.getUpdateModifyOrderFeeVO();
            if(updateModifyOrderFeeVO==null){
                throw new RuntimeException("修改后费用计算异常");
            }


            AdminModifyOrderFeeCompareVO adminModifyOrderFeeCompareVO = new AdminModifyOrderFeeCompareVO();
            AdminModifyOrderFeeVO before = new AdminModifyOrderFeeVO();
            if(initModifyOrderFeeVO.getModifyOrderCostVO()!=null) {
                BeanUtils.copyProperties(initModifyOrderFeeVO.getModifyOrderCostVO(), before);
            }
            if(initModifyOrderFeeVO.getModifyOrderDeductVO()!=null) {
                BeanUtils.copyProperties(initModifyOrderFeeVO.getModifyOrderDeductVO(), before);
            }
            if(initModifyOrderFeeVO.getModifyOrderFineVO()!=null) {
                BeanUtils.copyProperties(initModifyOrderFeeVO.getModifyOrderFineVO(), before);
            }

            adminModifyOrderFeeCompareVO.setBefore(before);

            AdminModifyOrderFeeVO after = new AdminModifyOrderFeeVO();
            if(updateModifyOrderFeeVO.getModifyOrderCostVO()!=null) {
                BeanUtils.copyProperties(updateModifyOrderFeeVO.getModifyOrderCostVO(), after);
            }
            if(updateModifyOrderFeeVO.getModifyOrderDeductVO()!=null) {
                BeanUtils.copyProperties(updateModifyOrderFeeVO.getModifyOrderDeductVO(), after);
            }
            if(updateModifyOrderFeeVO.getModifyOrderFineVO()!=null) {
                BeanUtils.copyProperties(updateModifyOrderFeeVO.getModifyOrderFineVO(), after);
            }
            adminModifyOrderFeeCompareVO.setAfter(after);
            return  adminModifyOrderFeeCompareVO;

        }catch (Exception e){
            log.error("Feign 获取订单修改前的费用,responseObject={},modifyOrderReq={}",JSON.toJSONString(responseObject),JSON.toJSONString(reqVO),e);
            Cat.logError("Feign 获取订单修改前的费用",e);
            t.setStatus(e);
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
            checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
            String memNo = responseObject.getData().getRenterMember().getMemNo();
            return memNo;
        }catch (Exception e){
            log.error("Feign 管理后台替车主操作修改申请,responseObject={},modifyOrderReq={}",JSON.toJSONString(responseObject),JSON.toJSONString(req),e);
            Cat.logError("Feign 管理后台替车主操作修改申请",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }


    }

    public String getOwnerMemNo(String orderNo){
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
            checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
            String memNo = responseObject.getData().getOwnerMember().getMemNo();
            return memNo;
        }catch (Exception e){
            log.error("Feign 管理后台替车主操作修改申请,responseObject={},modifyOrderReq={}",JSON.toJSONString(responseObject),JSON.toJSONString(req),e);
            Cat.logError("Feign 管理后台替车主操作修改申请",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }


    }

    public void transferCar(TransferReq req) {
        ResponseData<?> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "订单CoreAPI服务");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"feignOrderUpdateService.ownerHandleModifyApplication");
            log.info("Feign 管理后台换车操作,param={}", JSON.toJSONString(req));
            Cat.logEvent(CatConstants.FEIGN_PARAM,JSON.toJSONString(req));
            responseObject = feignOrderModifyService.transfer(req);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
        }catch (Exception e){
            log.error("Feign 管理后台换车操作,responseObject={},modifyOrderReq={}",JSON.toJSONString(responseObject),JSON.toJSONString(req),e);
            Cat.logError("Feign 管理后台换车操作",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }
    }

    private void checkResponse(ResponseData responseObject){
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
