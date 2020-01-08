package com.atzuche.order.coreapi.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.commons.LocalDateTimeUtils;
import com.atzuche.order.coreapi.entity.vo.req.CarRentTimeRangeReqVO;
import com.atzuche.order.coreapi.entity.vo.res.CarRentTimeRangeResVO;
import com.atzuche.order.coreapi.submitOrder.exception.RenterCarDetailFailException;
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
 * 车辆库存、调度、提前延后时间等
 *
 *
 * @author pengcheng.fu
 * @date 2020/1/8 11:01
 */

@Service
public class CarRentalTimeApiService {

    private final static Logger LOGGER = LoggerFactory.getLogger(CarRentalTimeApiService.class);


    @Autowired
    private CarRentalTimeApi carRentalTimeApi;



    /**
     * 提前延后时间计算
     *
     * @param reqVO 请求参数
     * @return CarRentTimeRangeResVO 返回结果
     */
    public CarRentTimeRangeResVO getCarRentTimeRange(CarRentTimeRangeReqVO reqVO) {
        LOGGER.info("提前延后时间计算. param is,reqVO:[{}]", JSON.toJSONString(reqVO));

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
            LOGGER.info("提前延后时间计算. result is,responseObject:[{}]", JSON.toJSONString(responseObject));

            if (null == responseObject || null == responseObject.getData() || !StringUtils.equals(responseObject.getResCode(), ErrorCode.SUCCESS.getCode())) {
                t.setStatus(new RenterCarDetailFailException(responseObject.getResCode(),responseObject.getResMsg()));
            } else {
                CarAddressDTO carAddress = responseObject.getData();
                CarRentTimeRangeResVO carRentTimeRangeResVO = new CarRentTimeRangeResVO();
                carRentTimeRangeResVO.setGetMinutes(carAddress.getGetMinutes());
                carRentTimeRangeResVO.setReturnMinutes(carAddress.getReturnMinutes());
                carRentTimeRangeResVO.setAdvanceStartDate(LocalDateTimeUtils.dateToLocalDateTime(carAddress.getAdvanceStartDate()));
                carRentTimeRangeResVO.setDelayEndDate(LocalDateTimeUtils.dateToLocalDateTime(carAddress.getDelayEndDate()));
                t.setStatus(Transaction.SUCCESS);
                return carRentTimeRangeResVO;
            }
        } catch (Exception e) {
            t.setStatus(e);
            LOGGER.info("提前延后时间计算异常.", e);
            Cat.logError("提前延后时间计算异常.", e);
        } finally {
            t.complete();
        }
        return null;
    }


}
