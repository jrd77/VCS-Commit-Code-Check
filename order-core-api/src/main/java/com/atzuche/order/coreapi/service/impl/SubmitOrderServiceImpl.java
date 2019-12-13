package com.atzuche.order.coreapi.service.impl;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.coreapi.dto.OrderContextDto;
import com.atzuche.order.coreapi.dto.SubmitReqDto;
import com.atzuche.order.coreapi.exception.SubmitOrderException;
import com.atzuche.order.coreapi.submitOrder.filter.SubmitOrderFilterService;
import com.atzuche.order.coreapi.service.SubmitOrderService;
import com.autoyol.commons.web.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SubmitOrderServiceImpl implements SubmitOrderService {
    @Autowired
    private SubmitOrderFilterService submitOrderFilterService;

    @Override
    public void submitOrder(SubmitReqDto submitReqDto) {
        //调用日志模块

        //获取车辆信息

        //获取车主会员信息

        //获取租客会员信息

        //组装数据
        OrderContextDto orderContextDto = new OrderContextDto();

        try{
            //开始校验规则 （前置校验 + 风控）
            submitOrderFilterService.checkRules(submitReqDto,orderContextDto);
        }catch (SubmitOrderException ex){
            String msg = ex.getMessage();  //除开errorcode之外的其他异常信息。
            ErrorCode error = ex.getErrorCode();
            log.error("下单校验不通过 msg={}",msg,ex);
            return;
        }catch (Exception ex){
            log.error("下单异常",ex);
            return;
        }

        //调用费用计算模块,组装数据orderContextDto

        //车主券抵扣,组装数据orderContextDto

        //限时红包抵扣,组装数据orderContextDto

        //优惠券抵扣,组装数据orderContextDto

        //凹凸比抵扣,组装数据orderContextDto

        //钱包抵扣,组装数据orderContextDto

//====================落库OrderContextDto=========================

        //调用主订单模块

        //调用租客模块 (子订单模块、商品、会员、交易流程)

        //调用取送车模块

        //调用车主模块（子订单模块、商品、会员）

    }

    public static void main(String[] args) {
        ErrorCode errorCode = ErrorCode.FAILED;
        System.out.println(JSON.toJSONString(errorCode));
    }
}
