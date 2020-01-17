package com.atzuche.order.admin.vo.req.order;

import com.autoyol.doc.annotation.AutoDocProperty;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/1/17 2:05 下午
 **/
public class OrderModifyConfirmReqVO {
    @NotBlank(message="车主memNo不能为空")
    @AutoDocProperty(value="车主memNo,必填，",required=true)
    private String memNo;

    @NotBlank(message="租客子单号")
    @AutoDocProperty(value="租客子单号,必填，",required=true)
    private String modifyApplicationId;

    @NotBlank(message="修改订单申请flag不能为空，1-同意，0-拒绝")
    @AutoDocProperty(value="修改订单申请flag不能为空，1-同意，0-拒绝",required=true)
    private String flag;
}
