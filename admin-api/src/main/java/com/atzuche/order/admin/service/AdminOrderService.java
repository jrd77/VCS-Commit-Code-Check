package com.atzuche.order.admin.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.admin.common.AdminUserUtil;
import com.atzuche.order.admin.enums.YesNoEnum;
import com.atzuche.order.admin.vo.req.order.*;
import com.atzuche.order.admin.vo.resp.MemAvailableCouponVO;
import com.atzuche.order.admin.vo.resp.order.AdminModifyOrderFeeCompareVO;
import com.atzuche.order.admin.vo.resp.order.AdminModifyOrderFeeVO;
import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.commons.entity.dto.OrderTransferRecordDTO;
import com.atzuche.order.commons.entity.dto.SearchCashWithdrawalReqDTO;
import com.atzuche.order.commons.entity.orderDetailDto.OrderDetailReqDTO;
import com.atzuche.order.commons.entity.orderDetailDto.OrderDetailRespDTO;
import com.atzuche.order.commons.exceptions.RemoteCallException;
import com.atzuche.order.commons.vo.req.*;
import com.atzuche.order.commons.vo.res.AdminOrderJudgeDutyResVO;
import com.atzuche.order.commons.vo.res.NormalOrderCostCalculateResVO;
import com.atzuche.order.commons.vo.res.order.*;
import com.atzuche.order.open.service.FeignCashWithdrawalService;
import com.atzuche.order.open.service.FeignOrderCostService;
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

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class AdminOrderService {
    @Autowired
    private FeignOrderUpdateService feignOrderUpdateService;
    @Autowired
    private FeignOrderModifyService feignOrderModifyService;

    @Autowired
    private FeignOrderDetailService feignOrderDetailService;

    @Autowired
    private FeignOrderCostService feignOrderCostService;
    @Autowired
    private FeignCashWithdrawalService feignCashWithdrawalService;


    public MemAvailableCouponVO getPreOrderCouponList(NormalOrderCostCalculateReqVO reqVO){
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "订单CoreAPI");
        MemAvailableCouponVO memAvailableCouponVO = new MemAvailableCouponVO();
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"feignOrderCostService.cancelOrder");
            log.info("Feign 开始下单前费用试算,reqVO={}", JSON.toJSONString(reqVO));
            Cat.logEvent(CatConstants.FEIGN_PARAM,JSON.toJSONString(reqVO));
            ResponseData<NormalOrderCostCalculateResVO> responseObject = feignOrderCostService.submitOrderBeforeCostCalculate(reqVO);
            log.info("response=[{}]",JSON.toJSONString(responseObject));
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
            NormalOrderCostCalculateResVO resVO = responseObject.getData();
            ReductionVO reductionVO = resVO.getReduction();
            if(reductionVO!=null){
                CouponReductionVO couponReductionVO = reductionVO.getCouponReduction();
                CarOwnerCouponReductionVO carOwnerCouponReductionVO = reductionVO.getCarOwnerCouponReduction();
                if(couponReductionVO!=null){
                    List<DisCouponMemInfoVO> discoupons = couponReductionVO.getDiscoupons();
                    if(discoupons!=null){
                        memAvailableCouponVO.setPlatCouponList(extractNotGetCarCouponList(discoupons));
                        memAvailableCouponVO.setGetCarCouponList(extractGetCarCouponList(discoupons));
                    }

                }
                if(carOwnerCouponReductionVO!=null){
                    List<CarOwnerCouponDetailVO>   availableCouponList = carOwnerCouponReductionVO.getAvailableCouponList();
                    memAvailableCouponVO.setCarOwnerCouponDetailVOList(availableCouponList);
                }
            }
            return memAvailableCouponVO;

        }catch (Exception e){
            log.error("调用远程开始下单前费用试算失败,param={}",reqVO,e);
            Cat.logError("Feign 开始下单前费用试算失败",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }
    }

    private List<DisCouponMemInfoVO> extractGetCarCouponList(List<DisCouponMemInfoVO> discoupons) {
        List<DisCouponMemInfoVO> list = new ArrayList<>();
        for(DisCouponMemInfoVO vo:discoupons){
            if("8".equals(vo.getPlatformCouponType())){
                list.add(vo);
            }
        }
        return list;
    }

    private List<DisCouponMemInfoVO> extractNotGetCarCouponList(List<DisCouponMemInfoVO> discoupons) {
        List<DisCouponMemInfoVO> list = new ArrayList<>();
        for(DisCouponMemInfoVO vo:discoupons){
            if(!"8".equals(vo.getPlatformCouponType())){
                list.add(vo);
            }
        }
        return list;
    }


    public ResponseData cancelOrder(CancelOrderVO cancelOrderVO) {
        AdminCancelOrderReqVO cancelOrderReqVO = new AdminCancelOrderReqVO();
        BeanUtils.copyProperties(cancelOrderVO,cancelOrderReqVO);
        if("2".equalsIgnoreCase(cancelOrderVO.getMemRole())){
            String renterNo = getRenterMemNo(cancelOrderVO.getOrderNo());
            cancelOrderReqVO.setMemNo(renterNo);
        }else{
            String ownerNo = getOwnerMemNo(cancelOrderReqVO.getOrderNo());
            cancelOrderReqVO.setMemNo(ownerNo);
        }
        cancelOrderReqVO.setOperatorName(AdminUserUtil.getAdminUser().getAuthName());
        ResponseData<?> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "订单中心服务");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"feignOrderUpdateService.adminCancelOrder");
            log.info("Feign 开始取消订单,cancelOrderReqVO={}", JSON.toJSONString(cancelOrderReqVO));
            Cat.logEvent(CatConstants.FEIGN_PARAM,JSON.toJSONString(cancelOrderReqVO));
            responseObject = feignOrderUpdateService.adminCancelOrder(cancelOrderReqVO);
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
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "订单中心服务");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"FeignOrderUpdateService.adminPlatformCancelOrder");
            log.info("Feign 开始修改订单,platVO={}", JSON.toJSONString(platVO));
            Cat.logEvent(CatConstants.FEIGN_PARAM,JSON.toJSONString(platVO));
            AdminOrderPlatformCancelReqVO adminOrderCancelReqVO = new AdminOrderPlatformCancelReqVO();
            BeanUtils.copyProperties(platVO,adminOrderCancelReqVO);
            ResponseData responseObject = feignOrderUpdateService.adminPlatformCancelOrder(adminOrderCancelReqVO);
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


    public List<OrderTransferRecordDTO> listTransferRecord(String orderNo) {
        ResponseData<List<OrderTransferRecordDTO>> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "订单CoreAPI服务");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"FeignOrderModifyService.listTransferRecord");
            log.info("Feign 管理后台查询换车记录,param={}", JSON.toJSONString(orderNo));
            Cat.logEvent(CatConstants.FEIGN_PARAM,JSON.toJSONString(orderNo));
            responseObject = feignOrderModifyService.listTransferRecord(orderNo);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
            return responseObject.getData();
        }catch (Exception e){
            log.error("Feign 管理后台查询换车记录,responseObject={},req={}",JSON.toJSONString(responseObject),JSON.toJSONString(orderNo),e);
            Cat.logError("Feign 管理后台查询换车记录",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }
    }

    public ResponseData agreeOrder(OwnerAgreeOrRefuseOrderReqVO reqVO) {
        AgreeOrderReqVO agreeOrderReqVO = new AgreeOrderReqVO();
        BeanUtils.copyProperties(reqVO,agreeOrderReqVO);
        agreeOrderReqVO.setOperatorName(AdminUserUtil.getAdminUser().getAuthName());
        agreeOrderReqVO.setIsConsoleInvoke(Integer.valueOf(YesNoEnum.YES.getType()));

        String ownerNo = getOwnerMemNo(reqVO.getOrderNo());
        agreeOrderReqVO.setMemNo(ownerNo);

        ResponseData<?> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "订单中心服务");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"feignOrderUpdateService.adminOwnerAgreeOrder");
            log.info("Feign 开始车主同意订单,agreeOrderReqVO:[{}]", JSON.toJSONString(agreeOrderReqVO));
            Cat.logEvent(CatConstants.FEIGN_PARAM,JSON.toJSONString(agreeOrderReqVO));
            responseObject = feignOrderUpdateService.adminOwnerAgreeOrder(agreeOrderReqVO);
            log.info("Feign 返回车主同意订单,responseObject:[{}]", JSON.toJSONString(responseObject));
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
        }catch (Exception e){
            log.error("Feign 车主同意订单异常,responseObject:[{}],agreeOrderReqVO:[{}]",JSON.toJSONString(responseObject),
                    JSON.toJSONString(agreeOrderReqVO),e);
            Cat.logError("Feign 车主同意订单异常",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }
        return responseObject;
    }


    public ResponseData refuseOrder(OwnerAgreeOrRefuseOrderReqVO reqVO) {
        RefuseOrderReqVO refuseOrderReqVO = new RefuseOrderReqVO();
        BeanUtils.copyProperties(reqVO,refuseOrderReqVO);
        refuseOrderReqVO.setOperatorName(AdminUserUtil.getAdminUser().getAuthName());
        refuseOrderReqVO.setIsConsoleInvoke(Integer.valueOf(YesNoEnum.YES.getType()));

        String ownerNo = getOwnerMemNo(reqVO.getOrderNo());
        refuseOrderReqVO.setMemNo(ownerNo);
        ResponseData<?> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "订单中心服务");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"feignOrderUpdateService.adminOwnerRefuseOrder");
            log.info("Feign 开始车主拒绝订单,refuseOrderReqVO={}", JSON.toJSONString(refuseOrderReqVO));
            Cat.logEvent(CatConstants.FEIGN_PARAM,JSON.toJSONString(refuseOrderReqVO));
            responseObject = feignOrderUpdateService.adminOwnerRefuseOrder(refuseOrderReqVO);
            log.info("Feign 返回车主拒绝订单,responseObject:[{}]", JSON.toJSONString(responseObject));
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
        }catch (Exception e){
            log.error("Feign 车主拒绝订单异常,responseObject={},refuseOrderReqVO={}",JSON.toJSONString(responseObject),JSON.toJSONString(refuseOrderReqVO),e);
            Cat.logError("Feign 车主拒绝订单异常",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }
        return responseObject;
    }


    public ResponseData cancelOrderJudgeDuty(CancelOrderJudgeDutyReqVO reqVO) {
        AdminOrderCancelJudgeDutyReqVO adminOrderCancelJudgeDutyReqVO = new AdminOrderCancelJudgeDutyReqVO();
        BeanUtils.copyProperties(reqVO,adminOrderCancelJudgeDutyReqVO);
        adminOrderCancelJudgeDutyReqVO.setOperatorName(AdminUserUtil.getAdminUser().getAuthName());

        ResponseData<?> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "订单中心服务");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"feignOrderUpdateService.adminCancelOrderJudgeDuty");
            log.info("Feign 开始责任判定,refuseOrderReqVO={}", JSON.toJSONString(adminOrderCancelJudgeDutyReqVO));
            Cat.logEvent(CatConstants.FEIGN_PARAM,JSON.toJSONString(adminOrderCancelJudgeDutyReqVO));
            responseObject = feignOrderUpdateService.adminCancelOrderJudgeDuty(adminOrderCancelJudgeDutyReqVO);
            log.info("Feign 返回责任判定,responseObject:[{}]", JSON.toJSONString(responseObject));
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
        }catch (Exception e){
            log.error("Feign 责任判定异常,responseObject={},adminOrderCancelJudgeDutyReqVO={}",JSON.toJSONString(responseObject),JSON.toJSONString(adminOrderCancelJudgeDutyReqVO),e);
            Cat.logError("Feign 责任判定异常",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }
        return responseObject;
    }


    public ResponseData<AdminOrderJudgeDutyResVO> cancelOrderJudgeDutyList(String orderNo) {
        AdminOrderJudgeDutyReqVO adminOrderJudgeDutyReqVO = new AdminOrderJudgeDutyReqVO();
        adminOrderJudgeDutyReqVO.setOrderNo(orderNo);

        ResponseData<AdminOrderJudgeDutyResVO> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "订单中心服务");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"feignOrderUpdateService.adminOrderJudgeDutyList");
            log.info("Feign 开始获取责任判定列表,adminOrderJudgeDutyReqVO={}", JSON.toJSONString(adminOrderJudgeDutyReqVO));
            Cat.logEvent(CatConstants.FEIGN_PARAM,JSON.toJSONString(adminOrderJudgeDutyReqVO));
            responseObject = feignOrderUpdateService.adminOrderJudgeDutyList(adminOrderJudgeDutyReqVO);
            log.info("Feign 返回责任判定列表,responseObject:[{}]", JSON.toJSONString(responseObject));
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
        }catch (Exception e){
            log.error("Feign 获取责任判定列表异常,responseObject={},adminOrderJudgeDutyReqVO={}",JSON.toJSONString(responseObject),JSON.toJSONString(adminOrderJudgeDutyReqVO),e);
            Cat.logError("Feign 获取责任判定列表异常",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }
        return responseObject;
    }
    
    
    /**
     * 获取欠款
     * @param memNo
     * @return Integer
     */
    public Integer getDebtAmt(String memNo){
    	SearchCashWithdrawalReqDTO req = new SearchCashWithdrawalReqDTO();
    	req.setMemNo(memNo);
        ResponseData<?> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "订单CoreAPI服务");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"feignOrderDetailService.getDebtAmt");
            log.info("Feign 获取欠款,param={}", JSON.toJSONString(req));
            Cat.logEvent(CatConstants.FEIGN_PARAM,JSON.toJSONString(req));
            responseObject =feignCashWithdrawalService.getDebtAmt(req);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
            if (responseObject.getData() != null) {
            	return Integer.valueOf(responseObject.getData().toString());
            }
        }catch (Exception e){
            log.error("Feign 管理后台获取欠款,responseObject={},modifyOrderReq={}",JSON.toJSONString(responseObject),JSON.toJSONString(req),e);
            Cat.logError("Feign 管理后台获取欠款",e);
            t.setStatus(e);
        }finally {
            t.complete();
        }
        return 0;
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
