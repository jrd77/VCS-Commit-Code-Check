package com.atzuche.order.settle.vo.req;

import com.atzuche.order.accountownercost.entity.AccountOwnerCostSettleDetailEntity;
import com.atzuche.order.accountplatorm.entity.AccountPlatformProfitDetailEntity;
import com.atzuche.order.accountplatorm.entity.AccountPlatformSubsidyDetailEntity;
import com.atzuche.order.accountrenterrentcost.entity.AccountRenterCostSettleDetailEntity;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 计算费用统计
 */
@Data
public class SettleOrdersDefinition {




    /** **************************************************************************平台端 统计费用 */
    /**
     * 平台收益总额
     */
    private int platformProfitAmt;

    /**
     * 平台收益明细
     */
    private List<AccountPlatformProfitDetailEntity> accountPlatformProfitDetails;
    /**
     * 平台补贴总额
     */
    private int platformSubsidyAmt;
    /**
     * 平台补贴
     */
    private List<AccountPlatformSubsidyDetailEntity> accountPlatformSubsidyDetails;

    /** ***********************************************************************************车主端统计费用 统计费用 */
    /**
     * 车主采购费
     */
    private int ownerCostAmt;
    /**
     * 车主补贴
     */
    private int ownerSubsidyAmt;

    /**
     * 车主最终收益金额
     */
    private int ownerCostAmtFinal;

    /**
     * 车主费用明细
     */
    private List<AccountOwnerCostSettleDetailEntity> accountOwnerCostSettleDetails;

    /**************************************************************************************** 租客端统计费用 */
    /**
     * 租客费用
     */
    private int rentCostAmt;
    /**
     * 租客补贴
     */
    private int rentSubsidyAmt;


    /**
     * 租客费用明细
     */
    private List<AccountRenterCostSettleDetailEntity> accountRenterCostSettleDetails;

    /**
     * 平台收益明细
     * @param accountPlatformProfitDetail
     */
    public void addPlatformProfit(AccountPlatformProfitDetailEntity accountPlatformProfitDetail){
        List<AccountPlatformProfitDetailEntity> accountPlatformProfitDetails = getAccountPlatformProfitDetails();
        if(!CollectionUtils.isEmpty(accountPlatformProfitDetails)){
            accountPlatformProfitDetails = new ArrayList<>();
        }
        accountPlatformProfitDetails.add(accountPlatformProfitDetail);
        setAccountPlatformProfitDetails(accountPlatformProfitDetails);
    }
    /**
     * 平台补贴 费用明细
     * @param accountPlatformSubsidyDetail
     */
    public void addPlatformSubsidy(AccountPlatformSubsidyDetailEntity accountPlatformSubsidyDetail){
        List<AccountPlatformSubsidyDetailEntity> accountPlatformSubsidyDetailEntitys = getAccountPlatformSubsidyDetails();
        if(!CollectionUtils.isEmpty(accountPlatformSubsidyDetailEntitys)){
            accountPlatformSubsidyDetailEntitys = new ArrayList<>();
        }
        accountPlatformSubsidyDetailEntitys.add(accountPlatformSubsidyDetail);
        setAccountPlatformSubsidyDetails(accountPlatformSubsidyDetailEntitys);
    }

    /**
     * 租客费用补贴总额
     * @param accountRenterCostSettleDetailEntity
     */
    public void addRentCosts(AccountRenterCostSettleDetailEntity accountRenterCostSettleDetailEntity){
        List<AccountRenterCostSettleDetailEntity> accountRenterCostSettleDetailEntitys = getAccountRenterCostSettleDetails();
        if(!CollectionUtils.isEmpty(accountRenterCostSettleDetailEntitys)){
            accountRenterCostSettleDetailEntitys = new ArrayList<>();
        }
        accountRenterCostSettleDetailEntitys.add(accountRenterCostSettleDetailEntity);
        setAccountRenterCostSettleDetails(accountRenterCostSettleDetailEntitys);
    }

    /**
     * 车主费用明细
     * @param accountOwnerCostSettleDetailEntity
     */
    public void addOwnerCosts(AccountOwnerCostSettleDetailEntity accountOwnerCostSettleDetailEntity){
        List<AccountOwnerCostSettleDetailEntity> accountOwnerCostSettleDetailEntitys = getAccountOwnerCostSettleDetails();
        if(!CollectionUtils.isEmpty(accountOwnerCostSettleDetailEntitys)){
            accountOwnerCostSettleDetailEntitys = new ArrayList<>();
        }
        accountOwnerCostSettleDetailEntitys.add(accountOwnerCostSettleDetailEntity);
        setAccountOwnerCostSettleDetails(accountOwnerCostSettleDetailEntitys);
    }
}
