package com.atzuche.order.coreapi.controller;

import com.atzuche.order.commons.BindingResultUtil;
import com.atzuche.order.commons.vo.delivery.OrderCarTrusteeshipVO;
import com.atzuche.order.commons.vo.delivery.SimpleOrderInfoVO;
import com.atzuche.order.commons.vo.req.DeliveryCarPriceReqVO;
import com.atzuche.order.commons.vo.res.delivery.DeliveryOilCostRepVO;
import com.atzuche.order.commons.vo.res.delivery.DistributionCostVO;
import com.atzuche.order.commons.vo.res.delivery.RenterOrderDeliveryRepVO;
import com.atzuche.order.coreapi.service.DeliveryOrderService;
import com.atzuche.order.delivery.entity.OrderCarTrusteeshipEntity;
import com.atzuche.order.delivery.entity.RenterOrderDeliveryEntity;
import com.atzuche.order.delivery.service.RenterOrderDeliveryService;
import com.atzuche.order.delivery.service.delivery.DeliveryCarInfoPriceService;
import com.atzuche.order.delivery.utils.CommonUtil;
import com.atzuche.order.delivery.vo.delivery.DeliveryOilCostVO;
import com.atzuche.order.delivery.vo.delivery.rep.DeliveryCarVO;
import com.atzuche.order.delivery.vo.delivery.req.DeliveryCarRepVO;
import com.atzuche.order.delivery.vo.delivery.req.DeliveryReqVO;
import com.atzuche.order.delivery.vo.trusteeship.OrderCarTrusteeshipReqVO;
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
    @Autowired
    private DeliveryOrderService deliveryOrderService;

    /**
     * 获取油费
     * @param deliveryCarPriceReqVO
     * @return
     */
    @PostMapping("/oil/getOilCrash")
    public ResponseData<DeliveryOilCostRepVO> getOilCostByRenterOrderNo(@RequestBody DeliveryCarPriceReqVO deliveryCarPriceReqVO, BindingResult bindingResult) {
        log.info("获取油费入参：{}", deliveryCarPriceReqVO.toString());
        BindingResultUtil.checkBindingResult(bindingResult);
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


    /**
     * 获取配送相关信息
     * @param deliveryCarDTO
     */
    @PostMapping("/getDeliveryCarVO")
    public ResponseData<DeliveryCarVO> getDeliveryCarVO(@RequestBody DeliveryCarRepVO deliveryCarDTO) {
    	DeliveryCarVO deliveryCarVO = deliveryOrderService.findDeliveryListByOrderNo(deliveryCarDTO);
        return ResponseData.success(deliveryCarVO);
    }


    
    /**
     * 更新交接车信息
     * @param deliveryCarVO
     */
    @PostMapping("/updateHandoverCarInfo")
    public ResponseData<?> updateHandoverCarInfo(@RequestBody DeliveryCarVO deliveryCarVO) {
    	try {
			deliveryOrderService.updateHandoverCarInfo(deliveryCarVO);
		} catch (Exception e) {
			log.error("更新交接车信息异常:", e);
            return ResponseData.error();
		}
    	return ResponseData.success();
    }


    /**
     * 更新取还车备注信息
     * @param deliveryCarVO
     */
    @PostMapping("/updateDeliveryRemark")
    public ResponseData<?> updateDeliveryRemark(@RequestBody DeliveryReqVO deliveryReqVO) {
    	try {
			deliveryOrderService.updateDeliveryRemark(deliveryReqVO);
		} catch (Exception e) {
			log.error("更新取还车备注信息异常:", e);
            return ResponseData.error();
		}
    	return ResponseData.success();
    }


    /**
     * 获取配送取还车信息
     * @param deliveryCarDTO
     */
    @PostMapping("/getDistributionCostVO")
    public ResponseData<com.atzuche.order.commons.vo.delivery.DistributionCostVO> getDistributionCostVO(@RequestBody DeliveryCarRepVO deliveryCarDTO) {
    	com.atzuche.order.commons.vo.delivery.DistributionCostVO distributionCostVO = deliveryOrderService.findDeliveryCostByOrderNo(deliveryCarDTO);
        return ResponseData.success(distributionCostVO);
    }
    
    
    /**
     * 获取简单订单信息
     * @param deliveryCarDTO
     */
    @GetMapping("/getSimpleOrderInfoVO")
    public ResponseData<SimpleOrderInfoVO> getSimpleOrderInfoVO(@RequestParam("orderNo") String orderNo) {
    	SimpleOrderInfoVO simpleOrderInfoVO = deliveryOrderService.getSimpleOrderInfoVO(orderNo);
        return ResponseData.success(simpleOrderInfoVO);
    }
    
    
    /**
     * 托管车新增
     * @param orderCarTrusteeshipVO
     */
    @PostMapping("/trusteeship/add")
    public ResponseData<?> addOrderCarTrusteeship(@RequestBody OrderCarTrusteeshipVO orderCarTrusteeshipVO) {
    	deliveryOrderService.addOrderCarTrusteeship(orderCarTrusteeshipVO);
    	return ResponseData.success();
    }
    
    
    /**
     * 获取托管车信息
     * @param deliveryCarVO
     */
    @PostMapping("/trusteeship/get")
    public ResponseData<?> getOrderCarTrusteeshipEntity(@RequestBody OrderCarTrusteeshipReqVO orderCarTrusteeshipReqVO) {
    	OrderCarTrusteeshipEntity orderCarTrusteeshipEntity = deliveryOrderService.getOrderCarTrusteeshipEntity(orderCarTrusteeshipReqVO);
    	return ResponseData.success(orderCarTrusteeshipEntity);
    }

}
