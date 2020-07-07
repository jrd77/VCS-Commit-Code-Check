package com.atzuche.order.commons.vo.req;

import javax.validation.constraints.Max;
import javax.validation.constraints.Pattern;

import com.autoyol.doc.annotation.AutoDocProperty;
import org.hibernate.validator.constraints.NotBlank;

import lombok.Data;
import lombok.ToString;

/**
 * 提现请求参数
 *
 * @author pengcheng.fu
 */

@Data
@ToString
public class AccountOwnerCashExamineReqVO {

    @AutoDocProperty(value = "会员号,必填", required = true)
    @NotBlank(message = "memNo不能为空")
    private String memNo;

    @AutoDocProperty(value = "银行卡标识,必填", required = true)
    @NotBlank(message = "卡号标识不能为空")
    private String cardId;

    @AutoDocProperty(value = "提现金额,必填", required = true)
    @NotBlank(message = "提现金额不能为空")
    @Pattern(regexp = "^\\d*$", message = "提现金额必须为数字")
    @Max(value = 10000, message = "提现金额不能超过1万")
    private String amt;

    @AutoDocProperty(value = "短信动态验证码(上海银行提现验证码)")
    private String dynamicCode;

    /**
     * 一天最大提现次数
     */
    private Integer limitSize;

    /**
     * 最少提现金额
     */
    private Integer cashMinAmt;

}
