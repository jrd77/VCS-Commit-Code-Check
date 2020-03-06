package com.atzuche.order.wallet.api;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/3/6 9:36 上午
 **/
@Data
@ToString
public class DeductBalanceVO {
    @AutoDocProperty(value = "会员号")
    @NotBlank(message = "会员号不能为空")
    private String memNo;
    @AutoDocProperty(value = "扣减金额")
    @NotNull(message = "扣减金额不能为空")
    private Integer deduct;
}
