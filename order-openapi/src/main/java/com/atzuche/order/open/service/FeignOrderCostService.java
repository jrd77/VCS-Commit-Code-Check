package com.atzuche.order.open.service;

import com.atzuche.order.commons.entity.orderDetailDto.RenterOrderWzCostDetailDTO;
import com.atzuche.order.commons.entity.rentCost.RenterCostDetailDTO;
import com.atzuche.order.commons.vo.req.AdminOrderReqVO;
import com.atzuche.order.commons.vo.req.NormalOrderCostCalculateReqVO;
import com.atzuche.order.commons.vo.req.OrderCostReqVO;
import com.atzuche.order.commons.vo.req.consolecost.GetTempCarDepositInfoReqVO;
import com.atzuche.order.commons.vo.req.consolecost.SaveTempCarDepositInfoReqVO;
import com.atzuche.order.commons.vo.res.NormalOrderCostCalculateResVO;
import com.atzuche.order.commons.vo.res.OrderOwnerCostResVO;
import com.atzuche.order.commons.vo.res.OrderRenterCostResVO;
import com.atzuche.order.commons.vo.res.OrderResVO;
import com.atzuche.order.commons.vo.res.consolecost.GetTempCarDepositInfoResVO;
import com.autoyol.commons.web.ResponseData;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.List;

/**
 * @author jing.huang
 *
 */
//@FeignClient(url = "http://localhost:1412" ,name="order-center-api")  //本地测试
//@FeignClient(url = "http://10.0.3.235:1412" ,name="order-center-api")
@FeignClient(name = "order-center-api")
public interface FeignOrderCostService {

    /**
     * 租客子订单费用详细
     *
     * @param req 请求参数
     * @return ResponseData<OrderRenterCostResVO>
     */
    @PostMapping("/order/cost/renter/get")
    ResponseData<OrderRenterCostResVO> orderCostRenterGet(@RequestBody OrderCostReqVO req);

    /**
     * 车主子订单费用详细
     *
     * @param req 请求参数
     * @return ResponseData<OrderOwnerCostResVO>
     */
    @PostMapping("/order/cost/owner/get")
    ResponseData<OrderOwnerCostResVO> orderCostOwnerGet(@RequestBody OrderCostReqVO req);

    /**
     * 下单前费用计算
     *
     * @param reqVO 请求参数
     * @return ResponseData<NormalOrderCostCalculateResVO>
     */
    @PostMapping("/order/normal/pre/cost/calculate")
    ResponseData<NormalOrderCostCalculateResVO> submitOrderBeforeCostCalculate(@Valid @RequestBody NormalOrderCostCalculateReqVO reqVO);

    /**
     * 下单
     *
     * @param adminOrderReqVO 请求参数
     * @return ResponseData<OrderResVO>
     */
    @PostMapping("/order/admin/req")
    ResponseData<OrderResVO> submitOrder(@RequestBody AdminOrderReqVO adminOrderReqVO);

    /**
     * 租客订单费用明细
     *
     * @param orderNo 订单号
     * @return ResponseData<RenterCostDetailDTO>
     */
    @GetMapping("/order/renter/cost/detail")
    ResponseData<RenterCostDetailDTO> renterCostDetail(@RequestParam("orderNo") String orderNo);

    /**
     * 获取订单车辆押金暂扣扣款明细接口
     *
     * @param reqVO 请求参数
     * @return ResponseData<GetTempCarDepositInfoResVO>
     */
    @PostMapping("/order/temp/get/deposit")
    ResponseData<GetTempCarDepositInfoResVO> getTempCarDepoists(@RequestBody GetTempCarDepositInfoReqVO reqVO);

    /**
     * 保存订单车辆押金暂扣扣款信息接口
     *
     * @param reqVO 请求参数
     * @return ResponseData
     */
    @PostMapping("/order/temp/save/depoist")
    ResponseData saveTempCarDepoist(@RequestBody SaveTempCarDepositInfoReqVO reqVO);

}
