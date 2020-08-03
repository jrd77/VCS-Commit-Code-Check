package com.atzuche.order.commons.vo.res;

import com.atzuche.order.commons.entity.dto.RenterInsureCoefficientDTO;
import com.atzuche.order.commons.vo.res.order.*;
import com.autoyol.doc.annotation.AutoDocProperty;

import java.io.Serializable;
import java.util.List;

/**
 * 下单前费用计算返回信息
 *
 * @author pengcheng.fu
 * @date 2020/1/11 14:17
 */
public class NormalOrderCostCalculateResVO implements Serializable {

    private static final long serialVersionUID = 527549908038579737L;

    @AutoDocProperty(value = "费用小计")
    private TotalCostVO totalCost;

    @AutoDocProperty(value = "费用信息列表")
    private List<CostItemVO> costItemList;

    @AutoDocProperty(value = "抵扣信息,包括凹凸币、充值余额、优惠券等")
    private ReductionVO reduction;

    @AutoDocProperty(value = "车辆押金信息")
    private DepositAmtVO deposit;

    @AutoDocProperty(value = "违章押金信息")
    private IllegalDepositVO illegalDeposit;
    
    @AutoDocProperty(value = "区间配送信息")
    private SectionDeliveryVO sectionDelivery;

    @AutoDocProperty(value = "驾驶行为评分、各项系数")
    private RenterInsureCoefficientDTO renterInsureCoefficientDTO;

    public TotalCostVO getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(TotalCostVO totalCost) {
        this.totalCost = totalCost;
    }

    public List<CostItemVO> getCostItemList() {
        return costItemList;
    }

    public void setCostItemList(List<CostItemVO> costItemList) {
        this.costItemList = costItemList;
    }

    public ReductionVO getReduction() {
        return reduction;
    }

    public void setReduction(ReductionVO reduction) {
        this.reduction = reduction;
    }

    public DepositAmtVO getDeposit() {
        return deposit;
    }

    public void setDeposit(DepositAmtVO deposit) {
        this.deposit = deposit;
    }

    public IllegalDepositVO getIllegalDeposit() {
        return illegalDeposit;
    }

    public void setIllegalDeposit(IllegalDepositVO illegalDeposit) {
        this.illegalDeposit = illegalDeposit;
    }

	public SectionDeliveryVO getSectionDelivery() {
		return sectionDelivery;
	}

	public void setSectionDelivery(SectionDeliveryVO sectionDelivery) {
		this.sectionDelivery = sectionDelivery;
	}

    public RenterInsureCoefficientDTO getRenterInsureCoefficientDTO() {
        return renterInsureCoefficientDTO;
    }

    public void setRenterInsureCoefficientDTO(RenterInsureCoefficientDTO renterInsureCoefficientDTO) {
        this.renterInsureCoefficientDTO = renterInsureCoefficientDTO;
    }
}
