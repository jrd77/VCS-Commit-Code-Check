package com.atzuche.order.coreapi.controller;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.enums.DispatcherStatusEnum;
import com.atzuche.order.commons.enums.PlatformCancelReasonEnum;
import com.atzuche.order.commons.vo.req.AdminOrderCancelReqVO;
import com.atzuche.order.commons.vo.req.CancelOrderReqVO;
import com.atzuche.order.coreapi.service.CancelOrderService;
import com.atzuche.order.coreapi.service.OwnerOrderFineApplyHandelService;
import com.atzuche.order.coreapi.service.PlatformCancelOrderService;
import com.atzuche.order.ownercost.service.OwnerOrderFineApplyService;
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
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

/**
 * 取消
 *
 * @author pengcheng.fu
 * @date 2020/1/7 11:49
 */

@RequestMapping("/order")
@RestController
@AutoDocVersion(version = "订单接口文档")
public class CancelOrderController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CancelOrderController.class);

    @Autowired
    private CancelOrderService cancelOrderService;
    @Autowired
    private OwnerOrderFineApplyHandelService ownerOrderFineApplyHandelService;
    @Autowired
    private PlatformCancelOrderService platformCancelOrderService;


    @AutoDocMethod(description = "取消订单", value = "取消订单")
    @PostMapping("/normal/cancel")
    public ResponseData<?> cancelOrder(@Valid @RequestBody CancelOrderReqVO cancelOrderReqVO,
                                       BindingResult bindingResult) {
        LOGGER.info("Cancel order.param is,cancelOrderReqVO:[{}]", JSON.toJSONString(cancelOrderReqVO));
        if (bindingResult.hasErrors()) {
            Optional<FieldError> error = bindingResult.getFieldErrors().stream().findFirst();
            return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), error.isPresent() ?
                    error.get().getDefaultMessage() : ErrorCode.INPUT_ERROR.getText());
        }
        cancelOrderService.cancel(cancelOrderReqVO);
        return ResponseData.success();
    }

    @AutoDocMethod(description = "管理后台取消订单", value = "管理后台取消订单")
    @PostMapping("/admin/cancel")
    public ResponseData<?> adminCancelOrder(@Valid @RequestBody AdminOrderCancelReqVO reqVO,
                                            BindingResult bindingResult) {
        LOGGER.info("User [{}] console cancel order.param is,reqVO:[{}]", reqVO.getOperator(), JSON.toJSONString(reqVO));
        if (bindingResult.hasErrors()) {
            Optional<FieldError> error = bindingResult.getFieldErrors().stream().findFirst();
            return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), error.isPresent() ?
                    error.get().getDefaultMessage() : ErrorCode.INPUT_ERROR.getText());
        }

        platformCancelOrderService.cancel(reqVO.getOrderNo(), reqVO.getOperator(),
                PlatformCancelReasonEnum.from(reqVO.getCancelType()));
        return ResponseData.success();
    }


    @AutoDocMethod(description = "测试车主取消订单进度后续操作", value = "测试车主取消订单进度后续操作")
    @PostMapping("/test/owner/cancel/{orderNo}/{dispatcherStatus}")
    public ResponseData<?> ownerCancelOrder(@PathVariable("orderNo") String orderNo,
                                            @PathVariable("dispatcherStatus") int dispatcherStatus) {
        boolean result = ownerOrderFineApplyHandelService.handleFineApplyRecord(orderNo,
                DispatcherStatusEnum.from(dispatcherStatus));
        return ResponseData.success(result);
    }


}
