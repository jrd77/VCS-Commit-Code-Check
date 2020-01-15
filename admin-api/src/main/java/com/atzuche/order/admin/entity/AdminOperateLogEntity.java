package com.atzuche.order.admin.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 管理后台操作记录
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/1/7 10:37 上午
 **/
public class AdminOperateLogEntity implements Serializable {

    private Integer id;


    private String orderNo;
    /**
     * 操作类型，分成两类，操作订单的时间和地址一类
     */
    private String opType;


    private String opSubType;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 操作人的id
     */
    private String operatorId;
    /**
     * 操作人的姓名
     */
    private String operatorName;


}
