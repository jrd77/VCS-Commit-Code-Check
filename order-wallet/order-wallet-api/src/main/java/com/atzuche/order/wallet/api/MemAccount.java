package com.atzuche.order.wallet.api;

import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/3/5 2:11 下午
 **/
@Data
@ToString
public class MemAccount {
    private String memNo;
    private List<AccountVO> accounts = new ArrayList<>();
}
