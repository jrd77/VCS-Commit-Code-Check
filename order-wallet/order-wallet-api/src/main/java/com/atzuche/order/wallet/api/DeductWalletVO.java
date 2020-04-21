package com.atzuche.order.wallet.api;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/1/9 12:55 下午
 **/
@Data
public class DeductWalletVO {
    @NotBlank(message = "会员号不能为空")
    private String memNo;
    @NotBlank(message = "订单号不能为空")
    private String orderNo;
    @NotNull(message = "amt不能为空")
    private Integer amt;
    @AutoDocProperty(value = "消费信息描述")
    private String expDesc;
}
