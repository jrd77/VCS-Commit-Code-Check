package com.atzuche.order.coreapi.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.GlobalConstant;
import com.atzuche.order.commons.LocalDateTimeUtils;
import com.atzuche.order.commons.entity.dto.CarDetailDto;
import com.atzuche.order.commons.entity.dto.OrderContextDto;
import com.atzuche.order.commons.entity.dto.OwnerMemberDto;
import com.atzuche.order.commons.entity.dto.RenterMemberDto;
import com.atzuche.order.coreapi.entity.request.SubmitOrderReq;
import com.atzuche.order.coreapi.submitOrder.exception.CarDetailByFeignException;
import com.atzuche.order.coreapi.submitOrder.exception.RenterMemberException;
import com.atzuche.order.coreapi.submitOrder.exception.SubmitOrderException;
import com.atzuche.order.coreapi.submitOrder.filter.SubmitOrderFilterService;
import com.autoyol.car.api.feign.api.CarDetailQueryFeignApi;
import com.autoyol.car.api.model.dto.OrderCarInfoParamDTO;
import com.autoyol.car.api.model.vo.CarDetailVO;
import com.autoyol.car.api.model.vo.ResponseObject;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.member.detail.api.MemberDetailFeignService;
import com.autoyol.member.detail.enums.MemberSelectKeyEnum;
import com.autoyol.member.detail.vo.res.*;
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
    @Autowired
    private CarDetailQueryFeignApi carDetailQueryFeignApi;

    public ResponseData submitOrder(SubmitOrderReq submitReqDto) {
        //调用日志模块 TODO

        try{

            //获取车辆信息
            CarDetailDto carDetail = getCarDetail(submitReqDto);
            //获取车主会员信息
            OwnerMemberDto ownerMemberDto = getOwnerMemberInfo(submitReqDto);

            //获取租客会员信息
            RenterMemberDto renterMemberDto = getRenterMemberInfo(submitReqDto);

            //组装数据
            OrderContextDto orderContextDto = new OrderContextDto();
            orderContextDto.setOwnerMemberDto(ownerMemberDto);
            orderContextDto.setRenterMemberDto(renterMemberDto);
            orderContextDto.setCarDetailDto(carDetail);


            //开始校验规则 （前置校验 + 风控）TODO
            submitOrderFilterService.checkRules(submitReqDto,orderContextDto);


            //调用费用计算模块,组装数据orderContextDto TODO

            //车主券抵扣,组装数据orderContextDto TODO

            //限时红包抵扣,组装数据orderContextDto TODO

            //优惠券抵扣,组装数据orderContextDto TODO

            //凹凸比抵扣,组装数据orderContextDto TODO

            //钱包抵扣,组装数据orderContextDto TODO
        }catch (SubmitOrderException ex){
            ErrorCode error = ex.getErrorCode();
            log.error("下单失败",ex);
            return ResponseData.createErrorCodeResponse(error.getCode(),error.getText());
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

    public RenterMemberDto getRenterMemberInfo(SubmitOrderReq submitReqDto) throws RenterMemberException {
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
        if(responseData == null || !ErrorCode.SUCCESS.getCode().equals(responseData.getResCode()) || responseData.getData() == null){
            log.error("Feign 获取租客会员信息失败,submitReqDto={},responseData={}",JSON.toJSONString(submitReqDto),JSON.toJSONString(responseData));
            //TODO  日志记录
            throw new RenterMemberException(ErrorCode.FAILED,"获取会员信息失败");
        }
        /*MemberTotalInfo memberTotalInfo = responseData.getData();
        MemberAuthInfo memberAuthInfo = memberTotalInfo.getMemberAuthInfo();
        MemberCoreInfo memberCoreInfo = memberTotalInfo.getMemberCoreInfo();
        MemberRoleInfo memberRoleInfo = memberTotalInfo.getMemberRoleInfo();
        RenterMemberDto renterMemberDto = new RenterMemberDto();
        renterMemberDto.setMemNo(submitReqDto.getMemNo());
        renterMemberDto.setPhone(memberCoreInfo.getPhone());
        renterMemberDto.setHeaderUrl(memberCoreInfo.getPortraitPath());
        renterMemberDto.setRealName(memberCoreInfo.getRealName());
        renterMemberDto.setNickName(memberCoreInfo.getNickName());
        renterMemberDto.setCertificationTime(LocalDateTime.parse(memberAuthInfo.getDriLicFirstTime()), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        renterMemberDto.setOrderSuccessCount();*/

         responseData.getData();
        return new RenterMemberDto();
    }

    public OwnerMemberDto getOwnerMemberInfo(SubmitOrderReq submitReqDto) throws RenterMemberException {
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
       responseData.getData();

        return new OwnerMemberDto();
    }


    public CarDetailDto getCarDetail(SubmitOrderReq submitReqDto) throws CarDetailByFeignException, RenterMemberException {
        OrderCarInfoParamDTO orderCarInfoParamDTO = new OrderCarInfoParamDTO();
        orderCarInfoParamDTO.setCarNo(submitReqDto.getCarNo());
        orderCarInfoParamDTO.setCarAddressIndex(Integer.valueOf(submitReqDto.getCarAddrIndex()));
        orderCarInfoParamDTO.setRentTime(LocalDateTimeUtils.localDateTimeToLong(submitReqDto.getRentTime()));
        orderCarInfoParamDTO.setRevertTime(LocalDateTimeUtils.localDateTimeToLong(submitReqDto.getRevertTime()));
        orderCarInfoParamDTO.setUseSpecialPrice(GlobalConstant.USE_SPECIAL_PRICE.equals(submitReqDto.getUseSpecialPrice()));
        ResponseObject<CarDetailVO> responseObject = null;
        try{
            log.info("Feign 开始获取车辆信息,orderCarInfoParamDTO={}",JSON.toJSONString(orderCarInfoParamDTO));
            responseObject = carDetailQueryFeignApi.getCarDetailOfTransByCarNo(orderCarInfoParamDTO);
        }catch (Exception e){
            log.error("Feign 获取车辆信息异常,responseObject={},orderCarInfoParamDTO={}",JSON.toJSONString(responseObject),JSON.toJSONString(orderCarInfoParamDTO),e);
            //TODO  日志记录
            throw new CarDetailByFeignException(ErrorCode.FAILED,"调用会员服务异常");
        }
        if(responseObject == null || !ErrorCode.SUCCESS.getCode().equals(responseObject.getResCode())){
            log.error("Feign 获取车辆信息失败,responseObject={},orderCarInfoParamDTO={}",JSON.toJSONString(responseObject),JSON.toJSONString(orderCarInfoParamDTO));
            //TODO  日志记录
            throw new RenterMemberException(ErrorCode.FAILED,"获取车辆信息失败");
        }
        //TODO 日志记录
        CarDetailVO data = responseObject.getData();

        return new CarDetailDto();
    }




}
