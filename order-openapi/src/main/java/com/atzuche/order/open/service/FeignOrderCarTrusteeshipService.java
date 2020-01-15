package com.atzuche.order.open.service;

import com.atzuche.order.commons.entity.trusteeship.OrderCarTrusteeshipRepVO;
import com.atzuche.order.commons.entity.trusteeship.TrusteeshipVO;
import com.autoyol.commons.web.ResponseData;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author 胡春林
 *
 */
@FeignClient(value = "order-center-api", path = "/api/carTrusteeship")
@Repository
public interface FeignOrderCarTrusteeshipService {

    /**
     * 新增
     * @param trusteeshipVO
     * @return
     */
    @PostMapping(value = "/select")
    ResponseData<OrderCarTrusteeshipRepVO> selectObjectByOrderNoAndCar(@RequestBody TrusteeshipVO trusteeshipVO);

    @PostMapping(value = "/delete")
    ResponseData<?> deleteOrderCarTrusteeship(@RequestBody TrusteeshipVO trusteeshipVO);

    @PostMapping(value = "/update")
    ResponseData<?> updateOrderCarTrusteeship(@RequestBody OrderCarTrusteeshipRepVO orderCarTrusteeshipEntity);

    @PostMapping(value = "/add")
    ResponseData<?> insertOrderCarTrusteeship(@RequestBody OrderCarTrusteeshipRepVO orderCarTrusteeshipEntity);

}
