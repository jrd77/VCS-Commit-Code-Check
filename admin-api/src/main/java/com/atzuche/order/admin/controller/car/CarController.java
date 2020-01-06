package com.atzuche.order.admin.controller.car;

import com.atzuche.order.admin.vo.resp.car.CarBaseInfoResVo;
import com.atzuche.order.admin.vo.req.car.CarBaseReqVO;
import com.atzuche.order.admin.vo.req.car.CarOtherConfigReqVo;
import com.atzuche.order.admin.vo.req.car.CarBaseInfoReqVO;
import com.atzuche.order.admin.vo.resp.car.CarOtherConfigResVo;
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
    @GetMapping(value = "/car/baseInfo")
    public ResponseData <?> getCarBaseInfo(@Valid CarBaseReqVO reqVo, BindingResult bindingResult) {

        return null;
    }

    @AutoDocMethod(description = "车辆信息保存", value = "保存车辆基础信息")
    @PostMapping(value = "/car/saveBaseInfo")
    public ResponseData <?> saveBaseInfo(@Valid @RequestBody CarBaseInfoReqVO reqVo, BindingResult bindingResult) {
        return null;
    }

    @AutoDocVersion(version = "【AUT-3785】油箱容量")
    @AutoDocMethod(description = "获取车辆其他配置", value = "获取车辆其他配置", response = CarOtherConfigResVo.class)
    @GetMapping(value = "/car/otherConfig")
    public ResponseData<?> getCarOtherConfig(@Valid CarBaseReqVO reqVo, BindingResult bindingResult) {
       return null;
    }

    @AutoDocMethod(description = "保存车辆其他配置", value = "保存车辆其他配置")
    @PostMapping(value = "/car/saveOtherConfig")
    public ResponseData<?> saveOtherConfig(@Valid @RequestBody CarOtherConfigReqVo reqVo, BindingResult bindingResult) {

        return ResponseData.success(null);
    }
}
