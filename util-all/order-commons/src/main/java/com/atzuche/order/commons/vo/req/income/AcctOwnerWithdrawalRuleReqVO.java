package com.atzuche.order.commons.vo.req.income;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Max;
import javax.validation.constraints.Pattern;

/**
 * @author pengcheng.fu
 * @date 2020/6/2410:18
 */
@Data
public class AcctOwnerWithdrawalRuleReqVO {

    @AutoDocProperty(value = "会员号", required = true)
    @NotBlank(message = "会员号不能为空")
    private String memNo;


    @AutoDocProperty(value = "提现金额", required = true)
    @NotBlank(message = "提现金额不能为空")
    @Pattern(regexp = "^\\d*$", message = "提现金额必须为数字")
    @Max(value = 10000, message = "提现金额不能超过1万")
    private String amt;


}
