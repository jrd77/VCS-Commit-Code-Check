package com.atzuche.order.ownercost.entity;

import lombok.Data;

/*****
 * 租客给车主、车主给租客的调价极其原因
 */
@Data
public class OrderPriceAdjustmentEntity {

    /**
     * 主键
     */
    private Long id;

    /**
     * 主订单ID
     */
    private String orderNo;

    /**车主Code*/
    private java.lang.String ownerMemberCode;

    /**租客code*/
    private java.lang.String renterMemberCode;

    /**
     * 费用
     */
    private Integer amt;

    /**
     * 1是车主给租客的补贴；2是租客给车主的补贴
     */
    private Integer type;

    /**
     * 补贴说明
     */
    private String remark;

    /**
     * 创建时间
     */
    private java.util.Date createTime;

    /**
     * 创建人员
     */
    private String creator;

    /**
     * 修改人员
     */
    private String updator;

    /**
     * 修改时间
     */
    private java.util.Date updateTime;

    /**
     * 是否删除：1是删除；0是未删除
     */
    private Integer isDelete;
}

