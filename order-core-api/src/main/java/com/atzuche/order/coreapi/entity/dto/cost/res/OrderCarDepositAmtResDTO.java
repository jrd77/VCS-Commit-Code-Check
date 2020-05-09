package com.atzuche.order.coreapi.entity.dto.cost.res;

import com.atzuche.order.renterorder.entity.RenterDepositDetailEntity;
import com.atzuche.order.renterorder.vo.RenterOrderCarDepositResVO;
import lombok.Data;

/**
 * @author pengcheng.fu
 * @date 2020/4/1 14:10
 */
@Data
public class OrderCarDepositAmtResDTO {

    /**
     * 车辆押金
     */
    private Integer carDepositAmt;

    /**
     * 车辆押金信息(收银台使用)
     */
    private RenterOrderCarDepositResVO carDeposit;

    /**
     * 车辆押金明细
     */
    private RenterDepositDetailEntity depositDetailEntity;

}
