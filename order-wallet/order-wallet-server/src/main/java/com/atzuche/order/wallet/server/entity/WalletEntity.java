package com.atzuche.order.wallet.server.entity;

import lombok.Data;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/1/9 11:08 上午
 **/
@Data
public class WalletEntity {

    private String memNo;
    private Integer balance;
    private Integer payBalance;
    private Integer giveBalance;


}
