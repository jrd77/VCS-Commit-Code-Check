package com.atzuche.order.open.service;

import com.atzuche.order.commons.vo.delivery.DeliveryCarRepVO;
import com.atzuche.order.commons.vo.delivery.DeliveryCarVO;
import com.atzuche.order.commons.vo.delivery.DeliveryReqVO;
import com.atzuche.order.commons.vo.delivery.OrderCarTrusteeshipEntity;
import com.atzuche.order.commons.vo.delivery.OrderCarTrusteeshipReqVO;
import com.atzuche.order.commons.vo.delivery.OrderCarTrusteeshipVO;
import com.atzuche.order.commons.vo.delivery.SimpleOrderInfoVO;
import com.atzuche.order.commons.vo.req.DeliveryCarPriceReqVO;
import com.atzuche.order.commons.vo.req.handover.rep.HandoverCarRespVO;
import com.atzuche.order.commons.vo.req.handover.req.HandoverCarInfoReqVO;
import com.atzuche.order.commons.vo.res.delivery.DeliveryOilCostRepVO;
import com.atzuche.order.commons.vo.res.delivery.DistributionCostVO;
import com.atzuche.order.commons.vo.res.delivery.RenterOrderDeliveryRepVO;
import com.autoyol.commons.web.ResponseData;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author 胡春林
 * 配送车服务
 */
@FeignClient(value = "order-center-api", path = "/api/delivery")
@Repository
public interface FeignDeliveryCarInfoService {

    /**
     * 获取油费
     * @param deliveryCarPriceReqVO
     * @return
     */
    @PostMapping("/oil/getOilCrash")
    ResponseData<DeliveryOilCostRepVO> getOilCostByRenterOrderNo(@RequestBody DeliveryCarPriceReqVO deliveryCarPriceReqVO);

    /**
     * 获取配送相关信息
     * @param orderNo
     * @return
     */
    @GetMapping("/delivery/list")
    ResponseData<List<RenterOrderDeliveryRepVO>> findRenterOrderListByOrderNo(@RequestParam("orderNo") String orderNo);

    /**
     * 获取取还车费用
     * @param
     * @return
     */
    @GetMapping("/getAndReturnCarCost")
     ResponseData<DistributionCostVO> findDeliveryCostByOrderNo(@RequestParam("carType") Integer carType);

    /**
     * 设置油耗里程
     * @param handoverCarReqVO
     * @return
     */
    @PostMapping("/oil/setOil")
    ResponseData<?> updateHandoverCarInfo(@RequestBody HandoverCarInfoReqVO handoverCarReqVO);

    /**
     * 获取油耗里程
     * @param orderNo
     * @return
     */
    @GetMapping("/oil/list")
    ResponseData<HandoverCarRespVO> updateHandoverCarInfo(@RequestParam("orderNo") String  orderNo);
    
    /**
     * 获取配送相关信息
     * @param deliveryCarDTO
     */
    @PostMapping("/getDeliveryCarVO")
    public ResponseData<DeliveryCarVO> getDeliveryCarVO(@RequestBody DeliveryCarRepVO deliveryCarDTO);
    
    /**
     * 更新交接车信息
     * @param deliveryCarVO
     */
    @PostMapping("/updateHandoverCarInfo")
    public ResponseData<?> updateHandoverCarInfo(@RequestBody DeliveryCarVO deliveryCarVO);
    
    /**
     * 更新取还车备注信息
     * @param deliveryCarVO
     */
    @PostMapping("/updateDeliveryRemark")
    public ResponseData<?> updateDeliveryRemark(@RequestBody DeliveryReqVO deliveryReqVO);
    
    /**
     * 获取配送相关信息
     * @param deliveryCarDTO
     */
    @PostMapping("/getDistributionCostVO")
    public ResponseData<com.atzuche.order.commons.vo.delivery.DistributionCostVO> getDistributionCostVO(@RequestBody DeliveryCarRepVO deliveryCarDTO);
    
    /**
     * 获取订单简单信息
     * @param orderNo
     */
    @GetMapping("/getSimpleOrderInfoVO")
    public ResponseData<SimpleOrderInfoVO> getSimpleOrderInfoVO(@RequestParam("orderNo") String orderNo);
    
    /**
     * 托管车新增
     * @param orderCarTrusteeshipVO
     */
    @PostMapping("/trusteeship/add")
    public ResponseData<?> addOrderCarTrusteeship(@RequestBody OrderCarTrusteeshipVO orderCarTrusteeshipVO);
    
    /**
     * 获取托管车信息
     * @param deliveryCarVO
     */
    @PostMapping("/trusteeship/get")
    public ResponseData<OrderCarTrusteeshipEntity> getOrderCarTrusteeshipEntity(@RequestBody OrderCarTrusteeshipReqVO orderCarTrusteeshipReqVO);

}
