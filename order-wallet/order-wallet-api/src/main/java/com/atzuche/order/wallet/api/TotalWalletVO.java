package com.atzuche.order.wallet.api;

import lombok.Data;

import java.io.Serializable;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/1/9 12:53 下午
 **/
@Data
public class TotalWalletVO implements Serializable {
    private String memNo;
    private String total;
}
