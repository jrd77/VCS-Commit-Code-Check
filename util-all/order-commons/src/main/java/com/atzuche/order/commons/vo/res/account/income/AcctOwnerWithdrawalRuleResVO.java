package com.atzuche.order.commons.vo.res.account.income;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

/**
 * @author pengcheng.fu
 * @date 2020/6/2410:21
 */
@Data
public class AcctOwnerWithdrawalRuleResVO {

    @AutoDocProperty("是否包含上海二清:0-否 1-是")
    private String isContainSecondary;

}
