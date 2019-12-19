package com.atzuche.order.coreapi.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.GlobalConstant;
import com.atzuche.order.commons.LocalDateTimeUtils;
import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.entity.dto.*;
import com.atzuche.order.commons.enums.InternalStaffEnum;
import com.atzuche.order.commons.enums.OwnerMemRightEnum;
import com.atzuche.order.commons.enums.RenterMemRightEnum;
import com.atzuche.order.coreapi.enums.SubmitOrderErrorEnum;
import com.atzuche.order.coreapi.submitOrder.exception.CarDetailByFeignException;
import com.atzuche.order.coreapi.submitOrder.exception.OwnerberByFeignException;
import com.atzuche.order.coreapi.submitOrder.exception.RenterMemberByFeignException;
import com.atzuche.order.coreapi.submitOrder.exception.SubmitOrderException;
import com.atzuche.order.coreapi.submitOrder.filter.SubmitOrderFilterService;
import com.atzuche.order.request.NormalOrderReqVO;
import com.autoyol.car.api.feign.api.CarDetailQueryFeignApi;
import com.autoyol.car.api.model.dto.OrderCarInfoParamDTO;
import com.autoyol.car.api.model.vo.*;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.member.detail.api.MemberDetailFeignService;
import com.autoyol.member.detail.enums.MemberSelectKeyEnum;
import com.autoyol.member.detail.vo.res.*;
import com.dianping.cat.Cat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SubmitOrderService {


    @Autowired
    private SubmitOrderFilterService submitOrderFilterService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private CarService carService;

    @Autowired
    private CarDetailQueryFeignApi carDetailQueryFeignApi;

    public ResponseData submitOrder(NormalOrderReqVO submitReqDto) {
        //调用日志模块 TODO

        try{
            OrderContextDto orderContextDto = new OrderContextDto();
            //获取租客商品信息
            RenterGoodsDetailDto renterGoodsDetailDto = carService.getRenterGoodsDetail(null);
            //获取车主商品信息
//            OwnerGoodsDetailDto ownerGoodsDetailDto = carService.getOwnerGoodsDetail(renterGoodsDetailDto);
            //获取车主会员信息
            OwnerMemberDto ownerMemberDto = memberService.getOwnerMemberInfo(submitReqDto.getMemNo());
            //获取租客会员信息
            RenterMemberDto renterMemberDto = memberService.getRenterMemberInfo(submitReqDto.getMemNo());

            //组装数据
            orderContextDto.setRenterGoodsDetailDto(renterGoodsDetailDto);
//            orderContextDto.setOwnerGoodsDetailDto(ownerGoodsDetailDto);
            orderContextDto.setOwnerMemberDto(ownerMemberDto);
            orderContextDto.setRenterMemberDto(renterMemberDto);


            //开始校验规则 （前置校验 + 风控）TODO
            submitOrderFilterService.checkRules(submitReqDto,orderContextDto);


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
