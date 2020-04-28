package com.atzuche.order.open.service;

import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.autoyol.commons.web.ResponseData;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="order-center-api")
public interface FeignGoodsService {

    /*
     * @Author ZhangBin
     * @Date 2020/4/28 15:54
     * @Description: 通过订单号查询车辆号
     *
     **/
    @RequestMapping("/goods/renter/queryCarNumByOrderNo")
    public ResponseData<String> queryCarNumByOrderNo(@RequestParam("orderNo") String orderNo);
    
    /*
     * @Author ZhangBin
     * @Date 2020/4/28 15:54 
     * @Description: 通过租客子订单号获取租客商品信息
     * 
     **/
    @RequestMapping("/goods/renter/queryRenterGoodsDetail")
    public ResponseData<RenterGoodsDetailDTO> queryRenterGoodsDetail(@RequestParam("renterOrderNo")String renterOrderNo, @RequestParam("isNeedPrice") boolean isNeedPrice);
}
