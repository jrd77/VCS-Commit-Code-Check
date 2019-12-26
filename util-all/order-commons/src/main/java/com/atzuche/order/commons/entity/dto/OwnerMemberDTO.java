package com.atzuche.order.commons.entity.dto;

import lombok.Data;

import java.util.List;

@Data
public class OwnerMemberDTO {
    /**
     * 会员号
     */
    private String memNo;
    /**
     * 会员类型
     */
    private Integer memType;
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
     * 成功下单次数
     */
    private Integer orderSuccessCount;

    private List<OwnerMemberRightDTO> ownerMemberRightDTOList;
}
