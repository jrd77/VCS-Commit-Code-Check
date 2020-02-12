package com.atzuche.order.coreapi.controller;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.BindingResultUtil;
import com.atzuche.order.commons.enums.DispatcherReasonEnum;
import com.atzuche.order.commons.vo.req.AgreeOrderReqVO;
import com.atzuche.order.commons.vo.req.GetCarReqVO;
import com.atzuche.order.commons.vo.req.RefuseOrderReqVO;
import com.atzuche.order.commons.vo.req.ReturnCarReqVO;
import com.atzuche.order.coreapi.service.OwnerAgreeOrderService;
import com.atzuche.order.coreapi.service.OwnerRefuseOrderService;
import com.atzuche.order.coreapi.service.OwnerReturnCarService;
import com.atzuche.order.coreapi.service.RenterGetCarService;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


/**
 * 拒单
 *
 * @author pengcheng.fu
 * @date 2020/1/9 15:58
 */
@RequestMapping("/order")
@RestController
@AutoDocVersion(version = "订单接口文档")
public class RefuseOrderController {

    @Autowired
    OwnerAgreeOrderService ownerAgreeOrderService;

    @Autowired
    OwnerRefuseOrderService ownerRefuseOrderService;

    @Autowired
    OwnerReturnCarService ownerReturnCarService;

    @Autowired
    RenterGetCarService renterGetCarService;

    private static final Logger LOGGER = LoggerFactory.getLogger(RefuseOrderController.class);


    @AutoDocMethod(description = "车主拒绝订单", value = "车主拒绝订单")
    @PostMapping("/normal/refuse")
    public ResponseData<?> refuseOrder(@Valid @RequestBody RefuseOrderReqVO reqVO, BindingResult bindingResult) {

        LOGGER.info("Refuse order.param is,reqVO:[{}]", JSON.toJSONString(reqVO));
        BindingResultUtil.checkBindingResult(bindingResult);
        ownerRefuseOrderService.refuse(reqVO, DispatcherReasonEnum.owner_refuse);
        return ResponseData.success();
    }


    @AutoDocMethod(description = "车主同意订单", value = "车主同意订单")
    @PostMapping("/normal/agree")
    public ResponseData<?> agreeOrder(@Valid @RequestBody AgreeOrderReqVO reqVO, BindingResult bindingResult) {

        LOGGER.info("Agree order.param is,reqVO:[{}]", JSON.toJSONString(reqVO));
        BindingResultUtil.checkBindingResult(bindingResult);
        ownerAgreeOrderService.agree(reqVO);
        return ResponseData.success();
    }


    @AutoDocMethod(description = "车主交车", value = "车主交车(已还车)")
    @PostMapping("/normal/returnCar")
    public ResponseData<?> returnCar(@Valid @RequestBody ReturnCarReqVO reqVO, BindingResult bindingResult) {

        LOGGER.info("Owner return car.param is,reqVO:[{}]", JSON.toJSONString(reqVO));
        BindingResultUtil.checkBindingResult(bindingResult);
        ownerReturnCarService.returnCar(reqVO);
        return ResponseData.success();
    }


    @AutoDocMethod(description = "租客取车", value = "租客取车")
    @PostMapping("/normal/getCar")
    public ResponseData<?> getCar(@Valid @RequestBody GetCarReqVO reqVO, BindingResult bindingResult) {
        LOGGER.info("Renter get car.param is,reqVO:[{}]", JSON.toJSONString(reqVO));
        BindingResultUtil.checkBindingResult(bindingResult);
        renterGetCarService.pickUpCar(reqVO);
        return ResponseData.success();
    }

}
