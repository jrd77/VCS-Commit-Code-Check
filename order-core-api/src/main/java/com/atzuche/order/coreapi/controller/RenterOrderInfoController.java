package com.atzuche.order.coreapi.controller;

import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.RenterMemberDTO;
import com.atzuche.order.commons.entity.orderDetailDto.RenterOrderDTO;
import com.atzuche.order.commons.exceptions.OrderNotFoundException;
import com.atzuche.order.rentercommodity.service.RenterGoodsService;
import com.atzuche.order.rentermem.service.RenterMemberService;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.service.RenterOrderService;
import com.autoyol.commons.web.ResponseData;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
