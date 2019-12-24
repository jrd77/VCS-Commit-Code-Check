package com.atzuche.order.coreapi.service;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.entity.dto.*;
import com.atzuche.order.coreapi.entity.request.SubmitOrderReq;
import com.atzuche.order.vo.request.NormalOrderReqVO;
import com.autoyol.car.api.feign.api.CarDetailQueryFeignApi;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SubmitOrderService {

    @Autowired
    private MemberService memberService;
    @Autowired
    private CarService carService;


    @Autowired
    private CarDetailQueryFeignApi carDetailQueryFeignApi;

    public ResponseData submitOrder(SubmitOrderReq submitReqDto) {
        //调用日志模块 TODO

        try{
            OrderContextDTO orderContextDto = new OrderContextDTO();
            //获取租客商品信息
            RenterGoodsDetailDTO renterGoodsDetailDto = carService.getRenterGoodsDetail(null);
            //获取车主商品信息
//            OwnerGoodsDetailDTO ownerGoodsDetailDto = carService.getOwnerGoodsDetail(renterGoodsDetailDto);
            //获取车主会员信息
            OwnerMemberDTO ownerMemberDto = memberService.getOwnerMemberInfo(submitReqDto.getMemNo());
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
        }catch (OrderException ex){
            String errorCode = ex.getErrorCode();
            String errorMsg = ex.getErrorMsg();
            log.error("下单失败",ex);
            return ResponseData.createErrorCodeResponse(errorCode,errorMsg);
        }catch (Exception ex){
            log.error("下单异常",ex);
            return ResponseData.createErrorCodeResponse(ErrorCode.FAILED.getCode(),ErrorCode.FAILED.getText());
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
     */
    public void submitOrder(NormalOrderReqVO normalOrderReqVO) {
        //1.请求参数处理

        //2.下单校验

        //3.生成主订单号

        //4.创建租客子订单
        //4.1.生成租客子订单号
        //4.2.调用租客订单模块处理租客订单相关业务
        //4.3.接收租客订单返回信息



        //5.创建车主子订单
        //5.1.生成车主子订单号
        //5.2.调用车主订单模块处理车主订单相关业务
        //5.3.接收车主订单返回信息




        //配送订单处理..............




        //6.主订单信息处理
        //6.1主订单信息封装
        //6.2主订单信息落库

        //6.3主订单扩展信息(统计信息)封装
        //6.4主订单扩展信息落库

        //6.5主订单状态信息封装
        //6.6主订单状态信息落库

        //6.7主订单类型信息封装
        //6.8主订单类型信息落库


        //7.订单完成事件发送
    }



}
