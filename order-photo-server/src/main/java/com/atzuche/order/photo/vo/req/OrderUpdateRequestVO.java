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

    @AutoDocProperty(value = "照片类型:1：取车照片，2:还车照片,4:违章缴纳凭证照片")
    @NotBlank(message = "照片类型不能为空")
    private String photoType;

}
