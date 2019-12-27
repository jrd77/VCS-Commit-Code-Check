package com.atzuche.order.rentercost.service;

import com.atzuche.order.commons.constant.OrderConstant;
import com.atzuche.order.commons.enums.SubsidySourceCodeEnum;
import com.atzuche.order.rentercost.entity.dto.CrmCustPointDTO;
import com.atzuche.order.rentercost.entity.dto.RenterOrderSubsidyDetailDTO;
import org.springframework.stereotype.Service;

/**
 * 凹凸币抵扣计算
 *
 * @author pengcheng.fu
 * @date 2019/12/26 20:47
 */

@Service
public class AutoCoinCostCalService {


    /**
     * 计算凹凸币抵扣信息
     * <p>租客下单时凹凸币抵扣不超过车辆租金(originalRentAmt)5%</p>
     *
     * @param originalRentAmt 原始租金
     * @param surplusRentAmt  剩余租金
     * @param crmCustPoint    锁定凹凸币数据
     * @return RenterOrderSubsidyDetailDTO 凹凸币抵扣信息
     */
    public RenterOrderSubsidyDetailDTO calAutoCoinDeductInfo(int originalRentAmt, int surplusRentAmt,
                                                             CrmCustPointDTO crmCustPoint) {

        if (0 == originalRentAmt || 0 == surplusRentAmt || null == crmCustPoint || 0 == crmCustPoint.getPointValue()) {
            return null;
        }

        int pointValue = crmCustPoint.getPointValue();
        //会员凹凸币金额(1:100),默认全部抵扣
        int pointValueDeducExchange = pointValue / 100;
        //原始租金的5%
        int rentAmtFivePercent = originalRentAmt * OrderConstant.AUTO_COIN_DEDUCT_RATIO / 100;
        if (rentAmtFivePercent <= pointValueDeducExchange) {
            pointValueDeducExchange = rentAmtFivePercent;
        }

        // 凹凸币只能抵扣租金
        if (surplusRentAmt <= pointValueDeducExchange) {
            pointValueDeducExchange = surplusRentAmt;
        }

        if (pointValueDeducExchange > 0) {
            RenterOrderSubsidyDetailDTO renterOrderSubsidyDetailDTO = new RenterOrderSubsidyDetailDTO();

            renterOrderSubsidyDetailDTO.setMemNo(crmCustPoint.getMemNo());
            renterOrderSubsidyDetailDTO.setSubsidyAmount(pointValueDeducExchange);
            renterOrderSubsidyDetailDTO.setSubsidySourceCode(SubsidySourceCodeEnum.PLATFORM.getCode());
            renterOrderSubsidyDetailDTO.setSubsidySourceName(SubsidySourceCodeEnum.PLATFORM.getDesc());

            renterOrderSubsidyDetailDTO.setSubsidyTargetCode(SubsidySourceCodeEnum.RENTER.getCode());
            renterOrderSubsidyDetailDTO.setSubsidTypeName(SubsidySourceCodeEnum.RENTER.getDesc());
            renterOrderSubsidyDetailDTO.setSubsidyDesc("使用凹凸币抵扣租金");
            return renterOrderSubsidyDetailDTO;
        }

        return null;

    }


}
