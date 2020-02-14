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
//@Data
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
        if(CollectionUtils.isEmpty(accountPlatformProfitDetails)){
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
        if(CollectionUtils.isEmpty(accountPlatformSubsidyDetailEntitys)){
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
        if(CollectionUtils.isEmpty(accountRenterCostSettleDetailEntitys)){
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
        if(CollectionUtils.isEmpty(accountOwnerCostSettleDetailEntitys)){
            accountOwnerCostSettleDetailEntitys = new ArrayList<>();
        }
        accountOwnerCostSettleDetailEntitys.add(accountOwnerCostSettleDetailEntity);
        setAccountOwnerCostSettleDetails(accountOwnerCostSettleDetailEntitys);
    }
	public int getPlatformProfitAmt() {
		return platformProfitAmt;
	}
	public void setPlatformProfitAmt(int platformProfitAmt) {
		this.platformProfitAmt = platformProfitAmt;
	}
	public List<AccountPlatformProfitDetailEntity> getAccountPlatformProfitDetails() {
		return accountPlatformProfitDetails;
	}
	public void setAccountPlatformProfitDetails(List<AccountPlatformProfitDetailEntity> accountPlatformProfitDetails) {
		this.accountPlatformProfitDetails = accountPlatformProfitDetails;
	}
	public int getPlatformSubsidyAmt() {
		return platformSubsidyAmt;
	}
	public void setPlatformSubsidyAmt(int platformSubsidyAmt) {
		this.platformSubsidyAmt = platformSubsidyAmt;
	}
	public List<AccountPlatformSubsidyDetailEntity> getAccountPlatformSubsidyDetails() {
		return accountPlatformSubsidyDetails;
	}
	public void setAccountPlatformSubsidyDetails(List<AccountPlatformSubsidyDetailEntity> accountPlatformSubsidyDetails) {
		this.accountPlatformSubsidyDetails = accountPlatformSubsidyDetails;
	}
	public int getOwnerCostAmt() {
		return ownerCostAmt;
	}
	public void setOwnerCostAmt(int ownerCostAmt) {
		this.ownerCostAmt = ownerCostAmt;
	}
	public int getOwnerSubsidyAmt() {
		return ownerSubsidyAmt;
	}
	public void setOwnerSubsidyAmt(int ownerSubsidyAmt) {
		this.ownerSubsidyAmt = ownerSubsidyAmt;
	}
	public int getOwnerCostAmtFinal() {
		return ownerCostAmtFinal;
	}
	public void setOwnerCostAmtFinal(int ownerCostAmtFinal) {
		this.ownerCostAmtFinal = ownerCostAmtFinal;
	}
	public List<AccountOwnerCostSettleDetailEntity> getAccountOwnerCostSettleDetails() {
		return accountOwnerCostSettleDetails;
	}
	public void setAccountOwnerCostSettleDetails(List<AccountOwnerCostSettleDetailEntity> accountOwnerCostSettleDetails) {
		this.accountOwnerCostSettleDetails = accountOwnerCostSettleDetails;
	}
	public int getRentCostAmt() {
		return rentCostAmt;
	}
	public void setRentCostAmt(int rentCostAmt) {
		this.rentCostAmt = rentCostAmt;
	}
	public int getRentSubsidyAmt() {
		return rentSubsidyAmt;
	}
	public void setRentSubsidyAmt(int rentSubsidyAmt) {
		this.rentSubsidyAmt = rentSubsidyAmt;
	}
	public List<AccountRenterCostSettleDetailEntity> getAccountRenterCostSettleDetails() {
		return accountRenterCostSettleDetails;
	}
	public void setAccountRenterCostSettleDetails(
			List<AccountRenterCostSettleDetailEntity> accountRenterCostSettleDetails) {
		this.accountRenterCostSettleDetails = accountRenterCostSettleDetails;
	}
    
    
}
