package com.atzuche.order.commons.vo.req;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

@Data
public class OwnerUpdateSeeVO {
    /*
    * 车主会员号
    * */
    @NotBlank(message = "会员号不能为空")
    private String ownerMemNo;
}
