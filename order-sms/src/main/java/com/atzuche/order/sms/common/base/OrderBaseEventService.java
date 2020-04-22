package com.atzuche.order.sms.common.base;

import com.atzuche.order.commons.entity.dto.OwnerGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.OwnerMemberDTO;
import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.RenterMemberDTO;
import com.atzuche.order.commons.entity.orderDetailDto.OwnerOrderDTO;
import com.atzuche.order.commons.entity.orderDetailDto.RenterOrderDTO;
import com.atzuche.order.open.service.FeignSMSOwnerOrderService;
import com.atzuche.order.open.service.FeignSMSRenterOrderService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author 胡春林
 * 基础service
 */
@Component
public abstract class OrderBaseEventService {
    @Autowired
    FeignSMSOwnerOrderService smsOwnerOrderService;
    @Autowired
    FeignSMSRenterOrderService smsRenterOrderService;

    /**
     * 发送短信/push数据
     * @param smsParamsMap
     */
    public void sendShortMessage(Map smsParamsMap) {
        String orderNo = String.valueOf(smsParamsMap.get("orderNo"));
        //根据订单号查询相关参数数据
        if (StringUtils.isNotBlank(String.valueOf(smsParamsMap.get("renterFlag")))) {
            //租客消息模版
            RenterOrderDTO renterOrderDTO = smsRenterOrderService.getRenterOrderByOrderNoAndIsEffective(orderNo).getData();
            RenterMemberDTO renterMemberDTO = smsRenterOrderService.selectrenterMemberByRenterOrderNo(renterOrderDTO.getRenterOrderNo()).getData();
            RenterGoodsDetailDTO renterGoodsDetailDTO = smsRenterOrderService.getRenterGoodsDetail(renterOrderDTO.getRenterOrderNo()).getData();
            String textCode = String.valueOf(smsParamsMap.get("renterFlag"));
            sendShortMessageData(textCode, smsParamsMap, renterMemberDTO.getPhone(), renterOrderDTO, renterMemberDTO, renterGoodsDetailDTO);

        }
        if (StringUtils.isNotBlank(String.valueOf(smsParamsMap.get("ownerFlag")))) {
            //车主消息模版
            OwnerOrderDTO ownerOrderDTO = smsOwnerOrderService.getOwnerOrderByOrderNoAndIsEffective(orderNo).getData();
            OwnerMemberDTO ownerMemberDTO = smsOwnerOrderService.selectownerMemberByOwnerOrderNo(ownerOrderDTO.getOwnerOrderNo()).getData();
            OwnerGoodsDetailDTO ownerGoodsDetailDTO = smsOwnerOrderService.getOwnerGoodsDetail(ownerOrderDTO.getOwnerOrderNo()).getData();
            String textCode = String.valueOf(smsParamsMap.get("ownerFlag"));
            sendShortMessageData(textCode, smsParamsMap, ownerMemberDTO.getPhone(), ownerOrderDTO, ownerMemberDTO, ownerGoodsDetailDTO);
        }
    }

    /**
     * 发送短信/push数据
     * @param textCode
     * @param smsParamsMap
     * @param phone
     * @param orderEntity
     * @param memberDTO
     * @param goodsDetailDTO
     */
    public abstract void sendShortMessageData(String textCode,Map smsParamsMap, String phone, Object orderEntity, Object memberDTO, Object goodsDetailDTO);

}
