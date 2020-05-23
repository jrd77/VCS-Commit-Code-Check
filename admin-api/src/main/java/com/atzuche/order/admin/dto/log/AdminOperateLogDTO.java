package com.atzuche.order.admin.dto.log;

import lombok.Data;

/**
 * @author pengcheng.fu
 * @date 2020/5/11 17:47
 */

@Data
public class AdminOperateLogDTO {

    /**
     * 主订单号
     */
    private String orderNo;
    /**
     * 租客子订单号
     */
    private String renterOrderNo;
    /**
     * 车主子订单号
     */
    private String ownerOrderNo;
    /**
     * 操作人ID
     */
    private String operatorId;
    /**
     * 操作人name
     */
    private String operatorName;
    /**
     * 部门ID
     */
    private Integer deptId;
    /**
     * 部门名称
     */
    private String deptName;
    /**
     * 操作类型
     */
    private Integer opTypeCode;
    /**
     * 操作类型描述
     */
    private String opTypeDesc;
    /**
     * 操作描述
     */
    private String desc;
    /**
     * 操作时间
     */
    private String operateTime;


}
