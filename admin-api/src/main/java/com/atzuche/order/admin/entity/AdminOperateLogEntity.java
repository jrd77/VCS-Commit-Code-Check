package com.atzuche.order.admin.entity;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 管理后台操作记录
 *
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/1/7 10:37 上午
 **/
@Data
@ToString
public class AdminOperateLogEntity implements Serializable {

    private static final long serialVersionUID = 3048425445744873323L;

    /**
     *
     */
    private Integer id;
    /**
     * 主订单号
     */
    private String orderNo;
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
    private LocalDateTime createTime;
    /**
     * 操作人名称
     */
    private String createOp;
    /**
     * 修改时间
     */
    private LocalDateTime updateTime;
    /**
     * 修改人
     */
    private String updateOp;
    /**
     * 0-正常，1-已逻辑删除
     */
    private Integer isDelete;
    /**
     * 租客子订单号
     */
    private String renterOrderNo;
    /**
     * 车主子订单号
     */
    private String ownerOrderNo;

}
