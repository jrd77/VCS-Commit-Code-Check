package com.atzuche.order.wallet.api;

import lombok.Data;
import lombok.ToString;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/3/5 2:19 下午
 **/
@Data
@ToString
public class MemBalanceVO {
    private String memNo;
    private Integer balance;
}
