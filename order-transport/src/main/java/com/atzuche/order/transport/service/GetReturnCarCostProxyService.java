package com.atzuche.order.transport.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.config.client.api.CityConfigSDK;
import com.atzuche.config.client.api.DefaultConfigContext;
import com.atzuche.config.common.entity.CityEntity;
import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.commons.LocalDateTimeUtils;
import com.atzuche.order.commons.ResponseCheckUtil;
import com.atzuche.order.commons.entity.dto.CostBaseDTO;
import com.atzuche.order.commons.entity.dto.GetReturnCarCostReqDto;
import com.atzuche.order.commons.entity.dto.RenterOrderSubsidyDetailDTO;
import com.atzuche.order.commons.enums.SubsidySourceCodeEnum;
import com.atzuche.order.commons.enums.SubsidyTypeCodeEnum;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.commons.vo.res.rentcosts.RenterOrderCostDetailEntity;
import com.atzuche.order.transport.common.TransPortErrorCode;
import com.atzuche.order.transport.utils.GetReturnCarCostNeedUtil;
import com.atzuche.order.transport.vo.CityDTO;
import com.atzuche.order.transport.vo.GetReturnCostDTO;
import com.atzuche.order.transport.vo.GetReturnResponseVO;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.feeservice.api.FetchBackCarFeeFeignService;
import com.autoyol.feeservice.api.request.GetFbcFeeRequest;
import com.autoyol.feeservice.api.request.GetFbcFeeRequestDetail;
import com.autoyol.feeservice.api.response.PriceFbcFeeResponseDetail;
import com.autoyol.feeservice.api.response.PriceGetFbcFeeResponse;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 获取租客取还车费用（待优化  去掉DTO 入参出参用基础数据类型返回）
 */
@Service
@Slf4j
public class GetReturnCarCostProxyService {

    @Autowired
    private FetchBackCarFeeFeignService fetchBackCarFeeFeignService;
    @Autowired
    private CityConfigSDK cityConfigSDK;

    /**
     * 获取租客取还车费用
     * @param getReturnCarCostReqDto
     * @return
     */
    public GetReturnCostDTO getReturnCarCost(GetReturnCarCostReqDto getReturnCarCostReqDto) {
        GetReturnCostDTO getReturnCostDto = new GetReturnCostDTO();
        List<RenterOrderCostDetailEntity> listCostDetail = new ArrayList<>();
        List<RenterOrderSubsidyDetailDTO> listCostSubsidy = new ArrayList<>();
        GetReturnResponseVO getReturnResponse = new GetReturnResponseVO();

        if(getReturnCarCostReqDto == null || (!getReturnCarCostReqDto.getIsGetCarCost() && !getReturnCarCostReqDto.getIsReturnCarCost())){
            log.info("不需要计算取还车费用getReturnCarCostReqDto=[{}]", JSON.toJSONString(getReturnCarCostReqDto));
            getReturnCostDto.setRenterOrderCostDetailEntityList(listCostDetail);
            getReturnCostDto.setRenterOrderSubsidyDetailDTOList(listCostSubsidy);
            getReturnCostDto.setGetReturnResponseVO(getReturnResponse);
            return getReturnCostDto;
        }

        CostBaseDTO costBaseDTO = getReturnCarCostReqDto.getCostBaseDTO();
        // 城市编码
        Integer cityCode = getReturnCarCostReqDto.getCityCode();
        // 订单来源
        Integer source = getReturnCarCostReqDto.getSource();
        // 场景号
        String entryCode = getReturnCarCostReqDto.getEntryCode();
        // 取车经度
        String srvGetLon = getReturnCarCostReqDto.getSrvGetLon();
        // 取车纬度
        String srvGetLat = getReturnCarCostReqDto.getSrvGetLat();
        // 还车经度
        String srvReturnLon = getReturnCarCostReqDto.getSrvReturnLon();
        // 还车纬度
        String srvReturnLat = getReturnCarCostReqDto.getSrvReturnLat();
        // 车辆经度
        String carLon = getReturnCarCostReqDto.getCarShowLon();
        // 车辆纬度
        String carLat = getReturnCarCostReqDto.getCarShowLat();
        boolean getFlag = StringUtils.isBlank(srvGetLon) || StringUtils.isBlank(srvGetLat) || "0.0".equalsIgnoreCase(srvGetLon) || "0.0".equalsIgnoreCase(srvGetLat);
        boolean returnFlag = StringUtils.isBlank(srvReturnLon) || StringUtils.isBlank(srvReturnLat) || "0.0".equalsIgnoreCase(srvReturnLon) || "0.0".equalsIgnoreCase(srvReturnLat);
        CityDTO cityDTO = null;
        if (getFlag || returnFlag) {
            cityDTO = new CityDTO();
            CityEntity configByCityCode = cityConfigSDK.getConfigByCityCode(new DefaultConfigContext(), cityCode);
            log.info("计算取还车费用-配置服务中获取配置信息configByCityCode=[{}]",configByCityCode);
            String lat = configByCityCode.getLat();
            String lon = configByCityCode.getLon();
            cityDTO.setLon(lon);
            cityDTO.setLat(lat);
        }
        if (getFlag && cityDTO != null) {
            srvGetLon = cityDTO.getLon();
            srvGetLat = cityDTO.getLat();
        }
        if (returnFlag && cityDTO != null) {
            srvReturnLon = cityDTO.getLon();
            srvReturnLat = cityDTO.getLat();
        }
        //获取取车的距离
        Float getDistance = GetReturnCarCostNeedUtil.getRealDistance(srvGetLon, srvGetLat, carLon, carLat);
        //获取还车的距离
        Float returnDistance = GetReturnCarCostNeedUtil.getRealDistance(srvReturnLon, srvReturnLat, carLon, carLat);
        String channelCode = GetReturnCarCostNeedUtil.getChannelCodeByEntryCode(entryCode).getTypeCode();
        if(getReturnCarCostReqDto.getIsPackageOrder() != null && getReturnCarCostReqDto.getIsPackageOrder()){
            channelCode = GetReturnCarCostNeedUtil.getChannelCode(source).getTypeCode();
        }
        // 租金+保险+不计免赔+手续费
        Integer sumJudgeFreeFee = getReturnCarCostReqDto.getSumJudgeFreeFee();
        // 订单的租金，平台保障费，全面保障费，平台手续费总和小于300，则取还车服务不享受免费
        sumJudgeFreeFee = sumJudgeFreeFee == null ? 0:sumJudgeFreeFee;
        String sumJudgeFreeFeeStr = String.valueOf(sumJudgeFreeFee);
        log.info("取还车费用计算，租金+保险+不计免赔+手续费 sumJudgeFreeFee=[{}]", sumJudgeFreeFee);

        GetFbcFeeRequest getFbcFeeRequest = new GetFbcFeeRequest();
        List<GetFbcFeeRequestDetail> reqList = new ArrayList<>();

        GetFbcFeeRequestDetail getCost = new GetFbcFeeRequestDetail();
        getCost.setChannelName(channelCode);
        getCost.setRequestTime(LocalDateTimeUtils.getNowDateLong());
        getCost.setGetReturnType("get");
        getCost.setGetReturnTime(String.valueOf(LocalDateTimeUtils.localDateTimeToLong(costBaseDTO.getStartTime())));
        getCost.setCityId(String.valueOf(cityCode));
        getCost.setOrderType(GetReturnCarCostNeedUtil.getIsPackageOrder(getReturnCarCostReqDto.getIsPackageOrder()));
        getCost.setDistance(String.valueOf(getDistance));
        if(returnFlag && cityDTO !=null) {
            getCost.setRenterLocation(cityDTO.getLon()+","+ cityDTO.getLat());
        } else {
            getCost.setRenterLocation(srvGetLon+","+srvGetLat);
        }

        getCost.setSumJudgeFreeFee(sumJudgeFreeFeeStr);
        reqList.add(getCost);


        GetFbcFeeRequestDetail returnCost = new GetFbcFeeRequestDetail();
        returnCost.setChannelName(channelCode);
        returnCost.setRequestTime(LocalDateTimeUtils.getNowDateLong());
        returnCost.setGetReturnType("return");
        returnCost.setGetReturnTime(String.valueOf(LocalDateTimeUtils.localDateTimeToLong(costBaseDTO.getEndTime())));
        returnCost.setCityId(String.valueOf(cityCode));
        returnCost.setOrderType(GetReturnCarCostNeedUtil.getIsPackageOrder(getReturnCarCostReqDto.getIsPackageOrder()));
        returnCost.setDistance(String.valueOf(returnDistance));
        if(returnFlag && cityDTO !=null) {
            returnCost.setRenterLocation(cityDTO.getLon()+","+ cityDTO.getLat());
        } else {
            returnCost.setRenterLocation(srvReturnLon+","+srvReturnLat);
        }
        returnCost.setSumJudgeFreeFee(sumJudgeFreeFeeStr);
        reqList.add(returnCost);

        getFbcFeeRequest.setReq(reqList);
        ResponseData<PriceGetFbcFeeResponse> responseData = null;
        Transaction t = Cat.newTransaction(com.atzuche.order.commons.CatConstants.FEIGN_CALL, "取还车费用");
        try{
            log.info("Feign 获取取还车费用入参:[{}]",JSON.toJSONString(getFbcFeeRequest));
            Cat.logEvent(CatConstants.FEIGN_METHOD,"FetchBackCarFeeFeignService.getFbcFee");
            Cat.logEvent(CatConstants.FEIGN_PARAM,JSON.toJSONString(getFbcFeeRequest));
            responseData = fetchBackCarFeeFeignService.getFbcFee(getFbcFeeRequest);
            log.info("Feign 获取取还车费用结果:[{}],获取取还车费用入参:[{}]",JSON.toJSONString(responseData),JSON.toJSONString(getFbcFeeRequest));
            ResponseCheckUtil.checkResponse(responseData);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseData));
            t.setStatus(Transaction.SUCCESS);
        }catch (Exception e){
            Cat.logError("Feign 获取取还车费用接口异常",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }
        List<PriceFbcFeeResponseDetail> fbcFeeResults = responseData.getData().getFbcFeeResults();

        getReturnResponse.setSumJudgeFreeFee(sumJudgeFreeFee);
        fbcFeeResults.forEach(fbcFeeResponse -> {
            if ("get".equalsIgnoreCase(fbcFeeResponse.getGetReturnType()) && getReturnCarCostReqDto.getIsGetCarCost()) {
                int expectedShouldFee = Integer.valueOf(fbcFeeResponse.getExpectedShouldFee());

                RenterOrderCostDetailEntity renterOrderCostDetailEntity = new RenterOrderCostDetailEntity();
                renterOrderCostDetailEntity.setOrderNo(costBaseDTO.getOrderNo());
                renterOrderCostDetailEntity.setRenterOrderNo(costBaseDTO.getRenterOrderNo());
                renterOrderCostDetailEntity.setMemNo(costBaseDTO.getMemNo());
                renterOrderCostDetailEntity.setCostCode(RenterCashCodeEnum.SRV_GET_COST.getCashNo());
                renterOrderCostDetailEntity.setCostDesc(RenterCashCodeEnum.SRV_GET_COST.getTxt());
                renterOrderCostDetailEntity.setCount(1D);
                renterOrderCostDetailEntity.setUnitPrice(Math.abs(expectedShouldFee));
                renterOrderCostDetailEntity.setTotalAmount(-expectedShouldFee);
                listCostDetail.add(renterOrderCostDetailEntity);
                int expectedRealFee = Integer.valueOf(fbcFeeResponse.getExpectedRealFee());
                int diffe = expectedShouldFee - expectedRealFee;
                if(diffe != 0){
                    RenterOrderSubsidyDetailDTO renterOrderSubsidy = new RenterOrderSubsidyDetailDTO();
                    renterOrderSubsidy.setOrderNo(costBaseDTO.getOrderNo());
                    renterOrderSubsidy.setRenterOrderNo(costBaseDTO.getRenterOrderNo());
                    renterOrderSubsidy.setMemNo(costBaseDTO.getMemNo());
                    renterOrderSubsidy.setSubsidyTypeName(SubsidyTypeCodeEnum.GET_CAR.getDesc());
                    renterOrderSubsidy.setSubsidyTypeCode(SubsidyTypeCodeEnum.GET_CAR.getCode());
                    renterOrderSubsidy.setSubsidySourceCode(SubsidySourceCodeEnum.PLATFORM.getCode());
                    renterOrderSubsidy.setSubsidySourceName(SubsidySourceCodeEnum.PLATFORM.getDesc());
                    renterOrderSubsidy.setSubsidyTargetCode(SubsidySourceCodeEnum.RENTER.getCode());
                    renterOrderSubsidy.setSubsidyTargetName(SubsidySourceCodeEnum.RENTER.getDesc());
                    renterOrderSubsidy.setSubsidyCostCode(RenterCashCodeEnum.SRV_GET_COST.getCashNo());
                    renterOrderSubsidy.setSubsidyCostName(RenterCashCodeEnum.SRV_GET_COST.getTxt());
                    renterOrderSubsidy.setSubsidyDesc("平台补贴给租客的取车费用！");
                    renterOrderSubsidy.setSubsidyAmount(diffe);
                    renterOrderSubsidy.setSubsidyVoucher("");
                    listCostSubsidy.add(renterOrderSubsidy);
                }
                getReturnResponse.setGetFee(Integer.valueOf(fbcFeeResponse.getExpectedRealFee()));
                getReturnResponse.setGetShouldFee(Integer.valueOf(fbcFeeResponse.getExpectedShouldFee()));
                getReturnResponse.setGetInitFee(Integer.valueOf(fbcFeeResponse.getBaseFee()));
                getReturnResponse.setGetTimePeriodUpPrice(fbcFeeResponse.getTimePeriodUpPrice());
                getReturnResponse.setGetDistanceUpPrice(fbcFeeResponse.getDistanceUpPrice());
                getReturnResponse.setGetCicrleUpPrice(fbcFeeResponse.getCicrleUpPrice());
                getReturnResponse.setGetShowDistance(Double.valueOf(fbcFeeResponse.getShowDistance()));

            } else if ("return".equalsIgnoreCase(fbcFeeResponse.getGetReturnType()) && getReturnCarCostReqDto.getIsReturnCarCost()) {
                int expectedShouldFee = Integer.valueOf(fbcFeeResponse.getExpectedShouldFee());
                RenterOrderCostDetailEntity renterOrderCostDetailEntity = new RenterOrderCostDetailEntity();
                renterOrderCostDetailEntity.setOrderNo(costBaseDTO.getOrderNo());
                renterOrderCostDetailEntity.setRenterOrderNo(costBaseDTO.getRenterOrderNo());
                renterOrderCostDetailEntity.setMemNo(costBaseDTO.getMemNo());
                renterOrderCostDetailEntity.setCostCode(RenterCashCodeEnum.SRV_RETURN_COST.getCashNo());
                renterOrderCostDetailEntity.setCostDesc(RenterCashCodeEnum.SRV_RETURN_COST.getTxt());
                renterOrderCostDetailEntity.setCount(1D);
                renterOrderCostDetailEntity.setUnitPrice(Math.abs(expectedShouldFee));
                renterOrderCostDetailEntity.setTotalAmount(-expectedShouldFee);
                listCostDetail.add(renterOrderCostDetailEntity);

                int expectedRealFee = Integer.valueOf(fbcFeeResponse.getExpectedRealFee());
                int diff = expectedShouldFee - expectedRealFee;
                if(diff != 0){
                    RenterOrderSubsidyDetailDTO renterOrderSubsidy = new RenterOrderSubsidyDetailDTO();
                    renterOrderSubsidy.setOrderNo(costBaseDTO.getOrderNo());
                    renterOrderSubsidy.setRenterOrderNo(costBaseDTO.getRenterOrderNo());
                    renterOrderSubsidy.setMemNo(costBaseDTO.getMemNo());
                    renterOrderSubsidy.setSubsidyTypeName(SubsidyTypeCodeEnum.RETURN_CAR.getDesc());
                    renterOrderSubsidy.setSubsidyTypeCode(SubsidyTypeCodeEnum.RETURN_CAR.getCode());
                    renterOrderSubsidy.setSubsidySourceCode(SubsidySourceCodeEnum.PLATFORM.getCode());
                    renterOrderSubsidy.setSubsidySourceName(SubsidySourceCodeEnum.PLATFORM.getDesc());
                    renterOrderSubsidy.setSubsidyTargetCode(SubsidySourceCodeEnum.RENTER.getCode());
                    renterOrderSubsidy.setSubsidyTargetName(SubsidySourceCodeEnum.RENTER.getDesc());
                    renterOrderSubsidy.setSubsidyCostCode(RenterCashCodeEnum.SRV_RETURN_COST.getCashNo());
                    renterOrderSubsidy.setSubsidyCostName(RenterCashCodeEnum.SRV_RETURN_COST.getTxt());
                    renterOrderSubsidy.setSubsidyDesc("平台补贴给租客的还车费用！");
                    renterOrderSubsidy.setSubsidyAmount(diff);
                    renterOrderSubsidy.setSubsidyVoucher("");
                    listCostSubsidy.add(renterOrderSubsidy);
                }
                getReturnResponse.setReturnFee(Integer.valueOf(fbcFeeResponse.getExpectedRealFee()));
                getReturnResponse.setReturnShouldFee(Integer.valueOf(fbcFeeResponse.getExpectedShouldFee()));
                getReturnResponse.setReturnInitFee(Integer.valueOf(fbcFeeResponse.getBaseFee()));
                getReturnResponse.setReturnTimePeriodUpPrice(fbcFeeResponse.getTimePeriodUpPrice());
                getReturnResponse.setReturnDistanceUpPrice(fbcFeeResponse.getDistanceUpPrice());
                getReturnResponse.setReturnCicrleUpPrice(fbcFeeResponse.getCicrleUpPrice());
                getReturnResponse.setReturnShowDistance(Double.valueOf(fbcFeeResponse.getShowDistance()));
            }
        });
        getReturnCostDto.setGetReturnResponseVO(getReturnResponse);
        getReturnCostDto.setRenterOrderCostDetailEntityList(listCostDetail);
        getReturnCostDto.setRenterOrderSubsidyDetailDTOList(listCostSubsidy);
        return getReturnCostDto;
    }
}
