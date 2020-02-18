package com.atzuche.order.coreapi.controller;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.car.CarProxyService;
import com.atzuche.order.commons.LocalDateTimeUtils;
import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.OrderReqContext;
import com.atzuche.order.commons.entity.dto.OwnerGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.OwnerMemberDTO;
import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.RenterMemberDTO;
import com.atzuche.order.commons.enums.OrderStatusEnum;
import com.atzuche.order.commons.vo.req.AdminOrderReqVO;
import com.atzuche.order.commons.vo.req.NormalOrderReqVO;
import com.atzuche.order.commons.vo.req.OrderReqVO;
import com.atzuche.order.commons.vo.res.OrderResVO;
import com.atzuche.order.coreapi.common.conver.OrderCommonConver;
import com.atzuche.order.coreapi.filter.OrderFilterChain;
import com.atzuche.order.coreapi.service.mq.OrderActionMqService;
import com.atzuche.order.coreapi.service.mq.OrderStatusMqService;
import com.atzuche.order.coreapi.service.remote.StockProxyService;
import com.atzuche.order.coreapi.service.SubmitOrderService;
import com.atzuche.order.mem.MemProxyService;
import com.atzuche.order.parentorder.entity.OrderRecordEntity;
import com.atzuche.order.parentorder.service.OrderRecordService;
import com.atzuche.order.rentercommodity.service.RenterCommodityService;
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
    private MemProxyService memberService;
    @Autowired
    private CarProxyService goodsService;

    @Autowired
    private OrderCommonConver orderCommonConver;

    @Autowired
    private RenterCommodityService renterCommodityService;

    @Autowired
    private OrderFilterChain orderFilterChain;

    @Autowired
    private OrderActionMqService orderActionMqService;

    @Autowired
    private OrderStatusMqService orderStatusMqService;

    @AutoDocMethod(description = "提交订单", value = "提交订单", response = OrderResVO.class)
    @PostMapping("/normal/req")
    public ResponseData<OrderResVO> submitOrder(@Valid @RequestBody NormalOrderReqVO normalOrderReqVO, BindingResult bindingResult) throws Exception {
        LOGGER.info("Submit order.param is,normalOrderReqVO:[{}]", JSON.toJSONString(normalOrderReqVO));
        if (bindingResult.hasErrors()) {
            Optional<FieldError> error = bindingResult.getFieldErrors().stream().findFirst();
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
        OrderReqContext context = buildOrderReqContext(orderReqVO);
        orderFilterChain.validate(context);
        try{
            orderResVO = submitOrderService.submitOrder(context);
            OrderRecordEntity orderRecordEntity = new OrderRecordEntity();
            orderRecordEntity.setErrorCode(ErrorCode.SUCCESS.getCode());
            orderRecordEntity.setErrorTxt(ErrorCode.SUCCESS.getText());
            orderRecordEntity.setMemNo(normalOrderReqVO.getMemNo());
            orderRecordEntity.setOrderNo(orderResVO.getOrderNo());
            orderRecordEntity.setParam(JSON.toJSONString(normalOrderReqVO));
            orderRecordEntity.setResult(JSON.toJSONString(orderResVO));
            orderRecordService.save(orderRecordEntity);
            //发送订单成功的MQ事件
            orderActionMqService.sendCreateOrderSuccess(orderResVO.getOrderNo(),context.getOwnerMemberDto().getMemNo(),context.getRiskAuditId(),orderReqVO);
            NewOrderMQStatusEventEnum newOrderMQStatusEventEnum = NewOrderMQStatusEventEnum.ORDER_PRECONFIRM;
            if(StringUtils.equals(orderResVO.getStatus(), String.valueOf(OrderStatusEnum.TO_PAY.getStatus()))) {
                newOrderMQStatusEventEnum = NewOrderMQStatusEventEnum.ORDER_PREPAY;
            }
            orderStatusMqService.sendOrderStatusToCreate(orderResVO.getOrderNo(),context.getOwnerMemberDto().getMemNo(),orderResVO.getStatus(),orderReqVO,newOrderMQStatusEventEnum);
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
            orderActionMqService.sendCreateOrderFail(orderResVO.getOrderNo(),context.getOwnerMemberDto().getMemNo(),context.getRiskAuditId(),orderReqVO);
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
            orderActionMqService.sendCreateOrderFail(orderResVO.getOrderNo(),context.getOwnerMemberDto().getMemNo(),context.getRiskAuditId(),orderReqVO);
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
        OrderReqContext context = buildOrderReqContext(orderReqVO);
        orderFilterChain.validate(context);
        try{
            orderResVO = submitOrderService.submitOrder(context);
            OrderRecordEntity orderRecordEntity = new OrderRecordEntity();
            orderRecordEntity.setErrorCode(ErrorCode.SUCCESS.getCode());
            orderRecordEntity.setErrorTxt(ErrorCode.SUCCESS.getText());
            orderRecordEntity.setMemNo(adminOrderReqVO.getMemNo());
            orderRecordEntity.setOrderNo(orderResVO.getOrderNo());
            orderRecordEntity.setParam(JSON.toJSONString(adminOrderReqVO));
            orderRecordEntity.setResult(JSON.toJSONString(orderResVO));
            orderRecordService.save(orderRecordEntity);

            //发送订单成功的MQ事件
            orderActionMqService.sendCreateOrderSuccess(orderResVO.getOrderNo(),context.getOwnerMemberDto().getMemNo(),context.getRiskAuditId(),orderReqVO);
            NewOrderMQStatusEventEnum newOrderMQStatusEventEnum = NewOrderMQStatusEventEnum.ORDER_PRECONFIRM;
            if(StringUtils.equals(orderResVO.getStatus(), String.valueOf(OrderStatusEnum.TO_PAY.getStatus()))) {
                newOrderMQStatusEventEnum = NewOrderMQStatusEventEnum.ORDER_PREPAY;
            }
            orderStatusMqService.sendOrderStatusToCreate(orderResVO.getOrderNo(),context.getOwnerMemberDto().getMemNo(),orderResVO.getStatus(),orderReqVO,newOrderMQStatusEventEnum);
        }catch(OrderException orderException){
            OrderRecordEntity orderRecordEntity = new OrderRecordEntity();
            orderRecordEntity.setErrorCode(orderException.getErrorCode());
            orderRecordEntity.setErrorTxt(orderException.getErrorMsg());
            orderRecordEntity.setMemNo(adminOrderReqVO.getMemNo());
            orderRecordEntity.setOrderNo(orderResVO==null?"":orderResVO.getOrderNo());
            orderRecordEntity.setParam(JSON.toJSONString(adminOrderReqVO));
            orderRecordEntity.setResult(JSON.toJSONString(orderResVO));
            orderRecordService.save(orderRecordEntity);

            //发送订单成功的MQ事件
            orderActionMqService.sendCreateOrderSuccess(orderResVO.getOrderNo(),context.getOwnerMemberDto().getMemNo(),context.getRiskAuditId(),orderReqVO);
            //释放库存
            String orderNo = orderResVO==null?"":orderResVO.getOrderNo();
            if(orderNo != null && orderNo.trim().length()>0){
                Integer carNo = Integer.valueOf(adminOrderReqVO.getCarNo());
                stockService.releaseCarStock(orderNo,carNo);
            }
            throw orderException;
        }catch (Exception e){
            OrderRecordEntity orderRecordEntity = new OrderRecordEntity();
            orderRecordEntity.setErrorCode(ErrorCode.SYS_ERROR.getCode());
            orderRecordEntity.setErrorTxt(ErrorCode.SYS_ERROR.getText());
            orderRecordEntity.setMemNo(adminOrderReqVO.getMemNo());
            orderRecordEntity.setOrderNo(orderResVO==null?"":orderResVO.getOrderNo());
            orderRecordEntity.setParam(JSON.toJSONString(adminOrderReqVO));
            orderRecordEntity.setResult(JSON.toJSONString(orderResVO));
            orderRecordService.save(orderRecordEntity);

            //发送订单成功的MQ事件
            orderActionMqService.sendCreateOrderSuccess(orderResVO.getOrderNo(),context.getOwnerMemberDto().getMemNo(),context.getRiskAuditId(),orderReqVO);
            //释放库存
            String orderNo = orderResVO==null?"":orderResVO.getOrderNo();
            if(orderNo != null && orderNo.trim().length()>0){
                Integer carNo = Integer.valueOf(adminOrderReqVO.getCarNo());
                stockService.releaseCarStock(orderNo,carNo);
            }
            throw e;
        }

        return ResponseData.success(orderResVO);
    }

    /**
     * 构建请求参数上下文
     * @param orderReqVO
     * @return
     */
    private OrderReqContext buildOrderReqContext(OrderReqVO orderReqVO){
        //1.请求参数处理
        OrderReqContext reqContext = new OrderReqContext();
        reqContext.setOrderReqVO(orderReqVO);
        //租客会员信息
        RenterMemberDTO renterMemberDTO =
                memberService.getRenterMemberInfo(orderReqVO.getMemNo());
        reqContext.setRenterMemberDto(renterMemberDTO);
        //租客商品明细
        RenterGoodsDetailDTO renterGoodsDetailDTO =
                goodsService.getRenterGoodsDetail(orderCommonConver.buildCarDetailReqVO(orderReqVO));
        reqContext.setRenterGoodsDetailDto(renterGoodsDetailDTO);

        //一天一价分组
        renterGoodsDetailDTO = renterCommodityService.setPriceAndGroup(renterGoodsDetailDTO);

        //车主商品明细
        OwnerGoodsDetailDTO ownerGoodsDetailDTO = goodsService.getOwnerGoodsDetail(renterGoodsDetailDTO);
        reqContext.setOwnerGoodsDetailDto(ownerGoodsDetailDTO);

        //车主会员信息
        OwnerMemberDTO ownerMemberDTO = memberService.getOwnerMemberInfo(renterGoodsDetailDTO.getOwnerMemNo());
        reqContext.setOwnerMemberDto(ownerMemberDTO);

        return reqContext;
    }
}
