package com.atzuche.order.admin.vo.resp.car;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

@Data
public class CarDepositReturnDetailResVO implements java.io.Serializable{
    @AutoDocProperty("操作时间")
    private String dateStr;
    @AutoDocProperty("操作人")
    private String operator;
    @AutoDocProperty("返还金额")
    private Integer monty;
    @AutoDocProperty("返还原因")
    private String remark;
}
