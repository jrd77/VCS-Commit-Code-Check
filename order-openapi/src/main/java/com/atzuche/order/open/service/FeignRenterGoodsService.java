package com.atzuche.order.open.service;

import com.atzuche.order.open.vo.RenterGoodWithoutPriceVO;
import com.autoyol.commons.web.ResponseData;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/2/5 11:48 上午
 **/
@FeignClient(name="order-center-api")
public interface FeignRenterGoodsService {

    /**
     * 根据订单号和车辆号返回租客的车辆详情（不包含价格），主要是renter_goods中的数据
     * @param orderNo
     * @param carNo
     * @return
     */
    @GetMapping("/renter/goods")
    public ResponseData<RenterGoodWithoutPriceVO> getRenterGoodsDetailWithoutPrice(@RequestParam("orderNo") String orderNo, @RequestParam("carNo") String carNo);
}
