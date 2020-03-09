package com.atzuche.order.wallet.api;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/3/6 3:45 下午
 **/
@Data
@ToString
public class MemAccountStatVO {
    @AutoDocProperty(value = "会员号")
    private String memNo;
    @AutoDocProperty(value = "卡的数量")
    private int cardNum;
}
