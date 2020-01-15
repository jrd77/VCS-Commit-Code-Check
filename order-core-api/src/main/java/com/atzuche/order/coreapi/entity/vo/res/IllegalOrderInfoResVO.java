package com.atzuche.order.coreapi.entity.vo.res;

import lombok.Data;

import java.util.Date;

/**
 * IllegalOrderInfoResVO
 *
 * @author shisong
 * @date 2020/1/14
 */
@Data
public class IllegalOrderInfoResVO {
    private static final long serialVersionUID = -6500250884917719041L;
    /**
     * 订单号
     */
    private String orderNo;
    /**
     * 剩余租金退还时间
     */
    private Date wzAmtReturnTime;
    /**
     * 违章处理办理完成时间
     */
    private Date wzHandleCompleteTime;
    /**
     * 违章查询:1未查询、2查询失败、3已查询-无违章、4已查询-有违章、5历史数据
     */
    private String illegalQuery;
    /**
     * 违章处理状态 5:未处理，10(处理中-租客处理)，20(已处理-无违章)，25(已处理-租客处理)，26(已处理-车主处理)，35(已处理-异常订单)，40(已处理-平台处理)，45(已处理-无信息)
     */
    private String wzDisposeStatus;
    /**
     * 违章扣款金额
     */
    private String wzFine;
    /**
     * 其他扣款金额
     */
    private String otherDeductionAmt;
    /**
     * 保险理赔金额
     */
    private String insuranceClaimAmt;
    /**
     * 凹凸代办服务费
     */
    private String wzServiceCost;
    /**
     * 不良用车处罚金
     */
    private String wzDysFine;
    /**
     * 停运费
     */
    private String wzOffStreamCost;
    /**
     * 租车人姓名
     */
    private String renterName;
    /**
     * 租客
     */
    private String renterNo;
    /**
     * 租客手机号
     */
    private String renterPhone;
    /**
     * 车主
     */
    private String ownerNo;
    /**
     * 车主手机号
     */
    private String ownerPhone;
    /**
     * 真实还车时间
     */
    private String realRevertTime;
    /**
     * 还车时间
     */
    private String revertTime;
    /**
     * 真实取车时间
     */
    private String realRentTime;
    /**
     * 取车时间
     */
    private String rentTime;
    /**
     * 订单状态
     */
    private Integer status;
    /**
     * 车辆年检标志到期日期
     */
    private Date inspectExpire;
    /**
     * 订单来源，6为携程订单
     */
    private String source;
    /**
     * 车辆编号
     */
    private String carNo;
    /**
     * 车辆品牌
     */
    private String carBrand;
    /**
     * 车型
     */
    private String carType;
    /**
     * 车牌
     */
    private String carPlateNum;

}
