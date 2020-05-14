package com.atzuche.order.open.service;

import com.atzuche.order.commons.entity.orderDetailDto.OwnerMemberDTO;
import com.autoyol.commons.web.ResponseData;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="order-center-api")
public interface FeignMemberService {
    /*
     * @Author ZhangBin
     * @Date 2020/4/28 11:22
     * @Description: 根据订单号和车主会员号查询车主信息
     * 
     **/
    @GetMapping("/member/owner/queryOwnerMemberByOrderNoAndOwnerNo")
    public ResponseData<OwnerMemberDTO> queryOwnerMemberByOrderNoAndOwnerNo(@RequestParam("orderNo") String orderNo, @RequestParam("ownerMemberNo")String ownerMemberNo);
    /*
     * @Author ZhangBin
     * @Date 2020/4/28 11:48
     * @Description: 根据车主子订单号查询车主会员和车主权益信息
     * 
     **/
    @GetMapping("/member/owner/queryOwnerMemberByOwnerOrderNo")
    public ResponseData<com.atzuche.order.commons.entity.dto.OwnerMemberDTO > queryOwnerMemberByOwnerOrderNo(@RequestParam("ownerOrderNo") String ownerOrderNo, @RequestParam("isNeedRight")boolean isNeedRight);

    /*
     * @Author ZhangBin
     * @Date 2020/4/28 11:48
     * @Description: 根据车主子订单号查询车主会员和车主权益信息
     *
     **/
    @GetMapping("/member/renter/queryRenterMemberByOwnerOrderNo")
    public ResponseData<com.atzuche.order.commons.entity.dto.RenterMemberDTO > queryRenterMemberByOwnerOrderNo(@RequestParam("renterOrderNo") String renterOrderNo, @RequestParam("isNeedRight")boolean isNeedRight);

    /*
     * @Author ZhangBin
     * @Date 2020/4/28 14:45
     * @Description: 更具订单号获取会员号
     *
     **/
    @GetMapping("/member/renter/getRenterMemberByOrderNo")
    public ResponseData<String> getRenterMemberByOrderNo(@RequestParam("orderNo")String orderNo);
}


