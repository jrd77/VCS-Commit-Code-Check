package com.atzuche.order.open.service;

import com.atzuche.order.commons.entity.dto.OwnerGoodsDetailDTO;
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
     ResponseData<String> queryCarNumByOrderNo(@RequestParam("orderNo") String orderNo);
    
    /*
     * @Author ZhangBin
     * @Date 2020/4/28 15:54 
     * @Description: 通过租客子订单号获取租客商品信息
     * 
     **/
    @RequestMapping("/goods/renter/queryRenterGoodsDetail")
     ResponseData<RenterGoodsDetailDTO> queryRenterGoodsDetail(@RequestParam("renterOrderNo")String renterOrderNo, @RequestParam("isNeedPrice") boolean isNeedPrice);
    
    /*
     * @Author ZhangBin
     * @Date 2020/4/28 16:15 
     * @Description: 根据订单号查询车主商品信息
     * 
     **/
    @RequestMapping("/goods/owner/queryRenterGoodsDetail")
    ResponseData<OwnerGoodsDetailDTO> queryOwnerGoodsByOrderNo(@RequestParam("orderNo")String orderNo);
    
    /*
     * @Author ZhangBin
     * @Date 2020/4/28 16:29 
     * @Description: 使用订单号和车辆号查询商品信息
     * 
     **/
    @RequestMapping("/goods/owner/queryOwnerGoodsByCarNoAndOrderNo")
    ResponseData<OwnerGoodsDetailDTO> queryOwnerGoodsByCarNoAndOrderNo(@RequestParam("carNo") Integer carNo,@RequestParam("orderNo") String orderNo);
}
