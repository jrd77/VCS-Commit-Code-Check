package com.atzuche.order.open.service;

import com.atzuche.order.commons.vo.req.DeliveryCarPriceReqVO;
import com.atzuche.order.commons.vo.res.delivery.DeliveryOilCostRepVO;
import com.atzuche.order.commons.vo.res.delivery.RenterOrderDeliveryRepVO;
import com.autoyol.commons.web.ResponseData;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Repository;
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

    @PostMapping("/oil/getOilCrash")
    ResponseData<DeliveryOilCostRepVO> getOilCostByRenterOrderNo(@RequestBody DeliveryCarPriceReqVO deliveryCarPriceReqVO);
    @PostMapping("/delivery/list")
    ResponseData<List<RenterOrderDeliveryRepVO>> findRenterOrderListByOrderNo(@RequestParam("orderNo") String orderNo);
}
