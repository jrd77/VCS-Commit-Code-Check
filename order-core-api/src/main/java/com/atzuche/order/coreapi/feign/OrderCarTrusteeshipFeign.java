package com.atzuche.order.coreapi.feign;

import com.atzuche.order.delivery.entity.OrderCarTrusteeshipEntity;
import com.atzuche.order.delivery.vo.delivery.req.TrusteeshipVO;
import com.autoyol.commons.web.ResponseData;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author 胡春林
 *
 */
@FeignClient(value = "auto-java-OrderAdminApi", path = "/api/carTrusteeship")
@Repository
public interface OrderCarTrusteeshipFeign {

    @PostMapping("/select")
    ResponseData<?> selectObjectByOrderNoAndCar(@RequestBody TrusteeshipVO trusteeshipVO);

    @PostMapping("/delete")
    ResponseData<?> deleteOrderCarTrusteeship(@RequestBody TrusteeshipVO trusteeshipVO);

    @PostMapping("/update")
    ResponseData<?> updateOrderCarTrusteeship(@RequestBody OrderCarTrusteeshipEntity orderCarTrusteeshipEntity);

    @PostMapping("/add")
    ResponseData<?> insertOrderCarTrusteeship(@RequestBody OrderCarTrusteeshipEntity orderCarTrusteeshipEntity);

}
