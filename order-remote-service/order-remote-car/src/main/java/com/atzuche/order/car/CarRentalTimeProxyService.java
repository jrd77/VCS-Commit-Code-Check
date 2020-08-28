package com.atzuche.order.car;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.commons.LocalDateTimeUtils;
import com.atzuche.order.commons.entity.dto.CarRentTimeRangeDTO;
import com.atzuche.order.commons.exceptions.RemoteCallException;
import com.autoyol.car.api.CarRentalTimeApi;
import com.autoyol.car.api.model.dto.CarAddressDTO;
import com.autoyol.car.api.model.dto.LocationDTO;
import com.autoyol.car.api.model.vo.ResponseObject;
import com.autoyol.commons.web.ErrorCode;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 提前延后时间
 *
 * @author pengcheng.fu
 * @date 2020/8/10 17:27
 */

@Service
public class CarRentalTimeProxyService {

    private final static Logger LOGGER = LoggerFactory.getLogger(CarRentalTimeProxyService.class);


    @Autowired
    private CarRentalTimeApi carRentalTimeApi;


    /**
     * 提前延后时间计算
     *
     * @param reqVO 请求参数
     * @return CarRentTimeRangeDTO 返回结果
     */
    public CarRentTimeRangeDTO getCarRentTimeRange(CarRentTimeRangeReqDTO reqVO) {
        LOGGER.info("提前延后时间计算 A. param is,reqVO:[{}]", JSON.toJSONString(reqVO));

        CarAddressDTO carAddressDTO = new CarAddressDTO();
        carAddressDTO.setCarNo(Integer.valueOf(reqVO.getCarNo()));
        carAddressDTO.setCityCode(Integer.valueOf(reqVO.getCityCode()));
        carAddressDTO.setStartDate(LocalDateTimeUtils.localDateTimeToDate(reqVO.getRentTime()));
        carAddressDTO.setEndDate(LocalDateTimeUtils.localDateTimeToDate(reqVO.getRevertTime()));

        LocationDTO getCarAddress = new LocationDTO();
        getCarAddress.setFlag(reqVO.getSrvGetFlag());
        getCarAddress.setCarAddress(reqVO.getSrvGetAddr());
        getCarAddress.setLat(StringUtils.isBlank(reqVO.getSrvGetLat()) ? null : Double.valueOf(reqVO.getSrvGetLat()));
        getCarAddress.setLon(StringUtils.isBlank(reqVO.getSrvGetLon()) ? null : Double.valueOf(reqVO.getSrvGetLon()));
        LocationDTO returnCarAddress = new LocationDTO();
        returnCarAddress.setFlag(reqVO.getSrvReturnFlag());
        returnCarAddress.setCarAddress(reqVO.getSrvReturnAddr());
        returnCarAddress.setLat(StringUtils.isBlank(reqVO.getSrvReturnLat()) ? null : Double.valueOf(reqVO.getSrvReturnLat()));
        returnCarAddress.setLon(StringUtils.isBlank(reqVO.getSrvReturnLon()) ? null : Double.valueOf(reqVO.getSrvReturnLon()));

        carAddressDTO.setGetCarAddress(getCarAddress);
        carAddressDTO.setReturnCarAddress(returnCarAddress);
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "车辆服务");
        try {
            Cat.logEvent(CatConstants.FEIGN_METHOD, "carRentalTimeApi.getCarRentTimeRange");
            Cat.logEvent(CatConstants.FEIGN_PARAM, "reqVO=" + JSON.toJSONString(reqVO));
            ResponseObject<CarAddressDTO> responseObject = carRentalTimeApi.getCarRentTimeRange(carAddressDTO);
            Cat.logEvent(CatConstants.FEIGN_RESULT, JSON.toJSONString(responseObject));
            LOGGER.info("提前延后时间计算 A. result is,responseObject:[{}]", JSON.toJSONString(responseObject));
            if (responseObject == null || !ErrorCode.SUCCESS.getCode().equalsIgnoreCase(responseObject.getResCode())) {
                RemoteCallException remoteCallException = null;
                if (responseObject != null) {
                    remoteCallException = new RemoteCallException(responseObject.getResCode(), responseObject.getResMsg(), responseObject.getData());
                } else {
                    remoteCallException = new RemoteCallException(com.atzuche.order.commons.enums.ErrorCode.REMOTE_CALL_FAIL.getCode(),
                            com.atzuche.order.commons.enums.ErrorCode.REMOTE_CALL_FAIL.getText());
                }
                throw remoteCallException;
            }


            CarAddressDTO carAddress = responseObject.getData();
            CarRentTimeRangeDTO carRentTimeRangeResVO = new CarRentTimeRangeDTO();
            carRentTimeRangeResVO.setGetMinutes(carAddress.getGetMinutes());
            carRentTimeRangeResVO.setReturnMinutes(carAddress.getReturnMinutes());
            carRentTimeRangeResVO.setAdvanceStartDate(LocalDateTimeUtils.dateToLocalDateTime(carAddress.getAdvanceStartDate()));
            carRentTimeRangeResVO.setDelayEndDate(LocalDateTimeUtils.dateToLocalDateTime(carAddress.getDelayEndDate()));
            t.setStatus(Transaction.SUCCESS);
            LOGGER.info("提前延后时间计算A. result is,carRentTimeRangeResVO:[{}]", JSON.toJSONString(carRentTimeRangeResVO));
            return carRentTimeRangeResVO;

        } catch (Exception e) {
            t.setStatus(e);
            LOGGER.info("提前延后时间计算异常A:reqVO is [{}]", reqVO, e);
            Cat.logError("提前延后时间计算异常A.", e);
            throw e;
        } finally {
            t.complete();
        }
    }

}
