package com.atzuche.order.admin.vo.req.car;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author meng.xu
 * 托管车出入库保存请求信息
 */
@Data
@ToString
public class ManagedCarUpdateReqVO {

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
}
