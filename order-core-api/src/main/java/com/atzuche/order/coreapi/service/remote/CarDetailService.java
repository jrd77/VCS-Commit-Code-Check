package com.atzuche.order.coreapi.service.remote;


import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.CatConstants;
import com.autoyol.car.api.exception.BaseException;
import com.autoyol.car.api.feign.api.CarDetailQueryFeignApi;
import com.autoyol.car.api.model.dto.OrderCarInfoParamDTO;
import com.autoyol.car.api.model.vo.CarDetailVO;
import com.autoyol.car.api.model.vo.CarGpsVO;
import com.autoyol.car.api.model.vo.ResponseObject;
import com.autoyol.commons.web.ErrorCode;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CarDetailService {

    private Logger logger = LoggerFactory.getLogger(CarDetailService.class);

    @Resource
    private CarDetailQueryFeignApi carDetailQueryFeignApi;

    private static final String SERIAL_NUMBER = "4";

    public String querySimNoByCar(OrderCarInfoParamDTO dto){
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "车辆服务");
        Cat.logEvent(CatConstants.FEIGN_METHOD, "carDetailQueryFeignApi.getCarDetailOfTransByCarNo");
        Cat.logEvent(CatConstants.FEIGN_PARAM, "reqVO=" + JSON.toJSONString(dto));
        try {
            ResponseObject<CarDetailVO> carDetailOfTransByCarNo = carDetailQueryFeignApi.getCarDetailOfTransByCarNo(dto);
            logger.info("调用车辆服务查询GPS信息入参 [{}],出参 [{}]",JSON.toJSONString(dto),JSON.toJSONString(carDetailOfTransByCarNo));
            if(carDetailOfTransByCarNo != null && carDetailOfTransByCarNo.getResCode().equals(ErrorCode.SUCCESS.getCode())){
                CarDetailVO data = carDetailOfTransByCarNo.getData();
                if(data == null){
                    return "";
                }
                List<CarGpsVO> carGpsVOS = data.getCarGpsVOS();
                if(CollectionUtils.isEmpty(carGpsVOS)){
                    return "";
                }
                List<CarGpsVO> collect = carGpsVOS.stream().filter(Objects::nonNull).filter(temp -> SERIAL_NUMBER.equals(temp.getSerialNumber())).collect(Collectors.toList());
                if(CollectionUtils.isEmpty(collect)){
                    return "";
                }
                return collect.get(0).getSimNo();
            }
        } catch (BaseException e) {
            t.setStatus(e);
            logger.error("查询车辆信息异常:reqVO is [{}]",dto, e);
            Cat.logError("查询车辆信息异常.", e);
        } finally {
            t.complete();
        }
        return "";
    }

}
