package com.atzuche.order.admin.entity;

import com.autoyol.doc.annotation.AutoDocProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 管理后台操作记录
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/1/7 10:37 上午
 **/
@Data
@ToString
public class AdminOperateLogEntity implements Serializable {

    private Integer id;


    @AutoDocProperty(value = "订单号")
    private String orderNo;
    /**
     * 操作类型，
     */
    @AutoDocProperty(value = "操作类型")
    private int opType;

    @AutoDocProperty(value = "操作类型")
    private String opTypeDesc;

    @AutoDocProperty(value = "操作内容")
    private String desc;

    /**
     * 创建时间
     */
    @AutoDocProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 操作人的id
     */
    private String operatorId;
    /**
     * 操作人的姓名
     */
    @AutoDocProperty(value = "操作人的姓名")
    private String operatorName;

    private String deptId;


}
