package com.atzuche.order.coreapi.controller;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.vo.req.AgreeOrderReqVO;
import com.atzuche.order.commons.vo.req.RefuseOrderReqVO;
import com.atzuche.order.commons.vo.req.ReturnCarReqVO;
import com.atzuche.order.coreapi.service.OwnerAgreeOrderService;
import com.atzuche.order.coreapi.service.OwnerRefuseOrderService;
import com.atzuche.order.coreapi.service.OwnerReturnCarService;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Optional;


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

    private static final Logger LOGGER = LoggerFactory.getLogger(RefuseOrderController.class);


    @AutoDocMethod(description = "车主拒绝订单", value = "车主拒绝订单")
    @PostMapping("/normal/refuse")
    public ResponseData<?> refuseOrder(@Valid @RequestBody RefuseOrderReqVO reqVO, BindingResult bindingResult) {

        LOGGER.info("Refuse order.param is,reqVO:[{}]", JSON.toJSONString(reqVO));
        if (bindingResult.hasErrors()) {
            Optional<FieldError> error = bindingResult.getFieldErrors().stream().findFirst();
            return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), error.isPresent() ?
                    error.get().getDefaultMessage() : ErrorCode.INPUT_ERROR.getText());
        }
        ownerRefuseOrderService.refuse(reqVO);
        return ResponseData.success();
    }


    @AutoDocMethod(description = "车主同意订单", value = "车主同意订单")
    @PostMapping("/normal/agree")
    public ResponseData<?> agreeOrder(@Valid @RequestBody AgreeOrderReqVO reqVO, BindingResult bindingResult) {

        LOGGER.info("Agree order.param is,reqVO:[{}]", JSON.toJSONString(reqVO));
        if (bindingResult.hasErrors()) {
            Optional<FieldError> error = bindingResult.getFieldErrors().stream().findFirst();
            return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), error.isPresent() ?
                    error.get().getDefaultMessage() : ErrorCode.INPUT_ERROR.getText());
        }
        ownerAgreeOrderService.agree(reqVO);
        return ResponseData.success();
    }


    @AutoDocMethod(description = "车主交车", value = "车主交车")
    @PostMapping("/normal/returnCar")
    public ResponseData<?> returnCar(@Valid @RequestBody ReturnCarReqVO reqVO, BindingResult bindingResult) {

        LOGGER.info("Owner return car.param is,reqVO:[{}]", JSON.toJSONString(reqVO));
        if (bindingResult.hasErrors()) {
            Optional<FieldError> error = bindingResult.getFieldErrors().stream().findFirst();
            return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), error.isPresent() ?
                    error.get().getDefaultMessage() : ErrorCode.INPUT_ERROR.getText());
        }
        ownerReturnCarService.returnCar(reqVO);
        return ResponseData.success();
    }
}
