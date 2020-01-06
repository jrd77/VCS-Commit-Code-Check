package com.atzuche.order.admin.vo.req.car;

import com.autoyol.doc.annotation.AutoDocProperty;

public class CarOrderImageDetailReqVO {
    @AutoDocProperty("图片路径")
    private String path;
    @AutoDocProperty("图片类型")
    private Integer type;
}
