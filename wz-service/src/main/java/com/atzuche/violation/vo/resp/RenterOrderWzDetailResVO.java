package com.atzuche.violation.vo.resp;

import com.alibaba.fastjson.annotation.JSONField;
import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * @author 胡春林
 */
@Data
@ToString
public class RenterOrderWzDetailResVO {
    @AutoDocProperty(value = "id")
    private Long id;
    @AutoDocProperty(value = "违章时间")
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date illegalTime;
    @AutoDocProperty(value = "违章地点")
    private String illegalAddr;
    @AutoDocProperty(value = "违章原因")
    private String illegalReason;
    @AutoDocProperty(value = "违章罚款")
    private String illegalAmt;
    @AutoDocProperty(value = "违章扣分")
    private String illegalDeduct;
    @AutoDocProperty(value = "协助违章处理费")
    private Integer illegalFine;
    @AutoDocProperty(value = "不良用车处罚金")
    private Integer illegalDysFine;
    @AutoDocProperty(value = "凹凸代办服务费")
    private Integer illegalServiceCost;
    @AutoDocProperty(value = "违章超证费")
    private Integer illegalSupercerCost;
}
