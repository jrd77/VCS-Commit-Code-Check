package com.atzuche.order.commons.vo.res.order;

import com.autoyol.doc.annotation.AutoDocProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.io.Serializable;
import java.util.Date;

/**
 * 优惠券信息
 *
 * @author pengcheng.fu
 * @date 2020/1/11 15:16
 */
@JsonInclude(value = Include.NON_EMPTY)
public class DisCouponMemInfoVO implements Serializable {

    private static final long serialVersionUID = 2651757933100923925L;

    @AutoDocProperty(value = "优惠券id")
    private String id;
    @AutoDocProperty(value = "优惠券名称")
    private String disName;
    @AutoDocProperty(value = "券类型:0,平台券")
    private String couponType;
    @AutoDocProperty(value = "优惠券有效期开始时间", hidden = true)
    private String startDate;
    @AutoDocProperty(value = "优惠券有效期结束时间", hidden = true)
    private String endDate;
    @AutoDocProperty(value = "优惠券使用时间", hidden = true)
    private Date useDate;
    @AutoDocProperty(value = "优惠券描述,条件组合详见4.0原型,如"
            + "1. 满X减X"
            + "2. 有效期：2016-11-14至2016-12-14，节假日不可用"
            + "3. 限精选优车用，仅抵扣租金")
    private String description;
    @AutoDocProperty(value = "优惠券描述,条件组合详见4.0原型,如"
            + "1. 满X减X"
            + "2. 有效期：2016-11-14至2016-12-14，节假日不可用"
            + "3. 限精选优车用，仅抵扣租金" +
            " 列表展示用")

    private String title;
    @AutoDocProperty(value = "优惠券使用满减条件,如满300", hidden = true)
    private String condAmt;
    @AutoDocProperty(value = "优惠券金额", hidden = true)
    private String disAmt;
    @AutoDocProperty(value = "是否叠加,1:是,0:否")
    private String overlaidType;
    @AutoDocProperty(value = "优惠券发放时间", hidden = true)
    private String createTime;
    @AutoDocProperty(value = "优惠券状态,1:未使用，2:已使用,3:已过期")
    private String status;
    @AutoDocProperty(value = "图片版优惠券，用于图片显示，图片地址")
    private String couponImagePath;

    @AutoDocProperty(value = "是否限首租使用，0：否，1：是")
    private String isFirstLimit;

    @AutoDocProperty(value = "本单可抵扣描述，如：本单可抵扣xxx元,只有在下单页面查看优惠券才有该字段")
    private String deductibleText;

    @AutoDocProperty(value = "优惠数据展示字段，含￥为金额，前端可以根据此判断是金额还是折扣-[5.9.1变更]")
    private String showPreferential;

    @AutoDocProperty(value = "取还车优惠券是否全免,0不全免,1全免")
    private String isCostFree;

    @AutoDocProperty(value = "平台券类型,该值为8表示取还车优惠券类型，不为8表示原来的平台券")
    private String platformCouponType;

    @Override
    public String toString() {
        return "DisCouponMemInfoVO{" +
                "id='" + id + '\'' +
                ", disName='" + disName + '\'' +
                ", couponType='" + couponType + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", useDate=" + useDate +
                ", description='" + description + '\'' +
                ", condAmt='" + condAmt + '\'' +
                ", disAmt='" + disAmt + '\'' +
                ", overlaidType='" + overlaidType + '\'' +
                ", createTime='" + createTime + '\'' +
                ", status='" + status + '\'' +
                ", couponImagePath='" + couponImagePath + '\'' +
                ", isFirstLimit='" + isFirstLimit + '\'' +
                ", deductibleText='" + deductibleText + '\'' +
                ", showPreferential='" + showPreferential + '\'' +
                ", isCostFree='" + isCostFree + '\'' +
                ", platformCouponType='" + platformCouponType + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisName() {
        return disName;
    }

    public void setDisName(String disName) {
        this.disName = disName;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCondAmt() {
        return condAmt;
    }

    public void setCondAmt(String condAmt) {
        this.condAmt = condAmt;
    }

    public String getDisAmt() {
        return disAmt;
    }

    public void setDisAmt(String disAmt) {
        this.disAmt = disAmt;
    }

    public String getOverlaidType() {
        return overlaidType;
    }

    public void setOverlaidType(String overlaidType) {
        this.overlaidType = overlaidType;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Date getUseDate() {
        return useDate;
    }

    public void setUseDate(Date useDate) {
        this.useDate = useDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCouponImagePath() {
        return couponImagePath;
    }

    public void setCouponImagePath(String couponImagePath) {
        this.couponImagePath = couponImagePath;
    }

    public String getIsFirstLimit() {
        return isFirstLimit;
    }

    public void setIsFirstLimit(String isFirstLimit) {
        this.isFirstLimit = isFirstLimit;
    }

    public String getDeductibleText() {
        return deductibleText;
    }

    public void setDeductibleText(String deductibleText) {
        this.deductibleText = deductibleText;
    }

    public String getCouponType() {
        return couponType;
    }

    public void setCouponType(String couponType) {
        this.couponType = couponType;
    }

    public String getShowPreferential() {
        return showPreferential;
    }

    public void setShowPreferential(String showPreferential) {
        this.showPreferential = showPreferential;
    }

    public String getIsCostFree() {
        return isCostFree;
    }

    public void setIsCostFree(String isCostFree) {
        this.isCostFree = isCostFree;
    }

    public String getPlatformCouponType() {
        return platformCouponType;
    }

    public void setPlatformCouponType(String platformCouponType) {
        this.platformCouponType = platformCouponType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
