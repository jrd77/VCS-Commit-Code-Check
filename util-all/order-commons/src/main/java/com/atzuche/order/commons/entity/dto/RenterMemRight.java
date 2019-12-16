package com.atzuche.order.commons.entity.dto;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2019/12/14 3:20 下午
 **/
public enum RenterMemRight {

    VIP(1,"VIP客户"),
    STAFF(2,"内部员工"),
    MEM_LEVEL(3,"会员等级"),
    CPIC_MEM(4,"太保会员"),
    OTA_MEM(5,"OTA会员"),
    INSURANCE_CLIENT(6,"保险公司客户");
    ;
    /**
     * 权益编码
     */
    private int rightCode;
    /**
     * 权益名称
     */
    private String rightName;

    RenterMemRight(int rightCode, String rightName) {
        this.rightCode = rightCode;
        this.rightName = rightName;
    }
}
