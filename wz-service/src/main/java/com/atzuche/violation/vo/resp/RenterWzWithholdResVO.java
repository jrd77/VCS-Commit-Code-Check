package com.atzuche.violation.vo.resp;

import com.autoyol.doc.annotation.AutoDocIgnoreProperty;
import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * RenterWzWithhold
 *
 * @author shisong
 * @date 2020/1/6
 */
@Data
@ToString
public class RenterWzWithholdResVO {

    @AutoDocProperty("剩余可用违章押金")
    private String shouldReturnDeposit;

    @AutoDocProperty("预计扣款金额 文案变更")
    private String provisionalDeduction;

    @AutoDocProperty("预计抵扣租车费用")
    private String yuJiDiKouZuCheFee;

    @AutoDocProperty("违章押金预计结算时间")
    private String expectSettleTimeStr;

    @AutoDocIgnoreProperty()
    private String expectSettleTime;

    @AutoDocProperty("违章押金实际结算时间")
    private String realSettleTimeStr;

    @AutoDocIgnoreProperty()
    private String realSettleTime;

    @AutoDocProperty("实际已暂扣金额")
    private String actuallyProvisionalDeduction;

    @AutoDocIgnoreProperty
    private Integer deductionStatus;

    @AutoDocProperty("扣款状态")
    private String deductionStatusStr;

    @AutoDocProperty("扣款时间")
    private String deductionTimeStr;

    @AutoDocIgnoreProperty
    private Date deductionTime;

    @AutoDocProperty("实际扣款金额 文案变更")
    private String shiJiZanKouJinE;

    @AutoDocProperty("剩余可用违章押金 文案变更")
    private String shiJiYiTuiWeiZhangYaJin;

    @AutoDocProperty("实际抵扣租车费用")
    private String shiJiDiKouZuCheFee;

    @AutoDocProperty("结算时抵扣历史欠款")
    private String jieSuanShiDiKouLiShiQianKuan;

    @AutoDocProperty("违章押金暂扣状态 1：暂扣 2：取消暂扣")
    private String detainStatus;
}
