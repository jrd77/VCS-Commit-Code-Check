package com.atzuche.order.admin.service.car;

import com.atzuche.order.admin.exception.OrderAdminException;
import com.atzuche.order.admin.vo.resp.car.CarBusinessResVO;
import com.atzuche.order.commons.DateUtils;
import com.atzuche.order.commons.JsonUtil;
import com.autoyol.car.api.feign.api.CarDetailQueryFeignApi;
import com.autoyol.car.api.model.vo.CarBaseVO;
import com.autoyol.car.api.model.vo.ResponseObject;
import com.autoyol.commons.web.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

@Service
public class CarService {

    private static final Logger logger = LoggerFactory.getLogger(CarService.class);
    @Autowired
    private CarDetailQueryFeignApi carDetailQueryFeignApi;

    public CarBusinessResVO getCarBusiness(Integer carNo){
        CarBusinessResVO resVO = new CarBusinessResVO();
        ResponseObject<CarBaseVO> responseObject = carDetailQueryFeignApi.getCarDetailByCarNo(carNo);
        if(Objects.isNull(responseObject)){
            logger.error("carDetailQueryFeignApi.getCarDetailByCarNo,responseObject:{}", JsonUtil.toJson(responseObject));
            throw new OrderAdminException("999999","系统错误");
        }
        if(!ErrorCode.SUCCESS.getCode().equals(responseObject.getResCode())){
            throw new OrderAdminException(responseObject.getResCode(),responseObject.getResMsg());
        }
        CarBaseVO carBaseVO = responseObject.getData();
        if(Objects.isNull(carBaseVO)){
            logger.error("carDetailQueryFeignApi.getCarDetailByCarNo,carBaseVO is null");
            throw new OrderAdminException("999999","系统错误");
        }
        resVO.setGetCarAddr(carBaseVO.getGetCarAddr());
        resVO.setLicenseOwer(carBaseVO.getLicenseOwer());
        resVO.setLicenseModel(carBaseVO.getLicenseModel());
        Date licenseExpire = carBaseVO.getLicenseExpire();
        if(licenseExpire!=null){
            resVO.setLicenseExpire(DateUtils.formate(licenseExpire, DateUtils.fmt_yyyyMMdd));
        }
        Date insuranceExpire = carBaseVO.getInsuranceExpire();
        if(insuranceExpire!=null){
            resVO.setInsuranceExpireDateStr(DateUtils.formate(insuranceExpire,DateUtils.fmt_yyyyMMdd));
        }
        resVO.setGps(carBaseVO.getGpsNo());
        resVO.setSimNo(carBaseVO.getSimNo());
        resVO.setMemo(carBaseVO.getMemo());
        //carSelectMap car_select_upgrade
        resVO.setCarRemark(carBaseVO.getCarDesc());
        resVO.setGetRevertExplain(carBaseVO.getGetRevertExplain());
        resVO.setDayMileage(carBaseVO.getDayMileage());
        //
        return resVO;
    }

}
