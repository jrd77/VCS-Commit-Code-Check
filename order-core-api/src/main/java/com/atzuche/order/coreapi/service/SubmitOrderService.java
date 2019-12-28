package com.atzuche.order.coreapi.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.OrderReqContext;
import com.atzuche.order.commons.OrderStatus;
import com.atzuche.order.commons.constant.OrderConstant;
import com.atzuche.order.commons.entity.dto.OrderContextDTO;
import com.atzuche.order.commons.entity.dto.OwnerMemberDTO;
import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.RenterMemberDTO;
import com.atzuche.order.commons.vo.req.NormalOrderReqVO;
import com.atzuche.order.commons.vo.res.NormalOrderResVO;
import com.atzuche.order.coreapi.entity.request.SubmitOrderReq;
import com.atzuche.order.parentorder.dto.OrderDTO;
import com.atzuche.order.parentorder.dto.OrderSourceStatDTO;
import com.atzuche.order.parentorder.dto.OrderStatusDTO;
import com.atzuche.order.parentorder.dto.ParentOrderDTO;
import com.atzuche.order.parentorder.service.ParentOrderService;
import com.autoyol.car.api.feign.api.CarDetailQueryFeignApi;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@Service
public class SubmitOrderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubmitOrderService.class);

    @Autowired
    private MemberService memberService;
    @Autowired
    private CarService carService;
    @Autowired
    private CarDetailQueryFeignApi carDetailQueryFeignApi;

    @Resource
    private UniqueOrderNoService uniqueOrderNoService;
    @Resource
    private ParentOrderService parentOrderService;


    public ResponseData submitOrder(SubmitOrderReq submitReqDto) {
        //调用日志模块 TODO

        try {
            OrderContextDTO orderContextDto = new OrderContextDTO();
            //获取租客商品信息
            RenterGoodsDetailDTO renterGoodsDetailDto = carService.getRenterGoodsDetail(null);
            //获取车主商品信息
//            OwnerGoodsDetailDTO ownerGoodsDetailDto = carService.getOwnerGoodsDetail(renterGoodsDetailDto);
            //获取车主会员信息
            OwnerMemberDTO ownerMemberDto = memberService.getOwnerMemberInfo(renterGoodsDetailDto.getOwnerMemNo());
            //获取租客会员信息
            RenterMemberDTO renterMemberDto = memberService.getRenterMemberInfo(submitReqDto.getMemNo());

            //组装数据
            orderContextDto.setRenterGoodsDetailDto(renterGoodsDetailDto);
//            orderContextDto.setOwnerGoodsDetailDto(ownerGoodsDetailDto);
            orderContextDto.setOwnerMemberDto(ownerMemberDto);
            orderContextDto.setRenterMemberDto(renterMemberDto);


            //开始校验规则 （前置校验 + 风控）TODO
//            submitOrderFilterService.checkRules(submitReqDto,orderContextDto);


            //调用费用计算模块,组装数据orderContextDto TODO


            //车主券抵扣,组装数据orderContextDto TODO

            //限时红包抵扣,组装数据orderContextDto TODO

            //优惠券抵扣,组装数据orderContextDto TODO

            //凹凸比抵扣,组装数据orderContextDto TODO

            //钱包抵扣,组装数据orderContextDto TODO
        } catch (OrderException ex) {
            String errorCode = ex.getErrorCode();
            String errorMsg = ex.getErrorMsg();
            LOGGER.error("下单失败", ex);
            return ResponseData.createErrorCodeResponse(errorCode, errorMsg);
        } catch (Exception ex) {
            LOGGER.error("下单异常", ex);
            return ResponseData.createErrorCodeResponse(ErrorCode.FAILED.getCode(), ErrorCode.FAILED.getText());
        }


//====================落库OrderContextDto=========================

        //调用主订单模块

        //调用租客模块 (子订单模块、商品、会员、交易流程)

        //调用取送车模块

        //调用车主模块（子订单模块、商品、会员）
        return null;
    }


    /**
     * 提交订单
     *
     * @param normalOrderReqVO 下单请求信息
     * @return NormalOrderResVO 下单返回结果
     */
    public NormalOrderResVO submitOrder(NormalOrderReqVO normalOrderReqVO) {
        //1.请求参数处理
        OrderReqContext reqContext = new OrderReqContext();
        reqContext.setNormalOrderReqVO(normalOrderReqVO);
        reqContext.setRenterMemberDto(memberService.getRenterMemberInfo(String.valueOf(normalOrderReqVO.getMemNo())));

        CarService.CarDetailReqVO carDetailReqVO = new CarService.CarDetailReqVO();
        carDetailReqVO.setAddrIndex(StringUtils.isBlank(normalOrderReqVO.getCarAddrIndex()) ? 0 : Integer.parseInt(normalOrderReqVO.getCarAddrIndex()));
        carDetailReqVO.setCarNo(normalOrderReqVO.getCarNo());
        carDetailReqVO.setRentTime(normalOrderReqVO.getRentTime());
        carDetailReqVO.setRevertTime(normalOrderReqVO.getRevertTime());
        carDetailReqVO.setUseSpecialPrice(false);
        RenterGoodsDetailDTO renterGoodsDetailDTO = carService.getRenterGoodsDetail(carDetailReqVO);
        reqContext.setRenterGoodsDetailDto(renterGoodsDetailDTO);

        reqContext.setOwnerGoodsDetailDto(null);
        reqContext.setOwnerMemberDto(memberService.getOwnerMemberInfo(""));
        //2.下单校验
        //2.1库存
        //2.2风控
        //2.3校验链



        //3.生成主订单号
        String orderNo = uniqueOrderNoService.getOrderNo();
        //4.创建租客子订单
        //4.1.生成租客子订单号
        String renterOrderNo = uniqueOrderNoService.getRenterOrderNo(orderNo);
        //4.2.调用租客订单模块处理租客订单相关业务
        //4.3.接收租客订单返回信息
        //4.4.租客商品信息处理


        //4.5.租客信息处理
        //4.6.租客权益信息处理


        //5.创建车主子订单
        //5.1.生成车主子订单号
        String ownerOrderNo = uniqueOrderNoService.getOwnerOrderNo(orderNo);
        //5.2.调用车主订单模块处理车主订单相关业务
        //5.3.接收车主订单返回信息


        //配送订单处理..............

        //6.主订单相关信息处理
        ParentOrderDTO parentOrderDTO = new ParentOrderDTO();
        //6.1主订单信息处理
        OrderDTO orderDTO = buildOrderDTO(normalOrderReqVO);
        orderDTO.setOrderNo(orderNo);
        orderDTO.setRiskAuditId(null);
        parentOrderDTO.setOrderDTO(orderDTO);

        //6.2主订单扩展信息(统计信息)处理
        OrderSourceStatDTO orderSourceStatDTO = buildOrderSourceStatDTO(normalOrderReqVO);
        orderSourceStatDTO.setOrderNo(orderNo);
        parentOrderDTO.setOrderSourceStatDTO(orderSourceStatDTO);

        //6.3主订单状态信息(统计信息)处理
        OrderStatusDTO orderStatusDTO = new OrderStatusDTO();
        orderStatusDTO.setOrderNo(orderNo);
        orderStatusDTO.setIsDispatch(OrderConstant.NO);
        orderStatusDTO.setDispatchStatus(OrderConstant.NO);
        if (null == renterGoodsDetailDTO.getReplyFlag() || renterGoodsDetailDTO.getReplyFlag() == OrderConstant.NO) {
            orderStatusDTO.setStatus(OrderStatus.TO_CONFIRM.getStatus());
        } else {
            orderStatusDTO.setStatus(OrderStatus.TO_PAY.getStatus());
        }
        parentOrderDTO.setOrderStatusDTO(orderStatusDTO);
        parentOrderService.saveParentOrderInfo(parentOrderDTO);

        //6.4 order_flow


        //7.订单完成事件发送

        //8.组装接口返回


        return new NormalOrderResVO();
    }


    /**
     * 组装主订单基本信息
     *
     * @param normalOrderReqVO 下单请求参数
     * @return OrderDTO 主订单基本信息
     */
    private OrderDTO buildOrderDTO(NormalOrderReqVO normalOrderReqVO) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setMemNoRenter(normalOrderReqVO.getMemNo());
        orderDTO.setCategory(Integer.valueOf(normalOrderReqVO.getOrderCategory()));
        orderDTO.setCityCode(normalOrderReqVO.getCityCode());
        orderDTO.setCityName(normalOrderReqVO.getCityName());
        orderDTO.setEntryCode(normalOrderReqVO.getSceneCode());
        orderDTO.setSource(Integer.valueOf(normalOrderReqVO.getSource()));
        orderDTO.setExpRentTime(normalOrderReqVO.getRentTime());
        orderDTO.setExpRevertTime(normalOrderReqVO.getRevertTime());
        //orderDTO.setIsFreeDeposit(Integer.valueOf(normalOrderReqVO.getFreeDoubleTypeId()));
        orderDTO.setIsOutCity(normalOrderReqVO.getIsLeaveCity());
        orderDTO.setReqTime(LocalDateTime.now());
        orderDTO.setIsUseAirPortService(normalOrderReqVO.getUseAirportService());

        LOGGER.info("Build order dto,result is ,orderDTO:[{}]", JSON.toJSONString(orderDTO));
        return orderDTO;
    }


    /**
     * 组装主订单来源统计信息
     *
     * @param normalOrderReqVO 下单请求参数
     * @return OrderSourceStatDTO 主订单来源统计信息
     */
    private OrderSourceStatDTO buildOrderSourceStatDTO(NormalOrderReqVO normalOrderReqVO) {
        OrderSourceStatDTO orderSourceStatDTO = new OrderSourceStatDTO();
        BeanCopier beanCopier = BeanCopier.create(NormalOrderReqVO.class, OrderSourceStatDTO.class, false);
        beanCopier.copy(normalOrderReqVO, orderSourceStatDTO, null);

        //差异处理
        orderSourceStatDTO.setAppVersion(normalOrderReqVO.getAppVersion());
        orderSourceStatDTO.setCategory(normalOrderReqVO.getOrderCategory());
        orderSourceStatDTO.setEntryCode(normalOrderReqVO.getSceneCode());
        orderSourceStatDTO.setModuleName(normalOrderReqVO.getModuleName());
        orderSourceStatDTO.setFunctionName(normalOrderReqVO.getFunctionName());
        orderSourceStatDTO.setOaid(normalOrderReqVO.getOAID());
        orderSourceStatDTO.setImei(normalOrderReqVO.getIMEI());
        orderSourceStatDTO.setOs(normalOrderReqVO.getOS());
        orderSourceStatDTO.setAppChannelId(normalOrderReqVO.getAppChannelId());
        orderSourceStatDTO.setAndroidId(normalOrderReqVO.getAndroidID());
        return orderSourceStatDTO;
    }


}
