package com.atzuche.order.coreapi.controller;

import com.atzuche.order.commons.entity.trusteeship.OrderCarTrusteeshipRepVO;
import com.atzuche.order.commons.entity.trusteeship.TrusteeshipVO;
import com.atzuche.order.delivery.common.DeliveryErrorCode;
import com.atzuche.order.delivery.entity.OrderCarTrusteeshipEntity;
import com.atzuche.order.delivery.service.OrderCarTrusteeshipService;
import com.atzuche.order.delivery.utils.CommonUtil;
import com.autoyol.commons.web.ResponseData;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

/**
 * @author 胡春林
 * 托管车服务
 */
@RestController
@RequestMapping("/api/carTrusteeship")
public class OrderCarTrusteeshipInfoController {

    @Autowired
    OrderCarTrusteeshipService orderCarTrusteeshipService;

    @PostMapping("/select")
    public ResponseData<OrderCarTrusteeshipRepVO> selectObjectByOrderNoAndCar(@RequestBody TrusteeshipVO trusteeshipVO) {
        if (Objects.isNull(trusteeshipVO) || StringUtils.isBlank(trusteeshipVO.getOrderNo())) {
            return ResponseData.createErrorCodeResponse(DeliveryErrorCode.DELIVERY_PARAMS_ERROR.getValue(), DeliveryErrorCode.DELIVERY_PARAMS_ERROR.getName());
        }
        OrderCarTrusteeshipEntity orderCarTrusteeship = orderCarTrusteeshipService.selectObjectByOrderNoAndCar(trusteeshipVO.getOrderNo(), trusteeshipVO.getCarNo());
        OrderCarTrusteeshipRepVO orderCarTrusteeshipRepVO = new OrderCarTrusteeshipRepVO();
        BeanUtils.copyProperties(orderCarTrusteeship,orderCarTrusteeshipRepVO);
        return ResponseData.success(orderCarTrusteeshipRepVO);
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
    public ResponseData<?> updateOrderCarTrusteeship(@RequestBody OrderCarTrusteeshipRepVO orderCarTrusteeshipEntity) {
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
    public ResponseData<?> insertOrderCarTrusteeship(@RequestBody OrderCarTrusteeshipRepVO orderCarTrusteeshipEntity) {
        if (Objects.isNull(orderCarTrusteeshipEntity) || StringUtils.isBlank(orderCarTrusteeshipEntity.getOrderNo())) {
            return ResponseData.createErrorCodeResponse(DeliveryErrorCode.DELIVERY_PARAMS_ERROR.getValue(), DeliveryErrorCode.DELIVERY_PARAMS_ERROR.getName());
        }
        OrderCarTrusteeshipEntity orderCarTrusteeship = orderCarTrusteeshipService.selectObjectByOrderNoAndCar(orderCarTrusteeshipEntity.getOrderNo(), orderCarTrusteeshipEntity.getCarNo());
        if (Objects.nonNull(orderCarTrusteeship)) {
            BeanUtils.copyProperties(orderCarTrusteeshipEntity, orderCarTrusteeship);
            orderCarTrusteeshipService.updateOrderCarTrusteeship(orderCarTrusteeship);
            return ResponseData.success();
        }
        int result = orderCarTrusteeshipService.insertOrderCarTrusteeship(orderCarTrusteeship);
        if (result > 0) {
            return ResponseData.success();
        }
        return ResponseData.error();
    }

}
