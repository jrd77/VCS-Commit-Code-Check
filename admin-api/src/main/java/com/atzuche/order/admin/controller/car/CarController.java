package com.atzuche.order.admin.controller.car;


import com.atzuche.order.admin.service.RemoteFeignService;
import com.atzuche.order.car.CarDetailDTO;
import com.atzuche.order.car.CarProxyService;
import com.atzuche.order.commons.LocalDateTimeUtils;
import com.atzuche.order.commons.entity.dto.OwnerGoodsDetailDTO;
import com.atzuche.order.commons.enums.CarUseTypeEnum;
import com.atzuche.order.commons.vo.OrderStopFreightInfo;
import com.atzuche.order.open.vo.RenterGoodWithoutPriceVO;
import com.autoyol.car.api.model.enums.OwnerTypeEnum;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;


@RestController
@AutoDocVersion(version = "车辆信息管理")
public class CarController {

    private  Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private CarProxyService carProxyService;

    @Autowired
    private RemoteFeignService remoteFeignService;


    @AutoDocMethod(description = "订单详细信息-查看车辆信息", value = "订单详细信息-查看车辆信息", response = CarDetailDTO.class)
    @GetMapping(value = "console/car/detail")
    public ResponseData <CarDetailDTO> getCarBusiness(@RequestParam("carNo")String carNo,@RequestParam("orderNo")String orderNo,@RequestParam("ownerOrderNo")String ownerOrderNo) {
        CarDetailDTO carBusiness = carProxyService.getCarDetail(carNo);
        OwnerGoodsDetailDTO ownerGoodsDetailDTO = remoteFeignService.queryOwnerGoods(false, ownerOrderNo);
        BeanUtils.copyProperties(ownerGoodsDetailDTO,carBusiness);
        carBusiness.setGps(ownerGoodsDetailDTO.getGpsSerialNumber());
        carBusiness.setDayMileage(ownerGoodsDetailDTO.getCarDayMileage());
        carBusiness.setEngineNum(ownerGoodsDetailDTO.getEngineNum());
        if(Objects.nonNull(ownerGoodsDetailDTO) && Objects.nonNull(ownerGoodsDetailDTO.getCarUseType())){
            carBusiness.setUseType(ownerGoodsDetailDTO.getCarUseType());
            carBusiness.setUseTypeTxt(CarUseTypeEnum.getNameByCode(ownerGoodsDetailDTO.getCarUseType()));
        }
        if(Objects.nonNull(ownerGoodsDetailDTO) && Objects.nonNull(ownerGoodsDetailDTO.getCarOwnerType())){
            carBusiness.setOwnerType(ownerGoodsDetailDTO.getCarOwnerType());
            carBusiness.setOwnerTypeTxt(OwnerTypeEnum.getRemark(ownerGoodsDetailDTO.getCarOwnerType()));
        }
        if(Objects.nonNull(ownerGoodsDetailDTO) && Objects.nonNull(ownerGoodsDetailDTO.getLicenseExpire())){
            String date = LocalDateTimeUtils.formatDateTime(ownerGoodsDetailDTO.getLicenseExpire());
            carBusiness.setLicenseExpire(date);
        }
        if(Objects.nonNull(ownerGoodsDetailDTO) && Objects.nonNull(ownerGoodsDetailDTO.getLicenseExpire())){
            String date = LocalDateTimeUtils.formatDateTime(ownerGoodsDetailDTO.getLicenseExpire());
            carBusiness.setLicenseExpire(date);
        }
        if(Objects.nonNull(ownerGoodsDetailDTO) && Objects.nonNull(ownerGoodsDetailDTO.getCarUseType())){
            carBusiness.setUseType(ownerGoodsDetailDTO.getCarUseType());
        }
        if(Objects.nonNull(ownerGoodsDetailDTO) && Objects.nonNull(ownerGoodsDetailDTO.getCarOwnerType())){
            carBusiness.setOwnerType(ownerGoodsDetailDTO.getCarOwnerType());
        }
        if(Objects.nonNull(ownerGoodsDetailDTO) && Objects.nonNull(ownerGoodsDetailDTO.getCarRating())){
            carBusiness.setRating(ownerGoodsDetailDTO.getCarRating());
        }
        if(Objects.nonNull(ownerGoodsDetailDTO) && Objects.nonNull(ownerGoodsDetailDTO.getCarDesc())){
            carBusiness.setCarDesc(ownerGoodsDetailDTO.getCarDesc());
        }
        if(Objects.nonNull(ownerGoodsDetailDTO) && Objects.nonNull(ownerGoodsDetailDTO.getCarRealAddr())){
            carBusiness.setGetCarAddr(ownerGoodsDetailDTO.getCarRealAddr());
        }
        if(Objects.nonNull(ownerGoodsDetailDTO) && Objects.nonNull(ownerGoodsDetailDTO.getFrameNo())){
            carBusiness.setFrameNo(ownerGoodsDetailDTO.getFrameNo());
        }
        if(Objects.nonNull(ownerGoodsDetailDTO) && Objects.nonNull(ownerGoodsDetailDTO.getCarSurplusPrice())){
            carBusiness.setSurplusPrice(ownerGoodsDetailDTO.getCarSurplusPrice());
        }
        if(Objects.nonNull(ownerGoodsDetailDTO) && Objects.nonNull(ownerGoodsDetailDTO.getCarGuidePrice())){
            carBusiness.setGuidePrice(ownerGoodsDetailDTO.getCarGuidePrice());
        }
        if(Objects.nonNull(ownerGoodsDetailDTO) && Objects.nonNull(ownerGoodsDetailDTO.getCarInmsrp())){
            carBusiness.setCarInmsrp(ownerGoodsDetailDTO.getCarInmsrp());
        }
        if(Objects.nonNull(ownerGoodsDetailDTO) && Objects.nonNull(ownerGoodsDetailDTO.getModelTxt())){
            carBusiness.setModelTxt(ownerGoodsDetailDTO.getModelTxt());
        }
        if(Objects.nonNull(ownerGoodsDetailDTO) && Objects.nonNull(ownerGoodsDetailDTO.getYear())){
            carBusiness.setYear(Integer.valueOf(ownerGoodsDetailDTO.getYear()));
        }
        if(Objects.nonNull(ownerGoodsDetailDTO) && Objects.nonNull(ownerGoodsDetailDTO.getCarType())){
            carBusiness.setType(String.valueOf(ownerGoodsDetailDTO.getCarType()));
        }
        if(Objects.nonNull(ownerGoodsDetailDTO) && Objects.nonNull(ownerGoodsDetailDTO.getCarTypeTxt())){
            carBusiness.setTypeTxt(ownerGoodsDetailDTO.getCarTypeTxt());
        }
        if(Objects.nonNull(ownerGoodsDetailDTO) && Objects.nonNull(ownerGoodsDetailDTO.getCarBrandTxt())){
            carBusiness.setBrandTxt(ownerGoodsDetailDTO.getCarBrandTxt());
        }
        if(Objects.nonNull(ownerGoodsDetailDTO) && Objects.nonNull(ownerGoodsDetailDTO.getCarRealLat())){
            carBusiness.setCityLat(Double.valueOf(ownerGoodsDetailDTO.getCarRealLat()));
        }
        if(Objects.nonNull(ownerGoodsDetailDTO) && Objects.nonNull(ownerGoodsDetailDTO.getCarRealLon())){
            carBusiness.setCityLon(Double.valueOf(ownerGoodsDetailDTO.getCarRealLon()));
        }
        if(Objects.nonNull(ownerGoodsDetailDTO) && Objects.nonNull(ownerGoodsDetailDTO.getCarPlateNum())){
            carBusiness.setPlateNum(ownerGoodsDetailDTO.getCarPlateNum());
        }
        // 获取车辆停运费信息
        OrderStopFreightInfo orderStopFreightInfo = remoteFeignService.getStopFreightInfo(orderNo);
        if (orderStopFreightInfo != null) {
        	carBusiness.setAgreementStopFreightPrice(orderStopFreightInfo.getAgreementStopFreightPrice());
        	carBusiness.setAgreementStopFreightRate(orderStopFreightInfo.getAgreementStopFreightRate()+"%");
        	carBusiness.setNotagreementStopFreightPrice(orderStopFreightInfo.getNotagreementStopFreightPrice());
        	carBusiness.setNotagreementStopFreightRate(orderStopFreightInfo.getNotagreementStopFreightRate()+"%");
        }
        
        return ResponseData.success(carBusiness);
    }



}
