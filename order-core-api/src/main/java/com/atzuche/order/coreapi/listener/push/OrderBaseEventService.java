package com.atzuche.order.coreapi.listener.push;

import com.atzuche.order.commons.entity.dto.OwnerGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.OwnerMemberDTO;
import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.RenterMemberDTO;
import com.atzuche.order.mq.common.sms.ShortMessageSendService;
import com.atzuche.order.owner.commodity.service.OwnerGoodsService;
import com.atzuche.order.owner.mem.service.OwnerMemberService;
import com.atzuche.order.ownercost.entity.OwnerOrderEntity;
import com.atzuche.order.ownercost.service.OwnerOrderService;
import com.atzuche.order.rentercommodity.service.RenterGoodsService;
import com.atzuche.order.rentermem.service.RenterMemberService;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.service.RenterOrderService;
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
    RenterMemberService renterMemberService;
    @Autowired
    OwnerMemberService ownerMemberService;
    @Autowired
    RenterGoodsService renterGoodsService;
    @Autowired
    OwnerGoodsService ownerGoodsService;
    @Autowired
    RenterOrderService renterOrderService;
    @Autowired
    OwnerOrderService ownerOrderService;

    /**
     * 发送短信/push数据
     * @param smsParamsMap
     */
    public void sendShortMessage(Map smsParamsMap) {
        String orderNo = String.valueOf(smsParamsMap.get("orderNo"));
        //根据订单号查询相关参数数据
        if (StringUtils.isNotBlank(String.valueOf(smsParamsMap.get("renterFlag")))) {
            //租客消息模版
            RenterOrderEntity renterOrderEntity = renterOrderService.getRenterOrderByOrderNoAndIsEffective(orderNo);
            RenterMemberDTO renterMemberDTO = renterMemberService.selectrenterMemberByRenterOrderNo(renterOrderEntity.getRenterOrderNo(), false);
            RenterGoodsDetailDTO renterGoodsDetailDTO = renterGoodsService.getRenterGoodsDetail(renterOrderEntity.getRenterOrderNo(), false);
            String textCode = String.valueOf(smsParamsMap.get("renterFlag"));
            sendShortMessageData(textCode, smsParamsMap, renterMemberDTO.getPhone(), renterOrderEntity, renterMemberDTO, renterGoodsDetailDTO);

        }
        if (StringUtils.isNotBlank(String.valueOf(smsParamsMap.get("ownerFlag")))) {
            //车主消息模版
            OwnerOrderEntity ownerOrderEntity = ownerOrderService.getOwnerOrderByOrderNoAndIsEffective(orderNo);
            OwnerMemberDTO ownerMemberDTO = ownerMemberService.selectownerMemberByOwnerOrderNo(ownerOrderEntity.getOwnerOrderNo(), false);
            OwnerGoodsDetailDTO ownerGoodsDetailDTO = ownerGoodsService.getOwnerGoodsDetail(ownerOrderEntity.getOwnerOrderNo(), false);
            String textCode = String.valueOf(smsParamsMap.get("ownerFlag"));
            sendShortMessageData(textCode, smsParamsMap, ownerMemberDTO.getPhone(), ownerOrderEntity, ownerMemberDTO, ownerGoodsDetailDTO);
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
