package com.atzuche.order.settle.vo.req;

import com.atzuche.order.accountownercost.entity.AccountOwnerCostSettleDetailEntity;
import com.atzuche.order.accountplatorm.entity.AccountPlatformProfitDetailEntity;
import com.atzuche.order.accountplatorm.entity.AccountPlatformSubsidyDetailEntity;
import com.atzuche.order.accountrenterrentcost.entity.AccountRenterCostSettleDetailEntity;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 计算费用统计
 */
@Data
public class SettleOrdersAccount {



    /** ***********************************************************************************车主端统计费用 统计费用 */


    /**
     * 车主最终收益金额
     */
    private int ownerCostAmtFinal;

    /**
     * 车主费用明细 （包含抵扣历史欠款费用明细 ，车主户头收益 明细)
     */
    private List<AccountOwnerCostSettleDetailEntity> accountOwnerCostSettleDetails;

    /**************************************************************************************** 租客端统计费用 */


    /**
     * 租客应付费用
     */
    private int rentCostAmtFinal;
    /**
     * 租客实付付费用
     */
    private int rentCostPayAmtFinal;

    /**
     * 租客实付 车辆押金
     */
    private int depositAmt;



    /**
     * 租客费用明细 （包含 车辆押金抵扣租车费用费用，历史欠款费用，）
     */
    private List<AccountRenterCostSettleDetailEntity> accountRenterCostSettleDetails;


}
