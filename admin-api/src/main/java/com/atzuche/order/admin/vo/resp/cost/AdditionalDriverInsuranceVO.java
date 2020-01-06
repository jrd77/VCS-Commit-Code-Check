package com.atzuche.order.admin.vo.resp.cost;

import com.autoyol.doc.annotation.AutoDocProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author 胡春林
 * 附加驾驶员险
 */
@Data
@ToString
public class AdditionalDriverInsuranceVO {

    @AutoDocProperty("姓名")
    private String realName;
    @AutoDocProperty("手机号")
    private String telephone;
    @AutoDocProperty("身份证号")
    private String IDcard;
    @AutoDocProperty("驾驶证号")
    private String driverLicenseNo;
    @AutoDocProperty("驾驶证级别")
    private String driverLevel;
    @AutoDocProperty("驾驶证有效起始时间")
    private String driverBeginTime;
    @AutoDocProperty("驾驶证有效终止时间")
    private String driverEndTime;
    @AutoDocProperty("附加驾驶员险")
    private String driverAmt;
    @ApiModelProperty(value="子订单号",required=true)
    private String renterOrderNo;
}
