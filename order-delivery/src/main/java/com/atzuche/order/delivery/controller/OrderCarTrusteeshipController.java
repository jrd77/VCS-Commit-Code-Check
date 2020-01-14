package com.atzuche.order.delivery.controller;

import com.atzuche.order.delivery.common.DeliveryErrorCode;
import com.atzuche.order.delivery.entity.OrderCarTrusteeshipEntity;
import com.atzuche.order.delivery.service.OrderCarTrusteeshipService;
import com.atzuche.order.delivery.utils.CommonUtil;
import com.atzuche.order.delivery.vo.delivery.req.TrusteeshipVO;
import com.autoyol.commons.web.ResponseData;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
 * @author 胡春林
 * 托管车服务
 */
@RestController
@RequestMapping("/api/carTrusteeship")
public class OrderCarTrusteeshipController {

    @Autowired
    OrderCarTrusteeshipService orderCarTrusteeshipService;

    @PostMapping("/select")
    public ResponseData<?> selectObjectByOrderNoAndCar(@RequestBody TrusteeshipVO trusteeshipVO) {
        if (Objects.isNull(trusteeshipVO) || StringUtils.isBlank(trusteeshipVO.getOrderNo())) {
            return ResponseData.createErrorCodeResponse(DeliveryErrorCode.DELIVERY_PARAMS_ERROR.getValue(), DeliveryErrorCode.DELIVERY_PARAMS_ERROR.getName());
        }
        return ResponseData.success(orderCarTrusteeshipService.selectObjectByOrderNoAndCar(trusteeshipVO.getOrderNo(), trusteeshipVO.getCarNo()));
    }

    @PostMapping("/delete")
    public ResponseData<?> deleteOrderCarTrusteeship(@RequestBody TrusteeshipVO trusteeshipVO) {
        if (Objects.isNull(trusteeshipVO) || StringUtils.isBlank(trusteeshipVO.getOrderNo())) {
            return ResponseData.createErrorCodeResponse(DeliveryErrorCode.DELIVERY_PARAMS_ERROR.getValue(), DeliveryErrorCode.DELIVERY_PARAMS_ERROR.getName());
        }
        int result = orderCarTrusteeshipService.deleteOrderCarTrusteeship(trusteeshipVO.getOrderNo(), trusteeshipVO.getCarNo());
        if (result > 0) {
            return ResponseData.success();
        }
        return ResponseData.error();
    }

    /**
     * 仅限内部这样调用
     *
     * @param orderCarTrusteeshipEntity
     * @return
     */
    @PostMapping("/update")
    public ResponseData<?> updateOrderCarTrusteeship(@RequestBody OrderCarTrusteeshipEntity orderCarTrusteeshipEntity) {
        if (Objects.isNull(orderCarTrusteeshipEntity) || StringUtils.isBlank(orderCarTrusteeshipEntity.getOrderNo())) {
            return ResponseData.createErrorCodeResponse(DeliveryErrorCode.DELIVERY_PARAMS_ERROR.getValue(), DeliveryErrorCode.DELIVERY_PARAMS_ERROR.getName());
        }
        OrderCarTrusteeshipEntity orderCarTrusteeship = orderCarTrusteeshipService.selectObjectByOrderNoAndCar(orderCarTrusteeshipEntity.getOrderNo(), orderCarTrusteeshipEntity.getCarNo());
        if (Objects.isNull(orderCarTrusteeship)) {
            return ResponseData.createErrorCodeResponse(DeliveryErrorCode.DELIVERY_PARAMS_ERROR.getValue(), "没找到对应的数据");
        }
        CommonUtil.copyPropertiesIgnoreNull(orderCarTrusteeshipEntity, orderCarTrusteeship);
        int result = orderCarTrusteeshipService.updateOrderCarTrusteeship(orderCarTrusteeship);
        if (result > 0) {
            return ResponseData.success();
        }
        return ResponseData.error();
    }

    @PostMapping("/add")
    public ResponseData<?> insertOrderCarTrusteeship(@RequestBody OrderCarTrusteeshipEntity orderCarTrusteeshipEntity) {
        if (Objects.isNull(orderCarTrusteeshipEntity) || StringUtils.isBlank(orderCarTrusteeshipEntity.getOrderNo())) {
            return ResponseData.createErrorCodeResponse(DeliveryErrorCode.DELIVERY_PARAMS_ERROR.getValue(), DeliveryErrorCode.DELIVERY_PARAMS_ERROR.getName());
        }
        OrderCarTrusteeshipEntity orderCarTrusteeship = orderCarTrusteeshipService.selectObjectByOrderNoAndCar(orderCarTrusteeshipEntity.getOrderNo(), orderCarTrusteeshipEntity.getCarNo());
        if (Objects.nonNull(orderCarTrusteeship)) {
            return ResponseData.createErrorCodeResponse(DeliveryErrorCode.DELIVERY_PARAMS_ERROR.getValue(), "已存在该数据");
        }
        int result = orderCarTrusteeshipService.insertOrderCarTrusteeship(orderCarTrusteeship);
        if (result > 0) {
            return ResponseData.success();
        }
        return ResponseData.error();
    }

}
