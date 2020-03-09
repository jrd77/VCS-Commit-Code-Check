package com.atzuche.order.wallet.server.entity;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/3/9 10:23 上午
 **/
@Data
@ToString
public class CarDepositEntity implements Serializable {
    private Integer id;
    private String carNo;
    private Integer hwDepositFlag;
    private Integer hwDepositTotal;
    private Integer hwDepositBill;
}
