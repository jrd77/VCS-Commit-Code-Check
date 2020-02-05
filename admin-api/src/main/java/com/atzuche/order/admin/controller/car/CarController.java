package com.atzuche.order.admin.controller.car;


import com.alibaba.fastjson.JSON;
import com.atzuche.order.admin.service.car.CarService;
import com.atzuche.order.admin.vo.req.car.CarBaseReqVO;
import com.atzuche.order.admin.vo.resp.car.CarBusinessResVO;
import com.atzuche.order.car.CarDetailDTO;
import com.atzuche.order.car.CarProxyService;
import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.commons.LocalDateTimeUtils;
import com.atzuche.order.commons.ResponseCheckUtil;
import com.atzuche.order.open.service.FeignRenterGoodsService;
import com.atzuche.order.open.vo.RenterGoodWithoutPriceVO;
import com.autoyol.car.api.feign.api.CarDetailQueryFeignApi;
import com.autoyol.car.api.model.vo.CarBaseVO;
import com.autoyol.car.api.model.vo.ResponseObject;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@AutoDocVersion(version = "车辆信息管理")
public class CarController {

    private  Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private CarProxyService carProxyService;

    private FeignRenterGoodsService feignRenterGoodsService;

    @AutoDocMethod(description = "订单详细信息-查看车辆信息", value = "订单详细信息-查看车辆信息", response = CarDetailDTO.class)
    @GetMapping(value = "console/car/detail")
    public ResponseData <CarDetailDTO> getCarBusiness(@RequestParam("carNo")String carNo,@RequestParam("orderNo")String orderNo) {
        CarDetailDTO carBusiness = carProxyService.getCarDetail(carNo);
        RenterGoodWithoutPriceVO renterGoodWithoutPriceVO = queryRenterGoods(orderNo,carNo);
        BeanUtils.copyProperties(renterGoodWithoutPriceVO,carBusiness);
        carBusiness.setGps(renterGoodWithoutPriceVO.getGpsSerialNumber());
        carBusiness.setDayMileage(renterGoodWithoutPriceVO.getCarDayMileage());
        carBusiness.setEngineNum(renterGoodWithoutPriceVO.getEngineNum());
        carBusiness.setLicenseExpire(LocalDateTimeUtils.localDateTimeToDate(renterGoodWithoutPriceVO.getLicenseExpire()));

        return ResponseData.success(carBusiness);
    }

    private RenterGoodWithoutPriceVO queryRenterGoods(String orderNo,String carNo){
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "订单中租客商品信息");
        ResponseData<RenterGoodWithoutPriceVO> responseObject = null;
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"feignRenterGoodsService.getRenterGoodsDetailWithoutPrice");
            logger.info("Feign 开始获取订单中租客商品信息,[orderNo={},carNo={}]", orderNo,JSON.toJSONString(carNo));
            Cat.logEvent(CatConstants.FEIGN_PARAM,JSON.toJSONString(carNo));
            responseObject = feignRenterGoodsService.getRenterGoodsDetailWithoutPrice(orderNo,carNo);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            ResponseCheckUtil.checkResponse(responseObject);
            RenterGoodWithoutPriceVO baseVO = responseObject.getData();
            logger.info("baseVo is {}",baseVO);
            t.setStatus(Transaction.SUCCESS);
            return baseVO;
        }catch (Exception e){
            logger.error("Feign 订单中租客商品信息,responseObject={},orderCarInfoParamDTO={}",JSON.toJSONString(responseObject),JSON.toJSONString(carNo),e);
            Cat.logError("Feign 获取订单中租客商品信息异常",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }
    }


}
