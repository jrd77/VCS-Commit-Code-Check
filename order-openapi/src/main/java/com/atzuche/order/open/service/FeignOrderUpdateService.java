package com.atzuche.order.open.service;

import com.atzuche.order.commons.entity.dto.RentCityAndRiskAccidentReqDTO;
import com.atzuche.order.commons.vo.req.AdminOrderCancelReqVO;
import com.atzuche.order.commons.vo.req.AgreeOrderReqVO;
import com.atzuche.order.commons.vo.req.CancelOrderReqVO;
import com.atzuche.order.commons.vo.req.RefuseOrderReqVO;
import com.autoyol.commons.web.ResponseData;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

//@FeignClient(url="http://10.0.3.235:1412",name = "order-center-api")
@FeignClient(name = "order-center-api")
public interface FeignOrderUpdateService {
    /*
     * @Author ZhangBin
     * @Date 2020/1/8 21:06
     * @Description: 修改用车城市和风控事故状态
     *
     **/
    @RequestMapping(method = RequestMethod.POST, value = "/order/update/rentCityAndRiskAccident")
    ResponseData<?> updateRentCityAndRiskAccident(@RequestBody RentCityAndRiskAccidentReqDTO rentCityAndRiskAccidentReqDTO);
    
    /**
     * @Author ZhangBin
     * @Date 2020/1/14 14:30
     * @Description: 带租客/车主取消订单
     * 
     **/
    @PostMapping("/order/normal/cancel")
    public ResponseData<?> cancelOrder(@RequestBody CancelOrderReqVO cancelOrderReqVO);


    /**
     * 平台取消
     *
     * @param adminOrderCancelReqVO 请求参数
     * @return ResponseData<?>
     */
    @PostMapping("/order/admin/cancel")
    public ResponseData<?> adminCancelOrder(@RequestBody AdminOrderCancelReqVO adminOrderCancelReqVO);


    /**
     * 车主同意订单
     *
     * @param reqVO
     * @return
     */
    @PostMapping("/order/normal/agree")
    public ResponseData<?> adminOwnerAgreeOrder(@RequestBody AgreeOrderReqVO reqVO) ;

    /**
     * 车主拒绝订单
     *
     * @param reqVO
     * @return
     */
    @PostMapping("/order/normal/refuse")
    public ResponseData<?> adminOwnerRefuseOrder(@RequestBody RefuseOrderReqVO reqVO) ;


}
