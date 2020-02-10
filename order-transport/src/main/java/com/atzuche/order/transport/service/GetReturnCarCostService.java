package com.atzuche.order.transport.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.config.client.api.CityConfigSDK;
import com.atzuche.config.client.api.DefaultConfigContext;
import com.atzuche.config.common.entity.CityEntity;
import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.commons.LocalDateTimeUtils;
import com.atzuche.order.commons.ResponseCheckUtil;
import com.atzuche.order.commons.entity.dto.CostBaseDTO;
import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.atzuche.order.commons.vo.res.delivery.RenterOrderDeliveryRepVO;
import com.atzuche.order.transport.common.DeliveryCarFeeBaseService;
import com.atzuche.order.transport.interfaces.IDeliveryCarFee;
import com.atzuche.order.transport.utils.GetReturnCarCostNeedUtil;
import com.atzuche.order.transport.vo.CityDTO;
import com.atzuche.order.transport.vo.GetReturnResponseVO;
import com.atzuche.order.transport.vo.delivery.DeliveryCarFeeBaseParamsVO;
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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author 胡春林
 * 获取租客取还车费用信息
 */
@Service
@Slf4j
public class GetReturnCarCostService extends DeliveryCarFeeBaseService implements IDeliveryCarFee<GetReturnResponseVO> {

    @Autowired
    private FetchBackCarFeeFeignService fetchBackCarFeeFeignService;
    @Autowired
    private CityConfigSDK cityConfigSDK;

    /**
     * 获取取还车数据
     * @param orderNo
     * @return
     */
    @Override
    public GetReturnResponseVO getDeliveryCarFee(String orderNo,List<RenterOrderDeliveryRepVO> renterOrderDeliveryRepVOList) {
        GetReturnResponseVO getReturnResponseVO = new GetReturnResponseVO();
        DeliveryCarFeeBaseParamsVO deliveryCarFeeBaseParamsVO = initNeedParams(orderNo,renterOrderDeliveryRepVOList);
        if(Objects.isNull(deliveryCarFeeBaseParamsVO))
        {
            return getReturnResponseVO;
        }
        GetFbcFeeRequest getFbcFeeRequest = createGetReturnCarFee(deliveryCarFeeBaseParamsVO);
        ResponseData<PriceGetFbcFeeResponse> responseData = getDeliveryCarFeeFromTargetFeign(getFbcFeeRequest);
        if(Objects.isNull(responseData) || CollectionUtils.isEmpty(responseData.getData().getFbcFeeResults()))
        {
            return getReturnResponseVO;
        }
        List<PriceFbcFeeResponseDetail> fbcFeeResults = responseData.getData().getFbcFeeResults();
        PriceFbcFeeResponseDetail getPriceFbcFeeResponseDetail = fbcFeeResults.stream().filter(r->r.getGetReturnType().equalsIgnoreCase("get")).findFirst().get();
        PriceFbcFeeResponseDetail returnPriceFbcFeeResponseDetail = fbcFeeResults.stream().filter(r->r.getGetReturnType().equalsIgnoreCase("return")).findFirst().get();
        getReturnResponseVO.setGetFee(Integer.valueOf(getPriceFbcFeeResponseDetail.getExpectedRealFee()));
        getReturnResponseVO.setGetShouldFee(Integer.valueOf(getPriceFbcFeeResponseDetail.getExpectedShouldFee()));
        getReturnResponseVO.setGetInitFee(Integer.valueOf(getPriceFbcFeeResponseDetail.getBaseFee()));
        getReturnResponseVO.setGetTimePeriodUpPrice(getPriceFbcFeeResponseDetail.getTimePeriodUpPrice());
        getReturnResponseVO.setGetDistanceUpPrice(getPriceFbcFeeResponseDetail.getDistanceUpPrice());
        getReturnResponseVO.setGetCicrleUpPrice(getPriceFbcFeeResponseDetail.getCicrleUpPrice());
        getReturnResponseVO.setGetShowDistance(Double.valueOf(getPriceFbcFeeResponseDetail.getShowDistance()));
        getReturnResponseVO.setReturnFee(Integer.valueOf(returnPriceFbcFeeResponseDetail.getExpectedRealFee()));
        getReturnResponseVO.setReturnShouldFee(Integer.valueOf(returnPriceFbcFeeResponseDetail.getExpectedShouldFee()));
        getReturnResponseVO.setReturnInitFee(Integer.valueOf(returnPriceFbcFeeResponseDetail.getBaseFee()));
        getReturnResponseVO.setReturnTimePeriodUpPrice(returnPriceFbcFeeResponseDetail.getTimePeriodUpPrice());
        getReturnResponseVO.setReturnDistanceUpPrice(returnPriceFbcFeeResponseDetail.getDistanceUpPrice());
        getReturnResponseVO.setReturnCicrleUpPrice(returnPriceFbcFeeResponseDetail.getCicrleUpPrice());
        getReturnResponseVO.setReturnShowDistance(Double.valueOf(returnPriceFbcFeeResponseDetail.getShowDistance()));
        return getReturnResponseVO;
    }

    @Override
    public Double getCarFee() {
        return null;
    }

    @Override
    public Double returnCarFee() {
        return null;
    }

    @Override
    public ResponseData<PriceGetFbcFeeResponse> getDeliveryCarFeeFromTargetFeign(Object serializable) {
        GetFbcFeeRequest getFbcFeeRequest = null;
        if (Objects.nonNull(serializable)) {
            try {
                getFbcFeeRequest = (GetFbcFeeRequest) serializable;
            } catch (Exception e) {
                log.error("序列化失败 class:[{}]", serializable.toString());
                return null;
            }
        }
        ResponseData<PriceGetFbcFeeResponse> responseData;
        Transaction t = Cat.newTransaction(com.atzuche.order.commons.CatConstants.FEIGN_CALL, "取还车费用");
        try{
            log.info("Feign 获取取还车费用入参:[{}]", JSON.toJSONString(getFbcFeeRequest));
            Cat.logEvent(CatConstants.FEIGN_METHOD,"GetReturnCarCostService.getDeliveryCarFeeFromTargetFeign");
            Cat.logEvent(CatConstants.FEIGN_PARAM,JSON.toJSONString(getFbcFeeRequest));
            responseData = fetchBackCarFeeFeignService.getFbcFee(getFbcFeeRequest);
            log.info("Feign 获取取还车费用结果:[{}],获取取还车费用出参入参:[{}]",JSON.toJSONString(responseData),JSON.toJSONString(getFbcFeeRequest));
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
        return responseData;
    }

    /**
     * 获取sendDataParams
     * @param deliveryCarFeeBaseParamsVO
     */
    private GetFbcFeeRequest createGetReturnCarFee(DeliveryCarFeeBaseParamsVO deliveryCarFeeBaseParamsVO) {

        List<RenterOrderDeliveryRepVO> renterOrderDeliveryRepVOList = deliveryCarFeeBaseParamsVO.getRenterOrderDeliveryRepVOList();
        RenterGoodsDetailDTO renterGoodsDetailDTO = deliveryCarFeeBaseParamsVO.getRenterGoodsDetailDTO();
        CostBaseDTO costBaseDTO = deliveryCarFeeBaseParamsVO.getCostBaseDTO();
        // 城市编码
        Integer cityCode = Integer.valueOf(renterOrderDeliveryRepVOList.get(0).getCityCode());
        // 取车经度
        String srvGetLon = renterOrderDeliveryRepVOList.stream().filter( r->r.getType().intValue() == 1).findFirst().get().getRenterGetReturnAddrLon();
        // 取车纬度
        String srvGetLat = renterOrderDeliveryRepVOList.stream().filter( r->r.getType().intValue() == 1).findFirst().get().getRenterGetReturnAddrLat();
        // 还车经度
        String srvReturnLon = renterOrderDeliveryRepVOList.stream().filter( r->r.getType().intValue() == 2).findFirst().get().getRenterGetReturnAddrLon();
        // 还车纬度
        String srvReturnLat = renterOrderDeliveryRepVOList.stream().filter( r->r.getType().intValue() == 2).findFirst().get().getRenterGetReturnAddrLat();
        // 车辆经度
        String carLon = renterGoodsDetailDTO.getCarShowLon();
        // 车辆纬度
        String carLat = renterGoodsDetailDTO.getCarShowLat();

        boolean getFlag = StringUtils.isBlank(srvGetLon) || StringUtils.isBlank(srvGetLat) || "0.0".equalsIgnoreCase(srvGetLon) || "0.0".equalsIgnoreCase(srvGetLat);
        boolean returnFlag = StringUtils.isBlank(srvReturnLon) || StringUtils.isBlank(srvReturnLat) || "0.0".equalsIgnoreCase(srvReturnLon) || "0.0".equalsIgnoreCase(srvReturnLat);
        CityDTO cityDTO = null;
        if (getFlag || returnFlag) {
            CityEntity configByCityCode = cityConfigSDK.getConfigByCityCode(new DefaultConfigContext(), cityCode);
            log.info("计算取还车费用-配置服务中获取配置信息configByCityCode=[{}]",configByCityCode);
            cityDTO =  CityDTO.builder().lon(configByCityCode.getLon()).lat(configByCityCode.getLat()).build();
        }
        //获取取车的距离
        Float getDistance = GetReturnCarCostNeedUtil.getRealDistance(srvGetLon, srvGetLat, carLon, carLat);
        //获取还车的距离
        Float returnDistance = GetReturnCarCostNeedUtil.getRealDistance(srvReturnLon, srvReturnLat, carLon, carLat);
        String channelCode = GetReturnCarCostNeedUtil.getChannelCodeByEntryCode(null).getTypeCode();
        // 租金+保险+不计免赔+手续费(传入)
        Integer sumJudgeFreeFee =null;
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
        getCost.setOrderType(GetReturnCarCostNeedUtil.getIsPackageOrder(null));
        getCost.setDistance(String.valueOf(getDistance));
        if(returnFlag && cityDTO !=null) {
            getCost.setRenterLocation(cityDTO.getLon()+","+ cityDTO.getLat());
        } else {
            getCost.setRenterLocation(srvGetLon+","+srvGetLat);
        }
        getCost.setSumJudgeFreeFee(sumJudgeFreeFeeStr);
        GetFbcFeeRequestDetail returnCost = new GetFbcFeeRequestDetail();
        BeanUtils.copyProperties(getCost,returnCost);
        returnCost.setGetReturnType("return");
        returnCost.setGetReturnTime(String.valueOf(LocalDateTimeUtils.localDateTimeToLong(costBaseDTO.getEndTime())));
        returnCost.setDistance(String.valueOf(returnDistance));
        reqList.add(getCost);
        reqList.add(returnCost);
        getFbcFeeRequest.setReq(reqList);
        return getFbcFeeRequest;
    }
}
