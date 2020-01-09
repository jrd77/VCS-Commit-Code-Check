package com.atzuche.order.wallet.api;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/1/9 12:55 下午
 **/
@Data
public class DeductWalletVO {
    @NotBlank
    private String memNo;
    @NotBlank
    private String orderNo;
    @NotNull
    private Integer amt;
}
