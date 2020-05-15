package com.atzuche.order.open.service;

import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.RenterMemberDTO;
import com.atzuche.order.commons.entity.orderDetailDto.CashierDTO;
import com.atzuche.order.commons.entity.orderDetailDto.OrderStatusDTO;
import com.atzuche.order.commons.entity.orderDetailDto.RenterOrderCostDetailDTO;
import com.atzuche.order.commons.entity.orderDetailDto.RenterOrderDTO;
import com.autoyol.commons.web.ResponseData;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author 胡春林
 * sms短信需要的租客feign接口
 */
@FeignClient(value="order-center-api",path ="/api/sms/renter")
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

    /**
     * 获取订单状态
     * @param orderNo
     * @return
     */
    @GetMapping("/getByOrderNo")
    ResponseData<OrderStatusDTO> getByOrderNo(@RequestParam("orderNo") String orderNo);

    /**
     * 获取收银数据
     * @param orderNo
     * @param memNo
     * @param amount
     * @param payPur
     * @return
     */
    @GetMapping("/getCashier")
    ResponseData<CashierDTO> getCashier(@RequestParam("orderNo") String orderNo, @RequestParam("memNo") String memNo, @RequestParam("amount") String amount, @RequestParam("payPur") String payPur);

    /**
     * 获取明细
     * @param orderNo
     * @return
     */
    @GetMapping("/getListRenterOrderCostDetail")
    ResponseData<List<RenterOrderCostDetailDTO>> listRenterOrderCostDetail(@RequestParam("orderNo") String orderNo);

}
