package com.atzuche.order.coreapi.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.coreapi.entity.dto.OrderContextDto;
import com.atzuche.order.coreapi.entity.request.SubmitReq;
import com.atzuche.order.coreapi.submitOrder.exception.RenterMemberException;
import com.atzuche.order.coreapi.submitOrder.exception.SubmitOrderException;
import com.atzuche.order.coreapi.submitOrder.filter.SubmitOrderFilterService;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.member.detail.api.MemberDetailFeignService;
import com.autoyol.member.detail.enums.MemberSelectKeyEnum;
import com.autoyol.member.detail.vo.res.MemberTotalInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class SubmitOrderService {
    @Autowired
    private SubmitOrderFilterService submitOrderFilterService;
    @Autowired
    private MemberDetailFeignService memberDetailFeignService;

    public ResponseData submitOrder(SubmitReq submitReqDto) {
        //调用日志模块

        try{


            //获取车辆信息

            //获取车主会员信息
            MemberTotalInfo ownerMemberInfo = getRenterMemberInfo(submitReqDto);

            //获取租客会员信息
            MemberTotalInfo renterMemberInfo = getRenterMemberInfo(submitReqDto);

            //组装数据
            OrderContextDto orderContextDto = new OrderContextDto();



            //开始校验规则 （前置校验 + 风控）
            submitOrderFilterService.checkRules(submitReqDto,orderContextDto);
        }catch (SubmitOrderException ex){
            String msg = ex.getMessage();  //除开errorcode之外的其他异常信息。
            ErrorCode error = ex.getErrorCode();
            log.error("下单校验不通过 msg={}",msg,ex);
            return null;
        }catch (Exception ex){
            log.error("下单异常",ex);
            return null;
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
        return null;
    }

    public MemberTotalInfo getRenterMemberInfo(SubmitReq submitReqDto) throws RenterMemberException {
        List<String> selectKey = Arrays.asList(
                MemberSelectKeyEnum.MEMBER_CORE_INFO.getKey(),
                MemberSelectKeyEnum.MEMBER_AUTH_INFO.getKey(),
                MemberSelectKeyEnum.MEMBER_BASE_INFO.getKey(),
                MemberSelectKeyEnum.MEMBER_ROLE_INFO.getKey());
        ResponseData<MemberTotalInfo> responseData = null;
        log.info("Feign 开始获取租客会员信息,submitReqDto={}",JSON.toJSONString(submitReqDto));
        try{
            responseData = memberDetailFeignService.getMemberSelectInfo(submitReqDto.getMemNo(), selectKey);

        }catch (Exception e){
            log.error("Feign 获取租客会员信息失败,submitReqDto={},responseData={}",JSON.toJSONString(submitReqDto),null,e);
            //TODO  日志记录
            /*throw new RenterMemberException(SubmitOrderErrorEnum.SUBMIT_ORDER_RENTER_MEMBER_ERR,"调用会员服务异常");*/
            throw new RenterMemberException(ErrorCode.FAILED,"调用会员服务异常");
        }
        if(responseData == null || !ErrorCode.SUCCESS.getCode().equals(responseData.getResCode())){
            log.error("Feign 获取租客会员信息失败,submitReqDto={},responseData={}",JSON.toJSONString(submitReqDto),JSON.toJSONString(responseData));
            //TODO  日志记录
            throw new RenterMemberException(ErrorCode.FAILED,"获取会员信息失败");
        }
        //TODO  日志记录
        return responseData.getData();
    }

    public MemberTotalInfo getOwnerMemberInfo(SubmitReq submitReqDto) throws RenterMemberException {
        List<String> selectKey = Arrays.asList(
                MemberSelectKeyEnum.MEMBER_CORE_INFO.getKey(),
                MemberSelectKeyEnum.MEMBER_AUTH_INFO.getKey(),
                MemberSelectKeyEnum.MEMBER_BASE_INFO.getKey(),
                MemberSelectKeyEnum.MEMBER_ROLE_INFO.getKey());
        ResponseData<MemberTotalInfo> responseData = null;
        log.info("Feign 开始获取车主会员信息,submitReqDto={}",JSON.toJSONString(submitReqDto));
        try{
            responseData = memberDetailFeignService.getMemberSelectInfo(submitReqDto.getMemNo(), selectKey);

        }catch (Exception e){
            log.error("Feign 获取车主会员信息失败,submitReqDto={},responseData={}",JSON.toJSONString(submitReqDto),null,e);
            //TODO  日志记录
            /*throw new RenterMemberException(SubmitOrderErrorEnum.SUBMIT_ORDER_RENTER_MEMBER_ERR,"调用会员服务异常");*/
            throw new RenterMemberException(ErrorCode.FAILED,"调用会员服务异常");
        }
        if(responseData == null || !ErrorCode.SUCCESS.getCode().equals(responseData.getResCode())){
            log.error("Feign 获取车主会员信息失败,submitReqDto={},responseData={}",JSON.toJSONString(submitReqDto),JSON.toJSONString(responseData));
            //TODO  日志记录
            throw new RenterMemberException(ErrorCode.FAILED,"获取会员信息失败");
        }
        //TODO  日志记录
        return responseData.getData();
    }






}
