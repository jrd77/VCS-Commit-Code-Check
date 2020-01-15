package com.atzuche.order.commons.entity.dto;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 租客订单费用补贴明细
 *
 * @author pengcheng.fu
 * @date 2019/12/26 19:48
 */
@Data
@ToString
public class RenterOrderSubsidyDetailDTO implements Serializable {

    private static final long serialVersionUID = 3710750452449495704L;

    /**
     * 主订单号
     */
    @AutoDocProperty("主订单号")
    private String orderNo;
    /**
     * 子订单号
     */
    @AutoDocProperty("子订单号")
    private String renterOrderNo;
    /**
     * 会员号
     */
    @AutoDocProperty("会员号")
    private String memNo;
    /**
     * 补贴费用类型名称 如：租金 、取还车费用
     */
    @AutoDocProperty("补贴费用类型名称 如：租金 、取还车费用")
    private String subsidyTypeName;
    /**
     * 补贴费用类型编码
     */
    @AutoDocProperty("补贴费用类型编码")
    private String subsidyTypeCode;
    /**
     * 补贴来源方 1、租客 2、车主 3、平台
     */
    @AutoDocProperty("补贴来源方 1、租客 2、车主 3、平台")
    private String subsidySourceName;
    /**
     * 补贴来源方编码
     */
    @AutoDocProperty("补贴来源方编码")
    private String subsidySourceCode;
    /**
     * 补贴方名称 1、租客 2、车主 3、平台
     */
    @AutoDocProperty("补贴方名称")
    private String subsidyTargetName;
    /**
     * 补贴方编码
     */
    @AutoDocProperty("补贴方编码")
    private String subsidyTargetCode;
    /**
     * 补贴费用项名称 如：凹凸比、优惠券等
     */
    @AutoDocProperty("补贴费用项名称 如：凹凸比、优惠券等")
    private String subsidyCostName;
    /**
     * 补贴费用项编码
     */
    @AutoDocProperty("补贴费用项编码")
    private String subsidyCostCode;
    /**
     * 补贴描述
     */
    @AutoDocProperty("补贴描述")
    private String subsidyDesc;
    /**
     * 补贴金额
     */
    @AutoDocProperty("补贴金额")
    private Integer subsidyAmount;
    /**
     * 补贴凭证
     */
    @AutoDocProperty("补贴凭证")
    private String subsidyVoucher;

}
