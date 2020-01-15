package com.atzuche.order.commons.enums;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * @author pengcheng.fu
 * @date 2020/1/14 14:51
 */
@Getter
public enum OsTypeEnum {
    /**
     * IOS
     **/
    IOS("IOS", "1", "APP"),
    /**
     * ANDROID
     **/
    ANDROID("ANDROID", "1", "APP"),
    /**
     * H5
     **/
    H5("H5", "2", "H5"),
    /**
     * WECHAT_APPLETS,微信小程序
     **/
    WECHAT_APPLETS("miniprogram-wechat", "3", "WX"),
    /**
     * ALIPAY_APPLETS,支付宝小程序
     **/
    ALIPAY_APPLETS("miniprogram-alipay", "4", "ALIPAY"),
    /**
     * OTHER
     **/
    OTHER("other", "5", "other");


    private String os;

    private String osVal;

    private String osDesc;


    OsTypeEnum(String os, String osVal, String osDesc) {
        this.os = os;
        this.osVal = osVal;
        this.osDesc = osDesc;
    }


    /**
     * convert int value to OsTypeEnum
     *
     * @param os int value
     * @return OsTypeEnum
     */
    public OsTypeEnum from(String os) {
        OsTypeEnum[] osTypes = values();
        for (OsTypeEnum osType : osTypes) {
            if (StringUtils.equals(osType.os, os)) {
                return osType;
            }
        }
        return OTHER;
    }
}
