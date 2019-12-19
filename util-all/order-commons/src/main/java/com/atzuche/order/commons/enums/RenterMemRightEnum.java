package com.atzuche.order.commons.enums;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.entity.dto.OrderContextDto;
import com.atzuche.order.commons.entity.dto.RenterMemberRightDto;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2019/12/14 3:20 下午
 **/
@Getter
public enum RenterMemRightEnum {

    VIP("1","会员标识"),
    STAFF("2","内部员工"),
    MEM_LEVEL("3","会员等级"),
    CPIC_MEM("4","太保会员"),
    OTA_MEM("5","OTA会员"),
    INSURANCE_CLIENT("6","保险公司客户");
    ;
    /**
     * 权益编码
     */
    private String rightCode;
    /**
     * 权益名称
     */
    private String rightName;

    RenterMemRightEnum(String rightCode, String rightName) {
        this.rightCode = rightCode;
        this.rightName = rightName;
    }

}
