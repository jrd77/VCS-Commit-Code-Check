package com.atzuche.order.cashieraccount.entity;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * account_virtual_pay_detail
 * @author 
 */
@Data
@ToString
public class AccountVirtualPayDetailEntity implements Serializable {
    private Integer id;

    /**
     * 账号编码
     */
    private String accountNo;

    /**
     * 账号名称
     */
    private String accountName;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 支付金额
     */
    private Integer amt;

    /**
     * 操作类型:0-支付,-1退款
     */
    private Integer payType;

    /**
     * 支付类型 1-租金，2-押金，3-违章押金
     */
    private Integer payCashType;

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