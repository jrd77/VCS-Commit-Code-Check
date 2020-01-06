package com.atzuche.order.admin.vo.response;

import com.autoyol.doc.annotation.AutoDocProperty;

/**
 * Created by qincai.lin on 2020/1/2.
 */
public class OrderRemarkListResponseVO {

    @AutoDocProperty(value = "备注id")
    private String remarkId;

    @AutoDocProperty(value = "备注编号")
    private String number;

    @AutoDocProperty(value = "备注类型 1:理赔备注,2:限制租客延时备注,3:违章备注,4:违章处理备注,5:交易备注,6:呼叫中心备注,7:跟进备注,8:运营清算备注,9:电销中心备注,10:商品运营备注,11:风控备注")
    private String remarkType;

    @AutoDocProperty(value = "操作人部门")
    private String departmentName;

    @AutoDocProperty(value = "操作人姓名")
    private String operatorName;

    @AutoDocProperty(value = "新增时间")
    private String createTime;

    @AutoDocProperty(value = "更新时间")
    private String updateTime;

    @AutoDocProperty(value = "备注内容")
    private String remarkContent;


    public String getRemarkType() {
        return remarkType;
    }

    public void setRemarkType(String remarkType) {
        this.remarkType = remarkType;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getRemarkContent() {
        return remarkContent;
    }

    public void setRemarkContent(String remarkContent) {
        this.remarkContent = remarkContent;
    }

    public String getRemarkId() {
        return remarkId;
    }

    public void setRemarkId(String remarkId) {
        this.remarkId = remarkId;
    }

}
