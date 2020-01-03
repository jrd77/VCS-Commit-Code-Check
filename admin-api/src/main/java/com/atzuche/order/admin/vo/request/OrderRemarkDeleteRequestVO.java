package com.atzuche.order.admin.vo.request;

import com.autoyol.doc.annotation.AutoDocProperty;

/**
 * Created by qincai.lin on 2019/12/30.
 */
public class OrderRemarkDeleteRequestVO {

    @AutoDocProperty(value = "备注id")
    private String remarkId;

    public String getRemarkId() {
        return remarkId;
    }

    public void setRemarkId(String remarkId) {
        this.remarkId = remarkId;
    }

}
