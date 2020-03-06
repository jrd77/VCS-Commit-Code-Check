package com.atzuche.order.wallet.server.entity;

import lombok.Data;
import lombok.ToString;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/3/4 10:34 上午
 **/
@Data
@ToString
public class BalanceEntity {
    private Integer id;
    private String memNo;
    private Integer balance;
}
