package com.atzuche.order.wallet.api;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/3/6 4:01 下午
 **/
@Data
@ToString
public class MemAccountStatReqVO {
    @AutoDocProperty(value = "会员列表")
    @NotNull
    private List<String> memNoList;
}
