package com.atzuche.order.admin.vo.resp.remark;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class OrderRemarkLogListResponseVO {


    @AutoDocProperty(value = "关联编号")
    private String number;

    @AutoDocProperty(value = "操作类型 1:新增,2更新,3删除")
    private String operateType;

    @AutoDocProperty(value = "操作人部门")
    private String departmentName;

    @AutoDocProperty(value = "操作人姓名")
    private String operatorName;

    @AutoDocProperty(value = "备注类型 1:理赔备注,2:限制租客延时备注,3:违章备注,4:违章处理备注,5:交易备注,6:呼叫中心备注,7:跟进备注,8:运营清算备注,9:电销中心备注,10:商品运营备注,11:风控备注")
    private String remarkType;

    @AutoDocProperty(value = "原备注内容")
    private String oldRemarkContent;

    @AutoDocProperty(value = "新备注内容")
    private String newRemarkContent;

    @AutoDocProperty(value = "操作时间")
    private String operateTime;

}
