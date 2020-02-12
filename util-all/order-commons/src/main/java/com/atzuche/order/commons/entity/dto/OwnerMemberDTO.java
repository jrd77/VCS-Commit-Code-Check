package com.atzuche.order.commons.entity.dto;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

import java.util.List;

@Data
public class OwnerMemberDTO {
	/**
	 * 主订单号
	 */
	@AutoDocProperty("主订单号")
	private String orderNo;
	/**
	 * 子订单号
	 */
	@AutoDocProperty("子订单号")
	private String ownerOrderNo;
    /**
     * 会员号
     */
    @AutoDocProperty("会员号")
    private String memNo;
    /**
     * 会员类型
     */
    @AutoDocProperty("会员类型")
    private Integer memType;
    /**
     * 手机号
     */
    @AutoDocProperty("手机号")
    private String phone;
    /**
     * 头像
     */
    @AutoDocProperty("头像")
    private String headerUrl;
    /**
     * 真实姓名
     */
    @AutoDocProperty("真实姓名")
    private String realName;
    /**
     * 昵称
     */
    @AutoDocProperty("昵称")
    private String nickName;

    /**
     * 成功下单次数
     */
    @AutoDocProperty("成功下单次数")
    private Integer orderSuccessCount;

    /**
     * 平台上架车辆数
     */
    @AutoDocProperty(value = "平台上架车辆数。")
    private Integer haveCar;

    /**
     * 身份证号码
     */
    @AutoDocProperty("身份证号码")
    private String idNo;

    private List<OwnerMemberRightDTO> ownerMemberRightDTOList;
}
