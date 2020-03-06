package com.atzuche.order.wallet.api;

import com.autoyol.doc.annotation.AutoDocProperty;
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
    @AutoDocProperty(value = "会员号")
    private String memNo;
    @AutoDocProperty(value = "绑卡的信息集合")
    private List<AccountVO> accounts = new ArrayList<>();
}
