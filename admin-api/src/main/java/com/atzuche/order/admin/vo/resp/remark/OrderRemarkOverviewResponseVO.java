package com.atzuche.order.admin.vo.resp.remark;

import com.autoyol.doc.annotation.AutoDocProperty;

/**
 * Created by qincai.lin on 2020/1/2.
 */
public class OrderRemarkOverviewResponseVO {

    @AutoDocProperty(value = "备注类型 1:理赔备注,2:限制租客延时备注,3:违章备注,4:违章处理备注,5:交易备注,6:呼叫中心备注,7:跟进备注,8:运营清算备注,9:电销中心备注,10:商品运营备注,11:风控备注")
    private String remarkType;

    @AutoDocProperty(value = "备注数量")
    private String count;

    public String getRemarkType() {
        return remarkType;
    }

    public void setRemarkType(String remarkType) {
        this.remarkType = remarkType;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

}
