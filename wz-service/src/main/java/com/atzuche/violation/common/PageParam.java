package com.atzuche.violation.common;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;


import javax.validation.constraints.NotNull;

@Data
public class PageParam {
    @NotNull(message = "页码不能为空")
    @AutoDocProperty(value = "当前页码数 默认1")
    private Integer pageNumber;  //当前页码数 (默认为1)

    @AutoDocProperty(value = "每页显示记录数")
    @NotNull(message = "页容量不能为空")
    private Integer pageSize;	  //每页显示记录数
}
