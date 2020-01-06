package com.atzuche.order.admin.vo.resp.car;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
/**
 * 车主响应信息
 */
@Data
@ToString
public class CarOwnerInfoRespVO implements Serializable {
    @AutoDocProperty(value = "会员号")
    private String ownerNo;
    @AutoDocProperty(value = "车主姓名")
    private String realName;
    @AutoDocProperty(value = "车主联系电话")
    private String ownerPhone;
    @AutoDocProperty(value = "车主邮箱")
    private String email;
    @AutoDocProperty(value = "车主性别")
    private String gender;
    @AutoDocProperty(value = "驾驶证档案号")
    private String driLicRecordNo;
    @AutoDocProperty(value = "车主身份证号")
    private String idNo;
    @AutoDocProperty(value = "车主户籍地区")
    private String province;
    @AutoDocProperty(value = "车主户籍城市")
    private String city;
    @AutoDocProperty(value = "车主等级")
    private String memLevel;
}
