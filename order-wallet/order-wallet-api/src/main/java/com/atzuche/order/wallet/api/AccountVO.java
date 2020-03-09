package com.atzuche.order.wallet.api;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/3/4 3:04 下午
 **/
@Data
@ToString
public class AccountVO implements Serializable {

    private Integer id;

    /**
     * 会员号
     */
    private Integer memNo;

    /**
     * 卡号
     */
    private String cardNo;
    /**
     * 卡号明文
     */
    private String cardNoPlain;

    /**
     * 持卡人
     */
    private String cardHolder;
    /**
     * 持卡人明文
     */
    private String cardHolderPlain;

    /**
     * 卡片类型，1：储蓄卡，2：信用卡
     */
    private Integer cardType;

    /**
     * 1:中国银行,2:中国工商银行,3:中国农业银行,4:中国建设银行,5:中国邮政储蓄银行,6:交通银行,7:招商银行,8:浦发银行,9:兴业银行,10:华夏银行,11:广发银行,12:中兴银行,13:光大银行,14:上海银行,15:上海农商银行,16:宁波银行,17:民生银行,18:杭州银行,19:南京银行
     */
    private Integer bankName;

    /**
     * 开户支行名称
     */
    private String branchBankName;

    /**
     * 是否已添加证件，1；是，0：否
     */
    private Integer addCert;

    /**
     * 证件类型：1：身份证,2：护照,3：军官证,4：士兵证,5：警官证,6：回乡证,7：港澳通行证
     */
    private Integer certType;

    /**
     * 证件号
     */
    private String certNo;
    /**
     * 证件号明文
     */
    private String certNoPlain;

    /**
     * 手机号
     */
    private Long mobile;

    /**
     * 盈余
     */
    private Integer surplus;

    /**
     * 收入总计
     */
    private Integer totalEarn;

    /**
     * 支出总计
     */
    private Integer totalPay;



    /**
     * icon图标路径
     */
    private String iconUrl;

    /**
     * 开户省份
     */
    private String province;

    /**
     * 开户城市
     */
    private String city;

    /**
     * 开户城市编号
     */
    private String cityCode;

    /**
     * 0:为旧版本，1为新版本
     */
    private Boolean isNew;
}
