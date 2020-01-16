package com.atzuche.order.admin.vo.resp.cost;

import java.util.List;

import com.atzuche.order.commons.entity.dto.CommUseDriverInfoDTO;
import com.autoyol.doc.annotation.AutoDocProperty;

import lombok.Data;
import lombok.ToString;

/**
 * @author 胡春林
 * 附加驾驶员险
 */
@Data
@ToString
public class AdditionalDriverInsuranceVO {
	@AutoDocProperty("附加驾驶人列表")
	List<CommUseDriverInfoDTO> listCommUseDriverInfoDTO;
	
	
//    @AutoDocProperty("姓名")
//    private String realName;
//    @AutoDocProperty("手机号")
//    private String telephone;
//    @AutoDocProperty("身份证号")
//    private String IDcard;
//    @AutoDocProperty("驾驶证号")
//    private String driverLicenseNo;
//    @AutoDocProperty("驾驶证级别")
//    private String driverLevel;
//    @AutoDocProperty("驾驶证有效起始时间")
//    private String driverBeginTime;
//    @AutoDocProperty("驾驶证有效终止时间")
//    private String driverEndTime;
//    @AutoDocProperty("附加驾驶员险费用")
//    private String driverAmt;
//    @ApiModelProperty(value="子订单号",required=true)
//    private String renterOrderNo;
}
