package com.atzuche.order.coreapi.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.GlobalConstant;
import com.atzuche.order.commons.LocalDateTimeUtils;
import com.atzuche.order.commons.entity.dto.*;
import com.atzuche.order.coreapi.entity.request.NormalOrderReqVO;
import com.atzuche.order.coreapi.enums.SubmitOrderErrorEnum;
import com.atzuche.order.coreapi.submitOrder.exception.CarDetailByFeignException;
import com.atzuche.order.coreapi.submitOrder.exception.OwnerberByFeignException;
import com.atzuche.order.coreapi.submitOrder.exception.RenterMemberByFeignException;
import com.atzuche.order.coreapi.submitOrder.exception.SubmitOrderException;
import com.atzuche.order.coreapi.submitOrder.filter.SubmitOrderFilterService;
import com.autoyol.car.api.feign.api.CarDetailQueryFeignApi;
import com.autoyol.car.api.model.dto.OrderCarInfoParamDTO;
import com.autoyol.car.api.model.vo.*;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.member.detail.api.MemberDetailFeignService;
import com.autoyol.member.detail.enums.MemberSelectKeyEnum;
import com.autoyol.member.detail.vo.res.MemberAuthInfo;
import com.autoyol.member.detail.vo.res.MemberCoreInfo;
import com.autoyol.member.detail.vo.res.MemberRoleInfo;
import com.autoyol.member.detail.vo.res.MemberTotalInfo;
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
    private MemberDetailFeignService memberDetailFeignService;
    @Autowired
    private CarDetailQueryFeignApi carDetailQueryFeignApi;

    public ResponseData submitOrder(NormalOrderReqVO submitReqDto) {
        //调用日志模块 TODO

        try{
            OrderContextDto orderContextDto = new OrderContextDto();
            //获取租客商品信息
            RenterGoodsDetailDto renterGoodsDetailDto = getRenterGoodsDetail(submitReqDto);
            //获取车主商品信息
            OwnerGoodsDetailDto ownerGoodsDetailDto = getOwnerGoodsDetail(renterGoodsDetailDto);
            //获取车主会员信息
            OwnerMemberDto ownerMemberDto = getOwnerMemberInfo(submitReqDto);
            //获取租客会员信息
            RenterMemberDto renterMemberDto = getRenterMemberInfo(submitReqDto);

            //组装数据
            orderContextDto.setRenterGoodsDetailDto(renterGoodsDetailDto);
            orderContextDto.setOwnerGoodsDetailDto(ownerGoodsDetailDto);
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
        }catch (SubmitOrderException ex){
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

    public RenterMemberDto getRenterMemberInfo(NormalOrderReqVO submitReqDto) throws RenterMemberByFeignException {
        List<String> selectKey = Arrays.asList(
                MemberSelectKeyEnum.MEMBER_CORE_INFO.getKey(),
                MemberSelectKeyEnum.MEMBER_AUTH_INFO.getKey(),
                MemberSelectKeyEnum.MEMBER_BASE_INFO.getKey(),
                MemberSelectKeyEnum.MEMBER_ROLE_INFO.getKey());
        ResponseData<MemberTotalInfo> responseData = null;
        log.info("Feign 开始获取租客会员信息,submitReqDto={}",JSON.toJSONString(submitReqDto));
        try{
            responseData = memberDetailFeignService.getMemberSelectInfo(Integer.valueOf(submitReqDto.getMemNo()), selectKey);

        }catch (Exception e){
            log.error("Feign 获取租客会员信息失败,submitReqDto={},responseData={}",JSON.toJSONString(submitReqDto),null,e);
            RenterMemberByFeignException renterMemberByFeignException = new RenterMemberByFeignException(SubmitOrderErrorEnum.FEIGN_GET_RENTER_MEMBER_ERROR.getCode(), SubmitOrderErrorEnum.FEIGN_GET_RENTER_MEMBER_ERROR.getText());
            Cat.logError("Feign 获取租客会员信息失败",renterMemberByFeignException);
            throw renterMemberByFeignException;
        }
        if(responseData == null || !ErrorCode.SUCCESS.getCode().equals(responseData.getResCode()) || responseData.getData() == null){
            log.error("Feign 获取租客会员信息失败,submitReqDto={},responseData={}",JSON.toJSONString(submitReqDto),JSON.toJSONString(responseData));
            RenterMemberByFeignException renterMemberByFeignException = new RenterMemberByFeignException(SubmitOrderErrorEnum.FEIGN_GET_RENTER_MEMBER_FAIL.getCode(), SubmitOrderErrorEnum.FEIGN_GET_RENTER_MEMBER_FAIL.getText());
            Cat.logError("Feign 获取租客会员信息失败",renterMemberByFeignException);
            throw renterMemberByFeignException;
        }
        MemberTotalInfo memberTotalInfo = responseData.getData();
        MemberAuthInfo memberAuthInfo = memberTotalInfo.getMemberAuthInfo();
        MemberCoreInfo memberCoreInfo = memberTotalInfo.getMemberCoreInfo();
        RenterMemberDto renterMemberDto = new RenterMemberDto();
        renterMemberDto.setMemNo(submitReqDto.getMemNo());
        renterMemberDto.setPhone(memberCoreInfo.getPhone());
        renterMemberDto.setHeaderUrl(memberCoreInfo.getPortraitPath());
        renterMemberDto.setRealName(memberCoreInfo.getRealName());
        renterMemberDto.setNickName(memberCoreInfo.getNickName());
        renterMemberDto.setCertificationTime(LocalDateTimeUtils.parseStringToLocalDate(memberAuthInfo.getDriLicFirstTime()));
        //renterMemberDto.setOrderSuccessCount();
        List<RenterMemberRightDto> rights = new ArrayList<>();

        MemberRoleInfo memberRoleInfo = memberTotalInfo.getMemberRoleInfo();
        if(memberRoleInfo != null){
            if(memberRoleInfo.getInternalStaff()!=null && memberRoleInfo.getInternalStaff().equals(1)){
               /* RenterMemberRightDto internalStaff = new RenterMemberRightDto();
                internalStaff.setRightCode(RenterMemRightEnum.STAFF.getRightCode());
                internalStaff.setRightName();
                internalStaff.setRightValue();
                internalStaff.setRightDesc();
                rights.add();*/
            }
        }


         responseData.getData();
        return new RenterMemberDto();
    }

    public OwnerMemberDto getOwnerMemberInfo(NormalOrderReqVO submitReqDto) throws RenterMemberByFeignException {
        List<String> selectKey = Arrays.asList(
                MemberSelectKeyEnum.MEMBER_CORE_INFO.getKey(),
                MemberSelectKeyEnum.MEMBER_AUTH_INFO.getKey(),
                MemberSelectKeyEnum.MEMBER_BASE_INFO.getKey(),
                MemberSelectKeyEnum.MEMBER_ROLE_INFO.getKey());
        ResponseData<MemberTotalInfo> responseData = null;
        log.info("Feign 开始获取车主会员信息,submitReqDto={}",JSON.toJSONString(submitReqDto));
        try{
            responseData = memberDetailFeignService.getMemberSelectInfo(Integer.valueOf(submitReqDto.getMemNo()), selectKey);

        }catch (Exception e){
            log.error("Feign 获取车主会员信息失败,submitReqDto={},responseData={}",JSON.toJSONString(submitReqDto),null,e);
            Cat.logError("Feign 获取车主会员信息失败",e);
            throw new OwnerberByFeignException(SubmitOrderErrorEnum.FEIGN_GET_OWNER_MEMBER_ERROR.getCode(),SubmitOrderErrorEnum.FEIGN_GET_OWNER_MEMBER_ERROR.getText());
        }
        if(responseData == null || !ErrorCode.SUCCESS.getCode().equals(responseData.getResCode())){
            log.error("Feign 获取车主会员信息失败,submitReqDto={},responseData={}",JSON.toJSONString(submitReqDto),JSON.toJSONString(responseData));
            OwnerberByFeignException ownerberByFeignException = new OwnerberByFeignException(SubmitOrderErrorEnum.FEIGN_GET_OWNER_MEMBER_FAIL.getCode(), SubmitOrderErrorEnum.FEIGN_GET_OWNER_MEMBER_FAIL.getText());
            Cat.logError("Feign 获取车主会员信息失败",ownerberByFeignException);
            throw ownerberByFeignException;
        }
        responseData.getData();



        return new OwnerMemberDto();
    }


    public RenterGoodsDetailDto getRenterGoodsDetail(NormalOrderReqVO submitOrderReq){
        OrderCarInfoParamDTO orderCarInfoParamDTO = new OrderCarInfoParamDTO();
        orderCarInfoParamDTO.setCarNo(Integer.parseInt(submitOrderReq.getCarNo()));
        orderCarInfoParamDTO.setCarAddressIndex(Integer.valueOf(submitOrderReq.getCarAddrIndex()));
        orderCarInfoParamDTO.setRentTime(LocalDateTimeUtils.localDateTimeToLong(submitOrderReq.getRentTime()));
        orderCarInfoParamDTO.setRevertTime(LocalDateTimeUtils.localDateTimeToLong(submitOrderReq.getRevertTime()));
        //FIXME:
//        orderCarInfoParamDTO.setUseSpecialPrice(GlobalConstant.USE_SPECIAL_PRICE.equals(submitOrderReq.getUseSpecialPrice()));
        ResponseObject<CarDetailVO> responseObject = null;
        try{
            log.info("Feign 开始获取车辆信息,orderCarInfoParamDTO={}",JSON.toJSONString(orderCarInfoParamDTO));
            responseObject = carDetailQueryFeignApi.getCarDetailOfTransByCarNo(orderCarInfoParamDTO);
        }catch (Exception e){
            log.error("Feign 获取车辆信息异常,responseObject={},orderCarInfoParamDTO={}",JSON.toJSONString(responseObject),JSON.toJSONString(orderCarInfoParamDTO),e);
            CarDetailByFeignException carDetailByFeignException = new CarDetailByFeignException(SubmitOrderErrorEnum.FEIGN_GET_CAR_DETAIL_ERROR.getCode(), SubmitOrderErrorEnum.FEIGN_GET_CAR_DETAIL_ERROR.getText());
            Cat.logError("Feign 获取车辆信息异常",carDetailByFeignException);
            throw carDetailByFeignException;
        }
        if(responseObject == null || !ErrorCode.SUCCESS.getCode().equals(responseObject.getResCode())){
            log.error("Feign 获取车辆信息失败,responseObject={},orderCarInfoParamDTO={}",JSON.toJSONString(responseObject),JSON.toJSONString(orderCarInfoParamDTO));
            RenterMemberByFeignException renterMemberByFeignException = new RenterMemberByFeignException(SubmitOrderErrorEnum.FEIGN_GET_CAR_DETAIL_FAIL.getCode(), SubmitOrderErrorEnum.FEIGN_GET_CAR_DETAIL_FAIL.getText());
            Cat.logError("Feign 获取车辆信息失败",renterMemberByFeignException);
            throw renterMemberByFeignException;
        }
        CarDetailVO data = responseObject.getData();
        CarBaseVO carBaseVO = data.getCarBaseVO();
        CarStewardVO carSteward = data.getCarSteward();
        CarDetailImageVO detailImageVO = data.getDetailImageVO();
        CarAddressOfTransVO carAddressOfTransVO = data.getCarAddressOfTransVO();
        RenterGoodsDetailDto renterGoodsDetailDto = new RenterGoodsDetailDto();
        renterGoodsDetailDto.setReplyFlag(carBaseVO.getTransReplyVO().getReplyFlag());
        renterGoodsDetailDto.setCarAddrIndex(Integer.valueOf(submitOrderReq.getCarAddrIndex()));
        renterGoodsDetailDto.setCarNo(carBaseVO.getCarNo());
        renterGoodsDetailDto.setCarPlateNum(carBaseVO.getPlateNum());
        renterGoodsDetailDto.setCarBrand(carBaseVO.getBrand());
        renterGoodsDetailDto.setCarBrandTxt(carBaseVO.getBrandTxt());
        renterGoodsDetailDto.setCarRating(carBaseVO.getRating());
        renterGoodsDetailDto.setCarType(Integer.valueOf(carBaseVO.getType()));
        renterGoodsDetailDto.setCarTypeTxt(carBaseVO.getTypeTxt());
        renterGoodsDetailDto.setCarDisplacement(carBaseVO.getCc());
        renterGoodsDetailDto.setCarGearboxType(carBaseVO.getGbType());
        renterGoodsDetailDto.setCarDayMileage(carBaseVO.getDayMileage());
        renterGoodsDetailDto.setCarIntrod(carBaseVO.getCarDesc());
        renterGoodsDetailDto.setCarSurplusPrice(carBaseVO.getSurplusPrice());
        //FIXME:
//        renterGoodsDetailDto.setCarUseSpecialPrice(Integer.valueOf(submitOrderReq.getUseSpecialPrice()));
        renterGoodsDetailDto.setCarGuidePrice(carBaseVO.getGuidePrice());
        renterGoodsDetailDto.setCarStatus(carBaseVO.getStatus());
        renterGoodsDetailDto.setCarImageUrl(getCoverPic(detailImageVO));
        renterGoodsDetailDto.setCarOwnerType(carBaseVO.getMajorType());
        renterGoodsDetailDto.setCarUseType(carBaseVO.getUseType());
        renterGoodsDetailDto.setCarOilVolume(carBaseVO.getOilVolume());
        renterGoodsDetailDto.setCarEngineType(carBaseVO.getEngineType());
        renterGoodsDetailDto.setCarDesc(carBaseVO.getCarDesc());
        renterGoodsDetailDto.setCarStewardPhone(carSteward.getStewardPhone()==null?"":String.valueOf(carSteward.getStewardPhone()));
        //renterGoodsDetailDto.setCarCheckStatus();
        renterGoodsDetailDto.setCarShowAddr(carAddressOfTransVO.getCarVirtualAddress());
        renterGoodsDetailDto.setCarShowLon(carAddressOfTransVO.getVirtualAddressLon()==null?"":String.valueOf(carAddressOfTransVO.getVirtualAddressLon()));
        renterGoodsDetailDto.setCarShowLat(carAddressOfTransVO.getVirtualAddressLat()==null?"":String.valueOf(carAddressOfTransVO.getVirtualAddressLat()));
        renterGoodsDetailDto.setCarRealAddr(carAddressOfTransVO.getCarRealAddress());
        renterGoodsDetailDto.setCarRealLon(carAddressOfTransVO.getRealAddressLon()==null?"":String.valueOf(carAddressOfTransVO.getRealAddressLon()));
        renterGoodsDetailDto.setCarRealLat(carAddressOfTransVO.getRealAddressLat()==null?"":String.valueOf(carAddressOfTransVO.getRealAddressLat()));
        return renterGoodsDetailDto;
    }

    private OwnerGoodsDetailDto getOwnerGoodsDetail(RenterGoodsDetailDto renterGoodsDetailDto) throws CarDetailByFeignException, RenterMemberByFeignException {
        OwnerGoodsDetailDto ownerGoodsDetailDto = new OwnerGoodsDetailDto();
        BeanUtils.copyProperties(renterGoodsDetailDto, ownerGoodsDetailDto);
        return ownerGoodsDetailDto;
    }

    //获取车辆封面图片路径
    private String getCoverPic(CarDetailImageVO detailImageVO){
        String coverPic = "";
        if(detailImageVO == null || detailImageVO.getCarImages() == null || detailImageVO.getCarImages().size()<=0){
            return coverPic;
        }
        List<ImageVO> collect = detailImageVO.getCarImages()
                .stream()
                .filter(x -> "0".equals(x.getCover()))
                .limit(1)
                .collect(Collectors.toList());
        coverPic = collect.size()<=0 ?  "" : collect.get(0).getPicPath();
        return coverPic;
    }
}
