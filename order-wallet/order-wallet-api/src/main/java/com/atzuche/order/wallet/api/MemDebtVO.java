package com.atzuche.order.wallet.api;

import lombok.Data;
import lombok.ToString;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/3/16 5:39 下午
 **/
@Data
@ToString
public class MemDebtVO {
    private String memNo;
    private Integer debt;
}
