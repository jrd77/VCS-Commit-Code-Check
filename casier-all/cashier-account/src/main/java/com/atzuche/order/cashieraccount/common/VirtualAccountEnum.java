package com.atzuche.order.cashieraccount.common;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/3/10 11:47 上午
 **/
public enum VirtualAccountEnum {
    GSHD("GSHD","公司活动"),
    HZYH("'HZYH'","'合作用户'"),
    DBCSY("DBCSY","'代步车使用'"),
    VIP("VIP","VIP"),
    TEST("TEST","订单测试"),
    NBRYYC("NBRYYC","内部人员用车"),
    ZCYJ0("ZCYJ0","租车押金为0"),
    ECTK("ECTK","二次退款"),
    XCDD("XCDD","携程订单"),
    BDXMDD("BDXMDD","BD项目订单"),
    DBCXZ("DBCXZ","代步车续租");
    private String accountNo;
    private String accountName;

    VirtualAccountEnum(String accountNo, String accountName) {
        this.accountNo = accountNo;
        this.accountName = accountName;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public String getAccountName() {
        return accountName;
    }

    public static VirtualAccountEnum fromAccountNo(String accountNo){
        VirtualAccountEnum[] accountEnums = values();
        for(VirtualAccountEnum accountEnum:accountEnums){
            if(accountEnum.accountNo.equals(accountNo)){
                return accountEnum;
            }
        }
        throw new RuntimeException("accountNo:"+accountNo+" is invalid");
    }
}


