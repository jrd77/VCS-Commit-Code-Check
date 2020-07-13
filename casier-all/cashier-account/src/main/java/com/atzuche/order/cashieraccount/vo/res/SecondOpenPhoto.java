package com.atzuche.order.cashieraccount.vo.res;

import java.util.Date;

import com.autoyol.doc.annotation.AutoDocProperty;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

/**
 * @author 胡春林
 * @since 2020-05-28
 */
@Data
public class SecondOpenPhoto {
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
     * 车主开户信息ID
     */
    @AutoDocProperty("车主开户信息ID")
    private Integer openId;

    /**
     * 1：营业执照+组织机构代码证 2：三证合一 3：五证合一
     */
    @AutoDocProperty("1：营业执照+组织机构代码证 2：三证合一 3：五证合一")
    private Integer cardType;

    /**
     * 身份证正面照片
     */
    @AutoDocProperty("身份证正面照片")
    private String idcardFrontUrl;

    /**
     * 身份证反面照片
     */
    @AutoDocProperty("身份证反面照片")
    private String idcardBackUrl;

    /**
     * 营业执照照片(三证合一  五证合一)
     */
    @AutoDocProperty("营业执照照片(三证合一  五证合一)")
    private String businessLicenseUrl;

    /**
     * 组织机构代码照片
     */
    @AutoDocProperty("组织机构代码照片")
    private String organizationCodeUrl;

    /**
     * 经办人身份证正面照片
     */
    @AutoDocProperty("经办人身份证正面照片")
    private String handledByFrontUrl;

    /**
     * 经办人身份证反面照片
     */
    @AutoDocProperty("经办人身份证反面照片")
    private String handledByBackUrl;

    /**
     * 法人授权委托书
     */
    @AutoDocProperty("法人授权委托书")
    private String personAttorneyUrl;

    /**
     * 授权委托书状态 2:失败 1：成功 0:未审核 3：审核中
     */
    @AutoDocProperty("授权委托书状态 2:失败 1：成功")
    private Integer attorneyStatus;

    /**
     * 经办人身份证正面照片状态 2：失败 1：成功
     */
    @AutoDocProperty("经办人身份证正面照片状态 2：失败 1：成功")
    private Integer handledFrontStatus;

    /**
     * 经办人身份证反面照片状态 2：失败 1：成功
     */
    @AutoDocProperty("经办人身份证反面照片状态 2：失败 1：成功")
    private Integer handledBackStatus;

    /**
     * 身份证正面照片状态 2：失败 1：成功
     */
    @AutoDocProperty("身份证正面照片状态 2：失败 1：成功")
    private Integer idcardFrontStatus;

    /**
     * 身份证反面照片状态 2：失败 1：成功
     */
    @AutoDocProperty("身份证反面照片状态 2：失败 1：成功")
    private Integer idcardBackStatus;

    /**
     * 营业执照状态 2：失败 1：成功
     */
    @AutoDocProperty("营业执照状态 2：失败 1：成功")
    private Integer businessLicenseStatus;

    /**
     * 组织机构代码照片状态 2：失败 1：成功
     */
    @AutoDocProperty("组织机构代码照片状态 2：失败 1：成功")
    private Integer organizationCodeStatus;

    /**
     * 创建时间
     */
    @AutoDocProperty("创建时间")
    @JsonFormat(pattern="yyyy-MM-dd",timezone="GMT+8")
    private Date createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern="yyyy-MM-dd",timezone="GMT+8")
    private Date updateTime;

    /**
     * 创建者
     */
    private String createOp;

    /**
     * 更新者
     */
    private String updateOp;

    /**
     * 逻辑删除 0：否 1：是
     */
    private Integer isDelete;
    
}
