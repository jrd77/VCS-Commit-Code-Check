package com.atzuche.order.commons.enums;

import lombok.Getter;

/**
 *
 *
 * @author pengcheng.fu
 * @date 2020/1/14 14:51
 */
@Getter
public enum OsTypeEnum {
    /**IOS**/
    IOS("IOS","1"),
    /**ANDROID**/
    ANDROID("ANDROID","1"),
    /**H5**/
    H5("H5","2"),
    /**WECHAT_APPLETS**/
    WECHAT_APPLETS("miniprogram-wechat", "3"),
    /**ALIPAY_APPLETS**/
    ALIPAY_APPLETS("miniprogram-alipay","4"),
    /**OTHER**/
    OTHER("other","5");


    private String os;

    private String osVal;


    OsTypeEnum(String os, String osVal) {
        this.os = os;
        this.osVal = osVal;
    }


    /**
     * convert int value to OsTypeEnum
     *
     * @param os int value
     * @return OsTypeEnum
     */
    public OsTypeEnum from(String os) {
        OsTypeEnum[] statuses = values();
        for (OsTypeEnum s : statuses) {
            if (os == s.os) {
                return s;
            }
        }
        throw new RuntimeException("the value :" + os + " not supported,please check");
    }
}
