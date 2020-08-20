package com.atzuche.order.admin.vo.resp.order;

import java.util.List;

import com.atzuche.order.commons.vo.DebtDetailVO;
import com.atzuche.order.commons.vo.res.order.CostItemVO;
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
    private DebtDetailVO debtDetailVO;
    @AutoDocProperty(value = "需要补付金额")
	private Integer needSupplementAmt; 
    @AutoDocProperty(value = "平台给租客的补贴明细")
	List<CostItemVO> platformToRenterSubsidyList;
   
    @AutoDocProperty(value = "钱包余额")
	private Integer walletBalance;
	
    @AutoDocProperty(value = "是否默认使用钱包：0-否，1-是")
	private Integer useWalletFlag;
	
    @AutoDocProperty(value = "是否可以编辑复选框 ：0-不能，1-可以")
	private Integer canUseWallet;
}
