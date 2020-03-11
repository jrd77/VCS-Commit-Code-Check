package com.atzuche.order.open.service;

import com.atzuche.order.commons.entity.dto.RentCityAndRiskAccidentReqDTO;
import com.atzuche.order.commons.vo.req.*;
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
     * 取消订单(车主/租客取消订单)
     * <p>前端调用，默认走自动判责逻辑</p>
     *
     * @param cancelOrderReqVO 请求参数
     * @return ResponseData<?>
     */
    @PostMapping("/order/normal/cancel")
    public ResponseData<?> cancelOrder(@RequestBody CancelOrderReqVO cancelOrderReqVO);


    /**
     * 取消订单(平台代车主/租客取消订单)
     * <p>管理后台调用，需手动判责</p>
     *
     * @param cancelOrderReqVO 请求参数
     * @return ResponseData<?>
     */
    @PostMapping("/order/admin/cancel")
    public ResponseData<?> adminCancelOrder(@RequestBody AdminCancelOrderReqVO cancelOrderReqVO);


    /**
     * 平台取消
     * <p>管理后台调用</p>
     *
     * @param adminOrderCancelReqVO 请求参数
     * @return ResponseData<?>
     */
    @PostMapping("/order/admin/platform/cancel")
    public ResponseData<?> adminPlatformCancelOrder(@RequestBody AdminOrderPlatformCancelReqVO adminOrderCancelReqVO);


    /**
     * 取消订单责任判定
     * <p>管理后台调用，手动判责</p>
     *
     * @param judgeDutyReqVO 请求参数
     * @return ResponseData<?>
     */
    @PostMapping("/order/admin/judgeDuty")
    public ResponseData<?> adminCancelOrderJudgeDuty(@RequestBody AdminOrderCancelJudgeDutyReqVO judgeDutyReqVO);


    /**
     * 车主同意订单
     *
     * @param reqVO 请求参数
     * @return ResponseData<?>
     */
    @PostMapping("/order/normal/agree")
    public ResponseData<?> adminOwnerAgreeOrder(@RequestBody AgreeOrderReqVO reqVO) ;

    /**
     * 车主拒绝订单
     *
     * @param reqVO 请求参数
     * @return ResponseData<?>
     */
    @PostMapping("/order/normal/refuse")
    public ResponseData<?> adminOwnerRefuseOrder(@RequestBody RefuseOrderReqVO reqVO) ;


    /**
     * 取消订单申诉
     *
     * @param orderCancelAppealReqVO 请求参数
     * @return ResponseData<?>
     */
    @PostMapping("/order/normal/appeal")
    public ResponseData<?> orderCancelAppeal(@RequestBody OrderCancelAppealReqVO orderCancelAppealReqVO);


}
