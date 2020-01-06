package com.atzuche.order.admin.controller.car;


import com.atzuche.order.admin.vo.req.car.CarOrderImageDetailReqVO;
import com.atzuche.order.admin.vo.resp.car.CarOrderImageDetailRespVO;

import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequestMapping("/console/car/order/image")
@RestController
@AutoDocVersion(version = "订单照片")
public class CarOrderImageController {

    @AutoDocMethod(description = "【liujun】订单照片列表;Type:1为取车照片，2为还车照片，3为违章缴纳凭证，4为车主上传，5为平台上传", value = "订单照片列表", response = CarOrderImageDetailRespVO.class)
    @GetMapping(value = "/list/{orderNum}/{type}")
    public ResponseData <?> getCarOrderImages(@PathVariable String orderNum,@PathVariable Integer type) {

        return null;
    }

    @AutoDocMethod(description = "【liujun】添加订单照片", value = "添加订单照片", response = ResponseData.class)
    @PostMapping(value = "/add/save/{orderNum}")
    public ResponseData <?> addCarOrderImage(@RequestBody CarOrderImageDetailReqVO carOrderImageDetailReqVO, @PathVariable String orderNum) {

        return null;
    }

    @AutoDocMethod(description = "【liujun】修改订单照片；Type:1为取车照片，2为还车照片，3为违章缴纳凭证，4为车主上传，5为平台上传", value = "修改订单照片", response = ResponseData.class)
    @PostMapping(value = "/update/save/{id}")
    public ResponseData <?> updateCarOrderImage(@RequestBody CarOrderImageDetailReqVO carOrderImageDetailReqVO, @PathVariable Long Id) {

        return null;
    }

    @AutoDocMethod(description = "【liujun】删除订单照片", value = "删除订单照片", response = ResponseData.class)
    @PostMapping(value = "/delete/{id}/{orderNum}/{type}")
    public ResponseData <?> deleteCarOrderImage(@RequestBody CarOrderImageDetailReqVO carOrderImageDetailReqVO, @PathVariable Long Id) {

        return null;
    }


}
