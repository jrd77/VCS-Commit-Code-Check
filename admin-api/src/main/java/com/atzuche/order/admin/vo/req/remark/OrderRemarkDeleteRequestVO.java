package com.atzuche.order.admin.vo.req.remark;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;

@Data
@ToString
public class OrderRemarkDeleteRequestVO {

    @AutoDocProperty(value = "备注id")
    @NotBlank(message = "备注id不能为空")
    private String remarkId;


}
