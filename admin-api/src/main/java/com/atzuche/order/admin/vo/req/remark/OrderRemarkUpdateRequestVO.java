package com.atzuche.order.admin.vo.req.remark;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;

@Data
@ToString
public class OrderRemarkUpdateRequestVO {

    @AutoDocProperty(value = "备注id")
    @NotBlank(message = "备注id不能为空")
    private String remarkId;

    @AutoDocProperty(value = "操作人部门id")
    @NotBlank(message = "操作人部门id不能为空")
    private String departmentId;

    @AutoDocProperty(value = "备注类型 1:理赔备注,2:限制租客延时备注,3:违章备注,4:违章处理备注,5:交易备注,6:呼叫中心备注,7:跟进备注,8:运营清算备注,9:电销中心备注,10:商品运营备注,11:风控备注")
    @NotBlank(message = "备注类型不能为空")
    private String remarkType;

    @AutoDocProperty(value = "备注内容")
    private String remarkContent;

    @AutoDocProperty(value = "跟进状态,只有remarkType=7时，才需要填写,跟进状态， 1:未跟进,2:跟进成功,3:跟进失败,4:找车中,5:重复需求,6:首次被拒,7:短信促单")
    private String followStatus;

    @AutoDocProperty(value = "跟进失败原因，只有remarkType=7时，才需要填写,跟进失败原因：1：租客放弃-支付失败，2：租客放弃-拒绝促单，3：租客放弃-电话不通，4：租客放弃-不愿意再等，5：租客放弃-租客需求变更，6：租客放弃-其他原因，7：租客自己找到车，8：找不到车-品牌车型不符，9：找不到车-价格不符，10：找不到车-取车位置不符，11：找不到车-来不及服务，12：找不到车-其他原因，13：其他原因-租客核查不通过，14：其他原因-系统问题，15：其他原因-内部测试，16：其他原因-已完成，17：其他原因-重新找车")
    private String followFailReason;

    @AutoDocProperty(value = "是否限制租客延时，只有remarkType=2时，才需要填写,1:是,0:否")
    private String limitDelayed;



}
