package com.atzuche.order.coreapi.entity.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RenterMemberDto {
    /**
     * 主订单号
     */
    private Integer orderNo;
    /**
     * 子订单号
     */
    private String renterOrderNo;
    /**
     * 会员号
     */
    private Integer memNo;
    /**
     * 手机号
     */
    private String phone;
    /**
     * 头像
     */
    private String headerUrl;
    /**
     * 真实姓名
     */
    private String realName;
    /**
     * 昵称
     */
    private String nickName;
    /**
     * 驾驶证初次领证日期
     */
    private LocalDateTime certificationTime;
    /**
     * 成功下单次数
     */
    private Integer orderSuccessCount;

    private List<RenterMemberRightDto> renterMemberRightDtoList;
}
