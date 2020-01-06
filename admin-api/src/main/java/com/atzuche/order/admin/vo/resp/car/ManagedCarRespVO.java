package com.atzuche.order.admin.vo.resp.car;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 托管车出入库初始化响应信息
 */
@Data
@ToString
public class ManagedCarRespVO implements Serializable {
    @AutoDocProperty(value = "车辆号")
    private String carNo;
    @AutoDocProperty(value = "车牌号")
    private String carPlateNum;
    @AutoDocProperty(value = "车管家姓名")
    private String carStewardName;
    @AutoDocProperty(value = "车管家手机号")
    private String carStewardPhone;

    @AutoDocProperty(value = "出库时间")
    private String outDepotTime;
    @AutoDocProperty(value = "入库时间")
    private String inDepotTime;

    @AutoDocProperty(value = "出库里程数")
    private String outDepotMileage;
    @AutoDocProperty(value = "入库里程数")
    private String inDepotMileage;

    @AutoDocProperty(value = "已保养里程数")
    private String maintainMileage;
    @AutoDocProperty(value = "需保养里程数")
    private String needMaintainMileage;
    @AutoDocProperty(value = "出库油量")
    private String outDepotOiMass;
    @AutoDocProperty(value = "入库油量")
    private String inDepotOiMass;
    @AutoDocProperty(value = "入库时是否有损伤")
    private String inDepotDamage;
    @AutoDocProperty(value = "托管车管理员")
    private String trusteeshipName;

    @AutoDocProperty(value = "行驶证是否正常交接")
    private String drivingLicenseJoin;
    @AutoDocProperty(value = "车辆钥匙是否正常交接")
    private String carKeyJoin;
    @AutoDocProperty(value = "操作日志列表信息")
    private List<ManagedCarOperateLogVO> lsLog;
}
