package com.atzuche.order.coreapi.controller;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.cashieraccount.service.CashierPayService;
import com.atzuche.order.cashieraccount.vo.req.pay.OrderPaySignReqVO;
import com.atzuche.order.commons.LocalDateTimeUtils;
import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.OrderReqContext;
import com.atzuche.order.commons.constant.OrderConstant;
import com.atzuche.order.commons.enums.ChangeSourceEnum;
import com.atzuche.order.commons.enums.OrderStatusEnum;
import com.atzuche.order.commons.exceptions.InputErrorException;
import com.atzuche.order.commons.vo.req.AdminOrderReqVO;
import com.atzuche.order.commons.vo.req.NormalOrderReqVO;
import com.atzuche.order.commons.vo.req.OrderReqVO;
import com.atzuche.order.commons.vo.res.OrderResVO;
import com.atzuche.order.coreapi.filter.LongOrderFilterChain;
import com.atzuche.order.coreapi.filter.OrderFilterChain;
import com.atzuche.order.coreapi.service.PayCallbackService;
import com.atzuche.order.coreapi.service.SubmitOrderInitContextService;
import com.atzuche.order.coreapi.service.SubmitOrderService;
import com.atzuche.order.coreapi.service.mq.OrderActionMqService;
import com.atzuche.order.coreapi.service.mq.OrderStatusMqService;
import com.atzuche.order.coreapi.service.remote.StockProxyService;
import com.atzuche.order.parentorder.entity.OrderRecordEntity;
import com.atzuche.order.parentorder.service.OrderRecordService;
import com.autoyol.commons.utils.GsonUtils;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
import com.autoyol.event.rabbit.neworder.NewOrderMQStatusEventEnum;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 下单
 *
 * @author pengcheng.fu
 * @date 2019/12/23 12:00
 */
@RequestMapping("/order")
@RestController
@AutoDocVersion(version = "订单接口文档")
public class SubmitOrderController {
    private static final Logger LOGGER = LoggerFactory.getLogger(SubmitOrderController.class);

    @Resource
    private SubmitOrderService submitOrderService;
    @Autowired
    private OrderRecordService orderRecordService;
    @Autowired
    private StockProxyService stockService;
    @Autowired
    private SubmitOrderInitContextService submitOrderInitContextService;
    @Autowired
    private OrderFilterChain orderFilterChain;
    @Autowired
    private OrderActionMqService orderActionMqService;
    @Autowired
    private OrderStatusMqService orderStatusMqService;
    @Autowired 
    private PayCallbackService payCallbackService;
    @Autowired
    private CashierPayService cashierPayService;
    @Autowired
    private LongOrderFilterChain longOrderFilterChain;


    @AutoDocMethod(description = "提交订单", value = "提交订单", response = OrderResVO.class)
    @PostMapping("/normal/req")
    public ResponseData<OrderResVO> submitOrder(@Valid @RequestBody NormalOrderReqVO normalOrderReqVO, BindingResult bindingResult) {
        LOGGER.info("Submit order.param is,normalOrderReqVO:[{}]", JSON.toJSONString(normalOrderReqVO));
        if (bindingResult.hasErrors()) {
            Optional<FieldError> error = bindingResult.getFieldErrors().stream().findFirst();
            LOGGER.error("输入错误："+error.get());
            return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), error.isPresent() ?
                    error.get().getDefaultMessage() : ErrorCode.INPUT_ERROR.getText());
        }

        OrderResVO orderResVO = null;

        BeanCopier beanCopier = BeanCopier.create(NormalOrderReqVO.class, OrderReqVO.class, false);
        OrderReqVO orderReqVO = new OrderReqVO();
        beanCopier.copy(normalOrderReqVO, orderReqVO, null);
        orderReqVO.setAbatement(Integer.valueOf(normalOrderReqVO.getAbatement()));
        orderReqVO.setIMEI(normalOrderReqVO.getIMEI());
        orderReqVO.setRentTime(LocalDateTimeUtils.parseStringToDateTime(normalOrderReqVO.getRentTime(),
                LocalDateTimeUtils.DEFAULT_PATTERN));
        orderReqVO.setRevertTime(LocalDateTimeUtils.parseStringToDateTime(normalOrderReqVO.getRevertTime(),
                LocalDateTimeUtils.DEFAULT_PATTERN));

        orderReqVO.setReqTime(LocalDateTime.now());
        orderReqVO.setChangeSource(ChangeSourceEnum.RENTER.getCode());
        OrderReqContext context = submitOrderInitContextService.convertOrderReqContext(orderReqVO);

        try{
            if(StringUtils.isNotBlank(normalOrderReqVO.getLongOwnerCouponNo()) && StringUtils.equals(normalOrderReqVO.getOrderCategory(),"3")) {
                longOrderFilterChain.validate(context);
                orderResVO = submitOrderService.submitLongOrder(context);
            } else if(StringUtils.equals(normalOrderReqVO.getOrderCategory(),"1")){
                orderFilterChain.validate(context);
                orderResVO = submitOrderService.submitOrder(context);
            }else{
                throw new InputErrorException();
            }

            OrderRecordEntity orderRecordEntity = new OrderRecordEntity();
            orderRecordEntity.setErrorCode(ErrorCode.SUCCESS.getCode());
            orderRecordEntity.setErrorTxt(ErrorCode.SUCCESS.getText());
            orderRecordEntity.setMemNo(normalOrderReqVO.getMemNo());
            orderRecordEntity.setOrderNo(orderResVO.getOrderNo());
            orderRecordEntity.setParam(JSON.toJSONString(normalOrderReqVO));
            orderRecordEntity.setResult(JSON.toJSONString(orderResVO));
            orderRecordService.save(orderRecordEntity);
            
            //自动接单的，刷新是否钱包支付抵扣
          //如果是使用钱包，检测是否钱包全额抵扣，推动订单流程。huangjing 200324  刷新钱包
            OrderPaySignReqVO vo = null;
            try {
            	//自动接单 并且 使用钱包
         	   if(orderReqVO.getUseBal() == OrderConstant.YES && orderResVO.getStatus().equals(String.valueOf(OrderStatusEnum.TO_PAY.getStatus()))) {
     	    	   vo = cashierPayService.buildOrderPaySignReqVO(orderResVO.getOrderNo(), orderReqVO.getMemNo(), orderReqVO.getUseBal());
     	           cashierPayService.getPaySignStrNew(vo,payCallbackService);
     	          LOGGER.info("(下单-自动接单)获取支付签名串A.params=[{}]",GsonUtils.toJson(vo));
         	   }
     		} catch (Exception e) {
     			LOGGER.error("刷新钱包支付抵扣:params=[{}]",(vo!=null)?GsonUtils.toJson(vo):"EMPTY",e);
     		}
            
            //发送订单成功的MQ事件
            orderActionMqService.sendCreateOrderSuccess(orderResVO.getOrderNo(),context.getOwnerMemberDto().getMemNo(),context.getRiskAuditId(),orderReqVO,context.getRenterGoodsDetailDto().getIsAutoReplayFlag());
            NewOrderMQStatusEventEnum newOrderMQStatusEventEnum = NewOrderMQStatusEventEnum.ORDER_PRECONFIRM;
            if(StringUtils.equals(orderResVO.getStatus(), String.valueOf(OrderStatusEnum.TO_PAY.getStatus()))) {
                newOrderMQStatusEventEnum = NewOrderMQStatusEventEnum.ORDER_PREPAY;
            }
            String ownerMemNo = null != context.getOwnerMemberDto() ? context.getOwnerMemberDto().getMemNo() : null;
            orderStatusMqService.sendOrderStatusToCreate(orderResVO.getOrderNo(),ownerMemNo,orderResVO.getStatus(),orderReqVO,newOrderMQStatusEventEnum);
        }catch(OrderException orderException){
            String orderNo = orderResVO==null?"":orderResVO.getOrderNo();
            OrderRecordEntity orderRecordEntity = new OrderRecordEntity();
            orderRecordEntity.setErrorCode(orderException.getErrorCode());
            orderRecordEntity.setErrorTxt(orderException.getErrorMsg());
            orderRecordEntity.setMemNo(normalOrderReqVO.getMemNo());
            orderRecordEntity.setOrderNo(orderNo);
            orderRecordEntity.setParam(JSON.toJSONString(normalOrderReqVO));
            orderRecordEntity.setResult(JSON.toJSONString(orderResVO));
            orderRecordService.save(orderRecordEntity);

            //发送订单失败的MQ事件
            String ownerMemNo = null != context.getOwnerMemberDto() ? context.getOwnerMemberDto().getMemNo() : null;
            orderActionMqService.sendCreateOrderFail(orderNo,ownerMemNo,context.getRiskAuditId(),orderReqVO);
            //释放库存
            if(orderNo != null && orderNo.trim().length()>0){
                Integer carNo = Integer.valueOf(normalOrderReqVO.getCarNo());
                stockService.releaseCarStock(orderNo,carNo);
            }
            throw orderException;
        }catch (Exception e){
            String orderNo = orderResVO==null?"":orderResVO.getOrderNo();
            OrderRecordEntity orderRecordEntity = new OrderRecordEntity();
            orderRecordEntity.setErrorCode(ErrorCode.SYS_ERROR.getCode());
            orderRecordEntity.setErrorTxt(ErrorCode.SYS_ERROR.getText());
            orderRecordEntity.setMemNo(normalOrderReqVO.getMemNo());
            orderRecordEntity.setOrderNo(orderResVO==null?"":orderResVO.getOrderNo());
            orderRecordEntity.setParam(JSON.toJSONString(normalOrderReqVO));
            orderRecordEntity.setResult(JSON.toJSONString(orderResVO));
            orderRecordService.save(orderRecordEntity);

            //发送订单失败的MQ事件
            String ownerMemNo = null != context.getOwnerMemberDto() ? context.getOwnerMemberDto().getMemNo() : null;
            orderActionMqService.sendCreateOrderFail(orderNo,ownerMemNo,context.getRiskAuditId(),orderReqVO);
            //释放库存
            if(orderNo != null && orderNo.trim().length()>0){
                Integer carNo = Integer.valueOf(normalOrderReqVO.getCarNo());
                stockService.releaseCarStock(orderNo,carNo);
            }
            throw e;
        }
        return ResponseData.success(orderResVO);
    }



    @AutoDocMethod(description = "提交订单(管理后台)", value = "提交订单(管理后台)", response = OrderResVO.class)
    @PostMapping("/admin/req")
    public ResponseData<OrderResVO> submitOrder(@Valid @RequestBody AdminOrderReqVO adminOrderReqVO,
                                                BindingResult bindingResult) {
        LOGGER.info("Submit order.param is,adminOrderReqVO:[{}]", JSON.toJSONString(adminOrderReqVO));
        if (bindingResult.hasErrors()) {
            Optional<FieldError> error = bindingResult.getFieldErrors().stream().findFirst();
            LOGGER.error("输入错误："+error.get());
            return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), error.isPresent() ?
                    error.get().getDefaultMessage() : ErrorCode.INPUT_ERROR.getText());
        }
        String memNo = adminOrderReqVO.getMemNo();
        if (StringUtils.isBlank(memNo)) {
            return new ResponseData<>(ErrorCode.NEED_LOGIN.getCode(), ErrorCode.NEED_LOGIN.getText());
        }
        OrderResVO orderResVO = null;

        BeanCopier beanCopier = BeanCopier.create(AdminOrderReqVO.class, OrderReqVO.class, false);
        OrderReqVO orderReqVO = new OrderReqVO();
        beanCopier.copy(adminOrderReqVO, orderReqVO, null);

        orderReqVO.setAbatement(Integer.valueOf(adminOrderReqVO.getAbatement()));
        orderReqVO.setIMEI(adminOrderReqVO.getIMEI());
        orderReqVO.setRentTime(LocalDateTimeUtils.parseStringToDateTime(adminOrderReqVO.getRentTime(),
                LocalDateTimeUtils.DEFAULT_PATTERN));
        orderReqVO.setRevertTime(LocalDateTimeUtils.parseStringToDateTime(adminOrderReqVO.getRevertTime(),
                LocalDateTimeUtils.DEFAULT_PATTERN));

        orderReqVO.setReqTime(LocalDateTime.now());
        orderReqVO.setChangeSource(ChangeSourceEnum.CONSOLE.getCode());
        OrderReqContext context = submitOrderInitContextService.convertOrderReqContext(orderReqVO);

        try{
            if(StringUtils.isNotBlank(adminOrderReqVO.getLongOwnerCouponNo()) && StringUtils.equals(adminOrderReqVO.getOrderCategory(),"3")) {
                longOrderFilterChain.validate(context);
                orderResVO = submitOrderService.submitLongOrder(context);
            } else if(StringUtils.equals(adminOrderReqVO.getOrderCategory(),"1")){
                orderFilterChain.validate(context);
                orderResVO = submitOrderService.submitOrder(context);
            }else{
                throw new InputErrorException();
            }
            OrderRecordEntity orderRecordEntity = new OrderRecordEntity();
            orderRecordEntity.setErrorCode(ErrorCode.SUCCESS.getCode());
            orderRecordEntity.setErrorTxt(ErrorCode.SUCCESS.getText());
            orderRecordEntity.setMemNo(adminOrderReqVO.getMemNo());
            orderRecordEntity.setOrderNo(orderResVO.getOrderNo());
            orderRecordEntity.setParam(JSON.toJSONString(adminOrderReqVO));
            orderRecordEntity.setResult(JSON.toJSONString(orderResVO));
            orderRecordService.save(orderRecordEntity);
            
            //自动接单的，刷新是否钱包支付抵扣
            //如果是使用钱包，检测是否钱包全额抵扣，推动订单流程。huangjing 200324  刷新钱包
            OrderPaySignReqVO vo = null;
            try {
              	//自动接单 并且 使用钱包
           	   if(orderReqVO.getUseBal() == OrderConstant.YES && orderResVO.getStatus().equals(String.valueOf(OrderStatusEnum.TO_PAY.getStatus()))) {
       	    	   vo = cashierPayService.buildOrderPaySignReqVO(orderResVO.getOrderNo(), orderReqVO.getMemNo(), orderReqVO.getUseBal());
       	           cashierPayService.getPaySignStrNew(vo,payCallbackService);
       	          LOGGER.info("(下单-自动接单)获取支付签名串A.params=[{}]",GsonUtils.toJson(vo));
           	   }
       		} catch (Exception e) {
       			LOGGER.error("刷新钱包支付抵扣:params=[{}]",(vo!=null)?GsonUtils.toJson(vo):"EMPTY",e);
       		}
              
            //发送订单成功的MQ事件
            orderActionMqService.sendCreateOrderSuccess(orderResVO.getOrderNo(),context.getOwnerMemberDto().getMemNo(),context.getRiskAuditId(),orderReqVO,context.getRenterGoodsDetailDto().getIsAutoReplayFlag());
            NewOrderMQStatusEventEnum newOrderMQStatusEventEnum = NewOrderMQStatusEventEnum.ORDER_PRECONFIRM;
            if(StringUtils.equals(orderResVO.getStatus(), String.valueOf(OrderStatusEnum.TO_PAY.getStatus()))) {
                newOrderMQStatusEventEnum = NewOrderMQStatusEventEnum.ORDER_PREPAY;
            }
            String ownerMemNo = null != context.getOwnerMemberDto() ? context.getOwnerMemberDto().getMemNo() : null;
            orderStatusMqService.sendOrderStatusToCreate(orderResVO.getOrderNo(),ownerMemNo,orderResVO.getStatus(),orderReqVO,newOrderMQStatusEventEnum);
        }catch(OrderException orderException){
            String orderNo = orderResVO==null?"":orderResVO.getOrderNo();
            OrderRecordEntity orderRecordEntity = new OrderRecordEntity();
            orderRecordEntity.setErrorCode(orderException.getErrorCode());
            orderRecordEntity.setErrorTxt(orderException.getErrorMsg());
            orderRecordEntity.setMemNo(adminOrderReqVO.getMemNo());
            orderRecordEntity.setOrderNo(orderNo);
            orderRecordEntity.setParam(JSON.toJSONString(adminOrderReqVO));
            orderRecordEntity.setResult(JSON.toJSONString(orderResVO));
            orderRecordService.save(orderRecordEntity);

            //发送订单失败的MQ事件
            String ownerMemNo = null != context.getOwnerMemberDto() ? context.getOwnerMemberDto().getMemNo() : null;
            orderActionMqService.sendCreateOrderFail(orderNo,ownerMemNo,context.getRiskAuditId(),orderReqVO);
            //释放库存
            if(orderNo != null && orderNo.trim().length()>0){
                Integer carNo = Integer.valueOf(adminOrderReqVO.getCarNo());
                stockService.releaseCarStock(orderNo,carNo);
            }
            throw orderException;
        }catch (Exception e){
            String orderNo = orderResVO==null?"":orderResVO.getOrderNo();
            OrderRecordEntity orderRecordEntity = new OrderRecordEntity();
            orderRecordEntity.setErrorCode(ErrorCode.SYS_ERROR.getCode());
            orderRecordEntity.setErrorTxt(ErrorCode.SYS_ERROR.getText());
            orderRecordEntity.setMemNo(adminOrderReqVO.getMemNo());
            orderRecordEntity.setOrderNo(orderResVO==null?"":orderResVO.getOrderNo());
            orderRecordEntity.setParam(JSON.toJSONString(adminOrderReqVO));
            orderRecordEntity.setResult(JSON.toJSONString(orderResVO));
            orderRecordService.save(orderRecordEntity);

            //发送订单失败的MQ事件
            String ownerMemNo = null != context.getOwnerMemberDto() ? context.getOwnerMemberDto().getMemNo() : null;
            orderActionMqService.sendCreateOrderFail(orderNo,ownerMemNo,context.getRiskAuditId(),orderReqVO);
            if(orderNo != null && orderNo.trim().length()>0){
                Integer carNo = Integer.valueOf(adminOrderReqVO.getCarNo());
                stockService.releaseCarStock(orderNo,carNo);
            }
            throw e;
        }

        return ResponseData.success(orderResVO);
    }

}
