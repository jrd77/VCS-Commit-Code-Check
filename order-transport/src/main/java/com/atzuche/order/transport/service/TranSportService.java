package com.atzuche.order.transport.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.commons.LocalDateTimeUtils;
import com.atzuche.order.commons.entity.dto.CostBaseDTO;
import com.atzuche.order.commons.entity.dto.GetReturnCarOverCostReqDto;
import com.atzuche.order.transport.common.TransPortErrorCode;
import com.atzuche.order.transport.exception.OrderTransPortException;
import com.atzuche.order.transport.vo.GetReturnOverCostDTO;
import com.atzuche.order.transport.vo.GetReturnOverTransportDTO;
import com.autoyol.car.api.feign.api.GetBackCityLimitFeignApi;
import com.autoyol.car.api.model.vo.ResponseObject;
import com.autoyol.commons.web.ErrorCode;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @author 胡春林
 * 超运能服务
 */
@Service
@Slf4j
public class TranSportService {

    @Autowired
    private GetBackCityLimitFeignApi getBackCityLimitFeignApi;

    /**
     * 获取取还车超运能信息
     * @param getReturnCarOverCostReqDto
     * @return GetReturnOverCostDTO
     */
    public GetReturnOverCostDTO getGetReturnOverCost(GetReturnCarOverCostReqDto getReturnCarOverCostReqDto) {
        GetReturnOverCostDTO getReturnOverCostDTO = new GetReturnOverCostDTO();
        CostBaseDTO costBaseDTO = getReturnCarOverCostReqDto.getCostBaseDTO();
        LocalDateTime rentTime = costBaseDTO.getStartTime();
        LocalDateTime revertTime = costBaseDTO.getEndTime();
        Integer cityCode = getReturnCarOverCostReqDto.getCityCode();

        // 初始化数据
        GetReturnOverTransportDTO getReturnOverTransport = new GetReturnOverTransportDTO(false, 0, false, 0);
        getReturnOverTransport.setIsUpdateRentTime(true);
        getReturnOverTransport.setIsUpdateRevertTime(true);
        try {
            // 超运能后计算附加费的订单类型列表1-短租订单和3-平台套餐订单
            String rentTimeLongStr = String.valueOf(LocalDateTimeUtils.localDateTimeToLong(rentTime));

            if (rentTime != null) {
                ResponseObject<Boolean> getFlgResponse;
                Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "取车是否超运能");
                try {
                    Cat.logEvent(CatConstants.FEIGN_METHOD, "GetBackCityLimitFeignApi.isCityServiceLimit");
                    Long rentTimeLong = Long.valueOf(rentTimeLongStr.substring(0, 12));
                    Cat.logEvent(CatConstants.FEIGN_PARAM, "cityCode=" + cityCode + "&rentTimeLong=" + rentTimeLong);
                    getFlgResponse = getBackCityLimitFeignApi.isCityServiceLimit(cityCode, rentTimeLong);
                    Cat.logEvent(CatConstants.FEIGN_RESULT, JSON.toJSONString(getFlgResponse));
                    if (getFlgResponse == null || getFlgResponse.getResCode() == null || !ErrorCode.SUCCESS.getCode().equals(getFlgResponse.getResCode())) {
                        log.error("取车超运能获取失败");
                        throw new OrderTransPortException(TransPortErrorCode.TRANS_PORT_ERROR.getValue(), "取车超运能获取失败");
                    }
                    t.setStatus(Transaction.SUCCESS);
                } catch (OrderTransPortException oe) {
                    Cat.logError("Feign 取车超运能获取失败", oe);
                    t.setStatus(oe);
                    throw oe;
                } catch (Exception e) {
                    OrderTransPortException getCarOverCostErrorException = new OrderTransPortException(TransPortErrorCode.TRANS_PORT_ERROR);
                    log.error("Feign 取车超运能接口异常", getCarOverCostErrorException);
                    Cat.logError("Feign 取车超运能接口异常", getCarOverCostErrorException);
                    t.setStatus(getCarOverCostErrorException);
                    throw getCarOverCostErrorException;
                } finally {
                    t.complete();
                }
                boolean getFlag = getFlgResponse.getData();
                if (getFlag) {
                    // 标记取车时间超出运能
                    getReturnOverTransport.setIsGetOverTransport(true);
                } else {
                    getReturnOverTransport.setGetOverTransportFee(0);
                    getReturnOverTransport.setIsGetOverTransport(false);
                }
            }
            if (revertTime != null) {
                String revertTimeLongStr = String.valueOf(LocalDateTimeUtils.localDateTimeToLong(revertTime));
                ResponseObject<Boolean> returnFlgResponse = null;
                Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "还车是否超运能");
                try {
                    Cat.logEvent(CatConstants.FEIGN_METHOD, "GetBackCityLimitFeignApi.isCityServiceLimit");
                    long revertTimeLong = Long.valueOf(revertTimeLongStr.substring(0, 12));
                    Cat.logEvent(CatConstants.FEIGN_PARAM, "cityCode=" + cityCode + "&revertTimeLong=" + revertTimeLong);
                    returnFlgResponse = getBackCityLimitFeignApi.isCityServiceLimit(cityCode, revertTimeLong);
                    Cat.logEvent(CatConstants.FEIGN_RESULT, JSON.toJSONString(returnFlgResponse));
                    if (returnFlgResponse == null || returnFlgResponse.getResCode() == null || !ErrorCode.SUCCESS.getCode().equals(returnFlgResponse.getResCode())) {
                        throw new OrderTransPortException(TransPortErrorCode.TRANS_PORT_ERROR);
                    }
                    t.setStatus(Transaction.SUCCESS);
                } catch (OrderTransPortException oe) {
                    Cat.logError("还车是否超运能获取失败", oe);
                    t.setStatus(oe);
                    throw oe;
                } catch (Exception e) {
                    OrderTransPortException returnCarOverCostErrorException = new OrderTransPortException(TransPortErrorCode.TRANS_PORT_ERROR);
                    Cat.logError("还车是否超运能接口异常", returnCarOverCostErrorException);
                    t.setStatus(returnCarOverCostErrorException);
                    throw returnCarOverCostErrorException;
                } finally {
                    t.complete();
                }

                Boolean returnFlag = returnFlgResponse.getData();
                if (returnFlag) {
                    // 取还车超出运能附加金额
                    getReturnOverTransport.setIsReturnOverTransport(true);
                } else {
                    getReturnOverTransport.setIsReturnOverTransport(false);
                    getReturnOverTransport.setReturnOverTransportFee(0);
                }
            }

        } catch (Exception e) {
            log.error("获取取还车超运能信息出错：", e);
        }
        getReturnOverCostDTO.setGetReturnOverTransportDTO(getReturnOverTransport);
        return getReturnOverCostDTO;
    }
}
