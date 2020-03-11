package com.atzuche.order.coreapi.entity.dto;

import lombok.Data;

/**
 * 责任判定返回结果
 *
 * @author pengcheng.fu
 * @date 2020/3/5 14:45
 */
@Data
public class CancelOrderJudgeDutyResDTO {

    /**
     * 获得节假日罚金的会员号
     */
    private Integer memNo;

    /**
     * 收取节假日罚金命中的节假日id
     */
    private Integer holidayId;

    /**
     * 是否补贴罚金
     */
    private Boolean isSubsidyFineAmt;


    public CancelOrderJudgeDutyResDTO() {
    }

    public CancelOrderJudgeDutyResDTO(Integer memNo, Integer holidayId, Boolean isSubsidyFineAmt) {
        this.memNo = memNo;
        this.holidayId = holidayId;
        this.isSubsidyFineAmt = isSubsidyFineAmt;
    }


}
