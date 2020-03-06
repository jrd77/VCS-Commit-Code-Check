package com.atzuche.order.wallet.api;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/3/6 4:03 下午
 **/
@ToString
@Data
public class MemAccountStatRespVO {
    @AutoDocProperty(value = "统计结果集")
    private List<MemAccountStatVO> statVOList;
}
