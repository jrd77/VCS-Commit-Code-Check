package com.atzuche.order.open.service;

import com.atzuche.order.commons.entity.dto.ExtraDriverDTO;
import com.atzuche.order.commons.entity.ownerOrderDetail.RenterRentDetailDTO;
import com.atzuche.order.commons.entity.rentCost.RenterCostDetailDTO;
import com.atzuche.order.commons.vo.rentercost.*;
import com.atzuche.order.commons.vo.req.AdminOrderReqVO;
import com.atzuche.order.commons.vo.req.NormalOrderCostCalculateReqVO;
import com.atzuche.order.commons.vo.req.OrderCostReqVO;
import com.atzuche.order.commons.vo.req.RenterAdjustCostReqVO;
import com.atzuche.order.commons.vo.req.consolecost.GetTempCarDepositInfoReqVO;
import com.atzuche.order.commons.vo.req.consolecost.SaveTempCarDepositInfoReqVO;
import com.atzuche.order.commons.vo.res.NormalOrderCostCalculateResVO;
import com.atzuche.order.commons.vo.res.OrderOwnerCostResVO;
import com.atzuche.order.commons.vo.res.OrderRenterCostResVO;
import com.atzuche.order.commons.vo.res.OrderResVO;
import com.atzuche.order.commons.vo.res.consolecost.GetTempCarDepositInfoResVO;
import com.atzuche.order.commons.vo.res.rentcosts.OrderConsoleCostDetailEntity;
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

    /**
     * 获取取还车费用和超运能费用
     * @param req
     */
    @PostMapping("/order/renter/cost/getreturnfee/detail")
	ResponseData<GetReturnAndOverFeeDetailVO> getGetReturnFeeDetail(@RequestBody GetReturnAndOverFeeVO req);

    /**
     * 获取附加驾驶员保障费
     * @param req
     */
    @PostMapping("/order/renter/cost/extraDriverInsure/detail")
	ResponseData<RenterOrderCostDetailEntity> getExtraDriverInsureDetail(@RequestBody ExtraDriverDTO req);

    /**
     * 获取租客补贴
     * @param orderNo
     * @param renterOrderNo
     */
    @GetMapping("/order/renter/cost/renterAndConsoleSubsidy")
	ResponseData<RenterAndConsoleSubsidyVO> getRenterAndConsoleSubsidyVO(@RequestParam("orderNo") String orderNo,@RequestParam("renterOrderNo") String renterOrderNo);

    /**
     * 获取管理后台费用
     * @param orderNo
     */
    @GetMapping("/order/renter/cost/listOrderConsoleCostDetail")
	ResponseData<List<OrderConsoleCostDetailEntity>> listOrderConsoleCostDetailEntity(@RequestParam("orderNo") String orderNo);

    /**
     * 保存调价
     * @param req
     */
    @PostMapping("/order/renter/cost/updateRenterPriceAdjustmentByOrderNo")
	ResponseData<?> updateRenterPriceAdjustmentByOrderNo(@RequestBody RenterAdjustCostReqVO req);

    /**
     * 租客需支付给平台的费用 修改
     * @param req
     */
    @PostMapping("/order/renter/cost/updateRenterToPlatFormListByOrderNo")
	ResponseData<?> updateRenterToPlatFormListByOrderNo(@RequestBody RenterToPlatformCostReqVO req);

    /**
     * 添加，车主需支付给平台的费用
     * @param req
     */
    @PostMapping("/order/owner/cost/updateOwnerToPlatFormListByOrderNo")
	public ResponseData<?> updateOwnerToPlatFormListByOrderNo(@RequestBody OwnerToPlatformCostReqVO req);

    /**
     * 租客租金明细
     * @param req
     */
    @PostMapping("/order/renter/cost/findRenterRentAmtListByOrderNo")
	ResponseData<RenterRentDetailDTO> findRenterRentAmtListByOrderNo(@RequestBody RenterCostReqVO req);

    /**
     * 获取租客罚金
     * @param orderNo
     * @param renterOrderNo
     */
    @GetMapping("/order/renter/cost/renterAndConsoleFine")
	ResponseData<RenterAndConsoleFineVO> getRenterAndConsoleFineVO(@RequestParam("orderNo") String orderNo,@RequestParam("renterOrderNo") String renterOrderNo);

    /**
     * 违约罚金 修改违约罚金
     * @param req
     */
    @PostMapping("/order/renterowner/cost/updatefineAmtListByOrderNo")
    ResponseData<?> updatefineAmtListByOrderNo(@RequestBody RenterFineCostReqVO req);

    /**
     * 平台给租客的补贴
     * @param req
     */
    @PostMapping("/order/renter/cost/updatePlatFormToRenterListByOrderNo")
	ResponseData<?> updatePlatFormToRenterListByOrderNo(@RequestBody PlatformToRenterSubsidyReqVO req);

    /**
     * 租金补贴
     * @param req
     */
    @PostMapping("/order/renter/cost/ownerToRenterRentAmtSubsidy")
	ResponseData<?> ownerToRenterRentAmtSubsidy(@RequestBody OwnerToRenterSubsidyReqVO req);


    /**
     * 平台给车主的补贴
     * @param req
     */
    @PostMapping("/order/renter/cost/updatePlatFormToOwnerListByOrderNo")
	ResponseData<?> updatePlatFormToOwnerListByOrderNo(@RequestBody PlatformToOwnerSubsidyReqVO req);

}
