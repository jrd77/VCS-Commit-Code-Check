package com.atzuche.order.cashieraccount.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * 提现记录处理日志
 *
 * @author pengcheng.fu
 * @date 2020-07-17 11:48:27
 */
@Data
public class AccountOwnerCashExamineHandleLogEntity implements Serializable {


    private static final long serialVersionUID = -7954006787750125557L;
    /**
     * 主键
     */
    private Integer id;

    /**
     * 会员号
     */
    private Integer memNo;

    /**
     * 提现记录ID:account_owner_cash_examine.id
     */
    private Integer cashExamineId;

    /**
     * 处理结果:0-未处理 1-处理成功 2-处理失败
     */
    private Integer handleResult;

    /**
     * 失败原因编码
     */
    private String failCode;

    /**
     * 失败原因文案
     */
    private String failMessage;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 创建人
     */
    private String createOp;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;

    /**
     * 修改人
     */
    private String updateOp;

    /**
     * 0-正常，1-已逻辑删除
     */
    private Integer isDelete;

}
