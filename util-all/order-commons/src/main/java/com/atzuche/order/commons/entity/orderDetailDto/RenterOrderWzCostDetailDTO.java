package com.atzuche.order.commons.entity.orderDetailDto;

import lombok.Data;

/**
 * 违章费用
 *
 * @author pengcheng.fu
 * @date 2020/4/29 10:49
 */

@Data
public class RenterOrderWzCostDetailDTO {
    /**
     * 主键
     */
    private Long id;
    /**
     * 订单号
     */
    private String orderNo;
    /**
     * 车牌
     */
    private String carPlateNum;
    /**
     * 会员号
     */
    private Integer memNo;
    /**
     * 费用编码
     */
    private String costCode;
    /**
     * 费用描述
     */
    private String costDesc;
    /**
     * 总价
     */
    private Integer amount;
    /**
     * 备注信息
     */
    private String note;
    /**
     * 来源信息：1、仁云、2、管理后台
     */
    private String sourceType;
    /**
     * 操作人ID
     */
    private String operatorId;
    /**
     * 操作人名称
     */
    private String operatorName;
    /**
     * 0-正常，1-失效,管理后台修改费用时会造成同类费用上一笔失效
     */
    private Integer costStatus;
    /**
     * 备注
     */
    private String remark;
}
