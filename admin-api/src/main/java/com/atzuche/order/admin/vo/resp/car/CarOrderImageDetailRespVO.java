package com.atzuche.order.admin.vo.resp.car;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

@Data
public class CarOrderImageDetailRespVO implements java.io.Serializable {
    @AutoDocProperty("路径")
    private String path;
    @AutoDocProperty("时间")
    private String dateStr;
    @AutoDocProperty("操作人员")
    private String operator;
    @AutoDocProperty("图片ID")
    private Long id;
}
