package com.atzuche.order.rentermem.entity.dto;

import com.atzuche.order.commons.entity.dto.RenterMemberRightDTO;
import lombok.Data;

import java.util.List;

@Data
public class MemRightCarDepositAmtReqDTO {
    /**
     * 车辆指导价
     */
    private Integer guidPrice;
    /**
     * 原始车辆押金
     */
    private Integer originalDepositAmt;
    /**
     * 会员权益列表
     */
    private List<RenterMemberRightDTO> renterMemberRightDTOList;
    /**
     * 订单类型
     */
    private String orderCategory;
}
