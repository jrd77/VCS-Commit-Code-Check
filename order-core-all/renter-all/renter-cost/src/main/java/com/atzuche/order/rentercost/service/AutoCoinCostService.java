package com.atzuche.order.rentercost.service;

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
public class AutoCoinCostService {


    /**
     * 计算凹凸币抵扣信息
     *
     * @param surplusRentAmt 剩余租金
     * @param crmCustPoint 锁定凹凸币数据
     * @return RenterOrderSubsidyDetailDTO 凹凸币抵扣信息
     */
    public RenterOrderSubsidyDetailDTO calAutoCoinDeductInfo(Integer surplusRentAmt, CrmCustPointDTO crmCustPoint) {

        if (surplusRentAmt == 0 || null == crmCustPoint || crmCustPoint.getPointValue() == 0) {
            return null;
        }




        return null;

    }




}
