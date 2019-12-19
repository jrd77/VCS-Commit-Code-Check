package com.atzuche.order.coreapi.service;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.entity.dto.*;
import com.atzuche.order.coreapi.entity.request.SubmitOrderReq;
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

}
