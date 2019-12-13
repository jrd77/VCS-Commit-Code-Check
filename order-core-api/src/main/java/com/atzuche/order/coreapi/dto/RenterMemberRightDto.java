package com.atzuche.order.coreapi.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RenterMemberRightDto {
    /**
     * 权益编码
     */
    private String rightCode;
    /**
     * 权益名称（会员等级、是否内部员工、vip等）
     */
    private String rightName;
    /**
     * 权益值
     */
    private String rightValue;
    /**
     * 权益描述
     */
    private String rightDesc;
}
