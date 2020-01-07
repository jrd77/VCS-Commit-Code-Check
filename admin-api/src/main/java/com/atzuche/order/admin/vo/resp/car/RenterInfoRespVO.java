package com.atzuche.order.admin.vo.resp.car;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 订单详细信息 - 查看租客信息响应信息
 */
@Data
@ToString
public class RenterInfoRespVO implements Serializable {
    @AutoDocProperty(value = "会员号")
    private String memNo;
    @AutoDocProperty(value = "租客姓名")
    private String realName;
    @AutoDocProperty(value = "租客联系电话")
    private String renterPhone;
    @AutoDocProperty(value = "租客邮箱")
    private String email;
    @AutoDocProperty(value = "租客性别")
    private String gender;
    @AutoDocProperty(value = "驾驶证档案号")
    private String driLicRecordNo;
    @AutoDocProperty(value = "内部员工")
    private String internalStaff;
    @AutoDocProperty(value = "太保用户")
    private String cpicFlag;
    @AutoDocProperty(value = "会员标签")
    private List<String> labelTagList;
    @AutoDocProperty(value = "风险说明")
    private String riskDescription;
    @AutoDocProperty(value = "风控审核状态")
    private String riskStatus;
    @AutoDocProperty(value = "建议后续工作")
    private String followWork;
    @AutoDocProperty(value = "下单地址")
    private String reqAddr;
    @AutoDocProperty(value = "附加驾驶人信息")
    private List<CommUseDriverVO> commUseDrivers;

    ///////////////////人工调度处理-租客信息////////////////////////
    @AutoDocProperty(value = "身份证")
    private String idNo;
    @AutoDocProperty(value = "加入时间")
    private String regTimeTxt;
    @AutoDocProperty(value = "驾龄")
    private String driveAge;
    @AutoDocProperty(value = "成功租车次数")
    private String buyTimes;
    @AutoDocProperty(value = "成功升级车辆次数")
    private String upgrades;

}
