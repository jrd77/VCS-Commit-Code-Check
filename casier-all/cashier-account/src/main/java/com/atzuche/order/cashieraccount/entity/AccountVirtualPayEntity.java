package com.atzuche.order.cashieraccount.entity;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * account_virtual_pay
 * @author 
 */
@Data
@ToString
public class AccountVirtualPayEntity implements Serializable {
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
     * 部门负担总成本
     */
    private Integer totalAmt;

    /**
     * 版本号
     */
    private Integer version;

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