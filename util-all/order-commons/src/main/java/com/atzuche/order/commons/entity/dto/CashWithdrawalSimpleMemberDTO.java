package com.atzuche.order.commons.entity.dto;

import com.autoyol.doc.annotation.AutoDocProperty;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CashWithdrawalSimpleMemberDTO {

	@AutoDocProperty(value = "会员号")
    private String memNo;

    @AutoDocProperty(value = "真实姓名")
    private String realName;

    @AutoDocProperty(value = "手机号")
    private String mobile;
    
    @AutoDocProperty(value = "0：未上传，1：已上传，2：已认证，3：认证不通过")
    private Integer idCardAuth;
    
    @AutoDocProperty(value = "可提现余额")
    private Integer balance;
}
