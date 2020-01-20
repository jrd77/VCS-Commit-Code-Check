package com.atzuche.order.admin.controller.car;


import com.atzuche.order.admin.service.car.CarService;
import com.atzuche.order.admin.vo.req.car.CarBaseReqVO;
import com.atzuche.order.admin.vo.resp.car.CarBusinessResVO;
import com.atzuche.order.car.CarDetailDTO;
import com.atzuche.order.car.CarProxyService;
import com.autoyol.car.api.feign.api.CarDetailQueryFeignApi;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
import com.dianping.cat.Cat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    @AutoDocMethod(description = "订单详细信息-查看车辆信息", value = "订单详细信息-查看车辆信息", response = CarDetailDTO.class)
    @GetMapping(value = "console/car/detail")
    public ResponseData <CarDetailDTO> getCarBusiness(@RequestParam("carNo")String carNo) {
        CarDetailDTO carBusiness = carProxyService.getCarDetail(carNo);
        return ResponseData.success(carBusiness);
    }


}
