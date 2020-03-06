package com.atzuche.order.open.service;


import com.atzuche.order.commons.entity.orderDetailDto.*;
import com.atzuche.order.commons.entity.ownerOrderDetail.AdminOwnerOrderDetailDTO;
import com.autoyol.commons.web.ResponseData;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@FeignClient(name = "order-center-api")
//@FeignClient(url = "http://10.0.3.235:1412" ,name="order-center-api")
//@FeignClient(url = "http://localhost:1412" ,name="order-center-api")
@FeignClient(name="order-center-api")
public interface FeignOrderDetailService {

    @RequestMapping(method = RequestMethod.POST, value = "/order/detail/query")
    ResponseData<OrderDetailRespDTO> getOrderDetail(@RequestBody OrderDetailReqDTO orderDetailReqDTO);

    /**
     * @Author ZhangBin
     * @Date 2020/1/8 21:06
     * @Description: 获取订单状态
     *
     **/
    @RequestMapping(method = RequestMethod.POST, value = "/order/detail/status")
    ResponseData<OrderStatusRespDTO> getOrderStatus(@RequestBody OrderDetailReqDTO orderDetailReqDTO);

    /**
     * @Author ZhangBin
     * @Date 2020/1/13 16:47
     * @Description:  获取历史订单列表
     *
     **/
    @RequestMapping(method = RequestMethod.POST, value = "/order/detail/childHistory")
    ResponseData<OrderHistoryRespDTO> orderHistory(@RequestBody OrderHistoryReqDTO orderHistoryReqDTO);


    /**
     * @Author ZhangBin
     * @Date 2020/1/13 16:47
     * @Description: 费用明细
     *
     **/
    @RequestMapping(method = RequestMethod.POST, value = "/order/detail/orderAccountDetail")
    ResponseData<OrderAccountDetailRespDTO> orderAccountDetail(@RequestBody OrderDetailReqDTO orderDetailReqDTO);
    
    
    /**
     * @Author ZhangBin
     * @Date 2020/1/15 16:20 
     * @Description: 车主子订单详情
     *
     **/
    @GetMapping("/order/detail/adminOwnerOrderDetail")
    public ResponseData<AdminOwnerOrderDetailDTO> adminOwnerOrderDetail(@RequestParam("ownerOrderNo") String ownerOrderNo, @RequestParam("orderNo")String orderNo);

    /**
     * 人工调度历史订单
     * @param orderNo
     * @return
     */
    @GetMapping("/order/detail/dispatchHistory")
    public ResponseData<OrderHistoryListDTO> dispatchHistory(@RequestParam("orderNo") String orderNo);

    /*
     * @Author ZhangBin
     * @Date 2020/2/27 10:59
     * @Description: 车主订单详情
     *
     **/
    @PostMapping("/order/detail/ownerOrderDetail")
    public ResponseData<OrderDetailRespDTO> ownerOrderDetail(@RequestBody OrderOwnerDetailReqDTO orderOwnerDetailReqDTO);
    /*
     * @Author ZhangBin
     * @Date 2020/2/27 11:02
     * @Description: 租客订单详情
     * 
     **/
    @GetMapping("/order/detail/renterOrderDetail")
    public ResponseData<RenterOrderDetailRespDTO> renterOrderDetail(@RequestParam("orderNo") String orderNo,
                                                                   @RequestParam(name = "renterOrderNo",required = false) String renterOrderNo);

    @GetMapping("/order/detail/queryInProcess")
    public ResponseData<List<OrderStatusDTO>> queryInProcess();

    @GetMapping("/order/detail/queryChangeApplyByOwnerOrderNo")
    public ResponseData<OrderDetailRespDTO> queryChangeApplyByOwnerOrderNo(String ownerOrderNo);
}
