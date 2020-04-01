package com.atzuche.order.admin.vo.resp.order;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/1/17 4:46 下午
 **/
@Data
@ToString
public class AdminModifyOrderFeeCompareVO {

    @AutoDocProperty(value = "修改前的费用明细")
    private AdminModifyOrderFeeVO before;
    @AutoDocProperty(value = "修改后的费用明细")
    private AdminModifyOrderFeeVO after;
    @AutoDocProperty(value = "用户总欠款")
    private Integer debtAmt;
}
