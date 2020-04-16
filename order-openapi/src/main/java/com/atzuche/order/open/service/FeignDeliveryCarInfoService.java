package com.atzuche.order.open.service;

import com.atzuche.order.commons.vo.req.DeliveryCarPriceReqVO;
import com.atzuche.order.commons.vo.req.handover.rep.HandoverCarRespVO;
import com.atzuche.order.commons.vo.req.handover.req.HandoverCarInfoReqVO;
import com.atzuche.order.commons.vo.res.delivery.DeliveryCarRepVO;
import com.atzuche.order.commons.vo.res.delivery.DeliveryOilCostRepVO;
import com.atzuche.order.commons.vo.res.delivery.DistributionCostVO;
import com.atzuche.order.commons.vo.res.delivery.RenterOrderDeliveryRepVO;
import com.autoyol.commons.web.ResponseData;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author 胡春林
 * 配送车服务
 */
@FeignClient(value = "order-center-api", path = "/api/delivery")
@Repository
public interface FeignDeliveryCarInfoService {

    /**
     * 获取油费
     * @param deliveryCarPriceReqVO
     * @return
     */
    @PostMapping("/oil/getOilCrash")
    ResponseData<DeliveryOilCostRepVO> getOilCostByRenterOrderNo(@RequestBody DeliveryCarPriceReqVO deliveryCarPriceReqVO);

    /**
     * 获取配送相关信息
     * @param orderNo
     * @return
     */
    @GetMapping("/delivery/list")
    ResponseData<List<RenterOrderDeliveryRepVO>> findRenterOrderListByOrderNo(@RequestParam("orderNo") String orderNo);

    /**
     * 获取取还车费用
     * @param
     * @return
     */
    @GetMapping("/getAndReturnCarCost")
     ResponseData<DistributionCostVO> findDeliveryCostByOrderNo(@RequestParam("carType") Integer carType);

    /**
     * 设置油耗里程
     * @param handoverCarReqVO
     * @return
     */
    @PostMapping("/oil/setOil")
    ResponseData<?> updateHandoverCarInfo(@RequestBody HandoverCarInfoReqVO handoverCarReqVO);

    /**
     * 获取油耗里程
     * @param orderNo
     * @return
     */
    @GetMapping("/oil/list")
    ResponseData<HandoverCarRespVO> updateHandoverCarInfo(@RequestParam("orderNo") String  orderNo);

}