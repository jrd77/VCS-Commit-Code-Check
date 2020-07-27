package com.atzuche.order.commons.vo.req.income;

import com.autoyol.commons.web.ErrorCode;
import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.util.Assert;

import javax.validation.constraints.NotNull;

@Data
public class AccountOwnerIncomExamineVO {
    @AutoDocProperty(value = "主订单号")
    @NotBlank(message = "主订单号不能为空")
    private String orderNo;
    @AutoDocProperty(value = "用户注册号")
    @NotBlank(message = "用户注册号不能为空")
    private String memNo;
    @AutoDocProperty(value = "收益编号")
    @NotNull(message = "收益编号不能为空")
    private Integer accountOwnerIncomeExamineId;

    public void check() {
        Assert.notNull(getMemNo(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getOrderNo(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getAccountOwnerIncomeExamineId(), ErrorCode.PARAMETER_ERROR.getText());
    }
}
