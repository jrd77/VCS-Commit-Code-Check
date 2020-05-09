package com.atzuche.order.coreapi.controller;

import com.atzuche.order.cashieraccount.entity.CashierEntity;
import com.atzuche.order.cashieraccount.mapper.CashierMapper;
import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.RenterMemberDTO;
import com.atzuche.order.commons.entity.orderDetailDto.CashierDTO;
import com.atzuche.order.commons.entity.orderDetailDto.OrderStatusDTO;
import com.atzuche.order.commons.entity.orderDetailDto.RenterOrderCostDetailDTO;
import com.atzuche.order.commons.entity.orderDetailDto.RenterOrderDTO;
import com.atzuche.order.commons.exceptions.OrderNotFoundException;
import com.atzuche.order.parentorder.entity.OrderStatusEntity;
import com.atzuche.order.parentorder.service.OrderStatusService;
import com.atzuche.order.rentercommodity.service.RenterGoodsService;
import com.atzuche.order.rentercost.entity.RenterOrderCostDetailEntity;
import com.atzuche.order.rentercost.mapper.RenterOrderCostDetailMapper;
import com.atzuche.order.rentermem.service.RenterMemberService;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.service.RenterOrderService;
import com.autoyol.autopay.gateway.constant.DataPayKindConstant;
import com.autoyol.autopay.gateway.constant.DataPayTypeConstant;
import com.autoyol.commons.web.ResponseData;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 胡春林
 * 租客订单相关的接口（主要给sms）
 */
@RestController
@RequestMapping("/api/sms/renter")
public class RenterOrderInfoController {

    @Autowired
    RenterGoodsService renterGoodsService;
    @Autowired
    RenterMemberService renterMemberService;
    @Autowired
    RenterOrderService renterOrderService;
    @Autowired
    OrderStatusService orderStatusService;
    @Resource
    CashierMapper cashierMapper;
    @Resource
    RenterOrderCostDetailMapper renterOrderCostDetailMapper;

    /**
     * 获取租客商品信息
     * @param renterOrderNo
     * @return
     */
    @GetMapping("/goodsDetail")
    public ResponseData<RenterGoodsDetailDTO> getRenterGoodsDetail(@RequestParam("renterOrderNo") String renterOrderNo) {
        RenterGoodsDetailDTO renterGoodsDetailDTO = renterGoodsService.getRenterGoodsDetail(renterOrderNo, false);
        if (renterGoodsDetailDTO == null) {
            throw new OrderNotFoundException(renterOrderNo);
        }
        return ResponseData.success(renterGoodsDetailDTO);
    }

    /**
     * 获取租客会员信息
     * @param renterOrderNo
     * @return
     */
    @GetMapping("/memberInfo")
    public ResponseData<RenterMemberDTO> selectrenterMemberByRenterOrderNo(@RequestParam("renterOrderNo") String renterOrderNo) {
        RenterMemberDTO renterMemberDTO = renterMemberService.selectrenterMemberByRenterOrderNo(renterOrderNo, false);
        if (renterMemberDTO == null) {
            throw new OrderNotFoundException(renterOrderNo);
        }
        return ResponseData.success(renterMemberDTO);
    }

    /**
     * 获取租客子订单信息
     * @param orderNo
     * @return
     */
    @GetMapping("/orderInfo")
    public ResponseData<RenterOrderDTO> getRenterOrderByOrderNoAndIsEffective(@RequestParam("orderNo") String orderNo) {
        RenterOrderEntity renterOrderEntity = renterOrderService.getRenterOrderByOrderNoAndIsEffective(orderNo);
        if (renterOrderEntity == null) {
            throw new OrderNotFoundException(orderNo);
        }
        RenterOrderDTO renterOrderDTO = new RenterOrderDTO();
        BeanUtils.copyProperties(renterOrderEntity, renterOrderDTO);
        return ResponseData.success(renterOrderDTO);
    }

    /**
     * 获取订单状态
     * @param orderNo
     * @return
     */
    @GetMapping("/getByOrderNo")
    public ResponseData<OrderStatusDTO> getByOrderNo(@RequestParam("orderNo") String orderNo) {
        OrderStatusEntity orderStatusEntity = orderStatusService.getByOrderNo(orderNo);
        if (orderStatusEntity == null) {
            throw new OrderNotFoundException(orderNo);
        }
        OrderStatusDTO orderStatusDTO = new OrderStatusDTO();
        BeanUtils.copyProperties(orderStatusEntity, orderStatusDTO);
        return ResponseData.success(orderStatusDTO);
    }


    /**
     * 获取收银表数据
     * @param orderNo
     * @param memNo
     * @param amount
     * @param payPur
     * @return
     */
    @GetMapping("/getCashier")
    public ResponseData<CashierDTO> getCashier(@RequestParam("orderNo") String orderNo, @RequestParam("memNo") String memNo, @RequestParam("amount") String amount, @RequestParam("payPur") String payPur) {
        CashierEntity cashierEntity = cashierMapper.getPayDeposit(orderNo, memNo, amount, payPur);
        if (cashierEntity == null) {
            return ResponseData.success();
        }
        CashierDTO cashierDTO = new CashierDTO();
        BeanUtils.copyProperties(cashierEntity, cashierDTO);
        return ResponseData.success(cashierDTO);
    }

    @GetMapping("/getListRenterOrderCostDetail")
    public ResponseData<List<RenterOrderCostDetailDTO>> listRenterOrderCostDetail(@RequestParam("orderNo") String orderNo) {
        RenterOrderEntity renterOrderEntity = renterOrderService.getRenterOrderByOrderNoAndIsEffective(orderNo);
        if (renterOrderEntity == null) {
            throw new OrderNotFoundException(orderNo);
        }
        List<RenterOrderCostDetailEntity> list = renterOrderCostDetailMapper.listRenterOrderCostDetail(orderNo, renterOrderEntity.getRenterOrderNo());
        if (CollectionUtils.isEmpty(list)) {
            throw new OrderNotFoundException(orderNo);
        }
        List renterOrderDetailList = Lists.newArrayList();
        for (RenterOrderCostDetailEntity renterOrderCostDetailEntity : list) {
            RenterOrderCostDetailDTO renterOrderCostDetailDTO = new RenterOrderCostDetailDTO();
            BeanUtils.copyProperties(renterOrderCostDetailEntity, renterOrderCostDetailDTO);
            renterOrderDetailList.add(renterOrderCostDetailDTO);
        }
        return ResponseData.success(renterOrderDetailList);
    }
}
