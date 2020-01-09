package com.atzuche.order.wallet.server.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/1/9 11:09 上午
 **/
@Data
public class WalletLogEntity implements Serializable {
    private Integer id;
    private String memNo;
    private String orderNo;
    private Integer amt;
    private String flag;
    private String flagTxt;
}
