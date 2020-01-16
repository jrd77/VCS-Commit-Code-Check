package com.atzuche.order.photo.vo.req;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;

@Data
@ToString
public class OrderUpdateRequestVO {

    @AutoDocProperty(value = "图片id")
    @NotBlank(message = "图片id不能为空")
    private String photoId;

}
