package com.atzuche.order.admin.vo.resp.remark;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

/**
 * Created by qincai.lin on 2020/1/2.
 */
@Data
@ToString
public class OrderRemarkOverviewResponseVO {

    @AutoDocProperty(value = "备注类型文本，如:理赔备注")
    private String remarkTypeText;

    @AutoDocProperty(value = "备注类型")
    private String remarkType;

    @AutoDocProperty(value = "备注数量")
    private String typeCount;

}
