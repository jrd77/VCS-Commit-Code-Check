package com.atzuche.order.admin.vo.resp.car;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 订单详细信息 - 查看更换车辆记录
 */
@Data
@ToString
public class ChangeCarRecordRespVO implements Serializable {
    @AutoDocProperty(value = "更换车辆时间")
    private String changeCarTime;
    @AutoDocProperty(value = "更换车辆方式")
    private String source;
    @AutoDocProperty(value = "更换车辆操作人")
    private String operator;
    @AutoDocProperty(value = "更换车牌")
    private String carPlateNum;
}
