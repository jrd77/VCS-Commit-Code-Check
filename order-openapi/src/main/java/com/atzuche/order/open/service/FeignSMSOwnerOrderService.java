package com.atzuche.order.open.service;

import com.atzuche.order.commons.entity.dto.OwnerGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.OwnerMemberDTO;
import com.atzuche.order.commons.entity.orderDetailDto.OwnerOrderDTO;
import com.autoyol.commons.web.ResponseData;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author 胡春林
 * sms短信需要的车主feign接口
 */
@FeignClient(name = "order-center-api")
public interface FeignSMSOwnerOrderService {

    /**
     * 获取车主商品信息
     * @param ownerOrderNo
     * @return
     */
    @GetMapping("/goodsDetail")
    ResponseData<OwnerGoodsDetailDTO> getOwnerGoodsDetail(@RequestParam("ownerOrderNo") String ownerOrderNo);

    /**
     * 获取车主会员信息
     * @param ownerOrderNo
     * @return
     */
    @GetMapping("/memberInfo")
    ResponseData<OwnerMemberDTO> selectownerMemberByOwnerOrderNo(@RequestParam("ownerOrderNo") String ownerOrderNo);

    /**
     * 获取车主子订单信息
     * @param orderNo
     * @return
     */
    @GetMapping("/orderInfo")
    ResponseData<OwnerOrderDTO> getOwnerOrderByOrderNoAndIsEffective(@RequestParam("orderNo") String orderNo);

}
