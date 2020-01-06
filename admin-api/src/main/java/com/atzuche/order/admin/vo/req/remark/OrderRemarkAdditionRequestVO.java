package com.atzuche.order.admin.vo.req.remark;

import com.autoyol.doc.annotation.AutoDocProperty;

/**
 * Created by qincai.lin on 2019/12/30.
 */
public class OrderRemarkAdditionRequestVO {

    @AutoDocProperty(value = "订单号")
    private String orderNo;

    @AutoDocProperty(value = "操作人部门id")
    private String departmentId;

    @AutoDocProperty(value = "备注类型 1:理赔备注,2:限制租客延时备注,3:违章备注,4:违章处理备注,5:交易备注,6:呼叫中心备注,7:跟进备注,8:运营清算备注,9:电销中心备注,10:商品运营备注,11:风控备注")
    private String remarkType;

    @AutoDocProperty(value = "备注内容")
    private String remarkContent;

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public String getRemarkType() {
        return remarkType;
    }

    public void setRemarkType(String remarkType) {
        this.remarkType = remarkType;
    }

    public String getRemarkContent() {
        return remarkContent;
    }

    public void setRemarkContent(String remarkContent) {
        this.remarkContent = remarkContent;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

}
