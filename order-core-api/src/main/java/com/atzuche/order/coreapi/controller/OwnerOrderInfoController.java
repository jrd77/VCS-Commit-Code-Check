package com.atzuche.order.coreapi.controller;

import com.atzuche.order.commons.entity.dto.OwnerGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.OwnerMemberDTO;
import com.atzuche.order.commons.entity.orderDetailDto.OwnerOrderDTO;
import com.atzuche.order.commons.exceptions.OrderNotFoundException;
import com.atzuche.order.owner.commodity.service.OwnerGoodsService;
import com.atzuche.order.owner.mem.service.OwnerMemberService;
import com.atzuche.order.ownercost.entity.OwnerOrderEntity;
import com.atzuche.order.ownercost.service.OwnerOrderService;
import com.autoyol.commons.web.ResponseData;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 胡春林
 * 车主订单相关的接口（主要给sms）
 */
@RestController
@RequestMapping("/api/sms/owner")
public class OwnerOrderInfoController {
    @Autowired
    OwnerMemberService ownerMemberService;
    @Autowired
    OwnerGoodsService ownerGoodsService;
    @Autowired
    OwnerOrderService ownerOrderService;

    /**
     * 获取车主商品信息
     * @param ownerOrderNo
     * @return
     */
    @GetMapping("/goodsDetail")
    public ResponseData<OwnerGoodsDetailDTO> getOwnerGoodsDetail(@RequestParam("ownerOrderNo") String ownerOrderNo) {
        OwnerGoodsDetailDTO ownerGoodsDetailDTO = ownerGoodsService.getOwnerGoodsDetail(ownerOrderNo, false);
        if (ownerGoodsDetailDTO == null) {
            throw new OrderNotFoundException(ownerOrderNo);
        }
        return ResponseData.success(ownerGoodsDetailDTO);
    }

    /**
     * 获取车主会员信息
     * @param ownerOrderNo
     * @return
     */
    @GetMapping("/memberInfo")
    public ResponseData<OwnerMemberDTO> selectownerMemberByOwnerOrderNo(@RequestParam("ownerOrderNo") String ownerOrderNo) {
        OwnerMemberDTO ownerMemberDTO = ownerMemberService.selectownerMemberByOwnerOrderNo(ownerOrderNo, false);
        if (ownerMemberDTO == null) {
            throw new OrderNotFoundException(ownerOrderNo);
        }
        return ResponseData.success(ownerMemberDTO);
    }

    /**
     * 获取车主子订单信息
     * @param orderNo
     * @return
     */
    @GetMapping("/orderInfo")
    public ResponseData<OwnerOrderDTO> getOwnerOrderByOrderNoAndIsEffective(@RequestParam("orderNo") String orderNo) {
        OwnerOrderEntity ownerOrderEntity = ownerOrderService.getOwnerOrderByOrderNoAndIsEffective(orderNo);
        if (ownerOrderEntity == null) {
            throw new OrderNotFoundException(orderNo);
        }
        OwnerOrderDTO ownerOrderDTO = new OwnerOrderDTO();
        BeanUtils.copyProperties(ownerOrderEntity, ownerOrderDTO);
        return ResponseData.success(ownerOrderDTO);
    }


}
