package com.atzuche.order.commons.vo;

import com.autoyol.doc.annotation.AutoDocProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/2/13 12:08 下午
 **/
@Data
@ToString
public class WzDepositDetailVO {
    @AutoDocProperty(value = "应收违章押金")
    private Integer yingshouDeposit;

    @AutoDocProperty(value = "实收违章押金")
    private Integer shishouDeposit;

    @AutoDocProperty(value = "实收违章押金")
    private Integer isAuthorize;

    /**
     * 应退押金
     */
    @AutoDocProperty(value = "应退违章押金")
    private Integer shouldReturnDeposit;
    /**
     * 实退押金
     */
    @AutoDocProperty(value = "实退违章押金")
    private Integer realReturnDeposit;
    /**
     * 结算状态
     */
    @AutoDocProperty(value = "结算状态")
    private Integer settleStatus;
    /**
     * 结算时间
     */
    @AutoDocProperty(value = "结算时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date settleTime;


    @AutoDocProperty(value = "免押方式")
    private Integer freeDepositType;

    public Integer getYingshouDeposit() {
        if(yingshouDeposit!=null) {
            return -yingshouDeposit;
        }
        return null;
    }

    public Integer getShouldReturnDeposit() {
        if(shouldReturnDeposit!=null) {
            return -shouldReturnDeposit;
        }
        return null;
    }
}
