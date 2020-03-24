package com.atzuche.order.commons.entity.orderDetailDto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * 订单取消原因
 * 
 * @author ZhangBin
 * @date 2020-01-08 20:41:54
 * @Description:
 */
@Data
public class OrderCancelReasonDTO implements Serializable {
	private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Integer id;
    /**
     * 主订单号
     */
    private String orderNo;

    /**
     * 租客订单号(与取消方保持一致)
     */
    private String renterOrderNo;

    /**
     * 车主订单号
     */
    private String ownerOrderNo;

    /**
     * 操作类型:1.取消订单 2.拒单
     */
    private String operateType;

    /**
     * 取消方 1-车主、2-租客、3-平台
     */
    private Integer cancelSource;
    /**
     * 1：租客责任，2：车主责任，；6. 双方无责、平台承担保险。
     */
    private Integer dutySource;
    /**
     * 取消原因
     */
    private String cancelReason;
    /**
     * 是否申诉:0,否 1,是
     */
    private Integer appealFlag;

    /**
     * 取消订单时间
     */
    private LocalDateTime cancelReqTime;

    /**
     * 违约罚金
     */
    private Integer fineAmt;

    /**
     * 保险罚金
     */
    private Integer insuranceFineAmt;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 创建人
     */
    private String createOp;

}
