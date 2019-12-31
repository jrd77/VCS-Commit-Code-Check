package com.atzuche.order.coreapi.controller;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.vo.req.NormalOrderReqVO;
import com.atzuche.order.commons.vo.res.NormalOrderResVO;
import com.atzuche.order.coreapi.service.SubmitOrderService;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 下单
 *
 * @author pengcheng.fu
 * @date 2019/12/23 12:00
 */
@RequestMapping("/order")
@RestController
@AutoDocVersion(version = "普通订单下单接口文档")
public class SubmitOrderController {
    private static final Logger LOGGER = LoggerFactory.getLogger(SubmitOrderController.class);

    @Resource
    private SubmitOrderService submitOrderService;


    @AutoDocMethod(description = "提交订单", value = "提交订单", response = NormalOrderResVO.class)
    @PostMapping("/req")
    public ResponseData<NormalOrderResVO> submitOrder(@RequestBody NormalOrderReqVO normalOrderReqVO, BindingResult bindingResult) {
        LOGGER.info("Submit order.param is,normalOrderReqVO:[{}]", JSON.toJSONString(normalOrderReqVO));
        if (bindingResult.hasErrors()) {
            return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), ErrorCode.INPUT_ERROR.getText());
        }
        String memNo = normalOrderReqVO.getMemNo();
        if (null == memNo) {
            return new ResponseData<>(ErrorCode.NEED_LOGIN.getCode(), ErrorCode.NEED_LOGIN.getText());
        }


        submitOrderService.submitOrder(normalOrderReqVO);

        return ResponseData.success(null);
    }


}
