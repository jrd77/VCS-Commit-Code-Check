package com.atzuche.order.admin.controller.car;

import com.atzuche.order.admin.exception.OrderAdminException;
import com.atzuche.order.admin.service.car.CarService;
import com.atzuche.order.admin.vo.req.car.CarBaseInfoReqVO;
import com.atzuche.order.admin.vo.req.car.CarBaseReqVO;
import com.atzuche.order.admin.vo.req.car.CarOtherConfigReqVo;
import com.atzuche.order.admin.vo.resp.car.CarBaseInfoResVo;
import com.atzuche.order.admin.vo.resp.car.CarBusinessResVO;
import com.atzuche.order.admin.vo.resp.car.CarOtherConfigResVo;
import com.autoyol.car.api.exception.BaseException;
import com.autoyol.car.api.feign.api.CarDetailQueryFeignApi;
import com.autoyol.car.api.model.vo.CarBaseVO;
import com.autoyol.car.api.model.vo.ResponseObject;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
import com.dianping.cat.Cat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Objects;


@RestController
@AutoDocVersion(version = "车辆信息管理")
public class CarController {

    @Autowired
    private CarDetailQueryFeignApi carDetailQueryFeignApi;

    @Autowired
    private CarService carService;

    private static final Logger LOGGER = LoggerFactory.getLogger(CarController.class);

    @AutoDocMethod(description = "【liujun】获取基础信息", value = "获取车辆基础信息", response = CarBaseInfoResVo.class)
    @GetMapping(value = "/car/baseInfo")
    public ResponseData <?> getCarBaseInfo(@Valid CarBaseReqVO reqVo, BindingResult bindingResult) {
        try {
            ResponseObject <CarBaseVO> responseObject = carDetailQueryFeignApi.getCarDetailByCarNo(reqVo.getCarNo());
            if(Objects.isNull(responseObject)){
                return ResponseData.error();
            }else if(!Objects.equals(responseObject.getResCode(), ErrorCode.SUCCESS.getCode())){
                return new ResponseData <>(responseObject.getResCode(), responseObject.getResMsg());
            }

            CarBaseVO carBaseVO = responseObject.getData();
            if(Objects.isNull(carBaseVO)){
                return ResponseData.success();
            }

            CarBaseInfoResVo carBaseInfoResVo = new CarBaseInfoResVo();
            carBaseInfoResVo.setBrand(convertIntegerToStr(carBaseVO.getBrand()));
            carBaseInfoResVo.setBrandNm(carBaseVO.getBrandTxt());
            carBaseInfoResVo.setCarAge(convertIntegerToStr(carBaseVO.getCarAge()));
            carBaseInfoResVo.setCarLevel(convertIntegerToStr(carBaseVO.getCarLevel()));
//            carBaseInfoResVo.setCarLevelNm(carBaseVO.getCarLevelStr());
            carBaseInfoResVo.setCarType(carBaseVO.getType());
            carBaseInfoResVo.setCarTypeNm(carBaseVO.getTypeTxt());
            carBaseInfoResVo.setCcUnit(carBaseVO.getCcUnit());
            carBaseInfoResVo.setCity(carBaseVO.getCity());
            carBaseInfoResVo.setColor(carBaseVO.getColor());
//            carBaseInfoResVo.setColorNm(carBaseVO.getC);
            carBaseInfoResVo.setCylinderCapacity(carBaseVO.getCc());
            carBaseInfoResVo.setEngineNum(carBaseVO.getEngineNum());
            carBaseInfoResVo.setEngineSource(carBaseVO.getEngineTypeTxt());
//            carBaseInfoResVo.setEngineSourceNm();
            carBaseInfoResVo.setEngineType(convertIntegerToStr(carBaseVO.getEngineType()));
            carBaseInfoResVo.setEngineTypeNm(carBaseVO.getEngineTypeTxt());
            carBaseInfoResVo.setGearboxType(convertIntegerToStr(carBaseVO.getGbType()));
//            carBaseInfoResVo.setGearboxTypeNm();
//            carBaseInfoResVo.setHasUpdateflag();
//            carBaseInfoResVo.setInmsrp();
            carBaseInfoResVo.setIsLocal(carBaseVO.getIsLocal());
//            carBaseInfoResVo.setLastMileage(carBaseVO.getLas);
            carBaseInfoResVo.setLicenseYear(carBaseVO.getYear());
//            carBaseInfoResVo.setLyCarParamSource();
//            carBaseInfoResVo.setLyCarParamSourceNm();
            carBaseInfoResVo.setMileage(convertIntegerToStr(carBaseVO.getMileage()));
//            carBaseInfoResVo.setMileageNm();
            carBaseInfoResVo.setModelTxt(carBaseVO.getModelTxt());
            carBaseInfoResVo.setMonth(carBaseVO.getMonth());
            carBaseInfoResVo.setOilTotalCalibration(carBaseVO.getOilTotalCalibration());
            carBaseInfoResVo.setOilVolume(convertIntegerToStr(carBaseVO.getOilVolume()));
            carBaseInfoResVo.setOwnerType(carBaseVO.getOwnerType());
            carBaseInfoResVo.setPlateNum(carBaseVO.getPlateNum());
            carBaseInfoResVo.setPurchasePrice(convertIntegerToStr(carBaseVO.getGuidePurchasePrice()));
            carBaseInfoResVo.setResidualValue(convertIntegerToStr(carBaseVO.getSurplusPrice()));
//            carBaseInfoResVo.setServiceProportion();
            carBaseInfoResVo.setServiceRate(convertIntegerToStr(carBaseVO.getServiceProportion()));
            carBaseInfoResVo.setServiceProportion(convertIntegerToStr(carBaseVO.getServiceProportion()));
            carBaseInfoResVo.setUseType(convertIntegerToStr(carBaseVO.getUseType()));

//            carBaseInfoResVo.setUseTypeNm();
            carBaseInfoResVo.setVin(carBaseVO.getFrameNo());
            carBaseInfoResVo.setYear(carBaseVO.getYear());


            carBaseVO.setCarDesc(carBaseVO.getCarDesc());


        } catch (BaseException e) {
            LOGGER.error("获取车辆基本信息异常[{}]", reqVo, e);
            Cat.logError("获取车辆基本信息异常[{" + reqVo + "}]", e);
            return new ResponseData <>(e.getCode(), e.getMsg());
        } catch (Exception e) {
            LOGGER.error("获取车辆基本信息异常[{}]", reqVo, e);
            Cat.logError("获取车辆基本信息异常[{" + reqVo + "}]", e);
        }
        return ResponseData.error();
    }

    @AutoDocMethod(description = "【liujun】车辆信息保存", value = "保存车辆基础信息")
    @PostMapping(value = "/car/saveBaseInfo")
    public ResponseData <?> saveBaseInfo(@Valid @RequestBody CarBaseInfoReqVO reqVo, BindingResult bindingResult) {
        return null;
    }


    @AutoDocMethod(description = "【liujun】获取车辆其他配置", value = "获取车辆其他配置", response = CarOtherConfigResVo.class)
    @GetMapping(value = "/car/otherConfig")
    public ResponseData <?> getCarOtherConfig(@Valid CarBaseReqVO reqVo, BindingResult bindingResult) {
        return null;
    }

    @AutoDocMethod(description = "【liujun】保存车辆其他配置", value = "保存车辆其他配置")
    @PostMapping(value = "/car/saveOtherConfig")
    public ResponseData <?> saveOtherConfig(@Valid @RequestBody CarOtherConfigReqVo reqVo, BindingResult bindingResult) {

        return ResponseData.success(null);
    }

    /**
     * 老后台参考:
     * com.autoyolConsole.controller.TransController.detail(String, String, HttpServletRequest)
     * /autoyolConsole/src/main/webapp/WEB-INF/view/trans/detail.jsp
     */
    @AutoDocMethod(description = "【liujun】订单详细信息-查看车辆信息-车辆运营信息", value = "【liujun】订单详细信息-查看车辆信息-车辆运营信息", response = CarBusinessResVO.class)
    @PostMapping(value = "/car/bussiness")
    public ResponseData <?> getCarBusiness(@Valid @RequestBody CarBaseReqVO reqVo, BindingResult bindingResult) {
        try {
            CarBusinessResVO carBusiness = carService.getCarBusiness(reqVo.getCarNo());
            return ResponseData.success(carBusiness);
        } catch (OrderAdminException e) {
            LOGGER.error("获取车辆运营信息异常[{}]", reqVo, e);
            Cat.logError("获取车辆运营信息异常[{" + reqVo + "}]", e);
            return new ResponseData <>(e.getErrorCode(), e.getErrorMsg());
        } catch (Exception e) {
            LOGGER.error("获取车辆运营信息异常[{}]", reqVo, e);
            Cat.logError("获取车辆运营信息异常[{" + reqVo + "}]", e);
            return ResponseData.error();
        }
    }

    private String convertIntegerToStr(Integer value){
        return Objects.isNull(value)?null:value.toString();
    }

}
