package com.atzuche.order.admin.dto.convert;

import lombok.Data;

/**
 * 车辆押金暂扣扣款信息变更日记
 *
 *
 * @author pengcheng.fu
 * @date 2020/4/24 15:41
 */

@Data
public class CarDepositDetainOptLogDTO {

    /**
     * 违章违约金
     */
    private Integer tempWzFine;
    /**
     * 违章停运费
     */
    private Integer tempWzStopCharge;
    /**
     * 理赔维修费
     */
    private Integer tempClaimRepairCharge;
    /**
     * 理赔停运费
     */
    private Integer tempClaimStopCharge;
    /**
     * 风控维修费
     */
    private Integer tempRiskRepairCharge;
    /**
     * 风控停运费
     */
    private Integer tempRiskStopCharge;
    /**
     * 风控收车费
     */
    private Integer tempRiskCollectCharge;


}
