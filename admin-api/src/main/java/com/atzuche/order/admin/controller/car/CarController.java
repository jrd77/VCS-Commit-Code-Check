package com.atzuche.order.admin.controller.car;

import com.atzuche.order.admin.vo.req.car.CarBaseInfoResVo;
import com.atzuche.order.admin.vo.req.car.CarBaseReqVo;
import com.atzuche.order.admin.vo.resp.car.CarBaseInfoReqVo;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


@RestController
@AutoDocVersion(version = "车辆信息管理")
public class CarController {

    @AutoDocMethod(description = "获取基础信息", value = "获取车辆基础信息", response = CarBaseInfoResVo.class)
    @GetMapping(value = "/console/car/baseInfo")
    public ResponseData <?> getCarBaseInfo(@Valid CarBaseReqVo reqVo, BindingResult bindingResult) {

        return null;
    }

    @AutoDocMethod(description = "车辆信息保存", value = "保存车辆基础信息")
    @PostMapping(value = "/console/car/saveBaseInfo")
    public ResponseData <?> saveBaseInfo(@Valid @RequestBody CarBaseInfoReqVo reqVo, BindingResult bindingResult) {
        return null;
    }
}
