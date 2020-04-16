package com.atzuche.order.transport.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.*;
import com.atzuche.order.commons.entity.dto.CostBaseDTO;
import com.atzuche.order.commons.entity.dto.GetReturnCarOverCostReqDto;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.transport.entity.RenterOrderCostDetailDTO;
import com.atzuche.order.transport.vo.GetReturnOverCostDTO;
import com.atzuche.order.transport.vo.GetReturnOverTransportDTO;
import com.autoyol.car.api.feign.api.GetBackCityLimitFeignApi;
import com.autoyol.car.api.model.vo.ResponseObject;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.feeservice.api.FetchBackCarFeeFeignService;
import com.autoyol.feeservice.api.request.GetFbcFeeConfigRequest;
import com.autoyol.feeservice.api.vo.pricefetchback.PriceCarHumanFeeRule;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * 超运能服务
 * 取还车超出运能附加金额 （待优化  去掉DTO 入参出参用基础数据类型返回）
 * @return
 */

@Service
@Slf4j
public class TranSportProxyService {

    @Value("${auto.cost.getReturnOverTransportFee}")
    private Integer getReturnOverTransportFee;
    @Value("${auto.cost.nightBegin}")
    private Integer nightBegin;
    @Value("${auto.cost.nightEnd}")
    private Integer nightEnd;
    @Value("${auto.cost.configHours}")
    private Integer configHours;

    @Autowired
    private GetBackCityLimitFeignApi getBackCityLimitFeignApi;
    @Autowired
    FetchBackCarFeeFeignService fetchBackCarFeeFeignService;
    private static final Integer [] ORDER_TYPES = {1,2};

    public GetReturnOverCostDTO getGetReturnOverCost(GetReturnCarOverCostReqDto getReturnCarOverCostReqDto) {
        GetReturnOverCostDTO getReturnOverCostDTO = new GetReturnOverCostDTO();
        List<RenterOrderCostDetailDTO> renterOrderCostDetailEntityList = new ArrayList<>();
        CostBaseDTO costBaseDTO = getReturnCarOverCostReqDto.getCostBaseDTO();
        LocalDateTime rentTime = costBaseDTO.getStartTime();
        LocalDateTime revertTime = costBaseDTO.getEndTime();
        Integer cityCode = getReturnCarOverCostReqDto.getCityCode();

        // 初始化数据
        GetReturnOverTransportDTO getReturnOverTransport = new GetReturnOverTransportDTO(false, 0, false, 0);
        getReturnOverTransport.setIsUpdateRentTime(true);
        getReturnOverTransport.setIsUpdateRevertTime(true);

        if (cityCode == null || (rentTime == null && revertTime == null)) {
            getReturnOverCostDTO.setRenterOrderCostDetailEntityList(renterOrderCostDetailEntityList);
            return getReturnOverCostDTO;
        }
        boolean getIsGetCarCost = getReturnCarOverCostReqDto.getIsGetCarCost() == null ? false : getReturnCarOverCostReqDto.getIsGetCarCost();
        boolean getIsReturnCarCost = getReturnCarOverCostReqDto.getIsReturnCarCost()==null?false:getReturnCarOverCostReqDto.getIsReturnCarCost();
        if(!getIsGetCarCost && !getIsReturnCarCost){
            log.info("不需要计算超运能费用getReturnCarOverCostReqDto={}",JSON.toJSONString(getReturnCarOverCostReqDto));
            getReturnOverCostDTO.setRenterOrderCostDetailEntityList(renterOrderCostDetailEntityList);
            return getReturnOverCostDTO;
        }
        try {
            // 超运能后计算附加费的订单类型列表1-短租订单和3-平台套餐订单
            List<Integer> orderTypeList = Arrays.asList(ORDER_TYPES);
            // 超运能后计算附加费标志
            Boolean isAddFee = orderTypeList.contains(getReturnCarOverCostReqDto.getOrderType());
            Integer overTransportFee = this.getGetReturnOverTransportFee(cityCode);
            String rentTimeLongStr = String.valueOf(LocalDateTimeUtils.localDateTimeToLong(rentTime));

            if (rentTime != null && getIsGetCarCost) {
                ResponseObject<Boolean> getFlgResponse = null;
                Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "取车是否超运能");
                try{
                    Cat.logEvent(CatConstants.FEIGN_METHOD,"GetBackCityLimitFeignApi.isCityServiceLimit");
                    Long rentTimeLong = Long.valueOf(rentTimeLongStr.substring(0, 12));
                    Cat.logEvent(CatConstants.FEIGN_PARAM,"cityCode="+cityCode+"&rentTimeLong="+rentTimeLong);
                    getFlgResponse = getBackCityLimitFeignApi.isCityServiceLimit(cityCode, rentTimeLong);
                    Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(getFlgResponse));
                    ResponseObjectCheckUtil.checkResponse(getFlgResponse);
                    t.setStatus(Transaction.SUCCESS);
                }catch (Exception e){
                    log.error("Feign 取车超运能接口异常",e);
                    Cat.logError("Feign 取车超运能接口异常5",e);
                    t.setStatus(e);
                    throw e;
                }finally {
                    t.complete();
                }

                boolean getFlag = getFlgResponse.getData();
                if (getFlag) {
                    // 取还车超出运能附加金额
                    if (isAddFee) {
                        getReturnOverTransport.setGetOverTransportFee(overTransportFee);
                        if(DateUtils.isNight(rentTimeLongStr, nightBegin, nightEnd)) {
                            //夜间
                            getReturnOverTransport.setNightGetOverTransportFee(overTransportFee==null?0:overTransportFee);
                        }
                        RenterOrderCostDetailDTO renterOrderCostDetailEntity = new RenterOrderCostDetailDTO();
                        renterOrderCostDetailEntity.setOrderNo(costBaseDTO.getOrderNo());
                        renterOrderCostDetailEntity.setRenterOrderNo(costBaseDTO.getRenterOrderNo());
                        renterOrderCostDetailEntity.setMemNo(costBaseDTO.getMemNo());
                        renterOrderCostDetailEntity.setTotalAmount(-overTransportFee);
                        renterOrderCostDetailEntity.setCount(1D);
                        renterOrderCostDetailEntity.setCostCode(RenterCashCodeEnum.GET_BLOCKED_RAISE_AMT.getCashNo());
                        renterOrderCostDetailEntity.setCostDesc(RenterCashCodeEnum.GET_BLOCKED_RAISE_AMT.getTxt());
                        renterOrderCostDetailEntity.setUnitPrice(overTransportFee);
                        renterOrderCostDetailEntityList.add(renterOrderCostDetailEntity);
                    }
                    // 标记取车时间超出运能
                    getReturnOverTransport.setIsGetOverTransport(true);
                } else {
                    getReturnOverTransport.setGetOverTransportFee(0);
                    getReturnOverTransport.setIsGetOverTransport(false);
                }
            }

            if (revertTime != null && getIsReturnCarCost) {
                String revertTimeLongStr = String.valueOf(LocalDateTimeUtils.localDateTimeToLong(revertTime));
                ResponseObject<Boolean>  returnFlgResponse = null;
                Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL,"还车是否超运能");
                try{
                    Cat.logEvent(CatConstants.FEIGN_METHOD,"GetBackCityLimitFeignApi.isCityServiceLimit");
                    long revertTimeLong = Long.valueOf(revertTimeLongStr.substring(0, 12));
                    Cat.logEvent(CatConstants.FEIGN_PARAM,"cityCode="+cityCode+"&revertTimeLong="+revertTimeLong);
                    returnFlgResponse = getBackCityLimitFeignApi.isCityServiceLimit(cityCode, revertTimeLong);
                    Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(returnFlgResponse));
                    ResponseObjectCheckUtil.checkResponse(returnFlgResponse);
                    t.setStatus(Transaction.SUCCESS);
                }catch (Exception e){
                    Cat.logError("还车是否超运能接口异常",e);
                    t.setStatus(e);
                    throw e;
                }finally {
                    t.complete();
                }

                Boolean returnFlag = returnFlgResponse.getData();
                if (returnFlag) {
                    // 取还车超出运能附加金额
                    if (isAddFee) {
                        getReturnOverTransport.setReturnOverTransportFee(overTransportFee);
                        if(DateUtils.isNight(String.valueOf(LocalDateTimeUtils.localDateTimeToLong(revertTime)), nightBegin, nightEnd)) {
                            //夜间
                            getReturnOverTransport.setNightReturnOverTransportFee(overTransportFee==null?0:overTransportFee);;
                        }
                        RenterOrderCostDetailDTO renterOrderCostDetailEntity = new RenterOrderCostDetailDTO();
                        renterOrderCostDetailEntity.setOrderNo(costBaseDTO.getOrderNo());
                        renterOrderCostDetailEntity.setRenterOrderNo(costBaseDTO.getRenterOrderNo());
                        renterOrderCostDetailEntity.setMemNo(costBaseDTO.getMemNo());
                        renterOrderCostDetailEntity.setTotalAmount(-overTransportFee);
                        renterOrderCostDetailEntity.setCount(1D);
                        renterOrderCostDetailEntity.setCostCode(RenterCashCodeEnum.RETURN_BLOCKED_RAISE_AMT.getCashNo());
                        renterOrderCostDetailEntity.setCostDesc(RenterCashCodeEnum.RETURN_BLOCKED_RAISE_AMT.getTxt());
                        renterOrderCostDetailEntity.setUnitPrice(overTransportFee);
                        renterOrderCostDetailEntityList.add(renterOrderCostDetailEntity);
                    }
                    // 标记还车时间超出运能
                    getReturnOverTransport.setIsReturnOverTransport(true);
                } else {
                    getReturnOverTransport.setIsReturnOverTransport(false);
                    getReturnOverTransport.setReturnOverTransportFee(0);
                }
            }

        } catch (Exception e) {
            log.error("获取取还车超运能信息出错：",e);
        }
        getReturnOverCostDTO.setGetReturnOverTransportDTO(getReturnOverTransport);
        getReturnOverCostDTO.setRenterOrderCostDetailEntityList(renterOrderCostDetailEntityList);
        return getReturnOverCostDTO;
    }


    /**
     * 取还车超出运能附加金额
     *
     * @param cityCode 城市编码
     * @return
     */
    @SuppressWarnings("unchecked")
    private Integer getGetReturnOverTransportFee(Integer cityCode) {
        String premiumAmt = null;
        //调用取还车服务接口获取城市对应的超能溢价金额
        GetFbcFeeConfigRequest reqParam = new GetFbcFeeConfigRequest();
        reqParam.setCityId(String.valueOf(cityCode));
        reqParam.setRequestTime(String.valueOf(LocalDateTimeUtils.localDateTimeToLong(LocalDateTime.now())));
        ResponseData<PriceCarHumanFeeRule> responseData = null;
        Transaction t = Cat.newTransaction(com.atzuche.order.commons.CatConstants.FEIGN_CALL, "取还车超出运能附加金额配置");
        try {
            log.info("Feign 获取取还车超出运能附加金额入参:[{}]", JSON.toJSONString(reqParam));
            Cat.logEvent(CatConstants.FEIGN_METHOD, "FetchBackCarFeeFeignService.getPriceCarHumanFeeRuleConfig");
            Cat.logEvent(CatConstants.FEIGN_PARAM, JSON.toJSONString(reqParam));
            responseData = fetchBackCarFeeFeignService.getPriceCarHumanFeeRuleConfig(String.valueOf(cityCode), String.valueOf(LocalDateTimeUtils.localDateTimeToLong(LocalDateTime.now())));
            log.info("Feign 获取取还车超出运能附加金额结果:[{}],获取取还车超出运能附加金额入参:[{}]", JSON.toJSONString(responseData), JSON.toJSONString(reqParam));
            ResponseCheckUtil.checkResponse(responseData);
            Cat.logEvent(CatConstants.FEIGN_RESULT, JSON.toJSONString(responseData));
            t.setStatus(Transaction.SUCCESS);
        }  catch (Exception e) {
            Cat.logError("Feign 获取取还车超出运能附加金额接口异常", e);
            t.setStatus(e);
            throw e;
        }
        if (ErrorCode.SUCCESS.getCode().equals(responseData.getResCode())) {
            return responseData.getData().getHumanFee().intValue();
        }
        try {
            return getReturnOverTransportFee;
        } catch (Exception e) {
            log.error("获取取还车超运能溢价默认值异常：", e);
        }
        return GlobalConstant.GET_RETURN_OVER_COST;
    }

}
