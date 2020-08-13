package com.atzuche.order.cashieraccount.vo.res;

import java.util.Date;

import com.autoyol.doc.annotation.AutoDocProperty;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * <p>
 * </p>
 *
 * @author 胡春林
 * @since 2020-05-21
 */
@Data
@NoArgsConstructor
public class SecondOpenOwner{
    /**
     * 主键
     */
    private Integer id;

    /**
     * 会员号
     */
    @AutoDocProperty("会员号")
    private Integer memNo;

    /**
     * 记账簿No
     */
    @AutoDocProperty("记账簿No")
    private String viracctNo;

    /**
     * 记账簿户名
     */
    @AutoDocProperty("记账簿户名")
    private String virAcctName;

    /**
     * 车主类型 1：个人 2：企业
     */
    @AutoDocProperty("车主类型 1：个人 2：企业")
    private Integer ownerType;

    /**
     * 证件类型（01、身份证,02、护照03、台胞证04、港澳通行证05、其他证件）
     */
    @AutoDocProperty("证件类型（01、身份证,02、护照03、台胞证04、港澳通行证05、其他证件）")
    private String cardType;

    /**
     * 证件号码(如是企业则是法人)
     */
    @AutoDocProperty("证件号码(如是企业则是法人)")
    private String corpIdNo;

    /**
     * YYYYMMDD,如为长期有效统一填写20990101(证件号码)
     */
    @AutoDocProperty("YYYYMMDD,如为长期有效统一填写20990101(证件号码)")
    @JsonFormat(pattern="yyyyMMdd",timezone="GMT+8")
    private Date corpLimitDate;

    /**
     * 法人手机号
     */
    @AutoDocProperty("法人手机号")
    private String cropTelephone;

    /**
     * 绑定卡清算行行号
     */
    @AutoDocProperty("绑定卡清算行行号")
    private String acctBank;

    /**
     * 银行预留手机号（企业为法人手机号）
     */
    @AutoDocProperty("银行预留手机号（企业为法人手机号）")
    private String reservedPhone;

    /**
     * 英文客户全称(英文企业全称)只有企业有
     */
    @AutoDocProperty("英文客户全称(英文企业全称)只有企业有")
    private String companyDesc;

    /**
     * 真实姓名(企业：法人的真实姓名)
     */
    @AutoDocProperty("真实姓名(企业：法人的真实姓名)")
    private String corpName;

    /**
     * 经办人姓名
     */
    @AutoDocProperty("经办人姓名")
    private String operateName;

    /**
     * 经办人身份证号码
     */
    @AutoDocProperty("经办人身份证号码")
    private String operateIdNo;

    /**
     * 经办人身份证到期期限 YYYYMMDD，如为长期有效,请统一填写20990101
     */
    @AutoDocProperty("经办人身份证到期期限 YYYYMMDD，如为长期有效,请统一填写20990101")
    @JsonFormat(pattern="yyyyMMdd",timezone="GMT+8")
    private Date operateLimitDate;

    /**
     * 经办人手机号
     */
    @AutoDocProperty("经办人手机号")
    private String operateMobile;

    /**
     * 组织机构代码/三证合一统一社会信用代码(只有企业有)
     */
    @AutoDocProperty("组织机构代码/三证合一统一社会信用代码(只有企业有)")
    private String identityNo;

    /**
     * 工商注册号请填写营业执照上的工商注册编号或者统一社会信用代码
     */
    @AutoDocProperty("工商注册号请填写营业执照上的工商注册编号或者统一社会信用代码")
    private String registeredNumber;

    /**
     * 注册期限 YYYYMMDD，如为长期有效,请统一填写20990101
     */
    @AutoDocProperty(" 注册期限 YYYYMMDD，如为长期有效,请统一填写20990101")
    @JsonFormat(pattern="yyyyMMdd",timezone="GMT+8")
    private Date expirationDate;

    /**
     * 开户行全称
     */
    @AutoDocProperty("开户行全称")
    private String bankName;

    /**
     * 开户行行号
     */
    @AutoDocProperty("开户行行号")
    private String bankNumber;

    /**
     * 注册资本(单位为“元”)
     */
    @AutoDocProperty("注册资本(单位为“元”)")
    private String registeredCapital;

    /**
     * 企业规模 L:大型/M:中型/S:小型/T:微型
     * T：1=少于15人;
     * S: 2=15-50人，3=50-150人;
     * M: 4=150-500人，5=500-2000人;
     * L: 6=2000人以上
     */
    @AutoDocProperty("企业规模 L:大型/M:中型/S:小型/T:微型")
    private String enterprisScale;

    /**
     * 重要信息，公司注册地址
     */
    @AutoDocProperty("重要信息，公司注册地址")
    private String registeredAddress;

    /**
     * 注册地址电话号码,如果没有,请填写法人的手机号码
     */
    @AutoDocProperty("注册地址电话号码,如果没有,请填写法人的手机号码")
    private String registeredAddPhone;

    /**
     * 办公地址
     */
    @AutoDocProperty("办公地址")
    private String workAddress;

    /**
     * 办公地固定电话
     */
    @AutoDocProperty("办公地固定电话")
    private String workAddPhone;

    /**
     * 填A或B或C或D
     * A - 企业法人营业执照（企业法人）
     * B - 企业营业执照（非企业法人）
     * C - 个体工商执照
     * D - 民办非企业登记证书
     * 如果不明确或者没有收集,可默认填写
     * A - 企业法人营业执照（企业法人）
     */
    @AutoDocProperty("填A或B或C或D A企业法人营业执照（企业法人)")
    private String registeredType;

    /**
     * 开户状态 0：未完成 1：完成
     */
    @AutoDocProperty("开户状态 0：未完成 1：完成")
    private Integer openStatus;

    /**
     * 绑卡状态 0：未完成  1：完成
     */
    @AutoDocProperty("绑卡状态 0：未完成  1：完成")
    private Integer bindCardStatus;

    /**
     * 打款激活状态 0：未完成 1：完成
     */
    @AutoDocProperty("打款激活状态 0：未完成 1：完成")
    private Integer paymentActivationStatus;

    /**
     * 创建时间
     */
    @AutoDocProperty("创建时间")
    @JsonFormat(pattern="yyyy-MM-dd",timezone="GMT+8")
    private Date createTime;

    /**
     * 更新时间
     */
    @AutoDocProperty("更新时间")
    @JsonFormat(pattern="yyyy-MM-dd",timezone="GMT+8")
    private Date updateTime;

    /**
     * 创建者
     */
    @AutoDocProperty("创建者")
    private String createOp;

    /**
     * 更新者
     */
    @AutoDocProperty("更新者")
    private String updateOp;

    /**
     * 逻辑删除 0：否 1：是
     */
    private Integer isDelete;

}
