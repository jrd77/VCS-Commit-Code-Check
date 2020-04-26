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


    private Integer id;

    /**
     * 订单号
     */
    private String orderNo;
    /**
     * 操作类型，
     */
    private int opType;

    /**
     * 操作类型描述
     */
    private String opTypeDesc;

    /**
     * 操作内容
     */
    private String desc;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 操作人ID
     */
    private String operatorId;

    /**
     * 操作人
     */
    private String operatorName;

    /**
     * 部门ID
     */
    private String deptId;


}
