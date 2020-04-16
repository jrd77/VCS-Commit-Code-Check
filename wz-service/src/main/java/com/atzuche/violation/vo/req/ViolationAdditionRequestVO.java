package com.atzuche.violation.vo.req;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Created by qincai.lin on 2020/3/16.
 */
@Data
@ToString
public class ViolationAdditionRequestVO {

    @AutoDocProperty(value = "订单号")
    @NotBlank(message = "主订单号不能为空")
    private String orderNo;

    @AutoDocProperty(value = "车牌号")
    @NotBlank(message = "车牌号不能为空")
    private String plateNum;

    @AutoDocProperty(value = "违章时间")
    @NotBlank(message = "违章时间不能为空")
    private String violationTime;

    @AutoDocProperty(value = "违章地址")
    @NotBlank(message = "违章地址不能为空")
    private String violationAddress;

    @AutoDocProperty(value = "违章内容")
    @NotBlank(message = "违章内容不能为空")
    private String violationContent;

    @AutoDocProperty(value = "罚款")
    @NotBlank(message = "罚款不能为空")
    private String violationFine;

    @AutoDocProperty(value = "违章扣分")
    @NotBlank(message = "违章扣分不能为空")
    private String violationScore;

}