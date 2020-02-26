package com.atzuche.order.open.service;

import com.atzuche.order.commons.entity.ownerOrderDetail.*;
import com.autoyol.commons.web.ResponseData;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

//@FeignClient(url="http://10.0.3.235:1412",name = "order-center-api")
//@FeignClient(url="http://localhost:1412",name = "order-center-api")
@FeignClient(name = "order-center-api")
public interface FeignOwnerOrderDetailService {
    /**
     * @Author ZhangBin
     * @Date 2020/1/15 21:29
     * @Description: 租金明细
     *
     **/
    @GetMapping("/owner/ownerRentDetail")
    ResponseData<OwnerRentDetailDTO> ownerRentDetail(@RequestParam("orderNo") String orderNo, @RequestParam("ownerOrderNo") String ownerOrderNo);
    /**
     * @Author ZhangBin
     * @Date 2020/1/15 21:29
     * @Description: 车主租客调价明细
     *
     **/
    @GetMapping("/owner/renterOwnerPrice")
    ResponseData<RenterOwnerPriceDTO> renterOwnerPrice(@RequestParam("orderNo") String orderNo, @RequestParam("ownerOrderNo") String ownerOrderNo);
    /**
     * @Author ZhangBin
     * @Date 2020/1/15 21:31
     * @Description: 违约罚金明细
     *
     **/
    @GetMapping("/owner/fienAmtDetail")
    ResponseData<FienAmtDetailDTO> fienAmtDetail(@RequestParam("orderNo") String orderNo, @RequestParam("ownerOrderNo") String ownerOrderNo);
    /**
     * @Author ZhangBin
     * @Date 2020/1/15 21:31
     * @Description: 服务费明细
     *
     **/
    @GetMapping("/owner/serviceDetail")
    ResponseData<ServiceDetailDTO> serviceDetail(@RequestParam("orderNo") String orderNo, @RequestParam("ownerOrderNo") String ownerOrderNo);
    /**
     * @Author ZhangBin
     * @Date 2020/1/15 21:37
     * @Description: 车主给平台的费用
     *
     **/
    @GetMapping("/owner/platformToOwner")
    ResponseData<PlatformToOwnerDTO> platformToOwner(@RequestParam("orderNo") String orderNo, @RequestParam("ownerOrderNo") String ownerOrderNo);
    /**
     * @Author ZhangBin
     * @Date 2020/1/15 21:34
     * @Description: 平台给车主的补贴
     *
     **/
    @GetMapping("/owner/platformToOwnerSubsidy")
    ResponseData<PlatformToOwnerSubsidyDTO> platformToOwnerSubsidy(@RequestParam("orderNo") String orderNo, @RequestParam("ownerOrderNo") String ownerOrderNo,@RequestParam("memNo") String memNo);

    /*
     * @Author ZhangBin
     * @Date 2020/1/17 17:12
     * @Description: 修改罚金
     *
     **/
    @PostMapping("/owner/updateFien")
    ResponseData<?> updateFineAmt(@RequestBody FienAmtUpdateReqDTO fienAmtUpdateReqDTO);
}
