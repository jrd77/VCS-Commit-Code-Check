package com.atzuche.order.admin.vo.resp.car;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
/**
 * 附加驾驶人信息信息
 */
@Data
@ToString
public class CommUseDriverVO implements Serializable {
    @AutoDocProperty(value = "真实姓名")
    private String realName;
    @AutoDocProperty(value = "手机号")
    private String mobile;
    @AutoDocProperty(value = "身份证")
    private String idCard;
    @AutoDocProperty(value = "准驾车型")
    private String driLicAllowCar;
    @AutoDocProperty(value = "驾驶证有效起始日期")
    private String validityStartDateTxt;
    @AutoDocProperty(value = "驾驶证有效终止日期")
    private String validityEndDate;

}
