package com.atzuche.order.coreapi.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.commons.LocalDateTimeUtils;
import com.atzuche.order.commons.vo.req.OrderReqVO;
import com.atzuche.order.coreapi.entity.vo.req.CarDispatchReqVO;
import com.atzuche.order.coreapi.entity.vo.req.CarRentTimeRangeReqVO;
import com.atzuche.order.coreapi.entity.vo.res.CarRentTimeRangeResVO;
import com.atzuche.order.parentorder.entity.OrderEntity;
import com.atzuche.order.parentorder.entity.OrderStatusEntity;
import com.atzuche.order.renterorder.entity.OrderCouponEntity;
import com.atzuche.order.transport.service.ResponseObjectCheckUtil;
import com.autoyol.car.api.CarRentalTimeApi;
import com.autoyol.car.api.model.dto.CarAddressDTO;
import com.autoyol.car.api.model.dto.CarDispatchDTO;
import com.autoyol.car.api.model.dto.LocationDTO;
import com.autoyol.car.api.model.vo.ResponseObject;
import com.autoyol.commons.web.ErrorCode;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.stereotype.Service;

/**
 * 车辆库存、调度、提前延后时间等
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
            ResponseObjectCheckUtil.checkResponse(responseObject);


            CarAddressDTO carAddress = responseObject.getData();
            CarRentTimeRangeResVO carRentTimeRangeResVO = new CarRentTimeRangeResVO();
            carRentTimeRangeResVO.setGetMinutes(carAddress.getGetMinutes());
            carRentTimeRangeResVO.setReturnMinutes(carAddress.getReturnMinutes());
            carRentTimeRangeResVO.setAdvanceStartDate(LocalDateTimeUtils.dateToLocalDateTime(carAddress.getAdvanceStartDate()));
            carRentTimeRangeResVO.setDelayEndDate(LocalDateTimeUtils.dateToLocalDateTime(carAddress.getDelayEndDate()));
            t.setStatus(Transaction.SUCCESS);
            return carRentTimeRangeResVO;

        } catch (Exception e) {
            t.setStatus(e);
            LOGGER.info("提前延后时间计算异常:reqVO is [{}]",reqVO, e);
            Cat.logError("提前延后时间计算异常.", e);
            throw e;
        } finally {
            t.complete();
        }
    }


    /**
     * 判断订单是否可以调度
     *
     * @param reqVO 请求参数
     * @return boolean true:可以 false:不可以
     */
    public boolean checkCarDispatch(CarDispatchReqVO reqVO) {
        LOGGER.info("判断是否进入调度. param is,reqVO:[{}]", JSON.toJSONString(reqVO));
        if(null == reqVO) {
            LOGGER.warn("Check car dispatch param is empty.");
            return false;
        }

        CarDispatchDTO dispatch = new CarDispatchDTO();
        BeanCopier beanCopier = BeanCopier.create(CarDispatchReqVO.class, CarDispatchDTO.class, false);
        beanCopier.copy(reqVO, dispatch, null);

        dispatch.setOrderDate(LocalDateTimeUtils.localDateTimeToDate(reqVO.getReqTime()));
        dispatch.setStartDate(LocalDateTimeUtils.localDateTimeToDate(reqVO.getRentTime()));
        dispatch.setEndDate(LocalDateTimeUtils.localDateTimeToDate(reqVO.getRevertTime()));

        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "车辆服务");
        try {
            Cat.logEvent(CatConstants.FEIGN_METHOD, "carRentalTimeApi.checkCarDispatch");
            Cat.logEvent(CatConstants.FEIGN_PARAM, "reqVO=" + JSON.toJSONString(dispatch));

            ResponseObject<Boolean> responseObject = carRentalTimeApi.checkCarDispatch(dispatch);

            LOGGER.info("判断是否进入调度. result is,responseObject:[{}]", JSON.toJSONString(responseObject));
            Cat.logEvent(CatConstants.FEIGN_RESULT, JSON.toJSONString(responseObject));

            ResponseObjectCheckUtil.checkResponse(responseObject);

            t.setStatus(Transaction.SUCCESS);

            if(responseObject.getData()!=null){
                return responseObject.getData();
            }
            return false;

        } catch (Exception e) {
            t.setStatus(e);
            LOGGER.info("判断是否进入调度异常.,reqVO=[{}]",reqVO, e);
            Cat.logError("判断是否进入调度异常.", e);
            throw e;
        } finally {
            t.complete();
        }
    }


    /**
     * 判定调度方法参数处理
     *
     * @param orderEntity       主订单信息
     * @param orderStatusEntity 订单状态信息
     * @param orderCouponEntity 订单车主券信息
     * @return CarDispatchReqVO 判定调度信息
     */
    public CarDispatchReqVO buildCarDispatchReqVO(OrderEntity orderEntity, OrderStatusEntity orderStatusEntity,
                                                  OrderCouponEntity orderCouponEntity,Integer type) {
        if(null == type) {
            return null;
        }
        CarDispatchReqVO carDispatchReqVO = new CarDispatchReqVO();
        carDispatchReqVO.setType(type);
        carDispatchReqVO.setCityCode(Integer.valueOf(orderEntity.getCityCode()));
        carDispatchReqVO.setReqTime(orderEntity.getReqTime());
        carDispatchReqVO.setRentTime(orderEntity.getExpRentTime());
        carDispatchReqVO.setRevertTime(orderEntity.getExpRevertTime());
        carDispatchReqVO.setPayFlag(orderStatusEntity.getRentCarPayStatus());
        carDispatchReqVO.setCouponFlag(null == orderCouponEntity || orderCouponEntity.getStatus() == 0 ? 0 : 1);
        return carDispatchReqVO;
    }


    /**
     * 提前延后时间计算请求参数封装
     *
     * @param orderReqVO 下单请求参数
     * @return CarRentTimeRangeReqVO 提前延后时间计算请求参数
     */
    public CarRentTimeRangeReqVO buildCarRentTimeRangeReqVO(OrderReqVO orderReqVO) {
        CarRentTimeRangeReqVO carRentTimeRangeReqVO = new CarRentTimeRangeReqVO();
        BeanCopier beanCopier = BeanCopier.create(OrderReqVO.class, CarRentTimeRangeReqVO.class, false);
        beanCopier.copy(orderReqVO, carRentTimeRangeReqVO, null);

        LOGGER.info("Submit order before build CarRentTimeRangeReqVO,result is ,carRentTimeRangeReqVO:[{}]",
                JSON.toJSONString(carRentTimeRangeReqVO));
        return carRentTimeRangeReqVO;
    }

}
