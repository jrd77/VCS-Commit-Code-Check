package com.atzuche.violation.vo.resp;

import com.autoyol.doc.annotation.AutoDocIgnoreProperty;
import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * TemporaryRefundLogResVO
 *
 * @author shisong
 * @date 2020/1/6
 */
@Data
@ToString
public class TemporaryRefundLogResVO {

    @AutoDocIgnoreProperty()
    private Date createTime;

    @AutoDocProperty("操作时间")
    private String createTimeStr;

    @AutoDocProperty("操作人")
    private String operator;

    @AutoDocProperty("返还金额")
    private String amount;

    @AutoDocProperty("返还原因")
    private String reason;
}
