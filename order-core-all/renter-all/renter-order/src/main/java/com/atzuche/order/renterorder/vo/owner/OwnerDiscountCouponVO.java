package com.atzuche.order.renterorder.vo.owner;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 车主优惠券信息
 *
 * @author pengcheng.fu
 * @date 2019/12/25 14:20
 */
@Data
public class OwnerDiscountCouponVO implements Serializable {

    private static final long serialVersionUID = 2201136204690443787L;

    /**
     * 优惠券码
     */
    private String couponNo;
    /**
     * 优惠券批次号
     */
    private String batchNo;
    /**
     * 车主会员编号
     */
    private Long ownerMemNo;
    /**
     * 券类型
     */
    private Integer couponType;
    /**
     * 条件金额
     */
    private Integer condAmount;
    /**
     * 抵扣金额
     */
    private Integer discount;
    /**
     * 有效开始时间(yyyy.MM.dd HH:mm)
     */
    private String validBeginTime;
    /**
     * 有效结束时间(yyyy.MM.dd HH:mm)
     */
    private String validEndTime;
    /**
     * 适用车辆
     */
    private List<OwnerCouponSuitCarVO> carDTOList;
    /**
     * 优惠券类型 例如:车主优惠券
     */
    private String couponName;
    /**
     * 优惠券适用条件 例如: 满500可用
     */
    private String abstractTxt;
    /**
     * 优惠券适用场景 例如: 仅抵扣租金
     */
    private String detail;
    /**
     * 是否过期：0是,1否
     */
    private Integer overdue;
    /**
     * 是否选中
     */
    private Boolean select;
    /**
     * 优惠券使用规则
     */
    private String couponText;


}
