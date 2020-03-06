package com.atzuche.order.wallet.server.entity;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * balance_deduct_log
 * @author 
 */
@Data
@ToString
public class BalanceDeductLogEntity implements Serializable {
    private Long id;

    private String memNo;

    /**
     * 扣减金额
     */
    private Integer deduct;

    /**
     * 操作结果
     */
    private String result;

    /**
     * 操作时间
     */
    private Date createTime;

    /**
     * 操作人名称
     */
    private String createOp;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 修改人
     */
    private String updateOp;

    /**
     * 0-正常，1-已逻辑删除
     */
    private Byte isDelete;

    private static final long serialVersionUID = 1L;
}