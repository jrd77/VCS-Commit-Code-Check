package com.atzuche.order.coreapi.controller;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.vo.req.NormalOrderCostCalculateReqVO;
import com.atzuche.order.commons.vo.req.NormalOrderReqVO;
import com.atzuche.order.commons.vo.res.NormalOrderCostCalculateResVO;
import com.atzuche.order.commons.vo.res.OrderResVO;
import com.atzuche.order.coreapi.service.SubmitOrderBeforeCostCalService;
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
 *
 *
 * @author pengcheng.fu
 * @date 2020/1/11 14:10
 */

@RequestMapping("/order")
@RestController
@AutoDocVersion(version = "订单接口文档")
public class SubmitOrderBeforeCostCalculateController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubmitOrderBeforeCostCalculateController.class);


    @Autowired
    private SubmitOrderBeforeCostCalService submitOrderBeforeCostCalService;


    @AutoDocMethod(description = "提交订单前费用计算", value = "提交订单前费用计算", response = NormalOrderCostCalculateResVO.class)
    @PostMapping("/normal/pre/cost/calculate")
    public ResponseData<NormalOrderCostCalculateResVO> submitOrderBeforeCostCalculate(@Valid @RequestBody NormalOrderCostCalculateReqVO reqVO,
                                                                                      BindingResult bindingResult) {
        LOGGER.info("Submit order before cost calculate.param is,reqVO:[{}]", JSON.toJSONString(reqVO));
        if (bindingResult.hasErrors()) {
            Optional<FieldError> error = bindingResult.getFieldErrors().stream().findFirst();
            return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), error.isPresent() ?
                    error.get().getDefaultMessage() : ErrorCode.INPUT_ERROR.getText());
        }
        String memNo = reqVO.getMemNo();
        if (StringUtils.isBlank(memNo)) {
            return new ResponseData<>(ErrorCode.NEED_LOGIN.getCode(), ErrorCode.NEED_LOGIN.getText());
        }

        NormalOrderCostCalculateResVO resVO = submitOrderBeforeCostCalService.costCalculate(reqVO);
        LOGGER.info("Submit order before cost calculate.result is,resVO:[{}]", JSON.toJSONString(resVO));
        return ResponseData.success(resVO);
    }



}
