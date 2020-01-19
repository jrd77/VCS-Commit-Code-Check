package com.atzuche.order.coreapi.controller;

import com.atzuche.order.commons.vo.req.DeliveryCarPriceReqVO;
import com.atzuche.order.commons.vo.res.delivery.DeliveryCarRepVO;
import com.atzuche.order.commons.vo.res.delivery.DeliveryOilCostRepVO;
import com.atzuche.order.commons.vo.res.delivery.DistributionCostVO;
import com.atzuche.order.commons.vo.res.delivery.RenterOrderDeliveryRepVO;
import com.atzuche.order.delivery.entity.RenterOrderDeliveryEntity;
import com.atzuche.order.delivery.service.RenterOrderDeliveryService;
import com.atzuche.order.delivery.service.delivery.DeliveryCarInfoPriceService;
import com.atzuche.order.delivery.utils.CommonUtil;
import com.atzuche.order.delivery.vo.delivery.DeliveryOilCostVO;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * @author 胡春林
 * 配送相关接口
 */
@RestController
@Slf4j
@RequestMapping("/api/delivery")
public class DeliveryCarInfoController {

    @Autowired
    DeliveryCarInfoPriceService deliveryCarInfoPriceService;
    @Autowired
    RenterOrderDeliveryService renterOrderDeliveryService;

    /**
     * 获取油费
     * @param deliveryCarPriceReqVO
     * @return
     */
    @PostMapping("/oil/getOilCrash")
    public ResponseData<DeliveryOilCostRepVO> getOilCostByRenterOrderNo(@RequestBody DeliveryCarPriceReqVO deliveryCarPriceReqVO, BindingResult bindingResult) {
        log.info("获取油费入参：{}", deliveryCarPriceReqVO.toString());
        if (bindingResult.hasErrors()) {
            Optional<FieldError> error = bindingResult.getFieldErrors().stream().findFirst();
            return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), error.isPresent() ?
                    error.get().getDefaultMessage() : ErrorCode.INPUT_ERROR.getText());
        }
        try {
            DeliveryOilCostRepVO deliveryOilCostRepVO = DeliveryOilCostRepVO.builder().build();
            DeliveryOilCostVO deliveryOilCostVO = deliveryCarInfoPriceService.getOilCostByRenterOrderNo(deliveryCarPriceReqVO.getOrderNo(), deliveryCarPriceReqVO.getCarEngineType());
            BeanUtils.copyProperties(deliveryOilCostVO,deliveryOilCostRepVO);
            return ResponseData.success(deliveryOilCostRepVO);
        } catch (Exception e) {
            log.error("获取油费异常:", e);
            return ResponseData.error();
        }
    }

    /**
     * 获取配送订单信息
     * @param orderNo
     * @return
     */
    @GetMapping("/delivery/list")
    public ResponseData<List<RenterOrderDeliveryRepVO>> findRenterOrderListByOrderNo(@RequestParam("orderNo") String orderNo) {
        if (StringUtils.isBlank(orderNo)) {
            return new ResponseData(ErrorCode.INPUT_ERROR.getCode(), ErrorCode.INPUT_ERROR.getText());
        }
        try {
            List<RenterOrderDeliveryEntity> renterOrderDeliveryEntityList = renterOrderDeliveryService.findRenterOrderListByOrderNo(orderNo);
            List<RenterOrderDeliveryRepVO> renterOrderDeliveryRepVOS = CommonUtil.copyList(renterOrderDeliveryEntityList);
            return ResponseData.success(renterOrderDeliveryRepVOS);
        } catch (Exception e) {
            log.error("获取获取配送订单信息异常:", e);
            return ResponseData.error();
        }

    }

    /**
     * 获取车主取还车费用
     * @param carType
     * @return
     */
    @GetMapping("/getAndReturnCarCost")
    public ResponseData<DistributionCostVO> findDeliveryCostByOrderNo(@RequestParam("carType") Integer carType) {
        DistributionCostVO distributionCostVO = deliveryCarInfoPriceService.getDistributionCost(carType);
        return ResponseData.success(distributionCostVO);
    }












}
