package com.atzuche.order.admin.controller;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.admin.service.AdminCarOwnerInfoService;
import com.atzuche.order.admin.vo.resp.car.CarOwnerInfoRespVO;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/console/carOwner")
@RestController
@AutoDocVersion(version = "订单详细信息 - 查看车主信息接口文档")
public class AdminCarOwnerInfoController {

    @Autowired
    private AdminCarOwnerInfoService adminCarOwnerInfoService;

    @AutoDocMethod(description = "查看车主信息接口信息", value = "查看车主信息接口信息", response = CarOwnerInfoRespVO.class)
    @GetMapping(value = "/info")
    public ResponseData<?> getCarOwnerInfo(@RequestParam("orderNo")String orderNo,@RequestParam("memNo")String memNo) {
        log.info("AdminCarOwnerInfoController.getCarOwnerInfo() 入参--> order={},memNo={}",orderNo,memNo);
        CarOwnerInfoRespVO carOwnerInfo = adminCarOwnerInfoService.getCarOwnerInfo(orderNo, memNo);
        log.info("AdminCarOwnerInfoController.getCarOwnerInfo() 返回值-->carOwnerInfo={}", JSON.toJSONString(carOwnerInfo));
        return ResponseData.success(carOwnerInfo);
    }

}
