package com.atzuche.order.open.service;

import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.RenterMemberDTO;
import com.atzuche.order.commons.entity.orderDetailDto.RenterOrderDTO;
import com.autoyol.commons.web.ResponseData;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author 胡春林
 * sms短信需要的租客feign接口
 */
@FeignClient(name = "order-center-api")
public interface FeignSMSRenterOrderService {

    /**
     * 获取租客商品信息
     * @param renterOrderNo
     * @return
     */
    @GetMapping("/goodsDetail")
    ResponseData<RenterGoodsDetailDTO> getRenterGoodsDetail(@RequestParam("renterOrderNo") String renterOrderNo);

    /**
     * 获取租客会员信息
     * @param renterOrderNo
     * @return
     */
    @GetMapping("/memberInfo")
    ResponseData<RenterMemberDTO> selectrenterMemberByRenterOrderNo(@RequestParam("renterOrderNo") String renterOrderNo);

    /**
     * 获取租客子订单信息
     * @param orderNo
     * @return
     */
    @GetMapping("/orderInfo")
    ResponseData<RenterOrderDTO> getRenterOrderByOrderNoAndIsEffective(@RequestParam("orderNo") String orderNo);
}
